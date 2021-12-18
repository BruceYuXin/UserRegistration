
CREATE DATABASE IF NOT EXISTS`user_registration`;

USE `user_registration`;

CREATE TABLE `customers` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `create_date` DATETIME NOT NULL,
    `update_date` DATETIME NOT NULL,
    PRIMARY KEY ( `id` )
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
CREATE INDEX customers_username_IDX USING BTREE ON user_registration.customers (username,email,create_date,address);
CREATE INDEX customers_username_IDX USING BTREE ON user_registration.customers (username,email);
CREATE INDEX customers_email_IDX USING BTREE ON user_registration.customers (email);
CREATE INDEX customers_create_date_IDX USING BTREE ON user_registration.customers (create_date);

/*Data for the table `customers` */

INSERT  INTO `customers`(`username`,`password`,`email`,`address`,`create_date`,`update_date`)
VALUES ('admin','123456','admin@test.com','admin','2021-12-19 00:00:00','2019-03-21 00:00:00');

