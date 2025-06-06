package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.ExpedienteFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpedienteFileRepository extends JpaRepository<ExpedienteFile, Long> {

    /**
     * Buscar archivos por tipo de expediente y ID
     */
    List<ExpedienteFile> findByExpedienteTypeAndExpedienteId(String expedienteType, Long expedienteId);

    /**
     * Buscar archivos por tipo de expediente y ID ordenados por fecha de subida descendente
     */
    List<ExpedienteFile> findByExpedienteTypeAndExpedienteIdOrderByUploadedAtDesc(String expedienteType, Long expedienteId);

//    /**
//     * Buscar archivos por categoría
//     */
//    List<ExpedienteFile> findByExpedienteTipoAndExpedienteIdAndCategory(
//            String expedienteType, Long expedienteId, String category);

    /**
     * Buscar archivos subidos por un usuario específico
     */
    List<ExpedienteFile> findByUploadedByOrderByUploadedAtDesc(String uploadedBy);

//    /**
//     * Buscar archivos por rango de fechas
//     */
//    List<ExpedienteFile> findByUploadedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Contar archivos por expediente
     */
    @Query("SELECT COUNT(f) FROM ExpedienteFile f WHERE f.expedienteType = :expedienteType AND f.expedienteId = :expedienteId")
    long countByExpediente(@Param("expedienteType") String expedienteType, @Param("expedienteId") Long expedienteId);

    /**
     * Calcular tamaño total de archivos por expediente
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM ExpedienteFile f WHERE f.expedienteType = :expedienteType AND f.expedienteId = :expedienteId")
    long calculateTotalSizeByExpediente(@Param("expedienteType") String expedienteType, @Param("expedienteId") Long expedienteId);

    /**
     * Buscar archivos grandes (para limpieza)
     */
    @Query("SELECT f FROM ExpedienteFile f WHERE f.fileSize > :sizeInBytes ORDER BY f.fileSize DESC")
    List<ExpedienteFile> findLargeFiles(@Param("sizeInBytes") long sizeInBytes);

    /**
     * Buscar archivos por tipo de contenido
     */
    List<ExpedienteFile> findByContentTypeContainingIgnoreCase(String contentType);

    /**
     * Estadísticas de archivos por usuario
     */
    @Query("SELECT f.uploadedBy, COUNT(f), SUM(f.fileSize) FROM ExpedienteFile f GROUP BY f.uploadedBy")
    List<Object[]> getFileStatisticsByUser();

    /**
     * Archivos huérfanos (expedientes que ya no existen)
     */
    @Query(value = """
        SELECT f.* FROM expediente_files f 
        WHERE (f.expediente_type = 'PRINCIPAL' 
               AND NOT EXISTS (SELECT 1 FROM expedientes_principales ep WHERE ep.id = f.expediente_id))
           OR (f.expediente_type = 'SECUNDARIO' 
               AND NOT EXISTS (SELECT 1 FROM expedientes_secundarios es WHERE es.id = f.expediente_id))
        """, nativeQuery = true)
    List<ExpedienteFile> findOrphanedFiles();

    @Query(value = """
    INSERT INTO expediente_files (
        category, content_type, description, expediente_id, expediente_type,
        file_data, file_size, original_file_name, stored_file_name, uploaded_at, uploaded_by
    ) VALUES (
        :#{#file.category}, 
        :#{#file.contentType}, 
        :#{#file.description}, 
        :#{#file.expedienteId}, 
        :#{#file.expedienteType},
        :#{#file.fileData}, 
        :#{#file.fileSize}, 
        :#{#file.originalFileName}, 
        :#{#file.storedFileName}, 
        :#{#file.uploadedAt}, 
        :#{#file.uploadedBy}
    ) RETURNING *
    """, nativeQuery = true)
    ExpedienteFile saveFile(@Param("file") ExpedienteFile file);
}