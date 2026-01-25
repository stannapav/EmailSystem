
# Email System

A system that manages users, sends them gmail emails(scheduled or with API) and keeps logs of emails sent.

The application runs with H2 in-memory database, and it's API exposed on 8080 port.

## Tech Stack

**Used:** Java 17, Maven, Spring Boot, H2 database, Gmail SMTP server, Swagger, Lombok, Validation, cron-utils, modelmapper, Mockito, JUnit, Docker, Docker Compose

## Installation

clone my project

```bash
git clone https://github.com/stannapav/EmailSystem.git
cd EmailSystem
docker compose up -d --build
```

transport yourself in EmailSystem directory 

```bash
cd EmailSystem
```

run docker compose that will start my program

```bash
docker compose up -d --build
```

Happy using(check out swagger or postman for examples of how to use this APIs)

To stop this app use

```bash
docker compose down
```

## Also

You can check out postman ready examples of  APIs to know how to use them: https://github.com/stannapav/EmailSystem/blob/3d54279590056777fd3e71171a47f36833195daf/postman/collections/email-system.postman_collection.json

You can check out Swagger documentation for short description and to see more easilly all APIs and schemas when project is running: http://localhost:8080/swagger-ui/index.html

## Author
Strikharchuk Anna