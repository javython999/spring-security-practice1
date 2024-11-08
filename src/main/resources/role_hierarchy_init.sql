insert into role_hierarchy (id, role_name, parent_id) values
(1, 'ROLE_ADMIN', null),
(2, 'ROLE_MANAGER', '1'),
(3, 'ROLE_DBA', '1'),
(4, 'ROLE_USER', '2'),
(5, 'ROLE_USER', '3');