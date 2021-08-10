-- INSERT INTO "best_users" (id, changed, created, email, family, name, status, username)
-- VALUES (1, SYSDATE, SYSDATE, 'admin@email.com', 'admin_family', 'name_family', 'ACTIVE', 'admin_username');
-- INSERT INTO "best_authorities" (user_id, authority)
-- values (1, 'ADMIN');




INSERT INTO "best_product" (changed, created, description, name, price, status, type)
VALUES (SYSDATE, SYSDATE, 'DESCRIPTION', 'Black Coffee', 4, 'AVAILABLE', 'MAIN');
INSERT INTO "best_product" (changed, created, description, name, price, status, type)
VALUES (SYSDATE, SYSDATE, 'DESCRIPTION', 'Latte', 5, 'AVAILABLE', 'MAIN');
INSERT INTO "best_product" (changed, created, description, name, price, status, type)
VALUES (SYSDATE, SYSDATE, 'DESCRIPTION', 'Mocha', 6, 'AVAILABLE', 'MAIN');
INSERT INTO "best_product" (changed, created, description, name, price, status, type)
VALUES (SYSDATE, SYSDATE, 'DESCRIPTION', 'Tea', 3, 'AVAILABLE', 'MAIN');

INSERT INTO "best_product" (changed, created, description, name, price, status, type)
VALUES (SYSDATE, SYSDATE, 'DESCRIPTION', 'Milk', 2, 'AVAILABLE', 'SIDE');
INSERT INTO "best_product" (changed, created, description, name, price, status, type)
VALUES (SYSDATE, SYSDATE, 'DESCRIPTION', 'Hazelnut syrup', 3, 'AVAILABLE', 'SIDE');
INSERT INTO "best_product" (changed, created, description, name, price, status, type)
VALUES (SYSDATE, SYSDATE, 'DESCRIPTION', 'Chocolate sauce', 5, 'AVAILABLE', 'SIDE');
INSERT INTO "best_product" (changed, created, description, name, price, status, type)
VALUES (SYSDATE, SYSDATE, 'DESCRIPTION', 'Lemon', 2, 'AVAILABLE', 'SIDE');