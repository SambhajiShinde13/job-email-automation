# 📁 Project Structure Overview

## Complete File Structure

```
job-email-automation/
│
├── 📄 pom.xml                          # Maven configuration & dependencies
├── 📄 Dockerfile                       # Docker container configuration
├── 📄 docker-compose.yml               # Docker Compose for MySQL + App
├── 📄 .gitignore                       # Git ignore rules
├── 📄 .env.template                    # Environment variables template
├── 📄 setup.sh                         # Automated setup script
├── 📄 test-api.sh                      # API testing script
├── 📄 admin-dashboard.html             # Web-based admin interface
├── 📄 sample-hr-contacts.json          # Sample data for bulk import
│
├── 📁 docs/
│   ├── README.md                       # Complete documentation
│   ├── QUICK_START.md                  # 15-minute quick start guide
│   └── AWS_DEPLOYMENT_GUIDE.md         # AWS EC2 deployment instructions
│
└── 📁 src/
    └── 📁 main/
        ├── 📁 java/com/jobautomation/
        │   ├── 📄 JobEmailAutomationApplication.java    # Main Spring Boot app
        │   │
        │   ├── 📁 controller/
        │   │   └── HRContactController.java             # REST API endpoints
        │   │
        │   ├── 📁 model/
        │   │   └── HRContact.java                       # Database entity
        │   │
        │   ├── 📁 repository/
        │   │   └── HRContactRepository.java             # Database operations
        │   │
        │   └── 📁 service/
        │       ├── EmailService.java                    # Email sending logic
        │       ├── EmailSchedulerService.java           # Daily scheduler
        │       └── HRContactService.java                # Business logic
        │
        └── 📁 resources/
            └── application.properties                    # App configuration
```

## 📚 File Descriptions

### Root Level Files

#### `pom.xml`
Maven build configuration with all dependencies:
- Spring Boot Web, Data JPA, Mail
- MySQL Driver
- Lombok
- Testing libraries

#### `Dockerfile`
Docker container setup for the application.
- Based on OpenJDK 17
- Includes resume directory
- Health check configured
- Port 8080 exposed

#### `docker-compose.yml`
Complete stack with MySQL + Application.
- MySQL 8.0 database
- Application container
- Network configuration
- Environment variables

#### `.gitignore`
Protects sensitive files from being committed:
- `application.properties` (contains passwords)
- `.env` (contains credentials)
- `resume/` (personal documents)
- Build artifacts, IDE files

#### `.env.template`
Template for environment variables.
- Database credentials
- Gmail configuration
- Personal information
- Copy to `.env` and fill with actual values

#### `setup.sh`
Automated setup script:
- Checks Java, Maven, MySQL
- Prompts for configuration
- Creates database
- Generates `application.properties`
- Builds the project

#### `test-api.sh`
API testing script:
- Health check
- Statistics endpoint
- CRUD operations
- Bulk import test

#### `admin-dashboard.html`
Beautiful web interface:
- View statistics
- Add new contacts
- See all contacts
- Real-time updates
- Responsive design

#### `sample-hr-contacts.json`
Example HR contacts data for testing bulk import.

### Documentation Files (`docs/`)

#### `README.md`
Complete documentation covering:
- Features and architecture
- Installation guide
- API reference
- Configuration options
- Troubleshooting
- Best practices
- Security guidelines
- Performance optimization

#### `QUICK_START.md`
Get started in 15 minutes:
- Prerequisites check
- Step-by-step setup
- Testing instructions
- Troubleshooting tips
- Daily operations guide

#### `AWS_DEPLOYMENT_GUIDE.md`
Production deployment on AWS EC2:
- EC2 instance setup
- MySQL configuration
- systemd service creation
- Security hardening
- Monitoring setup
- Maintenance procedures
- Cost estimation

### Java Source Files (`src/main/java/`)

#### `JobEmailAutomationApplication.java`
Main Spring Boot application class:
- Application entry point
- Enables scheduling
- Starts embedded Tomcat server

#### `controller/HRContactController.java`
REST API controller providing endpoints:
- `POST /api/contacts` - Add contact
- `POST /api/contacts/bulk` - Bulk add
- `GET /api/contacts` - Get all
- `GET /api/contacts/active` - Active only
- `GET /api/contacts/processed` - Processed only
- `GET /api/contacts/{id}` - Get by ID
- `PUT /api/contacts/{id}` - Update
- `DELETE /api/contacts/{id}` - Delete
- `POST /api/contacts/{id}/reactivate` - Reactivate
- `GET /api/contacts/stats` - Statistics
- `POST /api/contacts/send-emails` - Manual trigger
- `GET /api/contacts/health` - Health check

#### `model/HRContact.java`
Database entity representing HR contact:
- Fields: id, hrName, email, company, position, active, createdAt, emailSentAt, notes
- JPA annotations for database mapping
- Lombok for getters/setters
- Automatic timestamp management

#### `repository/HRContactRepository.java`
Database access layer using Spring Data JPA:
- CRUD operations
- Custom queries for active/inactive contacts
- Pagination support
- Email uniqueness check

#### `service/EmailService.java`
Email sending functionality:
- Personalized HTML email generation
- Resume attachment
- Gmail SMTP integration
- Error handling
- Logging

#### `service/EmailSchedulerService.java`
Scheduled email automation:
- Runs daily at 9:30 AM (weekdays only)
- Selects up to 10 contacts
- Sends emails with delays
- Updates contact status
- Comprehensive logging
- Manual trigger support

#### `service/HRContactService.java`
Business logic layer:
- Add single/multiple contacts
- Update/delete contacts
- Reactivate processed contacts
- Get statistics
- Duplicate prevention
- Input validation

### Configuration (`src/main/resources/`)

#### `application.properties`
Application configuration:
- Server port
- Database connection
- Gmail SMTP settings
- Resume file path
- Personal information
- Daily email limit
- Logging configuration
- Scheduler timezone

## 🔄 Data Flow

```
1. User adds HR contact via Dashboard or API
                ↓
2. Contact saved to MySQL database
                ↓
3. Scheduler wakes up at 9:30 AM (weekdays)
                ↓
4. Fetches active contacts (max 10)
                ↓
5. For each contact:
   - Generate personalized email
   - Attach resume
   - Send via Gmail SMTP
   - Mark as inactive
   - Log result
   - Wait 7 seconds
                ↓
6. Generate summary report
```

## 🎯 Key Features Implementation

### 1. Automated Scheduling
**File**: `EmailSchedulerService.java`
- Spring's `@Scheduled` annotation
- Cron expression: `0 30 9 * * MON-FRI`
- Timezone: Asia/Kolkata

### 2. Email Personalization
**File**: `EmailService.java`
- Dynamic subject line
- HR name in greeting
- Company and position mentions
- Professional HTML formatting
- Resume attachment

### 3. Duplicate Prevention
**File**: `HRContact.java` & `HRContactRepository.java`
- Unique email constraint in database
- `active` flag (true/false)
- One-time send guarantee

### 4. Safety Limits
**File**: `EmailSchedulerService.java`
- Maximum 10 emails per day
- 7-second delay between emails
- Transaction management
- Error recovery

### 5. REST API
**File**: `HRContactController.java`
- Full CRUD operations
- Statistics endpoint
- Manual trigger option
- Health check

### 6. Admin Dashboard
**File**: `admin-dashboard.html`
- Real-time statistics
- Add contacts via form
- View all contacts
- Beautiful UI design
- Auto-refresh

## 🗄️ Database Schema

### Table: `hr_contacts`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| hr_name | VARCHAR(255) | NOT NULL | HR person name |
| email | VARCHAR(255) | NOT NULL, UNIQUE | Email address |
| company | VARCHAR(255) | NOT NULL | Company name |
| position | VARCHAR(255) | NOT NULL | Job position |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Is active for sending |
| created_at | DATETIME | | When contact was added |
| email_sent_at | DATETIME | | When email was sent |
| notes | VARCHAR(500) | | Additional notes |

## 🔐 Security Features

1. **Gmail App Password**: Never stores regular Gmail password
2. **Environment Variables**: Sensitive data in `.env` file
3. **Git Ignore**: Credentials excluded from version control
4. **Input Validation**: Prevents SQL injection
5. **Transaction Management**: Database consistency
6. **Error Handling**: Graceful failure recovery

## 📊 Monitoring & Logging

### Log Levels
- **INFO**: Normal operations, successful emails
- **WARN**: Skipped duplicates, no active contacts
- **ERROR**: Failed emails, database errors

### What Gets Logged
- Application startup
- Scheduler execution
- Each email sent/failed
- Statistics summary
- Database operations
- API requests

## 🚀 Deployment Options

### Option 1: Local Development
```bash
mvn spring-boot:run
```

### Option 2: JAR Deployment
```bash
java -jar job-email-automation-1.0.0.jar
```

### Option 3: Docker
```bash
docker-compose up -d
```

### Option 4: AWS EC2 (Production)
See `AWS_DEPLOYMENT_GUIDE.md`

## 🛠️ Customization Points

### Change Schedule
**File**: `EmailSchedulerService.java`
```java
@Scheduled(cron = "0 30 9 * * MON-FRI")  // Modify this
```

### Change Daily Limit
**File**: `application.properties`
```properties
app.email.daily.limit=10  # Change this number
```

### Customize Email Template
**File**: `EmailService.java`
```java
private String buildEmailBody(HRContact contact) {
    // Modify email HTML here
}
```

### Change Database
**File**: `application.properties`
```properties
# Can use PostgreSQL, H2, or other JPA-supported databases
```

## 📈 Scaling Considerations

### Current Capacity
- **Contacts**: Unlimited in database
- **Daily Emails**: 10 (configurable)
- **Memory**: ~200MB
- **CPU**: Minimal

### To Scale Up:
1. Increase `app.email.daily.limit`
2. Use multiple Gmail accounts
3. Add more server resources
4. Implement email queue system
5. Add load balancer

## 🧪 Testing

### Manual Testing
```bash
# Use test-api.sh
./test-api.sh
```

### API Testing
```bash
# Use curl commands (see README.md)
curl http://localhost:8080/api/contacts/health
```

### Integration Testing
Add JUnit tests in `src/test/java/`

## 📞 Support & Maintenance

### Daily Tasks
- Add 10-15 new HR contacts
- Check logs for errors
- Monitor statistics

### Weekly Tasks
- Review sent emails count
- Backup database
- Update resume if needed

### Monthly Tasks
- Clean up old logs
- Review success rate
- Optimize contact list

## 🎓 Learning Resources

### Technologies Used
- **Spring Boot**: https://spring.io/projects/spring-boot
- **Spring Scheduler**: https://spring.io/guides/gs/scheduling-tasks
- **JavaMail**: https://javaee.github.io/javamail/
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **MySQL**: https://dev.mysql.com/doc/

### Concepts Applied
- Dependency Injection
- RESTful APIs
- Scheduled Tasks
- Email Automation
- Database ORM
- Docker Containerization

## ✅ Quality Checklist

Before deploying:
- [ ] All credentials configured
- [ ] Resume file accessible
- [ ] Database created
- [ ] Gmail App Password obtained
- [ ] Test email sent successfully
- [ ] API endpoints tested
- [ ] Dashboard loading correctly
- [ ] Logs showing no errors
- [ ] Scheduler working
- [ ] Documentation read

## 🎉 Success Metrics

After 30 days of operation:
- ✅ 200-300 emails sent
- ✅ 5-10 HR responses
- ✅ 2-5 interview calls
- ✅ 0 system failures
- ✅ 100% uptime

## 🤝 Contributing

To contribute to this project:
1. Fork the repository
2. Create feature branch
3. Make changes
4. Test thoroughly
5. Submit pull request

## 📄 License

This project is open source under MIT License.

## 🙏 Credits

Built with:
- Spring Boot Framework
- MySQL Database
- Gmail SMTP
- Love for automation ❤️

---

**Happy Job Hunting! 🚀**

This system is designed to help you focus on what matters: preparing for interviews and improving your skills, while it handles the repetitive task of sending applications.
