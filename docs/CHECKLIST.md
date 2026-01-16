# 📋 Complete Setup Checklist

## Pre-Installation Checklist

### System Requirements
- [ ] Operating System: Windows/Mac/Linux (or AWS EC2 with Ubuntu)
- [ ] RAM: Minimum 2GB (4GB recommended)
- [ ] Disk Space: Minimum 1GB free
- [ ] Internet Connection: Stable broadband

### Software Requirements
- [ ] Java 17 or higher installed
  ```bash
  java -version
  # Should show: java version "17" or higher
  ```
- [ ] Maven 3.6+ installed
  ```bash
  mvn -version
  # Should show: Apache Maven 3.6.x or higher
  ```
- [ ] MySQL 8.0+ installed and running
  ```bash
  mysql --version
  sudo systemctl status mysql  # Linux
  # Or check Services on Windows
  ```
- [ ] Git installed (optional, for cloning)
  ```bash
  git --version
  ```

### Account Requirements
- [ ] Gmail account created
- [ ] 2-Factor Authentication enabled on Gmail
- [ ] Gmail App Password generated (NOT regular password)
  - Visit: https://myaccount.google.com/apppasswords
  - Save the 16-character password securely

### Document Requirements
- [ ] Resume in PDF format
- [ ] Resume file size < 5MB (Gmail attachment limit)
- [ ] Resume professionally formatted
- [ ] Resume saved in accessible location

---

## Installation Checklist

### Step 1: Project Setup
- [ ] Download/clone the project
- [ ] Extract to desired location
- [ ] Navigate to project directory
  ```bash
  cd job-email-automation
  ```

### Step 2: Database Setup
- [ ] MySQL server running
- [ ] Create database
  ```sql
  CREATE DATABASE job_automation;
  ```
- [ ] Create MySQL user (optional but recommended)
  ```sql
  CREATE USER 'jobapp'@'localhost' IDENTIFIED BY 'StrongPassword123!';
  GRANT ALL PRIVILEGES ON job_automation.* TO 'jobapp'@'localhost';
  FLUSH PRIVILEGES;
  ```
- [ ] Test database connection
  ```bash
  mysql -u jobapp -p job_automation
  ```

### Step 3: Configuration
- [ ] Copy resume to accessible location
- [ ] Note down resume full path
- [ ] Edit `src/main/resources/application.properties`
- [ ] Update database credentials
  ```properties
  spring.datasource.username=jobapp
  spring.datasource.password=StrongPassword123!
  ```
- [ ] Update Gmail credentials
  ```properties
  spring.mail.username=your_email@gmail.com
  spring.mail.password=your_16_char_app_password
  ```
- [ ] Update resume path
  ```properties
  app.resume.path=/full/path/to/your/resume.pdf
  ```
- [ ] Update personal information
  ```properties
  app.sender.name=Your Full Name
  app.sender.phone=+91-9876543210
  app.sender.linkedin=https://linkedin.com/in/yourprofile
  ```
- [ ] Save configuration file

### Step 4: Build Project
- [ ] Open terminal in project directory
- [ ] Run Maven clean
  ```bash
  mvn clean
  ```
- [ ] Run Maven install
  ```bash
  mvn install
  ```
- [ ] Verify build success (look for "BUILD SUCCESS")
- [ ] Check if JAR file created in `target/` directory

### Step 5: First Run
- [ ] Start the application
  ```bash
  mvn spring-boot:run
  ```
- [ ] Wait for "Started JobEmailAutomationApplication" message
- [ ] Check for any errors in console
- [ ] Application running on port 8080

---

## Testing Checklist

### Basic Functionality Tests
- [ ] Health check works
  ```bash
  curl http://localhost:8080/api/contacts/health
  ```
  Expected: `{"status":"healthy",...}`

- [ ] Statistics endpoint works
  ```bash
  curl http://localhost:8080/api/contacts/stats
  ```
  Expected: `{"totalContacts":0,...}`

- [ ] Can add a contact
  ```bash
  curl -X POST http://localhost:8080/api/contacts \
    -H "Content-Type: application/json" \
    -d '{
      "hrName": "Test HR",
      "email": "test@example.com",
      "company": "Test Company",
      "position": "Developer",
      "active": true
    }'
  ```
  Expected: Contact details with ID

- [ ] Can retrieve contacts
  ```bash
  curl http://localhost:8080/api/contacts
  ```
  Expected: Array with 1 contact

### Admin Dashboard Tests
- [ ] Open `admin-dashboard.html` in browser
- [ ] Dashboard loads correctly
- [ ] Statistics showing (Total: 1, Active: 1, Processed: 0)
- [ ] Contact list showing test contact
- [ ] Can add contact via form
- [ ] Form validation working
- [ ] Success message appears after adding

### Email Sending Test
- [ ] Add a test contact with YOUR email
- [ ] Manually trigger email send
  ```bash
  curl -X POST http://localhost:8080/api/contacts/send-emails
  ```
- [ ] Check application logs for "Email sent successfully"
- [ ] Check your email inbox
- [ ] Verify email received with:
  - Correct subject line
  - Personalized content
  - Resume attached
  - Professional formatting

### Database Tests
- [ ] Log into MySQL
  ```bash
  mysql -u jobapp -p job_automation
  ```
- [ ] Check table created
  ```sql
  SHOW TABLES;
  ```
  Expected: `hr_contacts` table exists
- [ ] View table structure
  ```sql
  DESCRIBE hr_contacts;
  ```
- [ ] View data
  ```sql
  SELECT * FROM hr_contacts;
  ```
- [ ] Data matches what you added

---

## Production Deployment Checklist (AWS EC2)

### Pre-Deployment
- [ ] AWS account created and verified
- [ ] Credit card added (free tier available)
- [ ] Basic AWS knowledge or willingness to learn
- [ ] SSH key pair created or available

### EC2 Instance Setup
- [ ] EC2 instance launched (t2.micro)
- [ ] Ubuntu 22.04 LTS selected
- [ ] Security group configured:
  - [ ] SSH (port 22) from your IP
  - [ ] Custom TCP (port 8080) from anywhere
- [ ] Key pair downloaded and secured
- [ ] Instance running and accessible
- [ ] Public IP noted down

### Server Configuration
- [ ] Connected to EC2 via SSH
  ```bash
  ssh -i your-key.pem ubuntu@YOUR_EC2_IP
  ```
- [ ] System updated
  ```bash
  sudo apt update && sudo apt upgrade -y
  ```
- [ ] Java 17 installed
  ```bash
  sudo apt install openjdk-17-jdk -y
  java -version
  ```
- [ ] MySQL installed
  ```bash
  sudo apt install mysql-server -y
  sudo mysql_secure_installation
  ```
- [ ] Database created
  ```sql
  CREATE DATABASE job_automation;
  CREATE USER 'jobapp'@'localhost' IDENTIFIED BY 'StrongPassword123!';
  GRANT ALL PRIVILEGES ON job_automation.* TO 'jobapp'@'localhost';
  ```

### Application Deployment
- [ ] JAR file uploaded to EC2
  ```bash
  scp -i your-key.pem target/job-email-automation-1.0.0.jar ubuntu@YOUR_EC2_IP:~/
  ```
- [ ] Resume uploaded to EC2
  ```bash
  ssh -i your-key.pem ubuntu@YOUR_EC2_IP "mkdir -p ~/resume"
  scp -i your-key.pem your-resume.pdf ubuntu@YOUR_EC2_IP:~/resume/
  ```
- [ ] Configuration file uploaded
  ```bash
  scp -i your-key.pem application.properties ubuntu@YOUR_EC2_IP:~/
  ```
- [ ] Configuration file updated with EC2-specific paths

### systemd Service Setup
- [ ] Service file created
  ```bash
  sudo nano /etc/systemd/system/job-email-automation.service
  ```
- [ ] Service content correct (see AWS_DEPLOYMENT_GUIDE.md)
- [ ] Service enabled
  ```bash
  sudo systemctl enable job-email-automation
  ```
- [ ] Service started
  ```bash
  sudo systemctl start job-email-automation
  ```
- [ ] Service status checked
  ```bash
  sudo systemctl status job-email-automation
  ```
  Expected: Active (running)

### Post-Deployment Verification
- [ ] Service running without errors
- [ ] Logs showing normal startup
  ```bash
  sudo journalctl -u job-email-automation -n 50
  ```
- [ ] API accessible from internet
  ```bash
  curl http://YOUR_EC2_IP:8080/api/contacts/health
  ```
- [ ] Can add contact via API
- [ ] Admin dashboard accessible (after updating API URL)
- [ ] Test email sent successfully

### Security Hardening
- [ ] UFW firewall enabled
  ```bash
  sudo ufw enable
  sudo ufw allow 22/tcp
  sudo ufw allow 8080/tcp
  ```
- [ ] SSH key-only authentication
- [ ] Root login disabled
- [ ] Regular password changed
- [ ] MySQL remote access disabled (if not needed)

---

## Operational Checklist

### Daily Operations
- [ ] Check if service is running
  ```bash
  sudo systemctl status job-email-automation
  ```
- [ ] Add 10-15 new HR contacts via dashboard
- [ ] Review logs for today's email sends
  ```bash
  sudo journalctl -u job-email-automation --since today
  ```
- [ ] Check statistics
  ```bash
  curl http://YOUR_EC2_IP:8080/api/contacts/stats
  ```

### Weekly Operations
- [ ] Count emails sent this week
  ```bash
  sudo journalctl -u job-email-automation --since "1 week ago" | grep "Email sent successfully" | wc -l
  ```
- [ ] Review active contacts count
- [ ] Backup database
  ```bash
  mysqldump -u jobapp -p job_automation > backup_$(date +%Y%m%d).sql
  ```
- [ ] Check for any errors in logs
  ```bash
  sudo journalctl -u job-email-automation --since "1 week ago" | grep ERROR
  ```
- [ ] Update resume if needed

### Monthly Operations
- [ ] Review total emails sent
- [ ] Calculate response rate
- [ ] Update email template if needed
- [ ] Clean old logs
- [ ] Review and optimize contact list
- [ ] Update application if new version available
- [ ] Verify Gmail App Password still valid
- [ ] Check AWS billing (should be $0 in free tier)

---

## Troubleshooting Checklist

### Application Won't Start
- [ ] Check Java version (`java -version`)
- [ ] Check MySQL is running (`sudo systemctl status mysql`)
- [ ] Verify application.properties syntax
- [ ] Check file permissions
- [ ] Review error logs
- [ ] Test database connection manually

### Emails Not Sending
- [ ] Verify Gmail credentials in config
- [ ] Check App Password (not regular password)
- [ ] Test SMTP connection (`telnet smtp.gmail.com 587`)
- [ ] Check Gmail sending limits
- [ ] Review email logs for errors
- [ ] Verify resume file exists and is readable

### Database Errors
- [ ] Check MySQL is running
- [ ] Verify database exists
- [ ] Check user permissions
- [ ] Review connection string
- [ ] Check for table corruption
- [ ] Review MySQL error logs

### Dashboard Not Working
- [ ] Check if backend is running
- [ ] Verify API URL in dashboard HTML
- [ ] Check browser console for errors
- [ ] Test API endpoints with curl
- [ ] Clear browser cache
- [ ] Try different browser

---

## Performance Optimization Checklist

### Application Performance
- [ ] JVM memory settings optimized
- [ ] Database connection pool configured
- [ ] Indexes created on frequently queried columns
- [ ] Old logs cleaned regularly
- [ ] Database optimized (`OPTIMIZE TABLE hr_contacts`)

### Email Delivery Performance
- [ ] Daily limit set appropriately
- [ ] Delay between emails configured
- [ ] Email content optimized (not too large)
- [ ] Resume file size optimized
- [ ] SMTP timeout settings configured

---

## Security Checklist

### Credentials Security
- [ ] No credentials in git repository
- [ ] Application.properties in .gitignore
- [ ] .env file in .gitignore
- [ ] Strong database password used
- [ ] Gmail App Password used (not regular)
- [ ] Credentials not in logs

### Network Security
- [ ] Firewall enabled and configured
- [ ] Only necessary ports open
- [ ] SSH key authentication used
- [ ] Root login disabled
- [ ] HTTPS considered for production (optional)

### Application Security
- [ ] Input validation implemented
- [ ] SQL injection prevention (using JPA)
- [ ] Email rate limiting active
- [ ] Transaction management enabled
- [ ] Error messages don't leak sensitive info

---

## Maintenance Checklist

### Regular Maintenance
- [ ] Keep system updated (`sudo apt update && upgrade`)
- [ ] Monitor disk space (`df -h`)
- [ ] Review logs for warnings
- [ ] Backup database regularly
- [ ] Update dependencies periodically
- [ ] Test email sending monthly

### Emergency Procedures
- [ ] Know how to stop service
  ```bash
  sudo systemctl stop job-email-automation
  ```
- [ ] Know how to restart service
  ```bash
  sudo systemctl restart job-email-automation
  ```
- [ ] Have database backup strategy
- [ ] Know how to restore from backup
- [ ] Have contact for support/help

---

## Success Metrics Checklist

### After 1 Week
- [ ] 50+ emails sent
- [ ] 0 system failures
- [ ] Service running 24/7
- [ ] No Gmail warnings

### After 1 Month
- [ ] 200+ emails sent
- [ ] 10+ HR responses
- [ ] 2-3 interview calls
- [ ] Application stable

### After 3 Months
- [ ] 600+ emails sent
- [ ] 30+ HR responses
- [ ] 10+ interviews
- [ ] 1-2 job offers

---

## Final Pre-Launch Checklist

Before going live, verify:
- [ ] All tests passing
- [ ] Configuration correct
- [ ] Test email received
- [ ] Service auto-starts on reboot
- [ ] Logs accessible
- [ ] Dashboard functional
- [ ] Backups configured
- [ ] Documentation read
- [ ] Support contacts noted
- [ ] Emergency procedures known

---

## Post-Launch Checklist

First 24 hours:
- [ ] Service running
- [ ] No errors in logs
- [ ] Scheduler executed at 9:30 AM
- [ ] Emails sent successfully
- [ ] Contacts marked as processed
- [ ] Statistics accurate

First week:
- [ ] 50+ contacts added
- [ ] 30+ emails sent
- [ ] No failures
- [ ] Dashboard functional
- [ ] Logs normal

---

**Remember**: This is a marathon, not a sprint. Add contacts consistently, monitor regularly, and success will follow!

For support, refer to:
- [QUICK_START.md](QUICK_START.md) - Basic troubleshooting
- [README.md](README.md) - Complete documentation
- [AWS_DEPLOYMENT_GUIDE.md](AWS_DEPLOYMENT_GUIDE.md) - AWS-specific issues

**Good luck with your job search! 🚀**
