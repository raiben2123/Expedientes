package com.ruben.Expedientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDTO {
    private Long userId;
    private String username;
    private Boolean isOnline;
    private LocalDateTime lastSeen;
    private String status; // ONLINE, OFFLINE, BUSY, AWAY

    // Método helper para isOnline
    public Boolean getIsOnline() {
        return isOnline != null ? isOnline : false;
    }
}