package com.ruben.Expedientes.dto;

import com.ruben.Expedientes.model.NotificationType;
import com.ruben.Expedientes.model.NotificationPriority;

import java.time.LocalDateTime;

public class NotificationResponseDTO {
    
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationPriority priority;
    private Long expedienteId;
    private String expedienteNumero;
    private Boolean isRead;
    private LocalDateTime reminderDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructores
    public NotificationResponseDTO() {}
    
    public NotificationResponseDTO(Long id, String title, String message, NotificationType type, 
                                   NotificationPriority priority, Boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public NotificationPriority getPriority() {
        return priority;
    }
    
    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }
    
    public Long getExpedienteId() {
        return expedienteId;
    }
    
    public void setExpedienteId(Long expedienteId) {
        this.expedienteId = expedienteId;
    }
    
    public String getExpedienteNumero() {
        return expedienteNumero;
    }
    
    public void setExpedienteNumero(String expedienteNumero) {
        this.expedienteNumero = expedienteNumero;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public LocalDateTime getReminderDate() {
        return reminderDate;
    }
    
    public void setReminderDate(LocalDateTime reminderDate) {
        this.reminderDate = reminderDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}