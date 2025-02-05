-- MySQL Script generated by MySQL Workbench
-- Wed Apr  5 22:54:55 2023
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`institution`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`institution` (
  `institution_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `address` VARCHAR(100) NOT NULL,
  `phone_number` VARCHAR(15) NULL,
  `website` VARCHAR(100) NULL,
  PRIMARY KEY (`institution_id`),
  UNIQUE INDEX `phone_number_UNIQUE` (`phone_number` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`documents`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`documents` (
  `document_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(200) NULL,
  `price` DECIMAL(4,2) NULL,
  `institution_id` INT NOT NULL,
  PRIMARY KEY (`document_id`),
  INDEX `institution_id_idx` (`institution_id` ASC) VISIBLE,
  CONSTRAINT `institution_id`
    FOREIGN KEY (`institution_id`)
    REFERENCES `mydb`.`institution` (`institution_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`tasks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`tasks` (
  `task_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(200) NULL,
  `estimated_time` DATETIME NOT NULL,
  PRIMARY KEY (`task_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`task_documents`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`task_documents` (
  `task_id` INT NOT NULL,
  `document_id` INT NOT NULL,
  INDEX `task_id_idx` (`task_id` ASC) VISIBLE,
  INDEX `document_id_idx` (`document_id` ASC) VISIBLE,
  PRIMARY KEY (`document_id`, `task_id`),
  CONSTRAINT `task_id`
    FOREIGN KEY (`task_id`)
    REFERENCES `mydb`.`tasks` (`task_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `document_id`
    FOREIGN KEY (`document_id`)
    REFERENCES `mydb`.`documents` (`document_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `phone_number` VARCHAR(15) NOT NULL,
  `adress` VARCHAR(100) NULL,
  `created_at` TIMESTAMP NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `surname` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  UNIQUE INDEX `phone_number_UNIQUE` (`phone_number` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`user_documents`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`user_documents` (
  `user_id` INT NOT NULL,
  `document_id` INT NOT NULL,
  INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
  INDEX `document_id_idx` (`document_id` ASC) VISIBLE,
  PRIMARY KEY (`user_id`, `document_id`),
  CONSTRAINT `user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `mydb`.`users` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `document_id_from_UD`
    FOREIGN KEY (`document_id`)
    REFERENCES `mydb`.`documents` (`document_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`feedback`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`feedback` (
  `feedback_id` INT NOT NULL AUTO_INCREMENT,
  `task_id` INT NOT NULL,
  `rating` INT NOT NULL,
  `comment` VARCHAR(200) NULL DEFAULT 'None',
  `created_at` TIMESTAMP NOT NULL,
  `user_id` INT NOT NULL,
  `institution_id` INT NOT NULL,
  PRIMARY KEY (`feedback_id`),
  INDEX `task_id_idx` (`task_id` ASC) VISIBLE,
  INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
  INDEX `institution_id_idx` (`institution_id` ASC) VISIBLE,
  CONSTRAINT `task_id_from_feedback`
    FOREIGN KEY (`task_id`)
    REFERENCES `mydb`.`tasks` (`task_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `user_id_from_feedback`
    FOREIGN KEY (`user_id`)
    REFERENCES `mydb`.`users` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `institution_id_from_feedback`
    FOREIGN KEY (`institution_id`)
    REFERENCES `mydb`.`institution` (`institution_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`roles` (
  `role_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`role_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`user_tasks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`user_tasks` (
  `task_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`task_id`, `user_id`),
  INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `user_id_from_UT`
    FOREIGN KEY (`user_id`)
    REFERENCES `mydb`.`users` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `task_id_from_UT`
    FOREIGN KEY (`task_id`)
    REFERENCES `mydb`.`tasks` (`task_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`users_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`users_roles` (
  `user_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  INDEX `role_id_idx` (`role_id` ASC) VISIBLE,
  PRIMARY KEY (`user_id`, `role_id`),
  CONSTRAINT `user_id_from_UR`
    FOREIGN KEY (`user_id`)
    REFERENCES `mydb`.`users` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `role_id`
    FOREIGN KEY (`role_id`)
    REFERENCES `mydb`.`roles` (`role_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
