-- including ossp to enable UUID generation in Postgres
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- These UUIDs of users should match what's generated from Keycloak
INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('eb7801c2-7571-4221-98ce-febab4b8ad18', 'app_user', 'Standard', 'User');
INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('59c53e95-76db-4ab2-91f9-1df7d16f5097', 'app_admin', 'Admin', 'User');
INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('25078869-fa0c-45d1-83ee-bee2f7c57201', 'app_super_user', 'Super', 'User');

-- These document_ids should exist in current ElasticSearch for demo purposes
insert into documents(id, document_id)
values ('c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 1379629770976108544);
insert into documents(id, document_id)
values ('f2b2d644-3a08-4acb-ae07-20569f6f2a01', 1377112135247884297);
insert into documents(id, document_id)
values ('90573d2b-9a5d-409e-bbb6-b94189709a19', 5493830978435715276);

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(),'eb7801c2-7571-4221-98ce-febab4b8ad18', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(),'59c53e95-76db-4ab2-91f9-1df7d16f5097', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(),'59c53e95-76db-4ab2-91f9-1df7d16f5097', 'f2b2d644-3a08-4acb-ae07-20569f6f2a01', 'READ');

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(), '59c53e95-76db-4ab2-91f9-1df7d16f5097', '90573d2b-9a5d-409e-bbb6-b94189709a19', 'READ');

-- Despite this permission definition, Super users will be able to access all documents.
insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(), '25078869-fa0c-45d1-83ee-bee2f7c57201', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');


