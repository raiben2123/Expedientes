-- Script de verificación para solucionar el problema de LazyInitializationException en Tickets
-- Ejecutar estas consultas para verificar la estructura y datos

-- 1. Verificar la estructura de la tabla tickets
SELECT 
    column_name, 
    data_type, 
    is_nullable, 
    column_default
FROM information_schema.columns 
WHERE table_name = 'tickets' 
ORDER BY ordinal_position;

-- 2. Verificar la estructura de la tabla app_users
SELECT 
    column_name, 
    data_type, 
    is_nullable, 
    column_default
FROM information_schema.columns 
WHERE table_name = 'app_users' 
ORDER BY ordinal_position;

-- 3. Verificar las relaciones/foreign keys
SELECT 
    tc.constraint_name,
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM 
    information_schema.table_constraints AS tc 
    JOIN information_schema.key_column_usage AS kcu
        ON tc.constraint_name = kcu.constraint_name
        AND tc.table_schema = kcu.table_schema
    JOIN information_schema.constraint_column_usage AS ccu
        ON ccu.constraint_name = tc.constraint_name
        AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY' 
    AND tc.table_name IN ('tickets', 'app_users');

-- 4. Consulta de prueba para verificar datos existentes
SELECT 
    t.id as ticket_id,
    t.title,
    t.description,
    t.status,
    t.created_at,
    t.user_id,
    u.id as user_id_from_join,
    u.username,
    u.role
FROM tickets t
LEFT JOIN app_users u ON t.user_id = u.id
ORDER BY t.created_at DESC
LIMIT 10;

-- 5. Contar tickets por usuario
SELECT 
    u.username,
    u.role,
    COUNT(t.id) as ticket_count
FROM app_users u
LEFT JOIN tickets t ON u.id = t.user_id
GROUP BY u.id, u.username, u.role
ORDER BY ticket_count DESC;

-- 6. Insertar datos de prueba si no existen usuarios
INSERT INTO app_users (username, password, role) 
SELECT 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iDXdpEKPKczfBh/8VJJ7Lj8/PJJm', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM app_users WHERE username = 'admin');

INSERT INTO app_users (username, password, role) 
SELECT 'testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iDXdpEKPKczfBh/8VJJ7Lj8/PJJm', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM app_users WHERE username = 'testuser');

-- 7. Insertar ticket de prueba
INSERT INTO tickets (title, description, status, user_id, created_at)
SELECT 
    'Ticket de Prueba - Solucionado LazyInitializationException',
    'Este ticket se creó para probar la solución del problema de LazyInitializationException en el sistema de tickets.',
    'OPEN',
    u.id,
    NOW()
FROM app_users u 
WHERE u.username = 'admin'
AND NOT EXISTS (
    SELECT 1 FROM tickets 
    WHERE title = 'Ticket de Prueba - Solucionado LazyInitializationException'
);

-- 8. Verificar que la consulta JOIN FETCH funciona correctamente
SELECT 
    t.id,
    t.title,
    t.status,
    t.created_at,
    u.username as created_by_username,
    u.role as created_by_role
FROM tickets t
INNER JOIN app_users u ON t.user_id = u.id
ORDER BY t.created_at DESC;