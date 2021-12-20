# UserRegistration
The project for SpringCloud with Docker

1. Config SMTP information 

   You could use your personal email and enable SMTP function in your personal email.
 
   In the application.properties:
 
     spring.mail.host=smtp.XXXX.com

     spring.mail.username=XXXX@sina.com  (Your personal email address)

     spring.mail.password=YYYYY          (Your personal email password)

     mail.from=XXXX@sina.com             (Your personal email address)


2. Perform build-docker-compose.sh script

   Go to UserRegistration/docker fold

   (a) chmod 777 build-docker-compose.sh

   (b) ./build-docker-compose.sh

   (

     Note: If shell script does not work, please perform from Step #11 to Step #14.
     I have already tested the build-docker-compose.sh script, it works in my machine.

   )


3. After everything works well, perform Database initialization.
   
   In the src/main/resources/SQL, there is the 'db.sql' file

   (a) Connect to MySQL

       Server: localhost

       Port: 3306

       Username: root

       Password:

       (Note: The password of root is null so that do not need to input any password for root account)

   (b) CREATE DATABASE 'user_registration';

   (c) USE `user_registration`;

   (d) CREATE TABLE `customers` (
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

   (e) INSERT  INTO `customers`(`username`,`password`,`email`,`address`,`create_date`,`update_date`)
       VALUES ('admin','123456','admin@test.com','admin','2021-12-19 00:00:00','2019-03-21 00:00:00');
 

5. **Swagger UI address**
 
   http://localhost:8080/swagger-ui.html
 

6. Register / Create a new User
 
   This request is the POST type's one.
 
   Request-URL: http://localhost:8080/customer/create
 
   Request-Type: POST
 
   Request-Body:
 
   {
     "username": "test1",
     "password": "123456",
     "email": "test1@sina.com",
     "address": "test1 address"
   }
 
   (Note: if want to receive registration email, please use a real email rather than fake email address.)
 

7. Query user by email
 
    This request is the GET type's one.
 
       Request-URL: http://localhost:8080/customer/findbyemail?email=test1@sina.com
 
       Request-Type: POST
 

8. Query user by username
 
   This request is the GET type's one.
 
   Request-URL: http://localhost:8080/customer/findbyusername?username=test1
 

9. Modify user
 
   This request is the POST type's one.
 
   Request-URL: http://localhost:8080/customer/modify
 
   Request-Type: POST
 
   Request-Body:
 
   {
     "username": "test1",
     "password": "123456modification",
     "email": "test1modification@sina.com",
     "address": "test1 modification address"
   }
 

10. Delete user
 
    This project is the POST type's one.
 
    Request-URL: http://localhost:8080/customer/delete
 
    Request-Type: POST
 
    Request-Body: 
 
    {
      "username": "test1"
    }


**-----------------------------------------------------------------------------------------------------**

If Step #2 does not work, please perform the following steps. If Step #2 success, please ignore following.

11. Perform mvn package for this project

   You could import this project and perform mvn package in IDEAJ by maven

12. Copy this project Jar file into Docker folder

   In terminal, perform the following commands:

   cd UserRegistration/docker

   cp ../target/UserRegistration-1.0.0.jar UserRegistration-1.0.0.jar

13. After finishing the Step #3, perform Docker build command

   In the 'UserRegistration/docker' folder, perform the following commands:

   docker build -t userregistration .

14. After finishing the Step #4, perform docker-compose

   In the 'UserRegistration/docker' folder

   docker-compose up
 
