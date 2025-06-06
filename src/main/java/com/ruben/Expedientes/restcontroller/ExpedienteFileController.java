package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.ExpedienteFileDTO;
import com.ruben.Expedientes.service.ExpedienteFileService;
import com.ruben.Expedientes.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://143.131.204.234:*"}, allowCredentials = "true")
public class ExpedienteFileController {

    private final ExpedienteFileService fileService;
    private final WebSocketNotificationService notificationService;

    /**
     * Subir archivo a un expediente
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("expedienteType") String expedienteType,
            @RequestParam("expedienteId") Long expedienteId,
            @RequestParam(value = "category", required = false, defaultValue = "GENERAL") String category,
            @RequestParam(value = "description", required = false) String description,
            Authentication authentication) {
        
        log.info("Iniciando subida de archivo: {} para expediente {} tipo {}", 
                file.getOriginalFilename(), expedienteId, expedienteType);
        
        try {
            // Validaciones básicas
            if (file.isEmpty()) {
                log.warn("Archivo vacío recibido");
                return ResponseEntity.badRequest().body(new ErrorResponse("El archivo no puede estar vacío"));
            }

            if (authentication == null || authentication.getName() == null) {
                log.warn("Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Usuario no autenticado"));
            }

            String uploadedBy = authentication.getName();
            log.info("Usuario autenticado: {}", uploadedBy);

            ExpedienteFileDTO uploadedFile = fileService.uploadFile(
                    file, expedienteType, expedienteId, category, description, uploadedBy);

            log.info("Archivo subido exitosamente con ID: {}", uploadedFile.getId());

            // Notificar via WebSocket
            try {
                notificationService.broadcast(
                        "/topic/files/" + expedienteType.toLowerCase() + "/" + expedienteId,
                        new FileNotification("UPLOADED", uploadedFile)
                );
            } catch (Exception e) {
                log.warn("Error enviando notificación WebSocket: {}", e.getMessage());
                // No afecta la subida del archivo
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFile);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid file upload request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));

        } catch (IOException e) {
            log.error("Error de IO al subir archivo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al procesar el archivo: " + e.getMessage()));
                    
        } catch (Exception e) {
            log.error("Error inesperado al subir archivo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Obtener lista de archivos de un expediente
     */
    @GetMapping("/{expedienteType}/{expedienteId}")
    public ResponseEntity<List<ExpedienteFileDTO>> getFiles(
            @PathVariable String expedienteType,
            @PathVariable Long expedienteId) {

        try {
            log.info("Obteniendo archivos para expediente {} tipo {}", expedienteId, expedienteType);
            List<ExpedienteFileDTO> files = fileService.getFilesByExpediente(expedienteType, expedienteId);
            return ResponseEntity.ok(files);

        } catch (IllegalArgumentException e) {
            log.warn("Parámetros inválidos: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error obteniendo archivos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Descargar archivo
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileId) {

        try {
            log.info("Descargando archivo con ID: {}", fileId);
            Optional<ExpedienteFileDTO> fileOpt = fileService.downloadFile(fileId);

            if (fileOpt.isEmpty()) {
                log.warn("Archivo no encontrado: {}", fileId);
                return ResponseEntity.notFound().build();
            }

            ExpedienteFileDTO file = fileOpt.get();
            ByteArrayResource resource = new ByteArrayResource(file.getFileData());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getOriginalFileName() + "\"")
                    .contentLength(file.getFileSize())
                    .body(resource);

        } catch (Exception e) {
            log.error("Error descargando archivo {}: {}", fileId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al descargar el archivo"));
        }
    }

    /**
     * Eliminar archivo
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(
            @PathVariable Long fileId,
            Authentication authentication) {

        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Usuario no autenticado"));
            }

            String requestedBy = authentication.getName();
            log.info("Eliminando archivo {} solicitado por {}", fileId, requestedBy);
            
            boolean deleted = fileService.deleteFile(fileId, requestedBy);

            if (deleted) {
                // Notificar via WebSocket
                try {
                    notificationService.broadcast(
                            "/topic/files/deleted",
                            new FileNotification("DELETED", fileId)
                    );
                } catch (Exception e) {
                    log.warn("Error enviando notificación de eliminación: {}", e.getMessage());
                }

                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            log.error("Error eliminando archivo {}: {}", fileId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al eliminar el archivo"));
        }
    }

    /**
     * Obtener estadísticas de archivos
     */
    @GetMapping("/stats/{expedienteType}/{expedienteId}")
    public ResponseEntity<ExpedienteFileService.FileStatisticsDTO> getFileStatistics(
            @PathVariable String expedienteType,
            @PathVariable Long expedienteId) {

        try {
            log.info("Obteniendo estadísticas para expediente {} tipo {}", expedienteId, expedienteType);
            ExpedienteFileService.FileStatisticsDTO stats =
                    fileService.getFileStatistics(expedienteType, expedienteId);
            return ResponseEntity.ok(stats);

        } catch (IllegalArgumentException e) {
            log.warn("Parámetros inválidos para estadísticas: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error obteniendo estadísticas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DTOs para respuestas

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class ErrorResponse {
        private String message;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class FileNotification {
        private String action;
        private Object data;
    }
}