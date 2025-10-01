# Staging Employee ID Generation Fix

## Problem
Employees in staging are getting FARMER IDs (FAMXXXX) instead of EMPLOYEE IDs (DATE_EMP-XXXXX)

## Root Cause
The EMPLOYEE code format in staging database has NULL or empty `prefix` field.

---

## STEP-BY-STEP FIX

### Step 1: Connect to Staging Database
```bash
# SSH to staging server
ssh user@3.108.255.210

# Connect to PostgreSQL
psql -U your_username -d your_database_name
```

### Step 2: Check Current State
```sql
-- Check what's currently there
SELECT id, code_type, prefix, is_active, current_number 
FROM code_formats 
ORDER BY id;
```

**Expected Output:**
```
 id | code_type | prefix  | is_active | current_number
----+-----------+---------+-----------+----------------
  2 | FARMER    | FAM     | t         | 10000
  4 | EMPLOYEE  | (null)  | t         | 0              <-- THIS IS THE PROBLEM
```

### Step 3: Fix the EMPLOYEE Prefix
```sql
-- Update EMPLOYEE format with correct prefix
UPDATE code_formats 
SET prefix = 'DATE_EMP', is_active = true, updated_at = CURRENT_TIMESTAMP
WHERE code_type = 'EMPLOYEE';
```

### Step 4: Verify the Fix
```sql
-- Check again
SELECT id, code_type, prefix, is_active, current_number 
FROM code_formats 
WHERE code_type = 'EMPLOYEE';
```

**Should Now Show:**
```
 id | code_type | prefix   | is_active | current_number
----+-----------+----------+-----------+----------------
  4 | EMPLOYEE  | DATE_EMP | t         | 0
```

### Step 5: Exit Database
```sql
\q
```

### Step 6: Restart Backend Application
```bash
# Find your application process
ps aux | grep java

# Kill old process (replace PID with actual)
kill -9 PID

# Restart application (adjust path as needed)
cd /path/to/your/app
./start-application.sh

# OR if using systemd
sudo systemctl restart your-app-name
```

### Step 7: Test Employee Creation
1. Go to staging URL: http://3.108.255.210:3000
2. Login as Super Admin
3. Create a new employee
4. Check the employee ID in the list

**Should Get:** `DATE_EMP-00001`

---

## After First Fix - Changing Prefix via UI

Once the above is done **ONE TIME**, you can change the prefix anytime via:

1. Login to staging as Super Admin
2. Go to **Personalization** page
3. Edit **Employee Code Format**
4. Change prefix to whatever you want (e.g., 'EMP', 'STAFF', etc.)
5. Click **Save**
6. **No backend restart needed!**
7. Next employee will use the new prefix automatically

---

## Troubleshooting

### If Still Getting FARMER IDs:

**Check Backend Logs:**
```bash
# View application logs
tail -f /path/to/logs/application.log

# Look for these messages when creating employee:
# "🔄 Generating employee ID for state: ..."
# "📊 All CodeFormats in database: 2"
# "   - EMPLOYEE: 'DATE_EMP' (active: true, current: 0)"
# "✅ Generated employee ID: DATE_EMP-00001"
```

### If Logs Show "Using FARMER format":
- The UPDATE query didn't work
- Run the UPDATE query again
- Make sure you're connected to the correct database
- Verify with: `SELECT current_database();`

### If Logs Show "PREFIX IS NULL":
- The UPDATE query wasn't committed
- Make sure to run COMMIT after UPDATE
- Or add COMMIT to the UPDATE query

---

## Quick One-Liner Fix

If you just want a quick fix, run this in staging PostgreSQL:

```sql
UPDATE code_formats SET prefix = 'DATE_EMP', is_active = true WHERE code_type = 'EMPLOYEE'; SELECT * FROM code_formats WHERE code_type = 'EMPLOYEE';
```

Then restart backend. Done! ✅ 