package com.ruben.Expedientes.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationPriority {
    LOW("Baja"),
    MEDIUM("Media"),
    HIGH("Alta"),
    URGENT("Urgente");
    
    private final String displayName;
    
    NotificationPriority(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @JsonValue
    public String getValue() {
        return this.name();
    }
    
    @JsonCreator
    public static NotificationPriority fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        // Permitir tanto mayúsculas como minúsculas
        String upperValue = value.toUpperCase();
        
        for (NotificationPriority priority : NotificationPriority.values()) {
            if (priority.name().equals(upperValue)) {
                return priority;
            }
        }
        
        throw new IllegalArgumentException("Valor inválido para NotificationPriority: " + value + 
            ". Valores permitidos: " + java.util.Arrays.toString(NotificationPriority.values()));
    }
}