#!/bin/bash

# Job Email Automation System - Setup Script
# This script helps you set up the system quickly

echo "========================================="
echo "Job Email Automation System - Setup"
echo "========================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check Java installation
echo "Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo -e "${GREEN}✓ Java found: $JAVA_VERSION${NC}"
else
    echo -e "${RED}✗ Java not found. Please install Java 17 or higher.${NC}"
    exit 1
fi

# Check Maven installation
echo "Checking Maven installation..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    echo -e "${GREEN}✓ Maven found: $MVN_VERSION${NC}"
else
    echo -e "${RED}✗ Maven not found. Please install Maven 3.6 or higher.${NC}"
    exit 1
fi

# Check MySQL installation
echo "Checking MySQL installation..."
if command -v mysql &> /dev/null; then
    echo -e "${GREEN}✓ MySQL found${NC}"
else
    echo -e "${YELLOW}⚠ MySQL not found. Please install MySQL 8.0 or higher.${NC}"
fi

echo ""
echo "========================================="
echo "Configuration Setup"
echo "========================================="
echo ""

# Get user inputs
read -p "Enter MySQL username [root]: " DB_USER
DB_USER=${DB_USER:-root}

read -sp "Enter MySQL password: " DB_PASSWORD
echo ""

read -p "Enter your Gmail address: " GMAIL_ADDRESS

read -sp "Enter Gmail App Password (16 characters): " GMAIL_APP_PASSWORD
echo ""

read -p "Enter your full name: " SENDER_NAME

read -p "Enter your phone number: " SENDER_PHONE

read -p "Enter your LinkedIn profile URL: " SENDER_LINKEDIN

read -p "Enter path to your resume PDF: " RESUME_PATH

# Validate resume file
if [ ! -f "$RESUME_PATH" ]; then
    echo -e "${YELLOW}⚠ Resume file not found at: $RESUME_PATH${NC}"
    echo "Please make sure the file exists before running the application."
fi

# Create database
echo ""
echo "Creating database..."
mysql -u "$DB_USER" -p"$DB_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS job_automation;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Database created successfully${NC}"
else
    echo -e "${YELLOW}⚠ Could not create database. Please create it manually:${NC}"
    echo "  mysql -u root -p"
    echo "  CREATE DATABASE job_automation;"
fi

# Create application.properties
echo ""
echo "Creating configuration file..."
cat > src/main/resources/application.properties << EOF
# ============================================
# Job Email Automation System Configuration
# ============================================

# Application Configuration
spring.application.name=job-email-automation
server.port=8080

# ============================================
# Database Configuration (MySQL)
# ============================================
spring.datasource.url=jdbc:mysql://localhost:3306/job_automation?createDatabaseIfNotExist=true
spring.datasource.username=$DB_USER
spring.datasource.password=$DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# ============================================
# Gmail SMTP Configuration
# ============================================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=$GMAIL_ADDRESS
spring.mail.password=$GMAIL_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# ============================================
# Application Specific Configuration
# ============================================
app.resume.path=$RESUME_PATH
app.sender.name=$SENDER_NAME
app.sender.phone=$SENDER_PHONE
app.sender.linkedin=$SENDER_LINKEDIN
app.email.daily.limit=10

# ============================================
# Logging Configuration
# ============================================
logging.level.root=INFO
logging.level.com.jobautomation=INFO
logging.level.org.springframework.mail=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# ============================================
# Scheduler Configuration
# ============================================
spring.task.scheduling.pool.size=2
EOF

echo -e "${GREEN}✓ Configuration file created${NC}"

# Build the project
echo ""
echo "========================================="
echo "Building the project..."
echo "========================================="
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ Build successful!${NC}"
    echo ""
    echo "========================================="
    echo "Setup Complete!"
    echo "========================================="
    echo ""
    echo "To run the application:"
    echo "  mvn spring-boot:run"
    echo ""
    echo "Or run the JAR file:"
    echo "  java -jar target/job-email-automation-1.0.0.jar"
    echo ""
    echo "Access the application at:"
    echo "  http://localhost:8080"
    echo ""
    echo "Open the admin dashboard:"
    echo "  Open admin-dashboard.html in your browser"
    echo ""
    echo "========================================="
else
    echo -e "${RED}✗ Build failed. Please check the errors above.${NC}"
    exit 1
fi
