# Blog API

## Table of Contents

- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Project Structure](#project-structure)
- [Setup & Installation](#setup--installation)
- [API Documentation](#api-documentation)
- [To-dos](#to-dos)
- [History](#history)
- [License](#license)

## Overview

This API provides backend functionality for managing a blog platform. It allows users to perform actions such as
creating, retrieving, updating, and deleting blog posts. Additional features may include managing categories,
tags, user authentication. The API is designed to be RESTful and follows standard HTTP conventions, making it
easy to integrate with frontend applications or other services.

**Note**: The API is currently in its early development stage. Features may be limited, and the structure is subject to
change. Future updates will introduce additional capabilities such as enhance user authentication, comment management,
and more.

## Technologies Used

- **Java 21** - Core language
- **Apache maven** - Build and dependency management
- **Spring Boot 3.5.4** - Backend framework
- **PostgreSQL** - Database
- **JWT (JSON Web Token)** - Authentication

## Features

- CRUD operations for blog posts
- Basic tags management
- Basic category management
- Basic authentication
    - Sign-up
    - Login

## Project Structure

``` 
src/
└── main/
    |── java/
    |   └── com/
    |       └── phirom_02/
    |           └── blog_api/
    |               ├── config/               # Security config, etc.
    |               ├── controller/           # REST API controllers
    |               ├── domain/
    |               │   ├── entity/           # JPA entities like Post, User
    |               │   ├── dto/              # DTOs used in service/controller
    |               │   ├── PostStatus.java   # Enum (e.g. DRAFT, PUBLISHED)
    |               │   └── TokenType.java    # Jwt token type 
    |               ├── mappers/              # Data converters
    |               ├── repository/           # Spring Data JPA Repositories
    |               ├── security/             # JWT utilities, filters, etc.                          
    |               ├── service/              # Business logic
    |               ├── BlogApiApplication.java
    |
    |── resources/
    |   └── application.yml
    |
    └── test
        |── java
        |   └── com
        |       └── phirom_02/
        |           └── blog_api/
        |               └── BlogApiApplicationTests.java
        └── resources/
            └── application.yml
```

## Setup & Installation

### Prerequisites

- Java 21 JDK installed (if running locally)
- Apache Maven installed (to build the project)
- Docker and Docker Compose installed (for containerized setup)
- PostgreSQL (optional if running locally without Docker)

### Local Setup (Without Docker)

1. **Clone the repository:**

```bash
  git clone https://github.com/phirom-02/spring-boot-blog-api
  cd blog-api
```

2. **Build the project using Maven:**

```bash
  mvn clean package
```

3. **Configure the database connection:**

Edit `src/main/resources/application.yml` or set environment variables to point to your PostgreSQL database.

4. **Run the application:**

```bash
  java -jar target/{built-file-name}.jar
```

5. **Access the API**

   Open your browser at `http://localhost:{server-port}/swagger-ui.html` to explore API docs.

### Running with Docker Compose

1. **Clone the repository and navigate into it:**

```bash
  git clone https://github.com/phirom-02/spring-boot-blog-api
  cd blog-api
```

2. **Make build script executable:**

```bash
  chmod +x ./scripts/runner.sh
```

3. **Build and start the services:**

```bash
  ./script/runner.sh
```

3. **Access the API:**
   Once the containers are running, visit http://localhost:8080/swagger-ui.html to use the API.

### Notes

- The default PostgreSQL password is changemeinprod!. Change this in docker-compose.yml before production use.
- Data persistence requires enabling volumes in the docker-compose.yml (see Docker Compose Setup).

## API Documentation

This project uses **Swagger UI** for auto-generated, interactive API documentation.
After running the project, you can access the docs at:

## To-dos

- [ ] **Enhance authentication**
    - [ ] Implement refresh tokens and token rotation
    - [ ] Implement password strength validator
    - [ ] Add logout and "logout all devices" features
    - [ ] Add account verification (e.g., email confirmation)
    - [ ] Implement RBAC (Role-Based Access Control)

- [ ] **Add testing**
    - [ ] Unit tests for services and utils
    - [ ] Integration tests for API endpoints

- [x] **Improve documentation**
    - [x] Add Swagger/OpenAPI annotations
    - [x] Document code (JavaDocs where needed)
    - [x] Provide Clear **Setup & Installation** instructions in **README**

- [x] **Usability & Accessibility**
    - [x] Reconfigure Docker and Docker Compose for cross-machine consistency

## History

**Version 0.0.1 (2025-07-28)**

- Implement basic features for blog APIs

## License

The MIT License (MIT)

Copyright (c) 2015 Chris Kibble

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
