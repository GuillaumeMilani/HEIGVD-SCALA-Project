-- DROP SCHEMA IF EXISTS scala_project;
-- CREATE SCHEMA IF NOT EXISTS scala_project;
--
-- USE scala_project;

DROP TABLE IF EXISTS image_has_label;
DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS LABEL;
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS LEVEL;

-- -----------------------------------------------------
-- Table  label
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS LABEL (
  id    INT NOT NULL AUTO_INCREMENT,
  LABEL VARCHAR(45
        )   NOT NULL,
  PRIMARY KEY (id
  )
)
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table  image
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS image (
  id       INT NOT NULL AUTO_INCREMENT,
  filename VARCHAR(45
           )   NOT NULL,
  label_id INT NULL,
  PRIMARY KEY (id
  ),
  INDEX    FK_IMAGE_1_IDX(label_id ASC
           ),
  CONSTRAINT fk_image_1
  FOREIGN KEY (label_id
  )
  REFERENCES LABEL (id
  )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
)
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table  image_has_label
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS image_has_label (
  label_id INT        NOT NULL,
  image_id INT        NOT NULL,
  clicks   MEDIUMTEXT NOT NULL,
  PRIMARY KEY (label_id, image_id
  ),
  INDEX    FK_LABEL_HAS_IMAGE_IMAGE1_IDX(image_id ASC),
  INDEX    FK_LABEL_HAS_IMAGE_LABEL1_IDX(label_id ASC),
  CONSTRAINT fk_label_has_image_label1
  FOREIGN KEY (label_id
  )
  REFERENCES LABEL (id
  )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
  CONSTRAINT fk_label_has_image_image1
  FOREIGN KEY (image_id
  )
  REFERENCES image (id
  )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
)
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table  level
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS LEVEL (
  id         INT NOT NULL AUTO_INCREMENT,
  NAME       VARCHAR(45
             )   NULL,
  next_level INT NOT NULL,
  PRIMARY KEY (id
  ),
  INDEX      FK_LEVEL_LEVEL1_IDX(next_level ASC
             ),
  CONSTRAINT fk_level_level1
  FOREIGN KEY (next_level
  )
  REFERENCES LEVEL (id
  )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
)
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table  user
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS USER (
  id       INT NOT NULL AUTO_INCREMENT,
  LOGIN    VARCHAR(45
           )   NULL,
  PASSWORD VARCHAR(45
           )   NULL,
  score    VARCHAR(45
           )   NULL,
  level_id INT NOT NULL,
  PRIMARY KEY (id, level_id
  ),
  INDEX    FK_USER_LEVEL1_IDX(level_id ASC
           ),
  CONSTRAINT fk_user_level1
  FOREIGN KEY (level_id
  )
  REFERENCES LEVEL (id
  )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
)
  ENGINE = InnoDB;
