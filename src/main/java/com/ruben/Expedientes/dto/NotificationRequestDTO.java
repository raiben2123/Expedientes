package com.ruben.Expedientes.dto;

import com.ruben.Expedientes.model.NotificationType;
import com.ruben.Expedientes.model.NotificationPriority;

import java.time.LocalDateTime;

public class NotificationRequestDTO {

    private String title;

    private String message;

    private NotificationType type;
    
    private NotificationPriority priority = NotificationPriority.MEDIUM;
    
    private Long expedienteId;
    
    private String expedienteNumero;
    
    private LocalDateTime reminderDate;
    
    // Constructores
    public NotificationRequestDTO() {}
    
    public NotificationRequestDTO(String title, String message, NotificationType type) {
        this.title = title;
        this.message = message;
        this.type = type;
    }
    
    // Getters y Setters
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
    
    public LocalDateTime getReminderDate() {
        return reminderDate;
    }
    
    public void setReminderDate(LocalDateTime reminderDate) {
        this.reminderDate = reminderDate;
    }
}