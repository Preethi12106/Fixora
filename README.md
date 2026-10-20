# Fixora — Hostel Complaint and Feedback Collection System

Fixora is a beginner-friendly hostel management mini-project built with Java, Spring Boot, Thymeleaf, and MySQL. Students can submit complaints about hostel facilities and share feedback. Wardens can review complaints, update their status, and delete them.

The repository also includes a separate Spring Boot Notification Service to demonstrate HTTP communication between two applications.

## Features

- Submit complaints with a title, description, category, and room number.
- Select from Electrical, Plumbing, Cleanliness, Wi-Fi, and Other categories.
- Track complaints as Pending, In Progress, or Resolved.
- View submitted complaints and their status.
- Manage complaints from the warden dashboard.
- Open a complaint to view its full details.
- Submit feedback with a rating from 1 to 5.
- Use the complaint REST API for create, read, update-status, and delete operations.
- Store complaint and feedback data in MySQL using Spring Data JPA and Hibernate.
- Demonstrate HQL queries, `@MappedSuperclass`, constructor-based dependency injection, and AOP logging.
- Send a best-effort HTTP notification when a complaint is created.

## Technology

- Java 21
- Spring Boot 3.3.5, Spring MVC, and Thymeleaf
- Spring Data JPA, Hibernate, and MySQL 8+
- Maven Wrapper
- HTML and CSS

## Project layout

```text
Fixora/
├── main-app/                 # Main web application and REST API (port 8080)
├── notification-service/     # Independent notification app (port 8081)
├── database/
│   ├── schema.sql
│   └── sample-data.sql
├── docs/
│   ├── architecture.md
│   └── concepts.md
├── postman/
│   └── hostel-complaints.postman_collection.json
└── README.md
```

## Prerequisites

- JDK 21 or later, with `java` available in the terminal
- MySQL 8 or later, running locally
- A terminal such as PowerShell, Command Prompt, or the VS Code terminal

The Maven Wrapper is included, so a separate Maven installation is not required.

## Configure MySQL

Create the database:

```sql
CREATE DATABASE IF NOT EXISTS hostel_complaint_db;
```

The application reads its database username and password from `DB_USERNAME` and `DB_PASSWORD`. For PowerShell, set them in the terminal where you will launch the main app:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-mysql-password"
```

If your local MySQL root account has no password, set `$env:DB_PASSWORD = ""`. These values are only for the current terminal session. Do not put real passwords in GitHub.

The default connection URL is configured in `main-app/src/main/resources/application.properties` and targets `hostel_complaint_db`. If MySQL uses a different host, port, or database name, update that URL before running the app.

Hibernate creates or updates the entity tables at startup. This setting is for local development and demonstration; a production application should use database migrations. To create the database and tables using the supplied script:

```powershell
Get-Content -Raw database\schema.sql | mysql -u root -p
```

To insert sample complaints and feedback, run:

```powershell
Get-Content -Raw database\sample-data.sql | mysql -u root -p
```

## Run the applications on Windows

Check that Java is installed:

```powershell
java -version
```

### 1. Start the Notification Service

Open a terminal in the repository root and run:

```powershell
cd notification-service
.\mvnw.cmd spring-boot:run
```

It listens on `http://localhost:8081`.

### 2. Start the main application

Open a second terminal in the repository root. Set the database credentials in this terminal, then start the app:

```powershell
cd main-app
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-mysql-password"
.\mvnw.cmd spring-boot:run
```

The web application is at [http://localhost:8080](http://localhost:8080). If the Notification Service is unavailable, complaints submitted through either the web form or REST API are still saved.

To build either application as a JAR instead:

```powershell
.\mvnw.cmd clean package
java -jar target\main-app-0.0.1-SNAPSHOT.jar
```

For the notification app, run the same commands from `notification-service`; its JAR is `target\notification-service-0.0.1-SNAPSHOT.jar`.

## Web pages

| Page | URL |
|---|---|
| Home | `http://localhost:8080/` |
| Submit a complaint | `http://localhost:8080/complaints/new` |
| View complaints | `http://localhost:8080/complaints` |
| Complaint details | `http://localhost:8080/complaints/1` |
| Warden dashboard | `http://localhost:8080/admin` or `http://localhost:8080/warden` |
| Submit feedback | `http://localhost:8080/feedback` |

## REST API

The main application exposes these complaint endpoints:

| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/complaints` | List complaints |
| `GET` | `/api/complaints/{id}` | Get one complaint; returns `404` if it does not exist |
| `GET` | `/api/complaints/search/status/{status}` | Search with the status HQL query |
| `GET` | `/api/complaints/search/category/{category}` | Search with the category HQL query |
| `POST` | `/api/complaints` | Submit a complaint |
| `PUT` | `/api/complaints/{id}` | Update complaint details |
| `PUT` | `/api/complaints/{id}/status` | Update status to `Pending`, `In Progress`, or `Resolved` |
| `DELETE` | `/api/complaints/{id}` | Delete a complaint; returns `204` on success |
| `GET` | `/api/feedback` | List saved feedback |
| `POST` | `/api/feedback` | Save feedback |

### Create a complaint

Send a `POST` request to `http://localhost:8080/api/complaints` with `Content-Type: application/json`:

```json
{
  "title": "Fan not working",
  "description": "The ceiling fan in room 204 is not rotating properly.",
  "category": "Electrical",
  "roomNumber": "204"
}
```

The response has status `201 Created` and includes the generated ID, creation time, and initial status. The server assigns the ID, creation time, and `PENDING` status; clients cannot set them in the create request.

### Update complaint details

Send a `PUT` request to `http://localhost:8080/api/complaints/1` with the same title, description, category, and room number fields as the create request. The complaint ID and status are preserved by the server.

### Update complaint status

Send a `PUT` request to `http://localhost:8080/api/complaints/1/status`:

```json
{
  "status": "In Progress"
}
```

### Try the other endpoints

```text
GET    http://localhost:8080/api/complaints
GET    http://localhost:8080/api/complaints/1
GET    http://localhost:8080/api/complaints/search/status/PENDING
GET    http://localhost:8080/api/complaints/search/category/Electrical
DELETE http://localhost:8080/api/complaints/1
```

### Feedback

Send a `POST` request to `http://localhost:8080/api/feedback`:

```json
{
  "rating": 5,
  "comments": "The hostel maintenance team responded quickly."
}
```

Complaint and feedback creation return `201 Created`; the Notification Service returns `200 OK`. Invalid request fields return `400 Bad Request`; missing complaint IDs return `404 Not Found`.

### Postman

Import `postman/hostel-complaints.postman_collection.json` into Postman. Start the main app and Notification Service first, then execute the requests. Replace sample IDs with an ID returned by a successful create request. The collection includes complaint CRUD, feedback, and notification examples.

## Notification Service

The main application sends a notification request to `POST http://localhost:8081/api/notifications` after saving a complaint. The Notification Service returns an `ACCEPTED` confirmation. This is a simple demonstration endpoint; it does not send email or SMS.

## Assignment concepts

- `ComplaintRepository` contains HQL queries for searching complaints by status and category.
- `BaseRecord` is a `@MappedSuperclass`; its persistent fields are inherited by `Complaint` and `Feedback`, and it has no separate table.
- `ServiceLoggingAspect` logs service method execution without logging complaint descriptions.
- Spring Data JPA manages the regular Hibernate persistence and transactions.
- Additional explanations are in [`docs/concepts.md`](docs/concepts.md) and [`docs/architecture.md`](docs/architecture.md).

## Git and GitHub

After creating an empty GitHub repository, connect it to this project and push the `main` branch. Do not add real credentials or local environment files:

```powershell
git init
git add .
git commit -m "Add Fixora hostel complaint system"
git branch -M main
git remote add origin https://github.com/<username>/<repository>.git
git push -u origin main
```

The project `.gitignore` excludes Maven build output. Git tracks source changes; GitHub hosts and shares the repository.

## Troubleshooting

- **Java is not recognized:** install JDK 21 or later and reopen the terminal.
- **Database connection fails:** make sure MySQL is running, the database exists, and `DB_USERNAME` / `DB_PASSWORD` match your local account.
- **Port is already in use:** stop the other process using port 8080 or 8081 before starting the corresponding app.
- **Notification unavailable:** start `notification-service` on port 8081. Complaint storage remains available even if that service is stopped.

## License

This project is intended for educational use. Add a license file if you want to specify reuse terms.
