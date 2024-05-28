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
  `foto` BLOB NULL DEFAULT NULL,
  `oculto` ENUM('0', '1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `Email_UNIQUE` (`email` ASC) VISIBLE,
  UNIQUE INDEX `Tfno_UNIQUE` (`tfno` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 21
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`chat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`chat` (
  `id_chat` INT NOT NULL,
  `id_usuario1` INT NOT NULL,
  `id_usuario2` INT NOT NULL,
  PRIMARY KEY (`id_chat`),
  INDEX `fk_Chat_usuario1_idx` (`id_usuario1` ASC) VISIBLE,
  INDEX `fk_Chat_usuario2_idx` (`id_usuario2` ASC) VISIBLE,
  CONSTRAINT `fk_Chat_usuario1`
    FOREIGN KEY (`id_usuario1`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`),
  CONSTRAINT `fk_Chat_usuario2`
    FOREIGN KEY (`id_usuario2`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`viaje`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`viaje` (
  `id_viaje` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `descripcion` VARCHAR(200) NULL DEFAULT NULL,
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
AUTO_INCREMENT = 17
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
  PRIMARY KEY (`id_etapa`),
  INDEX `fk_Etapa_Viaje1_idx` (`id_viaje` ASC) VISIBLE,
  CONSTRAINT `fk_Etapa_Viaje1`
    FOREIGN KEY (`id_viaje`)
    REFERENCES `gotravel`.`viaje` (`id_viaje`))
ENGINE = InnoDB
AUTO_INCREMENT = 22
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
AUTO_INCREMENT = 4
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
  `descripcion` VARCHAR(100) NOT NULL,
  `precio` DOUBLE NOT NULL,
  `tipo_servicio` VARCHAR(100) NOT NULL,
  `id_direccion` INT NOT NULL,
  `id_usuario` INT NOT NULL,
  `oculto` ENUM('0', '1') NOT NULL DEFAULT '0',
  `fecha_inicio` DATE NOT NULL,
  `fecha_final` DATE NULL DEFAULT NULL,
  `hora` TIME NULL DEFAULT NULL,
  PRIMARY KEY (`id_servicio`),
  INDEX `fk_Servicio_TipoServicio1_idx` (`tipo_servicio` ASC) VISIBLE,
  INDEX `fk_Servicio_Direccion1_idx` (`id_direccion` ASC) VISIBLE,
  INDEX `fk_Servicio_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  CONSTRAINT `fk_Servicio_Direccion1`
    FOREIGN KEY (`id_direccion`)
    REFERENCES `gotravel`.`localizacion` (`id_localizacion`),
  CONSTRAINT `fk_Servicio_TipoServicio1`
    FOREIGN KEY (`tipo_servicio`)
    REFERENCES `gotravel`.`tiposervicio` (`nombre`),
  CONSTRAINT `fk_Servicio_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`contratacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`contratacion` (
  `id_contratacion` INT NOT NULL AUTO_INCREMENT,
  `id_servicio` INT NOT NULL,
  `id_usuario` INT NOT NULL,
  `fecha` DATE NOT NULL,
  `id_etapa` INT NOT NULL,
  `id_pago` INT NOT NULL,
  PRIMARY KEY (`id_contratacion`),
  INDEX `fk_Contratacion_Servicio1_idx` (`id_servicio` ASC) VISIBLE,
  INDEX `fk_Contratacion_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_contratacion_etapa1_idx` (`id_etapa` ASC) VISIBLE,
  INDEX `fk_contratacion_pago1_idx` (`id_pago` ASC) VISIBLE,
  CONSTRAINT `fk_contratacion_etapa1`
    FOREIGN KEY (`id_etapa`)
    REFERENCES `gotravel`.`etapa` (`id_etapa`),
  CONSTRAINT `fk_contratacion_pago1`
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
-- Table `gotravel`.`direccion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`direccion` (
  `id_direccion` INT NOT NULL AUTO_INCREMENT,
  `calle` VARCHAR(100) NOT NULL,
  `numero` VARCHAR(5) NOT NULL,
  `ciudad` VARCHAR(50) NOT NULL,
  `estado` VARCHAR(50) NOT NULL,
  `cp` CHAR(5) NOT NULL,
  PRIMARY KEY (`id_direccion`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`imagen`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`imagen` (
  `id_servicio` INT NOT NULL,
  `imagen` BLOB NOT NULL,
  PRIMARY KEY (`id_servicio`),
  CONSTRAINT `fk_Imagen_Servicio1`
    FOREIGN KEY (`id_servicio`)
    REFERENCES `gotravel`.`servicio` (`id_servicio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`mensaje`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`mensaje` (
  `id_mensaje` INT NOT NULL,
  `contenido` VARCHAR(200) NOT NULL,
  `fecha` DATE NOT NULL,
  `hora` TIME NOT NULL,
  `id_usuario` INT NOT NULL,
  `id_chat` INT NOT NULL,
  PRIMARY KEY (`id_mensaje`),
  INDEX `fk_Mensaje_usuario1_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_Mensaje_Chat1_idx` (`id_chat` ASC) VISIBLE,
  CONSTRAINT `fk_Mensaje_Chat1`
    FOREIGN KEY (`id_chat`)
    REFERENCES `gotravel`.`chat` (`id_chat`),
  CONSTRAINT `fk_Mensaje_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`resena`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`resena` (
  `id_usuario` INT NOT NULL,
  `id_contratacion` INT NOT NULL,
  `puntuacion` INT NOT NULL,
  `contenido` VARCHAR(200) NOT NULL,
  `oculto` ENUM('0', '1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_usuario`, `id_contratacion`),
  INDEX `fk_Usuario_has_Contratacion_Contratacion1_idx` (`id_contratacion` ASC) VISIBLE,
  INDEX `fk_Usuario_has_Contratacion_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  CONSTRAINT `fk_Usuario_has_Contratacion_Contratacion1`
    FOREIGN KEY (`id_contratacion`)
    REFERENCES `gotravel`.`contratacion` (`id_contratacion`),
  CONSTRAINT `fk_Usuario_has_Contratacion_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gotravel`.`usuario` (`id_usuario`))
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


-- -----------------------------------------------------
-- Table `gotravel`.`pagosPorSuscripción`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`pagosPorSuscripcion` (
  `id_suscripcion` VARCHAR(50) NOT NULL,
  `id_pago` INT NOT NULL,
  PRIMARY KEY (`id_pago`),
  INDEX `fk_suscripcion_has_pago_pago1_idx` (`id_pago` ASC) VISIBLE,
  INDEX `fk_suscripcion_has_pago_suscripcion1_idx` (`id_suscripcion` ASC) VISIBLE,
  CONSTRAINT `fk_suscripcion_has_pago_suscripcion1`
    FOREIGN KEY (`id_suscripcion`)
    REFERENCES `gotravel`.`suscripcion` (`id_suscripcion`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_suscripcion_has_pago_pago1`
    FOREIGN KEY (`id_pago`)
    REFERENCES `gotravel`.`pago` (`id_pago`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
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
CREATE TRIGGER tr_insertar_rol_profesional
AFTER INSERT ON Suscripcion
FOR EACH ROW
BEGIN

    DECLARE id_usuario INT;
    SET id_usuario = NEW.id_usuario;

    INSERT INTO usuariorol (rol, id_usuario)
    VALUES ('Profesional', id_usuario);

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
        INNER JOIN pago ON Contratacion.id_pago = pago.id
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
        INNER JOIN pago ON Contratacion.id_pago = pago.id
        WHERE Contratacion.id_etapa = e_id_etapa
    )
    WHERE id_etapa = e_id_etapa;

END;
//
DELIMITER ;