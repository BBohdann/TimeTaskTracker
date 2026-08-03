![CI](https://github.com/BBohdann/TimeTaskTracker/actions/workflows/ci.yml/badge.svg)
![User Service Coverage](badges/jacoco-user-service.svg)
![Task Service Coverage](badges/jacoco-task-service.svg)

# TimeTaskTracker
 
TimeTaskTracker is a backend application for managing users, tasks, and subtasks.
Built with **Java + Spring Boot** as two independently deployable microservices, and deployed to **Oracle Cloud** on a **k3s** Kubernetes cluster with a managed **AWS RDS PostgreSQL** database — demonstrating production-oriented backend architecture with JWT-based security, automated testing, and CI.
 
---
 
## Demo / Live API
 
- **User Service:** [Swagger UI](https://timetracker.pp.ua/user-service/swagger-ui/index.html?urls.primaryName=API)
- **Task Service:** [Swagger UI](https://timetracker.pp.ua/task-service/swagger-ui/index.html?urls.primaryName=task)
---
 
## Features
 
- **Authentication & Authorization** — Secure login with JWT, token refresh & validation, handled by a dedicated User Service
- **Task & Subtask Management** — Create, update, and delete tasks with hierarchical organization
- **Microservices Architecture** — User and Task services deployed and scaled independently
- **Database Migrations** — Schema versioned and managed with Flyway
- **Automated Testing** — Unit and integration tests (JUnit, Mockito, MockMvc) run automatically in CI
- **Containerized & Orchestrated** — Multi-stage Docker builds, deployed to Kubernetes (k3s) behind an NGINX Ingress with TLS and CORS configured for multiple frontend origins
- **CI Pipeline** — GitHub Actions builds and runs the full test suite on every push
---
 
## Tech Stack
 
- **Backend:** Java, Spring Boot
- **Security:** JWT
- **Database:** PostgreSQL, hosted on AWS RDS
- **Migrations:** Flyway
- **Testing:** JUnit, Mockito, MockMvc (unit + integration)
- **Containerization:** Docker (multi-stage builds)
- **Orchestration:** Kubernetes (k3s), hosted on Oracle Cloud
- **Networking:** NGINX Ingress, TLS, CORS
- **CI/CD:** GitHub Actions
- **API Docs:** Swagger / OpenAPI
---
 
## Project Status
 
- [x] Microservices architecture (User Service, Task Service)
- [x] JWT-based authentication & authorization
- [x] Database migrations with Flyway
- [x] Unit & integration tests (JUnit, Mockito, MockMvc)
- [x] Multi-stage Docker builds + local Docker Compose environment
- [x] Kubernetes (k3s) deployment with NGINX Ingress, TLS, and CORS
- [x] Managed PostgreSQL via AWS RDS
- [x] CI pipeline on GitHub Actions (build + test on every push)
- [ ] CD pipeline (automated deploy to cluster on merge)
- [ ] Centralized logging & metrics (Spring Actuator + Prometheus/Grafana)
- [ ] Async communication between services (e.g. Kafka/RabbitMQ)
---
 
## Getting Started (Optional)
 
Clone and run locally (the APIs are deployed live — see links above — but you can also run the project locally if needed):
 
```
git clone https://github.com/BBohdann/TimeTaskTracker.git
cd TimeTaskTracker
```
 
Run with Docker Compose:
 
```
docker-compose up --build
```
 
---
 
## Contributing
 
This project was created primarily as a learning and portfolio application. Contributions are welcome, but the main focus is showcasing modern backend practices.
 
## License
 
This project is open-source and available under the MIT License.
