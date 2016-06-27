--------------------------------------------------------------------------------
--Proceso de Instalación - PostgreSQL
--Crear Base de Datos
create database msl_ga;

--Se debe cambiar conexión a la nueva base de datos creada

--Crear Schema
create schema msl_ga_schema;

--Crear Tabla Rol
create table "msl_ga_schema".Rol
(
	id_rol NUMERIC(18) not null primary key,
	nombre_rol VARCHAR(30)
);
--Configuración de roles
insert into msl_ga_schema.Rol values (1, 'administrador');
insert into msl_ga_schema.Rol values (2, 'pm');

--Crear Tabla Rol_Usuario
create table "msl_ga_schema".Rol_Usuario
(
	id_rol NUMERIC(18),
	id_usuario VARCHAR(40)
);
--Asignar roles a usuarios
insert into  msl_ga_schema.Rol_Usuario values (1, 'plara');
insert into  msl_ga_schema.Rol_Usuario values (2, 'lpaez');
insert into  msl_ga_schema.Rol_Usuario values (2, 'efguchuvo');
insert into  msl_ga_schema.Rol_Usuario values (2, 'rnino');

--Crear Tabla Tipo_Actividad
create table "msl_ga_schema".Tipo_Actividad
(
	id_actividad NUMERIC(18) not null primary key,
	nombre VARCHAR(50)
);
--Configurar tipos de actividades
insert into msl_ga_schema.Tipo_Actividad values (1, 'Proyecto');
insert into msl_ga_schema.Tipo_Actividad values (2, 'Preventa');
insert into msl_ga_schema.Tipo_Actividad values (3, 'Prueba de Concepto');
insert into msl_ga_schema.Tipo_Actividad values (4, 'Contrato de Soporte');
insert into msl_ga_schema.Tipo_Actividad values (5, 'Servicio de Cortesía');
insert into msl_ga_schema.Tipo_Actividad values (6, 'Soporte con CA');
insert into msl_ga_schema.Tipo_Actividad values (7, 'Otro');
insert into msl_ga_schema.Tipo_Actividad values (8, 'Vacaciones');
insert into msl_ga_schema.Tipo_Actividad values (9, 'Incapacidad');

--Crear Tabla Programación
create table "msl_ga_schema".Programacion
(
	id_programacion NUMERIC(18) not null primary key,
	id_consultor VARCHAR(40),
	semana NUMERIC(18) not null
);
--Crear Tabla Actividad
create table "msl_ga_schema".Actividad
(
	id_actividad NUMERIC(18) not null primary key,
	id_programacion NUMERIC(18) not null,
	fecha_programacion TIMESTAMP not null,
	id_organizacion VARCHAR(50) not null,
	jornada NUMERIC(18) not null,
	id_tipo_actividad NUMERIC(18) not null,
	estado NUMERIC(18) not null,
	descripcion VARCHAR(200),
	id_proyecto NUMERIC(18)
);
--------------------------------------------------------------------------------
