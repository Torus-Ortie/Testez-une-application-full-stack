# Yoga App Backend

This is the backend of Yoga App. It is built with Java, Spring Boot, and MySQL.

## Prerequisites

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/)
- [MySQL](https://www.mysql.com/)

## Installation

1. Install the dependencies listed in the `pom.xml` file.

    ```bash
    mvn clean install
    ```

## Database Setup

Follow the instructions in the [main README](https://github.com/Torus-Ortie/Testez-une-application-full-stack) to set up the MySQL database.

## Launch the Application

1. Run the Spring Boot application.

    ```bash
    mvn spring-boot:run
    ```

The backend server will launch at `http://localhost:8080`

## Launch Tests

1. Run the tests and generate a coverage report.

    ```bash
    mvn clean test
    ```

Open the coverage report by navigating to the target/site/jacoco directory and opening the index.html file in a browser.
