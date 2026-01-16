# 🚀 Quick Start Guide - Job Email Automation

Get your automated job application system running in 15 minutes!

## ⚡ Prerequisites Check

Before starting, make sure you have:

- [ ] Java 17 installed (`java -version`)
- [ ] Maven installed (`mvn -version`)
- [ ] MySQL installed and running
- [ ] Gmail account with 2FA enabled
- [ ] Resume in PDF format

## 📝 Step 1: Get Gmail App Password (5 minutes)

1. Open: https://myaccount.google.com/apppasswords
2. Sign in to your Gmail account
3. Select App: **Mail**
4. Select Device: **Other** → Type "Job Automation"
5. Click **Generate**
6. Copy the 16-character password (remove spaces)
7. Keep it safe - you'll need it in Step 3

**Important**: This is NOT your Gmail password. It's a special app-specific password.

## 🗄️ Step 2: Set Up Database (2 minutes)

Open terminal and run:

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE job_automation;

# Exit
EXIT;
```

## ⚙️ Step 3: Configure Application (3 minutes)

1. **Download/Clone the project**

2. **Edit** `src/main/resources/application.properties`:

```properties
# Replace these with your values:

# Database (line 11-12)
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Gmail (line 23-24)
spring.mail.username=your_email@gmail.com
spring.mail.password=YOUR_16_CHAR_APP_PASSWORD

# Resume (line 32)
app.resume.path=/full/path/to/your/resume.pdf

# Your Info (line 35-37)
app.sender.name=Your Full Name
app.sender.phone=+91-1234567890
app.sender.linkedin=https://linkedin.com/in/yourprofile
```

3. **Save the file**

## 🏗️ Step 4: Build & Run (5 minutes)

```bash
# Navigate to project directory
cd job-email-automation

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Wait for: `Started JobEmailAutomationApplication in X seconds`

## ✅ Step 5: Test It Works (3 minutes)

### Test 1: Health Check
```bash
curl http://localhost:8080/api/contacts/health
```

Expected: `{"status":"healthy",...}`

### Test 2: Add Your First HR Contact
```bash
curl -X POST http://localhost:8080/api/contacts \
  -H "Content-Type: application/json" \
  -d '{
    "hrName": "John Smith",
    "email": "careers@example.com",
    "company": "Example Corp",
    "position": "Java Developer",
    "active": true
  }'
```

Expected: Contact details with ID returned

### Test 3: View Statistics
```bash
curl http://localhost:8080/api/contacts/stats
```

Expected: `{"totalContacts":1,"activeContacts":1,"processedContacts":0}`

## 🎨 Step 6: Open Admin Dashboard

1. Open `admin-dashboard.html` in your browser
2. You should see your contact in the table
3. Try adding more contacts through the UI

## 🎯 You're All Set!

### What Happens Now?

✅ **Every weekday at 9:30 AM**, the system will:
- Select up to 10 active HR contacts
- Send personalized resume emails
- Mark contacts as processed
- Log everything

✅ **What You Need To Do Daily**:
- Add 10-15 new HR contacts through the dashboard
- Check the logs to see emails being sent

### View Logs

```bash
# In the terminal where app is running, you'll see:
# [Scheduled task] EMAIL AUTOMATION STARTED
# [Email sent successfully to...]
```

## 🚨 Troubleshooting

### Problem: "Failed to connect to database"
**Solution**: Check MySQL is running: `sudo systemctl status mysql`

### Problem: "Failed to send email"
**Solutions**:
1. Verify Gmail App Password is correct
2. Check you're using App Password, not regular password
3. Test Gmail connection: `telnet smtp.gmail.com 587`

### Problem: "Resume file not found"
**Solution**: Check the path in application.properties is correct and file exists

### Problem: Port 8080 already in use
**Solution**: Change port in application.properties: `server.port=8081`

## 📚 Next Steps

1. **Add More Contacts**: Aim for 100-200 contacts initially
2. **Deploy to AWS**: See `AWS_DEPLOYMENT_GUIDE.md` for 24/7 operation
3. **Monitor Daily**: Check logs each day to ensure emails are sending

## 🎓 Tips for Success

### Finding HR Emails

✅ **Good Sources**:
- Company career pages (careers@company.com)
- LinkedIn job postings
- Job portals where email is visible
- Company "Contact Us" pages

❌ **Avoid**:
- Email scraping tools
- Purchased email lists
- Random emails without verification

### Managing Contacts

- **Add 10-15 new contacts daily**
- **Keep notes** about where you found each email
- **Verify emails** are legitimate before adding
- **Diversify companies** for better results

### Email Best Practices

- ✅ Keep resume updated
- ✅ Update your details in application.properties if they change
- ✅ Monitor which companies respond
- ✅ Follow up manually with interested companies

## 💡 Pro Tips

1. **Create Categories**: Use the notes field to categorize companies
   - Example: "Startup", "Product Company", "Service Company"

2. **Track Results**: Keep a spreadsheet of:
   - Date added
   - Company name
   - Response received?
   - Interview scheduled?

3. **Optimize Timing**: The default 9:30 AM works well, but you can change it:
   ```java
   @Scheduled(cron = "0 30 9 * * MON-FRI")  // 9:30 AM
   @Scheduled(cron = "0 0 10 * * MON-FRI")  // 10:00 AM
   ```

4. **Weekend Batch Add**: Spend 1-2 hours on weekends finding and adding contacts

## ❓ FAQ

**Q: How many emails will be sent per day?**
A: Maximum 10 emails per day (configurable in application.properties)

**Q: Will the same HR receive multiple emails?**
A: No. Once sent, contact is marked inactive and won't receive again.

**Q: Can I test without actually sending emails?**
A: Yes! Set `spring.mail.properties.mail.smtp.auth=false` for testing

**Q: What if I want to send to a contact again?**
A: Use the "Reactivate" button in dashboard or API endpoint

**Q: Is this legal?**
A: Yes, when used responsibly for legitimate job applications to publicly available HR emails

**Q: Will Gmail block me?**
A: No, if you:
   - Stay within 10 emails/day
   - Use App Password
   - Send to legitimate HR emails
   - Don't spam

## 📞 Getting Help

1. Check logs: Look at console output
2. Read README.md: Full documentation
3. Check AWS_DEPLOYMENT_GUIDE.md: For deployment issues
4. Test API: Use test-api.sh script

## 🎉 Success Checklist

After completing this guide, you should have:

- [x] Application running locally
- [x] Database connected
- [x] Gmail configured
- [x] First contact added
- [x] Admin dashboard working
- [x] Understanding of daily operations

## 🚀 Ready to Deploy 24/7?

Once comfortable with local setup, deploy to AWS for automated operation:

```bash
# See full deployment guide
cat AWS_DEPLOYMENT_GUIDE.md
```

---

**Congratulations! Your automated job application system is ready! 🎉**

Time to add more contacts and let the system work for you while you focus on interview preparation!
