-- Insertion des enseignants
INSERT INTO TEACHERS (first_name, last_name)
VALUES ('Margot', 'DELAHAYE'),
       ('Hélène', 'THIERCELIN');

-- Insertion d'un utilisateur Admin avec le mot de passe "test!1234"
INSERT INTO USERS (first_name, last_name, admin, email, password)
VALUES ('Yoga', 'Studio', true, 'yoga@studio.com', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq');

-- Insertion de nouveaux utilisateurs (participants) avec leur mot de passe
INSERT INTO USERS (last_name, first_name, admin, email, password, created_at, updated_at)
VALUES ('Studio', 'User1', false, 'user1@studio.com', '$2b$12$7nRUhkgWkmGhR/FrRRrn4O7chFb8aoGsBrNzp7NTln74o9KbVx.yy', NOW(), NOW()),
       ('Studio', 'User2', false, 'user2@studio.com', '$2b$12$7nRUhkgWkmGhR/FrRRrn4O7chFb8aoGsBrNzp7NTln74o9KbVx.yy', NOW(), NOW());

-- Insertion d'une SESSION avec Margot DELAHAYE en utilisant son nom complet
INSERT INTO SESSIONS (name, date, description, teacher_id, created_at, updated_at)
VALUES ('Yoga', '2024-12-01 01:00:00', 'Yoga session 1',
        (SELECT id FROM TEACHERS WHERE first_name = 'Margot' AND last_name = 'DELAHAYE' LIMIT 1), NOW(), NOW());

-- Insertion d'une autre SESSION avec Hélène THIERCELIN en utilisant son nom complet
INSERT INTO SESSIONS (name, date, description, teacher_id, created_at, updated_at)
VALUES ('Yoga', '2024-12-01 01:00:00', 'Yoga session 2',
        (SELECT id FROM TEACHERS WHERE first_name = 'Hélène' AND last_name = 'THIERCELIN' LIMIT 1), NOW(), NOW());

-- Ajout de l'utilisateur Jane DOE à la dernière SESSION par email
INSERT INTO PARTICIPATE (session_id, user_id)
VALUES ((SELECT id FROM SESSIONS ORDER BY id DESC LIMIT 1), (SELECT id FROM USERS WHERE email = 'user2@studio.com' LIMIT 1));
