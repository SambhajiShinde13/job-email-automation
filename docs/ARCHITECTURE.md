# System Architecture Diagram

## High-Level Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         USER INTERACTION                                 │
│                                                                           │
│  ┌─────────────────┐                    ┌──────────────────┐            │
│  │  Admin Dashboard│◄──────────────────►│   REST API       │            │
│  │  (HTML/JS)      │    HTTP Requests   │   (Port 8080)    │            │
│  └─────────────────┘                    └──────────────────┘            │
│                                                                           │
└───────────────────────────────────┬─────────────────────────────────────┘
                                    │
                                    │ API Calls
                                    │
┌───────────────────────────────────▼─────────────────────────────────────┐
│                    SPRING BOOT APPLICATION                                │
│                                                                           │
│  ┌────────────────────────────────────────────────────────────┐          │
│  │                    CONTROLLER LAYER                        │          │
│  │  ┌──────────────────────────────────────────────┐          │          │
│  │  │      HRContactController.java                │          │          │
│  │  │  • POST /api/contacts (Add contact)          │          │          │
│  │  │  • GET  /api/contacts (List all)             │          │          │
│  │  │  • POST /api/contacts/bulk (Bulk add)        │          │          │
│  │  │  • GET  /api/contacts/stats (Statistics)     │          │          │
│  │  └──────────────────────────────────────────────┘          │          │
│  └────────────────────────┬───────────────────────────────────┘          │
│                           │                                               │
│  ┌────────────────────────▼───────────────────────────────────┐          │
│  │                    SERVICE LAYER                           │          │
│  │  ┌─────────────────────────────────────────────┐           │          │
│  │  │  HRContactService.java                      │           │          │
│  │  │  • Business logic                           │           │          │
│  │  │  • Validation                               │           │          │
│  │  │  • Statistics                               │           │          │
│  │  └─────────────────────────────────────────────┘           │          │
│  │  ┌─────────────────────────────────────────────┐           │          │
│  │  │  EmailService.java                          │           │          │
│  │  │  • Email generation                         │           │          │
│  │  │  • SMTP integration                         │           │          │
│  │  │  • Resume attachment                        │           │          │
│  │  └─────────────────────────────────────────────┘           │          │
│  │  ┌─────────────────────────────────────────────┐           │          │
│  │  │  EmailSchedulerService.java ⏰              │           │          │
│  │  │  • Cron: 0 30 9 * * MON-FRI                │           │          │
│  │  │  • Fetch active contacts                    │           │          │
│  │  │  • Send emails (max 10/day)                │           │          │
│  │  │  • Mark as processed                        │           │          │
│  │  └─────────────────────────────────────────────┘           │          │
│  └────────────────────────┬───────────────────────────────────┘          │
│                           │                                               │
│  ┌────────────────────────▼───────────────────────────────────┐          │
│  │                  REPOSITORY LAYER                          │          │
│  │  ┌─────────────────────────────────────────────┐           │          │
│  │  │  HRContactRepository.java                   │           │          │
│  │  │  • Spring Data JPA                          │           │          │
│  │  │  • Database queries                         │           │          │
│  │  │  • CRUD operations                          │           │          │
│  │  └─────────────────────────────────────────────┘           │          │
│  └────────────────────────┬───────────────────────────────────┘          │
│                           │                                               │
└───────────────────────────┼───────────────────────────────────────────────┘
                            │ JDBC
                            │
┌───────────────────────────▼───────────────────────────────────────────────┐
│                         DATABASE LAYER                                    │
│  ┌─────────────────────────────────────────────────────────┐             │
│  │                  MySQL Database                         │             │
│  │                                                          │             │
│  │  Table: hr_contacts                                     │             │
│  │  ┌──────────────────────────────────────────┐          │             │
│  │  │ id (PK)         │ BIGINT                 │          │             │
│  │  │ hr_name         │ VARCHAR(255)           │          │             │
│  │  │ email           │ VARCHAR(255) UNIQUE    │          │             │
│  │  │ company         │ VARCHAR(255)           │          │             │
│  │  │ position        │ VARCHAR(255)           │          │             │
│  │  │ active          │ BOOLEAN                │          │             │
│  │  │ created_at      │ DATETIME               │          │             │
│  │  │ email_sent_at   │ DATETIME               │          │             │
│  │  │ notes           │ VARCHAR(500)           │          │             │
│  │  └──────────────────────────────────────────┘          │             │
│  └─────────────────────────────────────────────────────────┘             │
└───────────────────────────────────────────────────────────────────────────┘
                            │
                            │ External Service
                            │
┌───────────────────────────▼───────────────────────────────────────────────┐
│                      EXTERNAL SERVICES                                    │
│  ┌─────────────────────────────────────────────────────────┐             │
│  │              Gmail SMTP (smtp.gmail.com:587)            │             │
│  │  • Sends personalized emails                            │             │
│  │  • Attaches resume PDF                                  │             │
│  │  • TLS/STARTTLS encryption                              │             │
│  └─────────────────────────────────────────────────────────┘             │
└───────────────────────────────────────────────────────────────────────────┘
```

## Data Flow Sequence

### 1. User Adds HR Contact

```
User (Dashboard)
    │
    │ HTTP POST /api/contacts
    ▼
HRContactController
    │
    │ Validate & delegate
    ▼
HRContactService
    │
    │ Check duplicate
    │ Create entity
    ▼
HRContactRepository
    │
    │ SQL INSERT
    ▼
MySQL Database
    │
    │ Return saved entity
    ▼
User sees success message
```

### 2. Scheduled Email Sending (Daily at 9:30 AM)

```
⏰ Cron Trigger (9:30 AM Weekdays)
    │
    ▼
EmailSchedulerService
    │
    ├─► Check day (Mon-Fri only)
    │
    ├─► Query active contacts
    │   │
    │   ▼
    │   HRContactRepository.findActiveContacts(limit=10)
    │   │
    │   ▼
    │   Returns: [Contact1, Contact2, ... Contact10]
    │
    ├─► For each contact:
    │   │
    │   ├─► EmailService.sendResumeEmail(contact)
    │   │   │
    │   │   ├─► Build personalized email
    │   │   │   • Subject: "Application for {position} at {company}"
    │   │   │   • Body: HTML with HR name, company, position
    │   │   │
    │   │   ├─► Attach resume PDF
    │   │   │
    │   │   ├─► Send via Gmail SMTP
    │   │   │   │
    │   │   │   ▼
    │   │   │   smtp.gmail.com:587
    │   │   │   │
    │   │   │   ▼
    │   │   │   Email delivered to HR
    │   │   │
    │   │   └─► Return success/failure
    │   │
    │   ├─► Update contact
    │   │   • active = false
    │   │   • email_sent_at = NOW()
    │   │
    │   ├─► Save to database
    │   │
    │   └─► Wait 7 seconds (rate limiting)
    │
    └─► Log summary
        • Emails sent: X
        • Emails failed: Y
        • Remaining active: Z
```

### 3. User Views Statistics

```
User (Dashboard)
    │
    │ HTTP GET /api/contacts/stats
    ▼
HRContactController
    │
    │ Delegate to service
    ▼
HRContactService
    │
    │ Count queries:
    │ • COUNT(*) WHERE active = true
    │ • COUNT(*) WHERE active = false
    │ • COUNT(*)
    ▼
HRContactRepository
    │
    │ Execute SQL queries
    ▼
MySQL Database
    │
    │ Return counts
    ▼
User sees:
• Total: 100
• Active: 60
• Processed: 40
```

## Component Responsibilities

### Frontend Layer
**File**: `admin-dashboard.html`
- User interface
- Form validation
- API communication
- Real-time updates

### Controller Layer
**File**: `HRContactController.java`
- HTTP request handling
- Input validation
- Response formatting
- Error handling

### Service Layer
**Files**: 
- `HRContactService.java` - Business logic
- `EmailService.java` - Email operations
- `EmailSchedulerService.java` - Scheduling

Responsibilities:
- Business rules enforcement
- Transaction management
- Email personalization
- Scheduling logic

### Repository Layer
**File**: `HRContactRepository.java`
- Database operations
- Query execution
- Data persistence
- Transaction handling

### Model Layer
**File**: `HRContact.java`
- Data structure
- JPA annotations
- Database mapping
- Validation rules

### Configuration
**File**: `application.properties`
- Database connection
- SMTP settings
- Application properties
- Logging configuration

## Deployment Architecture

### Local Development
```
┌─────────────────┐
│  Your Computer  │
│                 │
│  ┣━ MySQL       │
│  ┗━ Spring Boot │
│     (Port 8080) │
└─────────────────┘
```

### AWS Production
```
┌──────────────────────────────────┐
│         AWS Cloud                │
│                                  │
│  ┌────────────────────────────┐ │
│  │      EC2 Instance          │ │
│  │   (Ubuntu 22.04)           │ │
│  │                            │ │
│  │  ┣━ MySQL 8.0              │ │
│  │  ┗━ Spring Boot App        │ │
│  │     • systemd service      │ │
│  │     • Auto-start on boot   │ │
│  │     • Port 8080            │ │
│  │                            │ │
│  │  Security Group:           │ │
│  │  • SSH (22)               │ │
│  │  • HTTP (8080)            │ │
│  └────────────────────────────┘ │
│                                  │
│  Elastic IP (Optional)           │
│  Monitoring (CloudWatch)         │
└──────────────────────────────────┘
```

### Docker Deployment
```
┌────────────────────────────────┐
│    Docker Environment          │
│                                │
│  ┌──────────┐   ┌──────────┐  │
│  │  MySQL   │   │   App    │  │
│  │Container │◄─►│Container │  │
│  │          │   │          │  │
│  │Port 3306 │   │Port 8080 │  │
│  └──────────┘   └──────────┘  │
│                                │
│  Docker Network: bridge        │
│  Volume: mysql_data            │
└────────────────────────────────┘
```

## Security Architecture

```
┌───────────────────────────────────────┐
│         Security Layers               │
│                                       │
│  1. Application Level                │
│     ┣━ Input validation              │
│     ┣━ SQL injection prevention      │
│     ┗━ Transaction management        │
│                                       │
│  2. Configuration Level              │
│     ┣━ Environment variables         │
│     ┣━ .gitignore (credentials)      │
│     ┗━ App passwords (not regular)   │
│                                       │
│  3. Network Level                    │
│     ┣━ Firewall rules (UFW)          │
│     ┣━ Security groups (AWS)         │
│     ┗━ TLS/SSL for SMTP              │
│                                       │
│  4. Email Level                      │
│     ┣━ Rate limiting (10/day)        │
│     ┣━ Delays (7 seconds)            │
│     ┗━ No duplicate sends            │
│                                       │
└───────────────────────────────────────┘
```

## Monitoring & Logging

```
┌─────────────────────────────────────────┐
│          Monitoring Stack               │
│                                         │
│  Application Logs                       │
│  ┣━ Console output                      │
│  ┣━ journalctl (systemd)                │
│  ┗━ Log files                           │
│                                         │
│  Metrics                                │
│  ┣━ Emails sent (daily)                 │
│  ┣━ Success rate                        │
│  ┣━ Active contacts                     │
│  ┗━ Database size                       │
│                                         │
│  Health Checks                          │
│  ┣━ /api/contacts/health                │
│  ┣━ MySQL status                        │
│  ┗━ System resources                    │
│                                         │
└─────────────────────────────────────────┘
```

## Technology Stack

```
┌──────────────────────────────────────────┐
│         Technology Layers                │
│                                          │
│  Frontend                                │
│  ┗━ HTML5 + CSS3 + Vanilla JavaScript    │
│                                          │
│  Backend Framework                       │
│  ┗━ Spring Boot 3.2.1                    │
│                                          │
│  Language                                │
│  ┗━ Java 17 (LTS)                        │
│                                          │
│  Database                                │
│  ┗━ MySQL 8.0                            │
│                                          │
│  ORM                                     │
│  ┗━ Spring Data JPA (Hibernate)          │
│                                          │
│  Build Tool                              │
│  ┗━ Maven 3.6+                           │
│                                          │
│  Email                                   │
│  ┗━ JavaMail + Gmail SMTP                │
│                                          │
│  Scheduling                              │
│  ┗━ Spring Task Scheduler (Cron)         │
│                                          │
│  Deployment                              │
│  ┣━ AWS EC2                              │
│  ┣━ Docker                               │
│  ┗━ systemd service                      │
│                                          │
└──────────────────────────────────────────┘
```
