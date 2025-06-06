package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages",
        indexes = {
                @Index(name = "idx_message_sender", columnList = "sender_id"),
                @Index(name = "idx_message_receiver", columnList = "receiver_id"),
                @Index(name = "idx_message_created_at", columnList = "createdAt"),
                @Index(name = "idx_message_conversation", columnList = "sender_id,receiver_id"),
                @Index(name = "idx_message_read_status", columnList = "isRead")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    // Para futuras funcionalidades como archivos adjuntos
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;

    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        SYSTEM // Para mensajes del sistema como "Usuario se unió", etc.
    }

    // Método para marcar como leído
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    // Método para obtener la conversación ID (útil para agrupar mensajes)
    public String getConversationId() {
        Long senderId = sender.getId();
        Long receiverId = receiver.getId();

        // Asegurar que el ID de conversación sea consistente independientemente del orden
        if (senderId < receiverId) {
            return senderId + "_" + receiverId;
        } else {
            return receiverId + "_" + senderId;
        }
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", senderId=" + (sender != null ? sender.getId() : null) +
                ", receiverId=" + (receiver != null ? receiver.getId() : null) +
                ", content='" + (content != null ? content.substring(0, Math.min(content.length(), 50)) : null) + "..." +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}