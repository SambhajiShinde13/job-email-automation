# 📧 Daily Job Email Automation System

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

An intelligent automation system that sends personalized resume emails to HR contacts every weekday at 9:30 AM. Built with Spring Boot, this system helps job seekers automate their daily job applications while maintaining professional standards.

## 🎯 Features

- ✅ **Automated Daily Emails**: Sends emails every weekday at 9:30 AM
- ✅ **Smart Scheduling**: Processes up to 10 contacts per day (Gmail safe limit)
- ✅ **Personalized Emails**: Custom email for each company and position
- ✅ **No Duplicate Sends**: Marks contacts as inactive after sending
- ✅ **REST API**: Full CRUD operations for managing HR contacts
- ✅ **Admin Dashboard**: Beautiful web interface to manage contacts
- ✅ **AWS Ready**: Designed to run 24/7 on AWS EC2
- ✅ **Safe & Professional**: Follows email best practices

## 🏗️ System Architecture

```
┌─────────────────┐
│   Admin UI      │
│  (Dashboard)    │
└────────┬────────┘
         │
         │ REST API
         ▼
┌─────────────────────┐
│   Spring Boot App   │
│  ┌───────────────┐  │
│  │  Scheduler    │  │ ──► Runs daily at 9:30 AM
│  └───────────────┘  │
│  ┌───────────────┐  │
│  │ Email Service │  │ ──► Sends personalized emails
│  └───────────────┘  │
│  ┌───────────────┐  │
│  │   Database    │  │ ──► Stores HR contacts
│  └───────────────┘  │
└─────────────────────┘
         │
         ▼
    Gmail SMTP
```

## 📋 Prerequisites

Before you begin, ensure you have:

- ☑️ Java 17 or higher
- ☑️ Maven 3.6+
- ☑️ MySQL 8.0+
- ☑️ Gmail account (for sending emails)
- ☑️ Gmail App Password (not regular password)

## 🚀 Quick Start Guide

### Step 1: Clone the Repository

```bash
git clone <your-repo-url>
cd job-email-automation
```

### Step 2: Set Up MySQL Database

```sql
CREATE DATABASE job_automation;
```

### Step 3: Configure Gmail App Password

1. Go to: https://myaccount.google.com/apppasswords
2. Sign in to your Google account
3. Click "Select app" → Choose "Mail"
4. Click "Select device" → Choose "Other"
5. Enter "Job Automation" → Click "Generate"
6. Copy the 16-character password (remove spaces)

### Step 4: Update Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

# Gmail
spring.mail.username=your_email@gmail.com
spring.mail.password=your_16_char_app_password

# Resume Path (update to your resume location)
app.resume.path=/path/to/your/resume.pdf

# Your Details
app.sender.name=Your Name
app.sender.phone=+91-9876543210
app.sender.linkedin=https://linkedin.com/in/yourprofile
```

### Step 5: Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on: `http://localhost:8080`

### Step 6: Open Admin Dashboard

Open in browser: `file:///path/to/admin-dashboard.html`

Or serve it via any web server.

## 📁 Project Structure

```
job-email-automation/
├── src/
│   └── main/
│       └── java/com/jobautomation/
│           ├── JobEmailAutomationApplication.java   # Main class
│           ├── controller/
│           │   └── HRContactController.java         # REST API
│           ├── model/
│           │   └── HRContact.java                   # Entity
│           ├── repository/
│           │   └── HRContactRepository.java         # Database layer
│           └── service/
│               ├── EmailService.java                # Email logic
│               ├── EmailSchedulerService.java       # Scheduler
│               └── HRContactService.java            # Business logic
├── src/main/resources/
│   └── application.properties                       # Configuration
├── admin-dashboard.html                             # Web UI
├── pom.xml                                          # Maven config
└── README.md                                        # This file
```

## 🔌 API Endpoints

### HR Contacts Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/contacts` | Add new HR contact |
| POST | `/api/contacts/bulk` | Add multiple contacts |
| GET | `/api/contacts` | Get all contacts |
| GET | `/api/contacts/active` | Get active contacts only |
| GET | `/api/contacts/processed` | Get processed contacts |
| GET | `/api/contacts/{id}` | Get contact by ID |
| PUT | `/api/contacts/{id}` | Update contact |
| DELETE | `/api/contacts/{id}` | Delete contact |
| POST | `/api/contacts/{id}/reactivate` | Reactivate contact |
| GET | `/api/contacts/stats` | Get statistics |
| POST | `/api/contacts/send-emails` | Manually trigger emails |
| GET | `/api/contacts/health` | Health check |

### Example: Add New Contact

```bash
curl -X POST http://localhost:8080/api/contacts \
  -H "Content-Type: application/json" \
  -d '{
    "hrName": "John Smith",
    "email": "careers@google.com",
    "company": "Google",
    "position": "Java Developer",
    "active": true
  }'
```

### Example: Get Statistics

```bash
curl http://localhost:8080/api/contacts/stats
```

Response:
```json
{
  "totalContacts": 50,
  "activeContacts": 30,
  "processedContacts": 20
}
```

## ⏰ Scheduler Configuration

The system automatically sends emails every weekday at **9:30 AM IST**.

To change the schedule, edit `EmailSchedulerService.java`:

```java
@Scheduled(cron = "0 30 9 * * MON-FRI", zone = "Asia/Kolkata")
```

**Cron Format**: `second minute hour day month weekday`

Examples:
- `0 30 9 * * MON-FRI` → 9:30 AM on weekdays
- `0 0 10 * * *` → 10:00 AM daily
- `0 0 8,12,17 * * MON-FRI` → 8 AM, 12 PM, 5 PM on weekdays

## 📧 Email Template

The system sends personalized HTML emails:

**Subject**: Application for {Position} Position at {Company}

**Body**: Includes:
- Personal greeting with HR name
- Company and position mention
- Professional introduction
- Key highlights
- Contact information
- Resume attachment

## 🛡️ Safety Features

### Gmail Daily Limits
- **Consumer Gmail**: 500 emails/day
- **Our Limit**: 10 emails/day (safe buffer)
- **Delay Between Emails**: 7 seconds (appears human-like)

### Anti-Spam Protection
- ✅ No email scraping
- ✅ Only sends to manually added contacts
- ✅ No duplicate emails (one-time send)
- ✅ Professional email content
- ✅ Proper email headers
- ✅ Rate limiting

### Database Protection
- ✅ Unique email constraint
- ✅ Transaction management
- ✅ Automatic timestamps
- ✅ Soft delete (active/inactive)

## ☁️ AWS Deployment

### Prerequisites
- AWS EC2 instance (t2.micro is sufficient)
- Ubuntu 22.04 LTS
- Security group allowing port 8080

### Deployment Steps

1. **Connect to EC2**
```bash
ssh -i your-key.pem ubuntu@your-ec2-ip
```

2. **Install Java and MySQL**
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Install MySQL
sudo apt install mysql-server -y
sudo mysql_secure_installation
```

3. **Set Up Database**
```bash
sudo mysql -u root -p
CREATE DATABASE job_automation;
CREATE USER 'jobapp'@'localhost' IDENTIFIED BY 'strong_password';
GRANT ALL PRIVILEGES ON job_automation.* TO 'jobapp'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

4. **Upload Application**
```bash
# Upload JAR file
scp -i your-key.pem target/job-email-automation-1.0.0.jar ubuntu@your-ec2-ip:~/

# Upload resume
scp -i your-key.pem your-resume.pdf ubuntu@your-ec2-ip:~/resume/
```

5. **Create Systemd Service**
```bash
sudo nano /etc/systemd/system/job-email-automation.service
```

Add this content:
```ini
[Unit]
Description=Job Email Automation Service
After=mysql.service

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu
ExecStart=/usr/bin/java -jar /home/ubuntu/job-email-automation-1.0.0.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

6. **Start Service**
```bash
sudo systemctl daemon-reload
sudo systemctl enable job-email-automation
sudo systemctl start job-email-automation
sudo systemctl status job-email-automation
```

7. **View Logs**
```bash
sudo journalctl -u job-email-automation -f
```

## 📊 Monitoring & Logs

### View Application Logs
```bash
# Real-time logs
tail -f logs/application.log

# Last 100 lines
tail -100 logs/application.log

# Search for errors
grep ERROR logs/application.log
```

### Monitor Email Sending
```bash
# Check scheduler runs
grep "EMAIL AUTOMATION STARTED" logs/application.log

# Check success rate
grep "Email sent successfully" logs/application.log | wc -l

# Check failures
grep "Failed to send email" logs/application.log
```

## 🔧 Troubleshooting

### Problem: Emails Not Sending

**Solution 1**: Check Gmail Settings
```bash
# Verify App Password is correct
# Try logging in manually at gmail.com
```

**Solution 2**: Check Firewall
```bash
# Allow SMTP port
sudo ufw allow 587/tcp
```

**Solution 3**: Test SMTP Connection
```bash
telnet smtp.gmail.com 587
```

### Problem: Database Connection Failed

```bash
# Check MySQL is running
sudo systemctl status mysql

# Check database exists
mysql -u root -p -e "SHOW DATABASES;"

# Check user privileges
mysql -u root -p -e "SHOW GRANTS FOR 'jobapp'@'localhost';"
```

### Problem: Resume Not Found

```bash
# Check file exists
ls -la /path/to/resume.pdf

# Check permissions
chmod 644 /path/to/resume.pdf
```

## 📈 Performance Optimization

### Database Indexing
```sql
ALTER TABLE hr_contacts ADD INDEX idx_active (active);
ALTER TABLE hr_contacts ADD INDEX idx_email (email);
ALTER TABLE hr_contacts ADD INDEX idx_created_at (created_at);
```

### JVM Tuning
```bash
java -Xms512m -Xmx1024m -jar job-email-automation-1.0.0.jar
```

### Connection Pooling
Add to `application.properties`:
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

## 🔐 Security Best Practices

1. **Never commit credentials** to version control
2. **Use environment variables** for sensitive data
3. **Enable SSL/TLS** for production
4. **Regular backups** of database
5. **Monitor failed login attempts**
6. **Use strong passwords** for database

### Environment Variables (Production)
```bash
export DB_PASSWORD=your_db_password
export MAIL_PASSWORD=your_app_password
export RESUME_PATH=/path/to/resume.pdf
```

Update `application.properties`:
```properties
spring.datasource.password=${DB_PASSWORD}
spring.mail.password=${MAIL_PASSWORD}
app.resume.path=${RESUME_PATH}
```

## 📝 Best Practices

### Adding HR Contacts
- ✅ Only add legitimate HR emails from career pages
- ✅ Verify company website before adding
- ✅ Add position-specific contacts
- ✅ Include notes about job posting

### Daily Maintenance
- ✅ Add 10-15 new contacts daily
- ✅ Review sent emails weekly
- ✅ Monitor bounce rates
- ✅ Update resume regularly

### Email Quality
- ✅ Keep resume updated
- ✅ Professional email format
- ✅ Clear subject lines
- ✅ Grammatically correct content

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Gmail for SMTP services
- MySQL for reliable database
- All contributors to this project

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Troubleshooting](#-troubleshooting) section
2. Search existing [GitHub Issues](https://github.com/your-repo/issues)
3. Create a new issue with detailed description

## 🎯 Roadmap

- [ ] Add support for multiple email providers
- [ ] Implement email tracking (open rate)
- [ ] Add response detection
- [ ] Create mobile app
- [ ] Add AI-powered email personalization
- [ ] Implement A/B testing for email templates

## ⭐ Star History

If this project helped you land a job, please give it a ⭐!

---

**Made with ❤️ by job seekers, for job seekers**

*Remember: This tool is for legitimate job applications only. Use responsibly and ethically.*
