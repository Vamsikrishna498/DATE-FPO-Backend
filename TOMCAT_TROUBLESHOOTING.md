# Tomcat Memory Issues - Troubleshooting Guide

## Problem Summary
Your Spring Boot application is experiencing continuous Tomcat crashes due to **Out of Memory (OOM)** errors. The JVM is running out of memory, causing the application to stop unexpectedly.

## Root Causes Identified

### 1. **System Memory Constraints**
- Your system has 16GB RAM but only 621MB-1GB free
- Multiple Java processes competing for memory
- Insufficient heap space allocation

### 2. **JVM Configuration Issues**
- Default JVM heap settings too high for available system memory
- No explicit memory limits set
- Garbage collection not optimized

### 3. **Application Configuration**
- No connection pool limits
- Unoptimized Tomcat thread settings
- Excessive logging levels

## Solutions Implemented

### 1. **Updated Application Properties** (`src/main/resources/application.properties`)
- Added Tomcat thread pool optimization
- Configured connection pool settings
- Reduced logging levels
- Added session management settings

### 2. **Created Startup Scripts**
- `start-application.bat` - Windows batch script
- `start-application.ps1` - PowerShell script
- Both include optimized JVM memory settings

### 3. **JVM Memory Optimization**
- **Heap Size**: 512MB initial, 1GB maximum
- **Garbage Collector**: G1GC for better performance
- **Memory Management**: Optimized GC settings

## How to Use the Solutions

### Option 1: Use the Startup Scripts (Recommended)

#### Windows Batch Script:
```bash
# Double-click or run in command prompt
start-application.bat
```

#### PowerShell Script:
```powershell
# Run in PowerShell
.\start-application.ps1
```

### Option 2: Manual JVM Arguments

If you prefer to run manually, use these JVM arguments:

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Option 3: IDE Configuration

If using an IDE (IntelliJ IDEA, Eclipse, VS Code):

1. **IntelliJ IDEA**:
   - Go to Run → Edit Configurations
   - Add VM options: `-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200`

2. **Eclipse**:
   - Right-click project → Run As → Run Configurations
   - Add VM arguments: `-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200`

3. **VS Code**:
   - Create `.vscode/launch.json` with JVM arguments

## Additional System Optimizations

### 1. **Free Up System Memory**
```powershell
# Check memory usage
Get-Process | Sort-Object WorkingSet -Descending | Select-Object -First 10

# Close unnecessary applications
# Restart your computer if needed
```

### 2. **Database Connection Optimization**
- PostgreSQL connection pool limited to 10 connections
- Connection timeout set to 30 seconds
- Idle timeout set to 10 minutes

### 3. **File Upload Limits**
- Maximum file size: 10MB
- Maximum request size: 10MB
- File size threshold: 2KB

## Monitoring and Debugging

### 1. **Check Application Status**
```bash
# Check if application is running
netstat -an | findstr :8080

# Check Java processes
jps -l
```

### 2. **Monitor Memory Usage**
```bash
# Check JVM memory usage
jstat -gc <pid>

# Check heap dump (if OOM occurs)
# Look for heapdump.hprof file
```

### 3. **Application Logs**
- Check `logs/` directory for application logs
- Monitor console output for memory warnings
- Look for GC (Garbage Collection) messages

## Emergency Solutions

### If Application Still Crashes:

1. **Reduce Memory Further**:
   ```bash
   -Xms256m -Xmx512m
   ```

2. **Disable Features Temporarily**:
   - Comment out file upload endpoints
   - Reduce connection pool size to 5
   - Disable detailed logging

3. **System Level**:
   - Close other applications
   - Restart your computer
   - Increase virtual memory/page file

## Prevention Measures

### 1. **Regular Maintenance**
- Monitor application logs
- Clean up old heap dumps
- Restart application periodically

### 2. **Development Best Practices**
- Use connection pooling
- Implement proper error handling
- Monitor memory usage in development

### 3. **Production Considerations**
- Use dedicated server with adequate RAM
- Implement proper monitoring
- Set up automatic restarts

## JVM Arguments Explained

- `-Xms512m`: Initial heap size (512MB)
- `-Xmx1024m`: Maximum heap size (1GB)
- `-XX:+UseG1GC`: Use G1 Garbage Collector
- `-XX:MaxGCPauseMillis=200`: Maximum GC pause time
- `-XX:+HeapDumpOnOutOfMemoryError`: Create heap dump on OOM
- `-XX:HeapDumpPath=./heapdump.hprof`: Heap dump location

## Contact Information

If issues persist after implementing these solutions:
1. Check the heap dump file (`heapdump.hprof`)
2. Review application logs
3. Monitor system memory usage
4. Consider upgrading system RAM if needed

## Quick Start Commands

```bash
# Clean and compile
mvn clean compile

# Start with optimized settings
.\start-application.ps1

# Or manually
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xms512m -Xmx1024m -XX:+UseG1GC"
``` 