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
-- Table `gotravel`.`localizacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`localizacion` (
  `idLocalizacion` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`idLocalizacion`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`tiposervicio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`tiposervicio` (
  `NombreTipoServicio` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`NombreTipoServicio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`usuario` (
  `idUsuario` INT NOT NULL AUTO_INCREMENT,
  `Nombre` VARCHAR(45) NOT NULL,
  `Apellidos` VARCHAR(200) NOT NULL,
  `Email` VARCHAR(150) NOT NULL,
  `contrasena` VARCHAR(300) NOT NULL,
  `Tfno` CHAR(9) NULL DEFAULT NULL,
  `Foto` BLOB NULL DEFAULT NULL,
  `oculto` ENUM('0', '1') NOT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE INDEX `Email_UNIQUE` (`Email` ASC) VISIBLE,
  UNIQUE INDEX `Tfno_UNIQUE` (`Tfno` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`servicio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`servicio` (
  `idServicio` INT NOT NULL AUTO_INCREMENT,
  `Nombre` VARCHAR(45) NOT NULL,
  `Descripcion` VARCHAR(100) NOT NULL,
  `Precio` DOUBLE NOT NULL,
  `NombreTipoServicio` VARCHAR(100) NOT NULL,
  `idLocalizacion` INT NOT NULL,
  `idUsuario` INT NOT NULL,
  `oculto` ENUM('0', '1') NOT NULL,
  PRIMARY KEY (`idServicio`),
  INDEX `fk_Servicio_TipoServicio1_idx` (`NombreTipoServicio` ASC) VISIBLE,
  INDEX `fk_Servicio_Localizacion1_idx` (`idLocalizacion` ASC) VISIBLE,
  INDEX `fk_Servicio_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_Servicio_Localizacion1`
    FOREIGN KEY (`idLocalizacion`)
    REFERENCES `gotravel`.`localizacion` (`idLocalizacion`),
  CONSTRAINT `fk_Servicio_TipoServicio1`
    FOREIGN KEY (`NombreTipoServicio`)
    REFERENCES `gotravel`.`tiposervicio` (`NombreTipoServicio`),
  CONSTRAINT `fk_Servicio_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `gotravel`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`viaje`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`viaje` (
  `idViaje` INT NOT NULL AUTO_INCREMENT,
  `Nombre` VARCHAR(45) NOT NULL,
  `Descripcion` VARCHAR(150) NOT NULL,
  `FechaIni` DATE NOT NULL,
  `FechaFin` DATE NOT NULL,
  `CosteTotal` DOUBLE NOT NULL,
  `idUsuario` INT NOT NULL,
  PRIMARY KEY (`idViaje`),
  INDEX `fk_Viaje_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_Viaje_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `gotravel`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`etapa`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`etapa` (
  `idEtapa` INT NOT NULL AUTO_INCREMENT,
  `Nombre` VARCHAR(45) NOT NULL,
  `FechaIni` DATE NOT NULL,
  `FechaFin` DATE NOT NULL,
  `CosteTotal` DOUBLE NOT NULL,
  `Tipo` ENUM('transporte', 'estancia') NOT NULL,
  `idViaje` INT NOT NULL,
  PRIMARY KEY (`idEtapa`),
  INDEX `fk_Etapa_Viaje1_idx` (`idViaje` ASC) VISIBLE,
  CONSTRAINT `fk_Etapa_Viaje1`
    FOREIGN KEY (`idViaje`)
    REFERENCES `gotravel`.`viaje` (`idViaje`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`contratacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`contratacion` (
  `idContratacion` INT NOT NULL AUTO_INCREMENT,
  `idServicio` INT NOT NULL,
  `idUsuario` INT NOT NULL,
  `fecha` DATE NOT NULL,
  `idEtapa` INT NULL,
  PRIMARY KEY (`idContratacion`),
  INDEX `fk_Contratacion_Servicio1_idx` (`idServicio` ASC) VISIBLE,
  INDEX `fk_Contratacion_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  INDEX `fk_contratacion_etapa1_idx` (`idEtapa` ASC) VISIBLE,
  CONSTRAINT `fk_Contratacion_Servicio1`
    FOREIGN KEY (`idServicio`)
    REFERENCES `gotravel`.`servicio` (`idServicio`),
  CONSTRAINT `fk_Contratacion_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `gotravel`.`usuario` (`idUsuario`),
  CONSTRAINT `fk_contratacion_etapa1`
    FOREIGN KEY (`idEtapa`)
    REFERENCES `gotravel`.`etapa` (`idEtapa`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`direccion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`direccion` (
  `idLocalizacion` INT NOT NULL,
  `Calle` VARCHAR(100) NOT NULL,
  `Numero` VARCHAR(5) NOT NULL,
  `Ciudad` VARCHAR(50) NOT NULL,
  `Estado` VARCHAR(50) NOT NULL,
  `CP` CHAR(5) NOT NULL,
  PRIMARY KEY (`idLocalizacion`),
  CONSTRAINT `fk_Direccion_Localizacion10`
    FOREIGN KEY (`idLocalizacion`)
    REFERENCES `gotravel`.`localizacion` (`idLocalizacion`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`imagen`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`imagen` (
  `idServicio` INT NOT NULL,
  `Imagen` BLOB NOT NULL,
  PRIMARY KEY (`idServicio`),
  CONSTRAINT `fk_Imagen_Servicio1`
    FOREIGN KEY (`idServicio`)
    REFERENCES `gotravel`.`servicio` (`idServicio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`mensaje`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`mensaje` (
  `idMensaje` INT NOT NULL AUTO_INCREMENT,
  `Contenido` VARCHAR(500) NOT NULL,
  `Fecha` DATE NOT NULL,
  `Hora` TIME NOT NULL,
  `idEmisor` INT NOT NULL,
  `idReceptor` INT NOT NULL,
  PRIMARY KEY (`idMensaje`),
  INDEX `fk_Mensaje_Usuario1_idx` (`idEmisor` ASC) VISIBLE,
  INDEX `fk_Mensaje_Usuario2_idx` (`idReceptor` ASC) VISIBLE,
  CONSTRAINT `fk_Mensaje_Usuario1`
    FOREIGN KEY (`idEmisor`)
    REFERENCES `gotravel`.`usuario` (`idUsuario`),
  CONSTRAINT `fk_Mensaje_Usuario2`
    FOREIGN KEY (`idReceptor`)
    REFERENCES `gotravel`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`metodopago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`metodopago` (
  `idMetodoPago` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  PRIMARY KEY (`idMetodoPago`),
  INDEX `fk_MetodoPago_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_MetodoPago_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `gotravel`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`pago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`pago` (
  `idPago` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `Coste` DOUBLE NOT NULL,
  `Fecha` DATE NOT NULL,
  `idMetodoPago` INT NOT NULL,
  PRIMARY KEY (`idPago`),
  INDEX `fk_Pago_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  INDEX `fk_Pago_MetodoPago1_idx` (`idMetodoPago` ASC) VISIBLE,
  CONSTRAINT `fk_Pago_MetodoPago1`
    FOREIGN KEY (`idMetodoPago`)
    REFERENCES `gotravel`.`metodopago` (`idMetodoPago`),
  CONSTRAINT `fk_Pago_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `gotravel`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`paypal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`paypal` (
  `idMetodoPago` INT NOT NULL,
  `Email` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idMetodoPago`),
  CONSTRAINT `fk_PayPal_MetodoPago1`
    FOREIGN KEY (`idMetodoPago`)
    REFERENCES `gotravel`.`metodopago` (`idMetodoPago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`resena`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`resena` (
  `idUsuario` INT NOT NULL,
  `idContratacion` INT NOT NULL,
  `Puntuacion` INT NOT NULL,
  `Contenido` VARCHAR(200) NOT NULL,
  `oculto` ENUM('0', '1') NOT NULL,
  PRIMARY KEY (`idUsuario`, `idContratacion`),
  INDEX `fk_Usuario_has_Contratacion_Contratacion1_idx` (`idContratacion` ASC) VISIBLE,
  INDEX `fk_Usuario_has_Contratacion_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_Usuario_has_Contratacion_Contratacion1`
    FOREIGN KEY (`idContratacion`)
    REFERENCES `gotravel`.`contratacion` (`idContratacion`),
  CONSTRAINT `fk_Usuario_has_Contratacion_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `gotravel`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`rol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`rol` (
  `NombreRol` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`NombreRol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`suscripcion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`suscripcion` (
  `idSuscripcion` INT NOT NULL AUTO_INCREMENT,
  `FechaIni` DATE NOT NULL,
  `FechaFin` DATE NOT NULL,
  `Estado` ENUM('activa', 'inactiva') NOT NULL,
  `idUsuario` INT NOT NULL,
  `idPago` INT NOT NULL,
  PRIMARY KEY (`idSuscripcion`),
  INDEX `fk_Suscripcion_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  INDEX `fk_Suscripcion_Pago1_idx` (`idPago` ASC) VISIBLE,
  CONSTRAINT `fk_Suscripcion_Pago1`
    FOREIGN KEY (`idPago`)
    REFERENCES `gotravel`.`pago` (`idPago`),
  CONSTRAINT `fk_Suscripcion_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `gotravel`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`tarjetacredito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`tarjetacredito` (
  `idMetodoPago` INT NOT NULL,
  `FechaVencimiento` DATE NOT NULL,
  `Nombre` VARCHAR(200) NOT NULL,
  `Ultimos4Digitos` CHAR(4) NOT NULL,
  PRIMARY KEY (`idMetodoPago`),
  CONSTRAINT `fk_TarjetaCredito_MetodoPago1`
    FOREIGN KEY (`idMetodoPago`)
    REFERENCES `gotravel`.`metodopago` (`idMetodoPago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`ubicacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`ubicacion` (
  `idLocalizacion` INT NOT NULL,
  `CoordenadaX` DECIMAL(9,6) NOT NULL,
  `CoordenadaY` DECIMAL(9,6) NOT NULL,
  PRIMARY KEY (`idLocalizacion`),
  CONSTRAINT `fk_Direccion_Localizacion1`
    FOREIGN KEY (`idLocalizacion`)
    REFERENCES `gotravel`.`localizacion` (`idLocalizacion`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gotravel`.`usuariorol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gotravel`.`usuariorol` (
  `NombreRol` VARCHAR(50) NOT NULL,
  `idUsuario` INT NOT NULL,
  PRIMARY KEY (`NombreRol`, `idUsuario`),
  INDEX `fk_Rol_has_Usuario_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  INDEX `fk_Rol_has_Usuario_Rol_idx` (`NombreRol` ASC) VISIBLE,
  CONSTRAINT `fk_Rol_has_Usuario_Rol`
    FOREIGN KEY (`NombreRol`)
    REFERENCES `gotravel`.`rol` (`NombreRol`),
  CONSTRAINT `fk_Rol_has_Usuario_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `gotravel`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;



-- -----------------------------------------------------
-- Insercion de datos por defecto
-- -----------------------------------------------------
INSERT INTO `GoTravel`.`Rol` (`NombreRol`) VALUES ('Usuario'), ('Profesional'), ('Administrador');
INSERT INTO `GoTravel`.`TipoServicio` (`NombreTipoServicio`) VALUES ('Transporte'), ('Guia'), ('Visita'), ('Alojamiento');


-- -----------------------------------------------------
-- Trigger de suscripcion que añade rol
-- -----------------------------------------------------

DELIMITER //
CREATE TRIGGER tr_insertar_rol_profesional
AFTER INSERT ON Suscripcion
FOR EACH ROW
BEGIN

    DECLARE idUsuario INT;
    SET idUsuario = NEW.idUsuario;

    INSERT INTO UsuarioRol (NombreRol, idUsuario)
    VALUES ('Profesional', idUsuario);

END;
//
DELIMITER ;