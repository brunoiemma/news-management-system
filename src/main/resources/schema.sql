-- Drop tables if they exist
DROP TABLE IF EXISTS user_permissions;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create user_permissions table
CREATE TABLE user_permissions (
    user_id BIGINT NOT NULL,
    permission VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, permission),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create article table
CREATE TABLE article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    author VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    image_url VARCHAR(255),
    source_url VARCHAR(255),
    source VARCHAR(255),
    view_count INT DEFAULT 0,
    publish_date TIMESTAMP,
    submission_date TIMESTAMP,
    last_modified TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    summary VARCHAR(500),
    FOREIGN KEY (author) REFERENCES users(username)
);

-- Create visitor_stats table
DROP TABLE IF EXISTS visitor_stats;
CREATE TABLE visitor_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL,
    page VARCHAR(255) NOT NULL,
    visit_date TIMESTAMP NOT NULL
);

-- Create indexes
CREATE INDEX idx_article_status ON article(status);
CREATE INDEX idx_article_category ON article(category);
CREATE INDEX idx_article_author ON article(author);
CREATE INDEX idx_article_publish_date ON article(publish_date);
CREATE INDEX idx_user_role ON users(role);
CREATE INDEX idx_user_active ON users(active);
CREATE INDEX idx_visitor_stats_date ON visitor_stats(visit_date);
