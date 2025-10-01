# Staging Employee ID - Complete Verification Checklist

## ❌ Problem: Employee getting FARMER ID (FAMXXXX0012) instead of EMPLOYEE ID

---

## ✅ CHECKLIST - Complete ALL Steps

### [ ] Step 1: Verify Database Connection
```bash
# SSH to staging
ssh user@3.108.255.210

# Connect to PostgreSQL and verify database name
psql -U your_username -d your_database_name

# Inside psql, check current database
SELECT current_database();
```

**✓ Confirm:** You're connected to the correct database

---

### [ ] Step 2: Check EMPLOYEE Code Format
```sql
-- Run this and look at the 'prefix' column
SELECT id, code_type, prefix, is_active, current_number 
FROM code_formats 
WHERE code_type = 'EMPLOYEE';
```

**Expected Problem:** `prefix` shows `(null)` or empty

---

### [ ] Step 3: Update EMPLOYEE Prefix
```sql
-- Run this UPDATE
UPDATE code_formats 
SET prefix = 'DATE_EMP', is_active = true, updated_at = CURRENT_TIMESTAMP
WHERE code_type = 'EMPLOYEE';

-- Check how many rows were updated (should show: UPDATE 1)
```

**✓ Confirm:** You see "UPDATE 1" message

---

### [ ] Step 4: Verify Update Worked
```sql
-- Check again - prefix should now show 'DATE_EMP'
SELECT id, code_type, prefix, is_active, current_number 
FROM code_formats 
WHERE code_type = 'EMPLOYEE';
```

**✓ Confirm:** `prefix` column now shows `DATE_EMP` (not null)

---

### [ ] Step 5: Commit Changes (PostgreSQL)
```sql
-- Commit the transaction
COMMIT;

-- Verify one more time
SELECT prefix FROM code_formats WHERE code_type = 'EMPLOYEE';
```

**✓ Confirm:** Still shows `DATE_EMP`

---

### [ ] Step 6: Exit Database
```sql
\q
```

---

### [ ] Step 7: Deploy Updated Backend Code

**Option A: If using Git deployment**
```bash
cd /path/to/backend
git pull origin master
mvn clean package
```

**Option B: If copying JAR file**
```bash
# Copy your updated JAR to staging
scp target/your-app.jar user@3.108.255.210:/path/to/app/
```

**✓ Confirm:** Latest code is on staging server

---

### [ ] Step 8: Restart Backend Application

**Find and stop old process:**
```bash
# Find Java process
ps aux | grep java

# Note the PID, then kill it
kill -9 <PID>
```

**Start new process:**
```bash
# Start application (adjust command for your setup)
cd /path/to/app
./start-application.sh

# OR if using systemd
sudo systemctl restart your-app-service

# OR manual start
nohup java -jar your-app.jar &
```

**✓ Confirm:** New process is running with updated code

---

### [ ] Step 9: Check Backend Logs

```bash
# Watch logs in real-time
tail -f /path/to/logs/application.log

# OR if using nohup
tail -f nohup.out
```

**✓ Look for:** Application started successfully

---

### [ ] Step 10: Test Employee Creation

1. Go to: http://3.108.255.210:3000
2. Login as Super Admin
3. Click "Add Employee"
4. Fill the form
5. Submit
6. **Watch the backend logs** for:
   ```
   🔄 Generating employee ID for state: ...
   📊 All CodeFormats in database: 2
      - FARMER: 'FAM' (active: true, current: 10000)
      - EMPLOYEE: 'DATE_EMP' (active: true, current: 0)    <-- Should show this
   ✅ Generated employee ID: DATE_EMP-00001
   ```

---

### [ ] Step 11: Verify in Employee List

Check employee list - new employee should show:
- **ID:** `DATE_EMP-00001` ✅
- **NOT:** `FAMXXXX0013` ❌

---

## 🔴 If STILL Getting FARMER ID

### Check These:

1. **Did you restart backend?**
   - Old code in memory won't use updated database
   - MUST restart for changes to take effect

2. **Is the UPDATE committed?**
   ```sql
   SELECT prefix FROM code_formats WHERE code_type = 'EMPLOYEE';
   ```
   - Should show: `DATE_EMP`
   - If shows NULL: Run UPDATE again and COMMIT

3. **Are you on correct server?**
   - Verify IP: `curl ifconfig.me`
   - Should be: `3.108.255.210`

4. **Check application.properties:**
   ```bash
   cat src/main/resources/application.properties | grep datasource
   ```
   - Verify it connects to correct database

5. **Check logs for errors:**
   ```bash
   tail -100 application.log | grep -i error
   ```

---

## 📞 Quick Test Without Creating Employee

Run this SQL to see what ID would be generated:
```sql
SELECT 
    code_type,
    prefix,
    current_number,
    prefix || '-' || LPAD((current_number + 1)::TEXT, 5, '0') as next_id
FROM code_formats 
WHERE code_type = 'EMPLOYEE';
```

**Should show:** `DATE_EMP-00001`

---

## Summary

**The fix requires:**
1. ✅ Update database (ONE TIME)
2. ✅ Restart backend (ONE TIME)
3. ✅ Test employee creation

**After that:**
- Change prefix anytime via Personalization UI
- No SQL needed
- No backend restart needed 