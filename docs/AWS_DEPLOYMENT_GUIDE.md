# AWS Deployment Guide
## Deploy Job Email Automation System on AWS EC2

This guide will help you deploy the Job Email Automation System on AWS EC2 for 24/7 operation.

## 📋 Prerequisites

- AWS Account
- Basic knowledge of EC2 and SSH
- Your application JAR file
- Your resume PDF file

## 🚀 Step-by-Step Deployment

### Step 1: Launch EC2 Instance

1. **Go to AWS Console** → EC2 → Launch Instance

2. **Configure Instance:**
   - **Name**: job-email-automation
   - **AMI**: Ubuntu Server 22.04 LTS (Free tier eligible)
   - **Instance Type**: t2.micro (1 GB RAM, sufficient for this app)
   - **Key Pair**: Create new or use existing SSH key pair
   - **Network Settings**:
     - Allow SSH (port 22) from your IP
     - Allow Custom TCP (port 8080) from anywhere (0.0.0.0/0)
   - **Storage**: 8 GB (default is fine)

3. **Launch Instance**

4. **Note down**:
   - Public IP address
   - SSH key location

### Step 2: Connect to EC2

```bash
# Make key read-only (required)
chmod 400 your-key.pem

# Connect to EC2
ssh -i your-key.pem ubuntu@YOUR_EC2_PUBLIC_IP
```

### Step 3: Install Dependencies

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Verify Java installation
java -version

# Install MySQL Server
sudo apt install mysql-server -y

# Secure MySQL installation
sudo mysql_secure_installation
# Answer: n, Y, Y, Y, Y
```

### Step 4: Configure MySQL

```bash
# Login to MySQL
sudo mysql

# Create database and user
CREATE DATABASE job_automation;
CREATE USER 'jobapp'@'localhost' IDENTIFIED BY 'StrongPassword123!';
GRANT ALL PRIVILEGES ON job_automation.* TO 'jobapp'@'localhost';
FLUSH PRIVILEGES;
EXIT;

# Test connection
mysql -u jobapp -p
# Enter password: StrongPassword123!
# Type: exit
```

### Step 5: Upload Application Files

**From your local machine:**

```bash
# Create resume directory on EC2
ssh -i your-key.pem ubuntu@YOUR_EC2_IP "mkdir -p ~/resume"

# Upload JAR file
scp -i your-key.pem target/job-email-automation-1.0.0.jar ubuntu@YOUR_EC2_IP:~/

# Upload resume
scp -i your-key.pem /path/to/your/resume.pdf ubuntu@YOUR_EC2_IP:~/resume/

# Upload application.properties (with your configured settings)
scp -i your-key.pem src/main/resources/application.properties ubuntu@YOUR_EC2_IP:~/
```

### Step 6: Configure Application Properties

**On EC2:**

```bash
# Edit application.properties
nano ~/application.properties
```

Update these values:
```properties
# Database
spring.datasource.username=jobapp
spring.datasource.password=StrongPassword123!

# Gmail (use your actual credentials)
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password

# Resume path
app.resume.path=/home/ubuntu/resume/resume.pdf

# Your details
app.sender.name=Your Name
app.sender.phone=+91-9876543210
app.sender.linkedin=https://linkedin.com/in/yourprofile
```

Save and exit (Ctrl+X, Y, Enter)

### Step 7: Create Systemd Service

Create service file:

```bash
sudo nano /etc/systemd/system/job-email-automation.service
```

Add this content:

```ini
[Unit]
Description=Job Email Automation Service
After=network.target mysql.service
Wants=mysql.service

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu
ExecStart=/usr/bin/java -jar /home/ubuntu/job-email-automation-1.0.0.jar --spring.config.location=/home/ubuntu/application.properties
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=job-email-automation

[Install]
WantedBy=multi-user.target
```

Save and exit.

### Step 8: Start the Service

```bash
# Reload systemd
sudo systemctl daemon-reload

# Enable service (start on boot)
sudo systemctl enable job-email-automation

# Start service
sudo systemctl start job-email-automation

# Check status
sudo systemctl status job-email-automation
```

You should see: **Active: active (running)**

### Step 9: View Logs

```bash
# Real-time logs
sudo journalctl -u job-email-automation -f

# Last 100 lines
sudo journalctl -u job-email-automation -n 100

# Logs from today
sudo journalctl -u job-email-automation --since today
```

### Step 10: Test the Application

**From your local machine:**

```bash
# Health check
curl http://YOUR_EC2_IP:8080/api/contacts/health

# Get statistics
curl http://YOUR_EC2_IP:8080/api/contacts/stats

# Add a test contact
curl -X POST http://YOUR_EC2_IP:8080/api/contacts \
  -H "Content-Type: application/json" \
  -d '{
    "hrName": "Test HR",
    "email": "test@example.com",
    "company": "Test Company",
    "position": "Developer",
    "active": true
  }'
```

### Step 11: Open Admin Dashboard

1. Edit `admin-dashboard.html` on your local machine
2. Change API_URL to: `http://YOUR_EC2_IP:8080/api/contacts`
3. Open the file in your browser

## 🔒 Security Enhancements

### 1. Restrict SSH Access

```bash
# Edit SSH config
sudo nano /etc/ssh/sshd_config

# Change these settings:
PermitRootLogin no
PasswordAuthentication no

# Restart SSH
sudo systemctl restart ssh
```

### 2. Enable UFW Firewall

```bash
# Enable firewall
sudo ufw enable

# Allow SSH
sudo ufw allow 22/tcp

# Allow application port
sudo ufw allow 8080/tcp

# Check status
sudo ufw status
```

### 3. Use Environment Variables

```bash
# Create environment file
sudo nano /etc/environment

# Add sensitive variables
SPRING_MAIL_PASSWORD=your_app_password
SPRING_DATASOURCE_PASSWORD=StrongPassword123!

# Reload
source /etc/environment
```

Update service file to use these variables.

## 📊 Monitoring

### Check Service Status

```bash
# Is service running?
sudo systemctl is-active job-email-automation

# Service status
sudo systemctl status job-email-automation

# Restart service
sudo systemctl restart job-email-automation

# Stop service
sudo systemctl stop job-email-automation
```

### Monitor Logs

```bash
# Watch logs live
sudo journalctl -u job-email-automation -f

# Check for errors
sudo journalctl -u job-email-automation | grep ERROR

# Check email sending
sudo journalctl -u job-email-automation | grep "Email sent successfully"
```

### Monitor System Resources

```bash
# CPU and Memory usage
htop

# Or
top

# Disk usage
df -h

# MySQL status
sudo systemctl status mysql
```

## 🔄 Updates and Maintenance

### Update Application

```bash
# Stop service
sudo systemctl stop job-email-automation

# Backup old version
cp ~/job-email-automation-1.0.0.jar ~/job-email-automation-1.0.0.jar.backup

# Upload new JAR (from local machine)
scp -i your-key.pem target/job-email-automation-1.0.0.jar ubuntu@YOUR_EC2_IP:~/

# Start service
sudo systemctl start job-email-automation

# Check logs
sudo journalctl -u job-email-automation -f
```

### Backup Database

```bash
# Create backup
mysqldump -u jobapp -p job_automation > backup_$(date +%Y%m%d).sql

# Restore from backup
mysql -u jobapp -p job_automation < backup_20250109.sql
```

### Update Resume

```bash
# From local machine
scp -i your-key.pem /path/to/new/resume.pdf ubuntu@YOUR_EC2_IP:~/resume/resume.pdf

# Restart service
ssh -i your-key.pem ubuntu@YOUR_EC2_IP "sudo systemctl restart job-email-automation"
```

## 🚨 Troubleshooting

### Problem: Service Won't Start

```bash
# Check logs
sudo journalctl -u job-email-automation -n 50

# Check Java installation
java -version

# Check file permissions
ls -la ~/job-email-automation-1.0.0.jar

# Test JAR manually
java -jar ~/job-email-automation-1.0.0.jar
```

### Problem: Cannot Connect to MySQL

```bash
# Check MySQL is running
sudo systemctl status mysql

# Test MySQL connection
mysql -u jobapp -p

# Check MySQL logs
sudo journalctl -u mysql -n 50
```

### Problem: Emails Not Sending

```bash
# Check Gmail credentials in application.properties
cat ~/application.properties | grep mail

# Test SMTP connection
telnet smtp.gmail.com 587

# Check logs for email errors
sudo journalctl -u job-email-automation | grep "email"
```

### Problem: Out of Memory

```bash
# Check memory
free -h

# Increase JVM memory in service file
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /home/ubuntu/job-email-automation-1.0.0.jar

# Reload and restart
sudo systemctl daemon-reload
sudo systemctl restart job-email-automation
```

## 💰 Cost Estimation

### AWS EC2 t2.micro (Free Tier)

- **First 12 months**: FREE
- **After 12 months**: ~$8-10/month
- **Data transfer**: Minimal cost

### Total Monthly Cost

- **Year 1**: $0 (Free tier)
- **Year 2+**: ~$10/month

## 📈 Scaling Tips

If you need more performance:

1. **Upgrade to t2.small**: ~$17/month
2. **Use RDS for MySQL**: Better reliability
3. **Add Load Balancer**: For high availability
4. **Use Elastic IP**: Static IP address

## ✅ Post-Deployment Checklist

- [ ] EC2 instance running
- [ ] MySQL configured
- [ ] Application deployed
- [ ] Service running and enabled
- [ ] Logs showing no errors
- [ ] Test email sent successfully
- [ ] Admin dashboard connected
- [ ] Firewall configured
- [ ] Backups scheduled
- [ ] Monitoring set up

## 🎯 Daily Operations

### Morning Routine

```bash
# SSH to EC2
ssh -i your-key.pem ubuntu@YOUR_EC2_IP

# Check service
sudo systemctl status job-email-automation

# Check today's logs
sudo journalctl -u job-email-automation --since today

# View statistics
curl http://localhost:8080/api/contacts/stats
```

### Add New HR Contacts

Use the admin dashboard or API to add 10-15 new contacts daily.

### Weekly Check

```bash
# Check email sent count
sudo journalctl -u job-email-automation | grep "Email sent successfully" | wc -l

# Check active contacts
curl http://localhost:8080/api/contacts/stats

# Backup database
mysqldump -u jobapp -p job_automation > backup_weekly_$(date +%Y%m%d).sql
```

## 🎉 Success!

Your Job Email Automation System is now running 24/7 on AWS!

The system will automatically:
- Send emails every weekday at 9:30 AM IST
- Process up to 10 contacts per day
- Never send duplicate emails
- Log all activities

**Remember**: Add new HR contacts regularly for continuous job opportunities!

---

**Need Help?**
- Check logs: `sudo journalctl -u job-email-automation -f`
- Restart service: `sudo systemctl restart job-email-automation`
- View API: `http://YOUR_EC2_IP:8080/api/contacts/health`
