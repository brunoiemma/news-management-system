# News Management System

![Build Status](https://img.shields.io/github/actions/workflow/status/brunoiemma/news-management-system/build.yml)
![Coverage](https://img.shields.io/codecov/c/github/brunoiemma/news-management-system)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![License](https://img.shields.io/badge/license-MIT-blue)

A robust Spring Boot application for managing news articles with a complete content workflow system. This system allows administrators to create, edit, and manage articles through different status stages while providing a public interface for viewing published content.

## Arquitectura del Sistema

### Diagrama C4 - Nivel 1 (Contexto)
```plantuml
@startuml
!include C4_Context.puml

Person(admin, "Administrador", "Gestiona usuarios y configuración")
Person(redactor, "Redactor", "Crea y edita contenido")
Person(lector, "Lector", "Consulta noticias")

System(sistema, "Sistema de Gestión de Noticias", "Procesa y distribuye contenido noticioso")

System_Ext(fuentes, "Fuentes Externas", "APIs de noticias (TN, etc.)")

Rel(admin, sistema, "Configura roles/scraping")
Rel(redactor, sistema, "Gestiona artículos")
Rel(lector, sistema, "Consulta noticias")
Rel(sistema, fuentes, "Obtiene noticias via scraping")
@enduml
```

### Diagrama C4 - Nivel 2 (Contenedores)
```plantuml
@startuml
!include C4_Container.puml

System_Boundary(sistema, "Sistema de Noticias") {
    Container(webapp, "Aplicación Web", "Spring Boot", "Provee interfaz web y API REST")
    Container(db, "Base de Datos", "MySQL/H2", "Almacena artículos, usuarios y estadísticas")
    Container(scraper, "Servicio Scraping", "Java + JSoup", "Recolecta noticias automáticamente")
}

System_Ext(navegador, "Navegador Web", "Interfaz de usuario")
System_Ext(externas, "APIs Externas", "Fuentes de noticias")

Rel(navegador, webapp, "Usa", "HTTP/HTTPS")
Rel(webapp, db, "Lee/Escribe", "JDBC")
Rel(scraper, externas, "Consulta", "HTTP/HTTPS")
Rel(scraper, db, "Almacena datos", "JDBC")
@enduml
```

## Features

### Article Management
- **Complete Article Lifecycle**:
  ```mermaid
  stateDiagram-v2
      [*] --> Draft: Redactor crea
      Draft --> PendingReview: Enviar
      PendingReview --> Published: Aprobar
      PendingReview --> Rejected: Rechazar
      Rejected --> Draft: Modificar
      Published --> Archived: 30 días
  ```

- **Rich Article Properties**:
  - Title and content
  - Category classification
  - View count tracking
  - Publication date
  - Source attribution
  - Image support
  - URL references

### Content Import & Scraping
- **Web Scraping Integration**:
  ```properties
  # application.properties
  scraper.sources=https://tn.com.ar,https://example-news.com
  scraper.interval=3600 # 1 hora
  scraper.timeout=5000 # 5 segundos
  scraper.categories=deportes,tecnologia,politica
  ```

### Admin Interface
- **Role-Based Dashboards**:
  | Role | Features |
  |------|----------|
  | Admin | User management, System config |
  | Publisher | Content approval, Analytics |
  | Redactor | Article creation/editing |
  | Subscriber | Personalized feed |

### Security & Authentication
- **Spring Security Integration**:
  ```java
  @Configuration
  @EnableWebSecurity
  public class SecurityConfig {
      // Configuración de seguridad
  }
  ```

## API Documentation

### REST Endpoints
| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| GET    | /api/news | Listar artículos | All |
| POST   | /api/news | Crear artículo | REDACTOR+ |
| PUT    | /api/news/{id} | Actualizar | PUBLISHER+ |
| DELETE | /api/news/{id} | Eliminar | ADMIN |

Documentación completa disponible en Swagger UI: `http://localhost:8080/swagger-ui.html`

## Technology Stack

- **Backend**:
  - Java 17
  - Spring Boot 3.1.5
  - Spring Security
  - Spring Data JPA
  - H2/MySQL Database

- **Frontend**:
  - Thymeleaf templates
  - Bootstrap 5.1.3
  - Chart.js para estadísticas
  - Responsive design

- **Testing & Quality**:
  - JUnit 5
  - Mockito
  - Codecov integration
  - SonarQube analysis

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Git

### Installation

1. Clone the repository:
```bash
git clone https://github.com/brunoiemma/news-management-system.git
cd news-management-system
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Access the application:
- Main site: `http://localhost:8080`
- Admin panel: `http://localhost:8080/admin`
- API docs: `http://localhost:8080/swagger-ui.html`

### Default Credentials
```
Admin:
  Username: admin
  Password: admin

Publisher:
  Username: publisher
  Password: publisher

Redactor:
  Username: redactor
  Password: redactor
```

## Project Structure
```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/          # Configuraciones
│   │   ├── controller/      # Endpoints REST y MVC
│   │   ├── model/          # Entidades JPA
│   │   ├── repository/     # Acceso a datos
│   │   ├── service/        # Lógica de negocio
│   │   └── util/           # Utilidades
│   └── resources/
│       ├── templates/      # Vistas Thymeleaf
│       ├── static/         # Assets
│       └── application.properties
```

## Screenshots

### Admin Dashboard
![Admin Dashboard](docs/screenshots/admin-dashboard.png)
- Gestión de usuarios
- Estadísticas en tiempo real
- Configuración del sistema

### Publisher View
![Publisher Interface](docs/screenshots/publisher-view.png)
- Aprobación de contenido
- Análisis de métricas
- Gestión de categorías

### Redactor Interface
![Redactor Dashboard](docs/screenshots/redactor-dashboard.png)
- Editor de artículos
- Vista previa
- Sistema de borradores

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Seguir estándares de código Java
- Documentar nuevas funcionalidades
- Incluir tests unitarios
- Mantener cobertura > 80%

## License

This project is licensed under the MIT License - see the LICENSE file for details.
