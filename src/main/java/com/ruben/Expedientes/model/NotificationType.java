package com.ruben.Expedientes.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {
    REMINDER("Recordatorio"),
    DEADLINE("Plazo/Deadline"),
    UPDATE("Actualización"),
    SYSTEM("Sistema"),
    WARNING("Advertencia"),
    INFO("Información");
    
    private final String displayName;
    
    NotificationType(String displayName) {
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
    public static NotificationType fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        // Permitir tanto mayúsculas como minúsculas
        String upperValue = value.toUpperCase();
        
        for (NotificationType type : NotificationType.values()) {
            if (type.name().equals(upperValue)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Valor inválido para NotificationType: " + value + 
            ". Valores permitidos: " + java.util.Arrays.toString(NotificationType.values()));
    }
}