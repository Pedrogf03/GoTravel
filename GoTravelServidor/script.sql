-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema gotravel
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema gotravel
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `gotravel` DEFAULT CHARACTER SET utf8mb3 ;
USE `gotravel` ;

-- -----------------------------------------------------
-- Table `gotravel`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`usuario` (
  `id_usuario` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `apellidos` VARCHAR(200) NULL DEFAULT NULL,
  `email` VARCHAR(150) NOT NULL,
  `contrasena` VARCHAR(300) NOT NULL,
  `tfno` CHAR(9) NULL DEFAULT NULL,
  `foto` LONGBLOB NULL DEFAULT NULL,
  `oculto` ENUM('0', '1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `Email_UNIQUE` (`email` ASC) VISIBLE,
  UNIQUE INDEX `Tfno_UNIQUE` (`tfno` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 27
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`viaje`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`viaje` (
  `id_viaje` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `descripcion` VARCHAR(500) NULL DEFAULT NULL,
  `fecha_inicio` DATE NOT NULL,
  `fecha_fin` DATE NOT NULL,
  `coste_total` DOUBLE NOT NULL,
  `id_usuario` INT NOT NULL,
  PRIMARY KEY (`id_viaje`),
  INDEX `fk_Viaje_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  CONSTRAINT `fk_Viaje_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`))
ENGINE = InnoDB
AUTO_INCREMENT = 26
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`etapa`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`etapa` (
  `id_etapa` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `fecha_inicio` DATE NOT NULL,
  `fecha_final` DATE NOT NULL,
  `coste_total` DOUBLE NOT NULL,
  `tipo` ENUM('transporte', 'estancia') NOT NULL,
  `id_viaje` INT NOT NULL,
  `pais` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id_etapa`),
  INDEX `fk_Etapa_Viaje1_idx` (`id_viaje` ASC) VISIBLE,
  CONSTRAINT `fk_Etapa_Viaje1`
    FOREIGN KEY (`id_viaje`)
    REFERENCES `gotravel`.`viaje` (`id_viaje`))
ENGINE = InnoDB
AUTO_INCREMENT = 32
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`pago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`pago` (
  `id_pago` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL,
  `coste` DOUBLE NOT NULL,
  `fecha` DATE NOT NULL,
  PRIMARY KEY (`id_pago`),
  INDEX `fk_Pago_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  CONSTRAINT `fk_Pago_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`))
ENGINE = InnoDB
AUTO_INCREMENT = 16
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`direccion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`direccion` (
  `id_direccion` INT NOT NULL AUTO_INCREMENT,
  `linea1` VARCHAR(200) NOT NULL,
  `linea2` VARCHAR(200) NULL DEFAULT NULL,
  `ciudad` VARCHAR(100) NOT NULL,
  `estado` VARCHAR(100) NOT NULL,
  `cp` CHAR(5) NOT NULL,
  `pais` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id_direccion`))
ENGINE = InnoDB
AUTO_INCREMENT = 31
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`tiposervicio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`tiposervicio` (
  `nombre` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`nombre`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`servicio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`servicio` (
  `id_servicio` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `descripcion` VARCHAR(500) NULL DEFAULT NULL,
  `precio` DOUBLE NOT NULL,
  `tipo_servicio` VARCHAR(100) NOT NULL,
  `id_usuario` INT NOT NULL,
  `oculto` ENUM('0', '1') NOT NULL DEFAULT '0',
  `fecha_inicio` DATE NOT NULL,
  `fecha_final` DATE NULL DEFAULT NULL,
  `hora` TIME NULL DEFAULT NULL,
  `id_direccion` INT NOT NULL,
  `publicado` ENUM('0', '1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_servicio`),
  INDEX `fk_Servicio_TipoServicio1_idx` (`tipo_servicio` ASC) VISIBLE,
  INDEX `fk_Servicio_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_Servicio_Direccion1_idx` (`id_direccion` ASC) VISIBLE,
  CONSTRAINT `fk_Servicio_Direccion1`
    FOREIGN KEY (`id_direccion`)
    REFERENCES `gotravel`.`direccion` (`id_direccion`),
  CONSTRAINT `fk_Servicio_TipoServicio1`
    FOREIGN KEY (`tipo_servicio`)
    REFERENCES `gotravel`.`tiposervicio` (`nombre`),
  CONSTRAINT `fk_Servicio_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`contratacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`contratacion` (
  `id_contratacion` VARCHAR(100) NOT NULL,
  `fecha` DATE NOT NULL,
  `id_servicio` INT NOT NULL,
  `id_usuario` INT NOT NULL,
  `id_etapa` INT NOT NULL,
  `id_pago` INT NOT NULL,
  PRIMARY KEY (`id_contratacion`),
  INDEX `fk_Contratacion_Servicio1_idx` (`id_servicio` ASC) VISIBLE,
  INDEX `fk_Contratacion_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_Contratacion_etapa1_idx` (`id_etapa` ASC) VISIBLE,
  INDEX `fk_Contratacion_Pago1_idx` (`id_pago` ASC) VISIBLE,
  CONSTRAINT `fk_Contratacion_Etapa1`
    FOREIGN KEY (`id_etapa`)
    REFERENCES `gotravel`.`etapa` (`id_etapa`),
  CONSTRAINT `fk_Contratacion_Pago1`
    FOREIGN KEY (`id_pago`)
    REFERENCES `gotravel`.`pago` (`id_pago`),
  CONSTRAINT `fk_Contratacion_Servicio1`
    FOREIGN KEY (`id_servicio`)
    REFERENCES `gotravel`.`servicio` (`id_servicio`),
  CONSTRAINT `fk_Contratacion_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`imagen`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`imagen` (
  `id_imagen` INT NOT NULL AUTO_INCREMENT,
  `id_servicio` INT NOT NULL,
  `imagen` LONGBLOB NOT NULL,
  PRIMARY KEY (`id_imagen`),
  INDEX `fk_Imagen_Servicio1` (`id_servicio` ASC) VISIBLE,
  CONSTRAINT `fk_Imagen_Servicio1`
    FOREIGN KEY (`id_servicio`)
    REFERENCES `gotravel`.`servicio` (`id_servicio`))
ENGINE = InnoDB
AUTO_INCREMENT = 61
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`mensaje`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`mensaje` (
  `id_mensaje` INT NOT NULL AUTO_INCREMENT,
  `id_emisor` INT NOT NULL,
  `id_receptor` INT NOT NULL,
  `texto` VARCHAR(500) NOT NULL,
  `fecha` DATE NOT NULL,
  `hora` TIME NOT NULL,
  PRIMARY KEY (`id_mensaje`),
  INDEX `fk_mensaje_usuario1_idx` (`id_emisor` ASC) VISIBLE,
  INDEX `fk_mensaje_usuario2_idx` (`id_receptor` ASC) VISIBLE,
  CONSTRAINT `fk_mensaje_usuario1`
    FOREIGN KEY (`id_emisor`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`),
  CONSTRAINT `fk_mensaje_usuario2`
    FOREIGN KEY (`id_receptor`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`))
ENGINE = InnoDB
AUTO_INCREMENT = 61
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`suscripcion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`suscripcion` (
  `id_suscripcion` VARCHAR(50) NOT NULL,
  `fecha_inicio` DATE NOT NULL,
  `fecha_final` DATE NOT NULL,
  `estado` VARCHAR(50) NOT NULL,
  `id_usuario` INT NOT NULL,
  `renovar` ENUM('0', '1') NOT NULL,
  PRIMARY KEY (`id_suscripcion`),
  INDEX `fk_Suscripcion_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  CONSTRAINT `fk_Suscripcion_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`pagosporsuscripcion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`pagosporsuscripcion` (
  `id_suscripcion` VARCHAR(50) NOT NULL,
  `id_pago` INT NOT NULL,
  PRIMARY KEY (`id_pago`),
  INDEX `fk_suscripcion_has_pago_pago1_idx` (`id_pago` ASC) VISIBLE,
  INDEX `fk_suscripcion_has_pago_suscripcion1_idx` (`id_suscripcion` ASC) VISIBLE,
  CONSTRAINT `fk_suscripcion_has_pago_pago1`
    FOREIGN KEY (`id_pago`)
    REFERENCES `gotravel`.`pago` (`id_pago`),
  CONSTRAINT `fk_suscripcion_has_pago_suscripcion1`
    FOREIGN KEY (`id_suscripcion`)
    REFERENCES `gotravel`.`suscripcion` (`id_suscripcion`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`resena`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`resena` (
  `id_usuario` INT NOT NULL,
  `id_servicio` INT NOT NULL,
  `puntuacion` INT NOT NULL,
  `contenido` VARCHAR(500) NOT NULL,
  `oculto` ENUM('0', '1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_usuario`, `id_servicio`),
  INDEX `FKb88egtle2sr6ru3jrqy3rcajl` (`id_servicio` ASC) VISIBLE,
  CONSTRAINT `fk_Usuario_has_Contratacion_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`),
  CONSTRAINT `FKb88egtle2sr6ru3jrqy3rcajl`
    FOREIGN KEY (`id_servicio`)
    REFERENCES `gotravel`.`servicio` (`id_servicio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`rol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`rol` (
  `nombre` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`nombre`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`usuariorol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`usuariorol` (
  `rol` VARCHAR(50) NOT NULL,
  `id_usuario` INT NOT NULL,
  PRIMARY KEY (`rol`, `id_usuario`),
  INDEX `fk_Rol_has_Usuario_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_Rol_has_Usuario_Rol_idx` (`rol` ASC) VISIBLE,
  CONSTRAINT `fk_Rol_has_Usuario_Rol`
    FOREIGN KEY (`rol`)
    REFERENCES `gotravel`.`rol` (`nombre`),
  CONSTRAINT `fk_Rol_has_Usuario_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


-- -----------------------------------------------------
-- Inserts por defecto
-- -----------------------------------------------------

INSERT INTO rol (nombre) VALUES ('Usuario'), ('Profesional'), ('Administrador');
INSERT INTO tiposervicio (nombre) VALUES ('Transporte'), ('Alojamiento'), ('Visita'), ('Guía');

-- -----------------------------------------------------
-- Triggers
-- -----------------------------------------------------

DELIMITER //
CREATE TRIGGER tr_insertar_rol_usuario
AFTER INSERT ON usuario
FOR EACH ROW
BEGIN

    DECLARE id_usuario INT;
    SET id_usuario = NEW.id_usuario;

    INSERT INTO usuariorol (rol, id_usuario)
    VALUES ('Usuario', id_usuario);

END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER tr_actualizar_costetotal_viaje_insert
AFTER INSERT ON Etapa
FOR EACH ROW
BEGIN

    DECLARE e_id_viaje INT;
    SET e_id_viaje = NEW.id_viaje;

    UPDATE viaje
    SET coste_total = (
        SELECT SUM(coste_total)
        FROM Etapa
        WHERE id_viaje = e_id_viaje
    )
    WHERE id_viaje = e_id_viaje;

END;
//
DELIMITER ;


DELIMITER //
CREATE TRIGGER tr_actualizar_costetotal_viaje_update
AFTER UPDATE ON Etapa
FOR EACH ROW
BEGIN

    DECLARE e_id_viaje INT;
    SET e_id_viaje = NEW.id_viaje;

    UPDATE viaje
    SET coste_total = (
        SELECT SUM(coste_total)
        FROM Etapa
        WHERE id_viaje = e_id_viaje
    )
    WHERE id_viaje = e_id_viaje;

END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER tr_actualizar_costetotal_etapa_insert
AFTER INSERT ON Contratacion
FOR EACH ROW
BEGIN

    DECLARE e_id_etapa INT;
    SET e_id_etapa = NEW.id_etapa;

    UPDATE etapa
    SET coste_total = (
        SELECT SUM(pago.coste)
        FROM Contratacion
        INNER JOIN pago ON Contratacion.id_pago = pago.id_pago
        WHERE Contratacion.id_etapa = e_id_etapa
    )
    WHERE id_etapa = e_id_etapa;

END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER tr_actualizar_costetotal_etapa_update
AFTER UPDATE ON Contratacion
FOR EACH ROW
BEGIN

    DECLARE e_id_etapa INT;
    SET e_id_etapa = NEW.id_etapa;

    UPDATE etapa
    SET coste_total = (
        SELECT SUM(pago.coste)
        FROM Contratacion
        INNER JOIN pago ON Contratacion.id_pago = pago.id_pago
        WHERE Contratacion.id_etapa = e_id_etapa
    )
    WHERE id_etapa = e_id_etapa;

END;
//
DELIMITER ;

-- Contraseña = admin123!
INSERT INTO usuario (nombre, email, contrasena) values ('Administrador', 'inf@gotravel.com', '5c06eb3d5a05a19f49476d694ca81a36344660e9d5b98e3d6a6630f31c2422e7');
INSERT INTO usuariorol (rol, id_usuario) values ('Administrador', (SELECT id_usuario FROM usuario WHERE email = 'inf@gotravel.com'));