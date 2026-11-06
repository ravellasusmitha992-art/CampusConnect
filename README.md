# CampusConnect

Role-based campus collaboration portal (Student / Faculty / Admin).
Stack: React + Vite (frontend), Spring Boot 3 + Spring Security + JWT (backend), MySQL (DB).

## Folder structure
```
CampusConnect/
  backend/    Spring Boot REST API (Java 17, Maven)
  frontend/   React app (Vite)
```

## 1. Backend setup (VS Code)

Requirements: JDK 17, Maven (or use VS Code's bundled Maven), MySQL running locally.

1. Open `backend/` folder in VS Code (install the "Extension Pack for Java" + "Spring Boot Extension Pack" if prompted).
2. Create the database (or let Hibernate auto-create it):
   ```sql
   CREATE DATABASE campusconnect;
   ```
3. Edit `src/main/resources/application.properties` — set your MySQL username/password.
4. Run:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   API starts on **http://localhost:8080**.

   No MySQL handy yet? Uncomment the H2 lines in `application.properties` and comment out the MySQL lines — it'll run against a local file-based H2 DB instead, no install needed.

### Key endpoints
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | public |
| POST | `/api/auth/login` | public |
| GET  | `/api/events` | any authenticated user |
| POST | `/api/events` | FACULTY, ADMIN |
| POST | `/api/events/{id}/join` | any authenticated user |
| GET/POST | `/api/announcements` | GET: any; POST: FACULTY, ADMIN |
| GET/POST | `/api/questions`, `/api/questions/{id}/answers` | any authenticated user |
| GET | `/api/admin/users` | ADMIN |
| DELETE | `/api/admin/users/{id}` | ADMIN |

Send the JWT from login/register as `Authorization: Bearer <token>` on subsequent requests.

## 2. Frontend setup (VS Code)

Requirements: Node.js 18+.

```bash
cd frontend
npm install
npm run dev
```
App starts on **http://localhost:5173** and proxies `/api` calls to the backend on port 8080 (see `vite.config.js`).

## 3. Typical flow
1. Register as FACULTY → post an event/announcement.
2. Register as STUDENT → browse events, join one, ask a question in the forum.
3. Register as ADMIN → view/delete users from `/admin`.

## Notes / next steps
- Passwords are BCrypt-hashed; JWT secret and expiry are in `application.properties` — change `jwt.secret` before any real deployment.
- CORS is locked to `http://localhost:5173` via `app.cors.allowed-origin` — update if you deploy the frontend elsewhere.
- The AI Chat Assistant / event recommendation feature from the SRS isn't wired in yet — it needs an OpenAI API key plumbed into a new `AiController`; ask if you want that added next.
- `spring.jpa.hibernate.ddl-auto=update` auto-creates tables on first run — fine for dev, switch to migrations (Flyway/Liquibase) before production.
