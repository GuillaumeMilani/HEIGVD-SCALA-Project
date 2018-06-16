# --- !Ups
-- -----------------------------------------------------
-- Table  label
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS label (
  id    INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
  label VARCHAR(45) NOT NULL
)
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table  image
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS image (
  id       INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
  filename VARCHAR(45) NOT NULL,
  label_id INT         NULL,
  FOREIGN KEY (label_id) REFERENCES label (id)
)
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table  image_has_label
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS image_has_label (
  label_id INT NOT NULL,
  image_id INT NOT NULL,
  clicks   INT NOT NULL,
  PRIMARY KEY (label_id, image_id),
  FOREIGN KEY (label_id) REFERENCES label (id),
  FOREIGN KEY (image_id) REFERENCES image (id)
)
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table  level
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS level (
  id         INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
  NAME       VARCHAR(45) NULL,
  next_level INT         NOT NULL,
  FOREIGN KEY (next_level) REFERENCES level (id)
)
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table  user
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS user (
  id       INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
  LOGIN    VARCHAR(45) NULL,
  PASSWORD VARCHAR(45) NULL,
  score    VARCHAR(45) NULL,
  level_id INT         NOT NULL,
  FOREIGN KEY (level_id) REFERENCES level (id)
)
  ENGINE = InnoDB;

# --- !Downs

DROP TABLE image_has_label;
DROP TABLE image;
DROP TABLE label;
DROP TABLE user;
DROP TABLE level;
