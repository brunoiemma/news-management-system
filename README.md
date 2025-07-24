# News Management System

A robust Spring Boot application for managing news articles with a complete content workflow system. This system allows administrators to create, edit, and manage articles through different status stages (Draft, In Review, Published, Deleted) while providing a public interface for viewing published content.

## Features

### Article Management
- **Complete Article Lifecycle**: Articles move through multiple states:
  - `DRAFT`: Initial state for new articles
  - `IN_REVIEW`: Articles under editorial review
  - `PUBLISHED`: Live articles visible to the public
  - `DELETED`: Archived/removed articles

- **Rich Article Properties**:
  - Title and content
  - Category classification
  - View count tracking
  - Publication date
  - Source attribution
  - Image support
  - URL references

### Content Import
- **Web Scraping Integration**:
  - Import articles from any URL
  - Automatic extraction of:
    - Article title
    - Main content
    - Featured images
    - Category detection
    - Source attribution
  - Content cleanup and formatting

### Admin Interface
- **Article Management Dashboard**:
  - Status-based filtering
  - Quick status updates
  - View count tracking
  - Publication date management
  - Bulk article importing

- **Article Actions**:
  - Create new articles
  - Edit existing content
  - Update article status
  - Delete articles
  - Import from URLs

### Security
- **Admin Authentication**:
  - Secure login system
  - Role-based access control
  - Protected admin routes

## Technology Stack

- **Backend**:
  - Java 17
  - Spring Boot 3.1.5
  - Spring Security
  - Spring Data JPA
  - H2 Database

- **Frontend**:
  - Thymeleaf templates
  - Bootstrap 5.1.3
  - Responsive design

- **Web Scraping**:
  - JSoup for HTML parsing
  - Automatic content extraction
  - Image detection
  - Category inference

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
- Default admin credentials:
  - Username: `admin`
  - Password: `admin`

## Usage

### Managing Articles

1. **Creating Articles**:
   - Log into admin panel
   - Click "Create New Article"
   - Fill in article details
   - Articles start in DRAFT status

2. **Importing Articles**:
   - Go to "Buscar Noticias"
   - Enter article URL
   - System will automatically extract content
   - Review and edit as needed

3. **Publishing Workflow**:
   - Articles start as DRAFT
   - Move to IN_REVIEW when ready for review
   - Set to PUBLISHED to make public
   - Use DELETED for archived content

4. **Filtering Articles**:
   - Use status dropdown in admin panel
   - Filter by DRAFT/IN_REVIEW/PUBLISHED/DELETED
   - View all articles or specific status

## Development

### Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/example/demo/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       └── util/
│   └── resources/
│       ├── templates/
│       ├── static/
│       ├── application.properties
│       ├── schema.sql
│       └── data.sql
```

### Key Components

- `ArticleStatus.java`: Enum defining article states
- `Article.java`: Main entity model
- `NewsService.java`: Core business logic
- `WebScraper.java`: URL content extraction
- `AdminController.java`: Admin panel endpoints
- `SecurityConfig.java`: Authentication setup

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
