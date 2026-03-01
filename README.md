🚀 RevWorkforce HRM – Backend

RevWorkforce is a Spring Boot-based Human Resource Management (HRM) system designed to streamline employee management, leave tracking, performance reviews, and goal management.

This backend provides secure REST APIs using JWT Authentication and Role-Based Access Control.

📌 Tech Stack

Java 17

Spring Boot 3.2.0

Spring Security (JWT Authentication)

Spring Data JPA (Hibernate)

MySQL

Maven

Lombok

🏗️ Architecture

The project follows a layered architecture:

Controller → Service → Repository → Database
Layers:

Controller Layer → Handles REST APIs

Service Layer → Business logic

Repository Layer → Database operations (Spring Data JPA)

Security Layer → JWT Authentication & Authorization

DTO Layer → Request & Response Mapping

Global Exception Handling → Centralized error handling

🔐 Authentication & Authorization

JWT-based authentication

Stateless session management

Role-based access control

Roles:

ADMIN

MANAGER

EMPLOYEE

Access is restricted using:

SecurityFilterChain

@PreAuthorize

Role-based URL mapping

🗂️ Modules Implemented
1️⃣ Authentication Module
Method	Endpoint	Description
POST	/api/auth/login	Login and receive JWT token

2️⃣ Employee Module
Method	Endpoint	Access
GET	/api/employees/me	All Roles
PUT	/api/employees/me	All Roles
GET	/api/employees	All Roles
GET	/api/employees/{id}	All Roles
GET	/api/employees/managers	All Roles
GET	/api/employees/my-team	Manager/Admin

3️⃣ Admin Module
Method	Endpoint
GET	/api/admin/dashboard
POST	/api/admin/employees
PUT	/api/admin/employees/{id}/assign-manager/{managerId}
PUT	/api/admin/employees/{id}/toggle-status
POST	/api/admin/leave-quotas
GET	/api/admin/leave-quotas
POST	/api/admin/reset-leave-balances/{year}

4️⃣ Leave Management Module
Method	Endpoint
GET	/api/leaves/balance
POST	/api/leaves/apply
GET	/api/leaves/my-leaves
PUT	/api/leaves/{id}/cancel
GET	/api/leaves/team
GET	/api/leaves/team/pending
PUT	/api/leaves/{id}/process
GET	/api/leaves/all

5️⃣ Performance Review Module
Reviews
Method	Endpoint
POST	/api/performance/reviews
PUT	/api/performance/reviews/{id}
PUT	/api/performance/reviews/{id}/submit
GET	/api/performance/reviews/my
GET	/api/performance/reviews/team
GET	/api/performance/reviews/team/pending
PUT	/api/performance/reviews/{id}/feedback
Goals
Method	Endpoint
POST	/api/performance/goals
PUT	/api/performance/goals/{id}
GET	/api/performance/goals/my
GET	/api/performance/goals/my/{year}
DELETE	/api/performance/goals/{id}

🗄️ Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/Abhidata
spring.datasource.username=root
spring.datasource.password=*****
spring.jpa.hibernate.ddl-auto=update


Token Expiry: 24 hours

Header: Authorization: Bearer <token>

🧪 Default Test Users

Created automatically on application startup:

Role	Email	Password
Admin	admin@revworkforce.com
admin123
Manager	manager@revworkforce.com
manager123
Employee	employee@revworkforce.com
employee123
▶️ How to Run the Project
1️⃣ Clone the repository
git clone https://github.com/abhishek/.git
2️⃣ Configure MySQL

Create database Abhidata

Update username & password in application.properties

3️⃣ Run Application
mvn spring-boot:run

Application will start at:

http://localhost:8080
🛡️ Security Flow

User logs in using /api/auth/login

Server validates credentials

JWT token is generated

Client sends token in Authorization header

JwtAuthFilter validates token

SecurityContext is populated

Role-based access control is applied

🧩 Key Features

✔ JWT Authentication
✔ Role-Based Access Control
✔ Leave Balance Auto Initialization
✔ Manager Approval Workflow
✔ Performance Review Lifecycle
✔ Goal Tracking
✔ Global Exception Handling
✔ DTO-based Clean API Responses
✔ Layered Architecture

📊 Entity Relationships (High Level)

User → Manager (Self Relationship)

User → LeaveApplications (1:N)

User → PerformanceReviews (1:N)

User → Goals (1:N)

User → LeaveBalances (1:N)