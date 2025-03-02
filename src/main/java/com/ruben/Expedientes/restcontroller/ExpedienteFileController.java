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

        try {
            String uploadedBy = authentication.getName();

            ExpedienteFileDTO uploadedFile = fileService.uploadFile(
                    file, expedienteType, expedienteId, category, description, uploadedBy);

            // Notificar via WebSocket
            notificationService.broadcast(
                    "/topic/files/" + expedienteType.toLowerCase() + "/" + expedienteId,
                    new FileNotification("UPLOADED", uploadedFile)
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFile);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid file upload request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));

        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al procesar el archivo"));
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
            List<ExpedienteFileDTO> files = fileService.getFilesByExpediente(expedienteType, expedienteId);
            return ResponseEntity.ok(files);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Descargar archivo
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileId) {

        Optional<ExpedienteFileDTO> fileOpt = fileService.downloadFile(fileId);

        if (fileOpt.isEmpty()) {
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
    }

    /**
     * Eliminar archivo
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(
            @PathVariable Long fileId,
            Authentication authentication) {

        String requestedBy = authentication.getName();
        boolean deleted = fileService.deleteFile(fileId, requestedBy);

        if (deleted) {
            // Notificar via WebSocket
            notificationService.broadcast(
                    "/topic/files/deleted",
                    new FileNotification("DELETED", fileId)
            );

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
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
            ExpedienteFileService.FileStatisticsDTO stats =
                    fileService.getFileStatistics(expedienteType, expedienteId);
            return ResponseEntity.ok(stats);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
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