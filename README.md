# Yoga Application

This application allows to create, join or leave yoga classes depending if you are an admin or a user.  

## Setting up the Database

1. Install MySQL (on your local machine or in a Docker container) and create "yoga" and "test" databases for the application and test.
2. Run the script `script.sql` in the `resources/sql` directory to set up the application database.
3. Run the script `test_schema.sql` and `test_data.sql` in the `back/src/test/resources` directory to set up the test database.
4. For your databases connections use these configuration files :
    - back/src/main/resources/application.properties
    - back/src/test/resources/application-test.properties

### Necessary Environment Variables for MySQL

- `DB_USERNAME`: The root for your MySQL database.
- `DB_PASSWORD`: The password for your MySQL database.

## Installing the Application

1. Make sure you've installed all needed dependencies: 
    - NodeJS v16
    - Angular CLI v14
    - Java 11
    - SpringBoot 2.6.1
    - MySQL v8.0
    - Maven 3.8.6
2. Clone this repo to your local environment.
3. Move to the back-end project folder and follow the README.md instruction for installation.
4. Go to the front-end project folder and follow the README.md instruction for installation.

## Launching the Application

1. In the back-end project folder, follow the README.md instruction to launch the application.
2. In the front-end project folder, follow the README.md instruction to launch the application.

## Executing Tests

### Front-end Unit and Integration Testing (Jest)
You can read this : https://github.com/Torus-Ortie/Testez-une-application-full-stack/blob/master/front/README.md
1. In the front-end project folder, execute `npm run test` to conduct front-end unit tests via Jest.
2. For the coverage report, utilize `npm run test:coverage`.
3. A `index.html` report will be created in the `front/coverage/jest/lcov-report/index.html` path.

### End-to-End Testing (Cypress)
You can read this : https://github.com/Torus-Ortie/Testez-une-application-full-stack/blob/master/front/README.md
1. In the front-end project area, execute `npm run e2e` for conducting end-to-end testing using Cypress.
2. For the coverage overview, utilize `npm run e2e:coverage`.

### Back-end Unit and Integration Testing (JUnit and Mockito)
You can read this : https://github.com/Torus-Ortie/Testez-une-application-full-stack/blob/master/back/README.md
1. Make sure to execute the database creation script found in the `resources/sql` directory before running tests.
2. In the back-end project area, run `mvn clean test` to perform back-end unit and integration tests via JUnit and Mockito.
3. A coverage report will be accessible in the `back/target/site/jacoco/index.html` folder.

