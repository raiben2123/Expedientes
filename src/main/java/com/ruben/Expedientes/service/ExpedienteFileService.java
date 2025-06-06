package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.ExpedienteFileDTO;
import com.ruben.Expedientes.model.ExpedienteFile;
import com.ruben.Expedientes.repository.ExpedienteFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpedienteFileService {

    private final ExpedienteFileRepository fileRepository;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Transactional
    public ExpedienteFileDTO uploadFile(
            MultipartFile file,
            String expedienteType,
            Long expedienteId,
            String category,
            String description,
            String uploadedBy) throws IOException {

        validateFile(file);
        validateExpedienteType(expedienteType);

        ExpedienteFile expedienteFile = ExpedienteFile.builder()
                .originalFileName(file.getOriginalFilename())
                .storedFileName(generateUniqueFileName(file.getOriginalFilename()))
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .expedienteType(expedienteType.toUpperCase())
                .expedienteId(expedienteId)
                .category(category)
                .description(description)
                .uploadedBy(uploadedBy)
                .fileData(file.getBytes()) // Asegurarse de que esto se establece correctamente
                .uploadedAt(LocalDateTime.now())
                .build();

        try {
            ExpedienteFile savedFile = fileRepository.save(expedienteFile);
            log.info("Archivo guardado correctamente: {}", savedFile.getId());
            return convertToDTO(savedFile, false);
        } catch (Exception e) {
            log.error("Error al guardar el archivo: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el archivo", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ExpedienteFileDTO> getFilesByExpediente(String expedienteType, Long expedienteId) {
        validateExpedienteType(expedienteType);

        List<ExpedienteFile> files = fileRepository.findByExpedienteTypeAndExpedienteIdOrderByUploadedAtDesc(
                expedienteType.toUpperCase(), expedienteId);

        return files.stream()
                .map(file -> convertToDTO(file, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ExpedienteFileDTO> downloadFile(Long fileId) {
        return fileRepository.findById(fileId)
                .map(file -> convertToDTO(file, true));
    }

    public boolean deleteFile(Long fileId, String requestedBy) {
        Optional<ExpedienteFile> fileOpt = fileRepository.findById(fileId);
        if (fileOpt.isPresent()) {
            fileRepository.deleteById(fileId);
            log.info("File deleted: {} by {}", fileOpt.get().getOriginalFileName(), requestedBy);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public FileStatisticsDTO getFileStatistics(String expedienteType, Long expedienteId) {
        validateExpedienteType(expedienteType);

        List<ExpedienteFile> files = fileRepository.findByExpedienteTypeAndExpedienteId(
                expedienteType.toUpperCase(), expedienteId);

        long totalFiles = files.size();
        long totalSizeBytes = files.stream().mapToLong(ExpedienteFile::getFileSize).sum();

        return FileStatisticsDTO.builder()
                .totalFiles(totalFiles)
                .totalSizeBytes(totalSizeBytes)
                .totalSizeMB(totalSizeBytes / (1024.0 * 1024.0))
                .build();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (10MB)");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + file.getContentType());
        }
    }

    private void validateExpedienteType(String expedienteType) {
        if (expedienteType == null ||
                (!expedienteType.equalsIgnoreCase("PRINCIPAL") &&
                        !expedienteType.equalsIgnoreCase("SECUNDARIO"))) {
            throw new IllegalArgumentException("Tipo de expediente debe ser PRINCIPAL o SECUNDARIO");
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private ExpedienteFileDTO convertToDTO(ExpedienteFile file, boolean includeData) {
        return ExpedienteFileDTO.builder()
                .id(file.getId())
                .originalFileName(file.getOriginalFileName())
                .contentType(file.getContentType())
                .fileSize(file.getFileSize())
                .uploadedAt(file.getUploadedAt())
                .uploadedBy(file.getUploadedBy())
                .description(file.getDescription())
                .category(file.getCategory())
                .expedienteType(file.getExpedienteType())
                .expedienteId(file.getExpedienteId())
                .fileData(includeData ? file.getFileData() : null)
                .build();
    }

    @lombok.Builder
    @lombok.Data
    public static class FileStatisticsDTO {
        private long totalFiles;
        private long totalSizeBytes;
        private double totalSizeMB;
    }
}
