package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.ChatMessage;
import com.ruben.Expedientes.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Obtener conversación entre dos usuarios con paginación
     * Ordenado por fecha descendente (más recientes primero)
     */
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE ((cm.sender = :user1 AND cm.receiver = :user2) " +
            "OR (cm.sender = :user2 AND cm.receiver = :user1)) " +
            "AND cm.isDeleted = false " +
            "ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findConversationBetweenUsersPageable(
            @Param("user1") User user1,
            @Param("user2") User user2,
            Pageable pageable);

    /**
     * Obtener las últimas conversaciones de un usuario
     * Retorna el último mensaje de cada conversación
     */
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE (cm.sender = :user OR cm.receiver = :user) " +
            "AND cm.isDeleted = false " +
            "AND cm.id IN (" +
            "  SELECT MAX(cm2.id) FROM ChatMessage cm2 " +
            "  WHERE (cm2.sender = :user OR cm2.receiver = :user) " +
            "  AND cm2.isDeleted = false " +
            "  GROUP BY " +
            "    CASE " +
            "      WHEN cm2.sender = :user THEN cm2.receiver.id " +
            "      ELSE cm2.sender.id " +
            "    END" +
            ") " +
            "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findUserConversations(@Param("user") User user);

    /**
     * Versión alternativa usando userId para mejor rendimiento
     */
    @Query(value = "SELECT cm.* FROM chat_messages cm " +
            "WHERE (cm.sender_id = :userId OR cm.receiver_id = :userId) " +
            "AND cm.is_deleted = false " +
            "AND cm.id IN (" +
            "  SELECT MAX(cm2.id) FROM chat_messages cm2 " +
            "  WHERE (cm2.sender_id = :userId OR cm2.receiver_id = :userId) " +
            "  AND cm2.is_deleted = false " +
            "  GROUP BY " +
            "    CASE " +
            "      WHEN cm2.sender_id = :userId THEN cm2.receiver_id " +
            "      ELSE cm2.sender_id " +
            "    END" +
            ") " +
            "ORDER BY cm.created_at DESC",
            nativeQuery = true)
    List<ChatMessage> findUserConversations(@Param("userId") Long userId);

    /**
     * Contar mensajes no leídos para un usuario
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
            "WHERE cm.receiver = :user " +
            "AND cm.isRead = false " +
            "AND cm.isDeleted = false")
    long countUnreadMessages(@Param("user") User user);

    /**
     * Contar mensajes no leídos entre dos usuarios específicos
     * (mensajes que el receptor no ha leído del sender)
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
            "WHERE cm.receiver = :receiver " +
            "AND cm.sender = :sender " +
            "AND cm.isRead = false " +
            "AND cm.isDeleted = false")
    long countUnreadMessagesBetweenUsers(
            @Param("receiver") User receiver,
            @Param("sender") User sender);

    /**
     * Marcar mensajes como leídos entre dos usuarios
     * Marca todos los mensajes del sender al receiver como leídos
     */
    @Modifying
    @Query("UPDATE ChatMessage cm " +
            "SET cm.isRead = true, cm.readAt = :readAt " +
            "WHERE cm.receiver = :receiver " +
            "AND cm.sender = :sender " +
            "AND cm.isRead = false " +
            "AND cm.isDeleted = false")
    int markMessagesAsReadBetweenUsers(
            @Param("receiver") User receiver,
            @Param("sender") User sender,
            @Param("readAt") LocalDateTime readAt);

    /**
     * Buscar mensajes por contenido para un usuario
     * Busca en todas las conversaciones del usuario
     */
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE (cm.sender = :user OR cm.receiver = :user) " +
            "AND LOWER(cm.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "AND cm.isDeleted = false " +
            "ORDER BY cm.createdAt DESC")
    List<ChatMessage> searchMessagesForUser(
            @Param("user") User user,
            @Param("searchTerm") String searchTerm);

    /**
     * Buscar mensajes por contenido en una conversación específica
     */
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE ((cm.sender = :user1 AND cm.receiver = :user2) " +
            "OR (cm.sender = :user2 AND cm.receiver = :user1)) " +
            "AND LOWER(cm.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "AND cm.isDeleted = false " +
            "ORDER BY cm.createdAt DESC")
    List<ChatMessage> searchMessagesInConversation(
            @Param("user1") User user1,
            @Param("user2") User user2,
            @Param("searchTerm") String searchTerm);

    /**
     * Eliminar mensaje (soft delete)
     * Solo permite eliminar mensajes propios
     */
    @Modifying
    @Query("UPDATE ChatMessage cm " +
            "SET cm.isDeleted = true, cm.deletedAt = :deletedAt " +
            "WHERE cm.id = :messageId " +
            "AND cm.sender = :user")
    int softDeleteMessage(
            @Param("messageId") Long messageId,
            @Param("user") User user,
            @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * Obtener estadísticas de mensajes para un usuario
     * Retorna: [totalSent, totalReceived, totalRead]
     */
    @Query("SELECT " +
            "COALESCE(SUM(CASE WHEN cm.sender = :user THEN 1 ELSE 0 END), 0) as totalSent, " +
            "COALESCE(SUM(CASE WHEN cm.receiver = :user THEN 1 ELSE 0 END), 0) as totalReceived, " +
            "COALESCE(SUM(CASE WHEN cm.receiver = :user AND cm.isRead = true THEN 1 ELSE 0 END), 0) as totalRead " +
            "FROM ChatMessage cm " +
            "WHERE (cm.sender = :user OR cm.receiver = :user) " +
            "AND cm.isDeleted = false")
    Object[] getMessageStatisticsForUser(@Param("user") User user);
    
    /**
     * Métodos alternativos para obtener estadísticas por separado (más robustos)
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "WHERE cm.sender = :user AND cm.isDeleted = false")
    Long countMessagesSentByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "WHERE cm.receiver = :user AND cm.isDeleted = false")
    Long countMessagesReceivedByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "WHERE cm.receiver = :user AND cm.isRead = true AND cm.isDeleted = false")
    Long countMessagesReadByUser(@Param("user") User user);

    /**
     * Obtener usuarios con los que el usuario actual ha tenido conversaciones
     */
    @Query("SELECT DISTINCT " +
            "CASE " +
            "  WHEN cm.sender = :user THEN cm.receiver " +
            "  ELSE cm.sender " +
            "END " +
            "FROM ChatMessage cm " +
            "WHERE (cm.sender = :user OR cm.receiver = :user) " +
            "AND cm.isDeleted = false")
    List<User> findUsersInConversationWith(@Param("user") User user);

    /**
     * Obtener mensajes recientes de un usuario (últimos X días)
     */
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE (cm.sender = :user OR cm.receiver = :user) " +
            "AND cm.createdAt >= :since " +
            "AND cm.isDeleted = false " +
            "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findRecentMessagesForUser(
            @Param("user") User user,
            @Param("since") LocalDateTime since);

    /**
     * Obtener el último mensaje entre dos usuarios
     */
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE ((cm.sender = :user1 AND cm.receiver = :user2) " +
            "OR (cm.sender = :user2 AND cm.receiver = :user1)) " +
            "AND cm.isDeleted = false " +
            "ORDER BY cm.createdAt DESC " +
            "LIMIT 1")
    ChatMessage findLastMessageBetweenUsers(
            @Param("user1") User user1,
            @Param("user2") User user2);

    /**
     * Verificar si existe conversación entre dos usuarios
     */
    @Query("SELECT COUNT(cm) > 0 FROM ChatMessage cm " +
            "WHERE ((cm.sender = :user1 AND cm.receiver = :user2) " +
            "OR (cm.sender = :user2 AND cm.receiver = :user1)) " +
            "AND cm.isDeleted = false")
    boolean existsConversationBetween(
            @Param("user1") User user1,
            @Param("user2") User user2);

    /**
     * Obtener mensajes por tipo
     */
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE (cm.sender = :user OR cm.receiver = :user) " +
            "AND cm.messageType = :messageType " +
            "AND cm.isDeleted = false " +
            "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findMessagesByType(
            @Param("user") User user,
            @Param("messageType") ChatMessage.MessageType messageType);

    /**
     * Obtener mensajes con archivos adjuntos
     */
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE (cm.sender = :user OR cm.receiver = :user) " +
            "AND cm.attachmentUrl IS NOT NULL " +
            "AND cm.isDeleted = false " +
            "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findMessagesWithAttachments(@Param("user") User user);

    /**
     * Obtener conversaciones con mensajes no leídos
     */
    @Query("SELECT DISTINCT " +
            "CASE " +
            "  WHEN cm.receiver = :user THEN cm.sender " +
            "  ELSE cm.receiver " +
            "END " +
            "FROM ChatMessage cm " +
            "WHERE cm.receiver = :user " +
            "AND cm.isRead = false " +
            "AND cm.isDeleted = false")
    List<User> findUsersWithUnreadMessages(@Param("user") User user);

    /**
     * Contar total de conversaciones activas para un usuario
     */
    @Query("SELECT COUNT(DISTINCT " +
            "CASE " +
            "  WHEN cm.sender = :user THEN cm.receiver.id " +
            "  ELSE cm.sender.id " +
            "END) " +
            "FROM ChatMessage cm " +
            "WHERE (cm.sender = :user OR cm.receiver = :user) " +
            "AND cm.isDeleted = false")
    long countActiveConversations(@Param("user") User user);

    /**
     * Eliminar todos los mensajes de una conversación (soft delete)
     */
    @Modifying
    @Query("UPDATE ChatMessage cm " +
            "SET cm.isDeleted = true, cm.deletedAt = :deletedAt " +
            "WHERE ((cm.sender = :user1 AND cm.receiver = :user2) " +
            "OR (cm.sender = :user2 AND cm.receiver = :user1)) " +
            "AND cm.isDeleted = false")
    int deleteConversation(
            @Param("user1") User user1,
            @Param("user2") User user2,
            @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * Marcar todos los mensajes de un usuario como leídos
     */
    @Modifying
    @Query("UPDATE ChatMessage cm " +
            "SET cm.isRead = true, cm.readAt = :readAt " +
            "WHERE cm.receiver = :user " +
            "AND cm.isRead = false " +
            "AND cm.isDeleted = false")
    int markAllMessagesAsRead(
            @Param("user") User user,
            @Param("readAt") LocalDateTime readAt);
}