package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.Notification;
import com.ruben.Expedientes.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Obtener todas las notificaciones de un usuario ordenadas por fecha de creación descendente
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Obtener notificaciones no leídas de un usuario
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    // Obtener notificaciones por tipo
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NotificationType type);
    
    // Obtener notificaciones de un expediente específico
    List<Notification> findByExpedienteIdOrderByCreatedAtDesc(Long expedienteId);
    
    // Obtener notificaciones que deben enviarse (recordatorios pendientes)
    @Query("SELECT n FROM Notification n WHERE n.reminderDate <= :currentTime AND n.isRead = false AND n.type = 'REMINDER'")
    List<Notification> findPendingReminders(@Param("currentTime") LocalDateTime currentTime);
    
    // Contar notificaciones no leídas por usuario
    long countByUserIdAndIsReadFalse(Long userId);
    
    // Marcar todas las notificaciones de un usuario como leídas
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.updatedAt = :updatedAt WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId, @Param("updatedAt") LocalDateTime updatedAt);
    
    // Eliminar notificaciones antiguas (limpieza automática)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate AND n.isRead = true")
    int deleteOldReadNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Buscar notificaciones por texto en título o mensaje
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR LOWER(n.message) LIKE LOWER(CONCAT('%', :searchText, '%'))) ORDER BY n.createdAt DESC")
    List<Notification> searchNotifications(@Param("userId") Long userId, @Param("searchText") String searchText);
}