# TimeTaskTracker

TimeTaskTracker is a scalable backend application for managing users, tasks, and subtasks.  
Built with **Java + Spring Boot** and deployed to **Google Cloud** using **Docker** and **Kubernetes**, it demonstrates production-ready architecture with JWT-based security.

---

## Demo / Live API
- **User Service:** [Swagger UI](https://timetracker.pp.ua/user-service/swagger-ui/index.html?urls.primaryName=API)  
- **Task Service:** [Swagger UI](https://timetracker.pp.ua/task-service/swagger-ui/index.html?urls.primaryName=task)  

---

## Features
- **Authentication & Authorization** — Secure login with JWT, token refresh & validation  
- **Task & Subtask Management** — Create, update, and delete tasks with hierarchical organization  
- **Scalable Microservices** — User and Task services separated for clean architecture  
- **Containerized & Orchestrated** — Docker for portability, Kubernetes for resilience and scaling  

---

## Tech Stack
- **Backend:** Java, Spring Boot  
- **Security:** JWT  
- **Database:** PostgreSQL  
- **Containerization:** Docker  
- **Orchestration:** Kubernetes (Google Cloud)  
- **API Docs:** Swagger / OpenAPI  

---

## Getting Started (Optional)

Clone and run locally (The APIs are deployed live (see links above), but you can also run the project locally if needed):
```bash
git clone https://github.com/BBohdann/TimeTaskTracker.git
cd TimeTaskTracker
```
Run with Docker Compose
```bash
docker-compose up --build
```

##  Contributing
This project was created primarily as a learning and demo application. Contributions are welcome, but the main focus is showcasing modern backend practices. 

##  License 
This project is open-source and available under the MIT License.
