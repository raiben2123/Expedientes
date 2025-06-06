package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.NotificationRequestDTO;
import com.ruben.Expedientes.dto.NotificationResponseDTO;
import com.ruben.Expedientes.model.Notification;
import com.ruben.Expedientes.model.NotificationType;
import com.ruben.Expedientes.model.User;
import com.ruben.Expedientes.repository.NotificationRepository;
import com.ruben.Expedientes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final WebSocketNotificationService webSocketService;
    
    // Crear una nueva notificación
    public NotificationResponseDTO createNotification(NotificationRequestDTO requestDTO, Long userId) {
        log.debug("Creating notification for user {}: {}", userId, requestDTO.getTitle());
        
        Notification notification = new Notification();
        notification.setTitle(requestDTO.getTitle());
        notification.setMessage(requestDTO.getMessage());
        notification.setType(requestDTO.getType());
        notification.setPriority(requestDTO.getPriority());
        notification.setExpedienteId(requestDTO.getExpedienteId());
        notification.setExpedienteNumero(requestDTO.getExpedienteNumero());
        notification.setUserId(userId);
        notification.setReminderDate(requestDTO.getReminderDate());
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Enviar notificación en tiempo real via WebSocket
        NotificationResponseDTO responseDTO = convertToResponseDTO(savedNotification);
        
        // Obtener username para WebSocket
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            webSocketService.sendToUser(
                userOpt.get().getUsername(), 
                "/queue/notifications", 
                responseDTO
            );
        }
        
        log.info("Notification created successfully with ID: {}", savedNotification.getId());
        return responseDTO;
    }
    
    // Obtener todas las notificaciones de un usuario
    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        log.debug("Getting notifications for user: {}", userId);
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Obtener notificaciones no leídas
    public List<NotificationResponseDTO> getUnreadNotifications(Long userId) {
        log.debug("Getting unread notifications for user: {}", userId);
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Contar notificaciones no leídas
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    // Marcar una notificación como leída
    public NotificationResponseDTO markAsRead(Long notificationId, Long userId) {
        log.debug("Marking notification {} as read for user {}", notificationId, userId);
        
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            
            // Verificar que la notificación pertenece al usuario
            if (!notification.getUserId().equals(userId)) {
                throw new RuntimeException("No tienes permisos para modificar esta notificación");
            }
            
            notification.setIsRead(true);
            notification.setUpdatedAt(LocalDateTime.now());
            
            Notification updatedNotification = notificationRepository.save(notification);
            log.info("Notification {} marked as read", notificationId);
            
            return convertToResponseDTO(updatedNotification);
        } else {
            throw new RuntimeException("Notificación no encontrada");
        }
    }
    
    // Marcar todas las notificaciones como leídas
    public int markAllAsRead(Long userId) {
        log.debug("Marking all notifications as read for user: {}", userId);
        int updatedCount = notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
        log.info("Marked {} notifications as read for user {}", updatedCount, userId);
        return updatedCount;
    }
    
    // Eliminar una notificación
    public void deleteNotification(Long notificationId, Long userId) {
        log.debug("Deleting notification {} for user {}", notificationId, userId);
        
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            
            // Verificar que la notificación pertenece al usuario
            if (!notification.getUserId().equals(userId)) {
                throw new RuntimeException("No tienes permisos para eliminar esta notificación");
            }
            
            notificationRepository.deleteById(notificationId);
            log.info("Notification {} deleted successfully", notificationId);
        } else {
            throw new RuntimeException("Notificación no encontrada");
        }
    }
    
    // Obtener notificaciones de un expediente específico
    public List<NotificationResponseDTO> getExpedienteNotifications(Long expedienteId) {
        log.debug("Getting notifications for expediente: {}", expedienteId);
        List<Notification> notifications = notificationRepository.findByExpedienteIdOrderByCreatedAtDesc(expedienteId);
        return notifications.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Crear notificación automática del sistema
    public void createSystemNotification(String title, String message, Long userId, Long expedienteId, String expedienteNumero) {
        log.debug("Creating system notification for user {}: {}", userId, title);
        
        NotificationRequestDTO requestDTO = new NotificationRequestDTO();
        requestDTO.setTitle(title);
        requestDTO.setMessage(message);
        requestDTO.setType(NotificationType.SYSTEM);
        requestDTO.setExpedienteId(expedienteId);
        requestDTO.setExpedienteNumero(expedienteNumero);
        
        createNotification(requestDTO, userId);
    }
    
    // Tarea programada para procesar recordatorios pendientes
    @Scheduled(fixedRate = 60000) // Ejecutar cada minuto
    public void processReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> pendingReminders = notificationRepository.findPendingReminders(now);
        
        for (Notification reminder : pendingReminders) {
            try {
                // Enviar notificación push
                NotificationResponseDTO responseDTO = convertToResponseDTO(reminder);
                
                Optional<User> userOpt = userRepository.findById(reminder.getUserId());
                if (userOpt.isPresent()) {
                    webSocketService.sendToUser(
                        userOpt.get().getUsername(), 
                        "/queue/reminders", 
                        responseDTO
                    );
                    
                    log.info("Reminder notification sent to user {}: {}", 
                            userOpt.get().getUsername(), reminder.getTitle());
                }
                
            } catch (Exception e) {
                log.error("Error processing reminder {}: {}", reminder.getId(), e.getMessage());
            }
        }
        
        if (!pendingReminders.isEmpty()) {
            log.debug("Processed {} reminder notifications", pendingReminders.size());
        }
    }
    
    // Limpieza automática de notificaciones antiguas (ejecutar diariamente)
    @Scheduled(cron = "0 0 2 * * ?") // A las 2:00 AM todos los días
    public void cleanupOldNotifications() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30); // Eliminar notificaciones leídas de más de 30 días
        int deletedCount = notificationRepository.deleteOldReadNotifications(cutoffDate);
        log.info("Cleanup: deleted {} old read notifications", deletedCount);
    }
    
    // Buscar notificaciones
    public List<NotificationResponseDTO> searchNotifications(Long userId, String searchText) {
        log.debug("Searching notifications for user {} with text: {}", userId, searchText);
        List<Notification> notifications = notificationRepository.searchNotifications(userId, searchText);
        return notifications.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Método privado para convertir entidad a DTO
    private NotificationResponseDTO convertToResponseDTO(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setPriority(notification.getPriority());
        dto.setExpedienteId(notification.getExpedienteId());
        dto.setExpedienteNumero(notification.getExpedienteNumero());
        dto.setIsRead(notification.getIsRead());
        dto.setReminderDate(notification.getReminderDate());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setUpdatedAt(notification.getUpdatedAt());
        return dto;
    }
}