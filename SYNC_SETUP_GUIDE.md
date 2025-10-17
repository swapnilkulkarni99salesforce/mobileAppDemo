# Perfect Fit - MongoDB Sync Setup Guide

Complete guide to set up Room database synchronization with MongoDB for the Perfect Fit Android app.

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [What's Been Implemented](#whats-been-implemented)
3. [Setting Up the Backend](#setting-up-the-backend)
4. [Configuring the Android App](#configuring-the-android-app)
5. [Testing the Sync](#testing-the-sync)
6. [How It Works](#how-it-works)
7. [Troubleshooting](#troubleshooting)

---

## 🏗️ Architecture Overview

```
┌─────────────────────┐
│   Android Device    │
│  ┌───────────────┐  │
│  │  Room DB      │  │  ← Local SQLite storage
│  │  (Offline)    │  │
│  └───────┬───────┘  │
│          │          │
│  ┌───────▼───────┐  │
│  │  SyncManager  │  │  ← Sync coordination
│  └───────┬───────┘  │
│          │          │
└──────────┼──────────┘
           │
    Retrofit HTTP
           │
    ┌──────▼──────┐
    │  Node.js    │
    │  Express    │  ← REST API Server
    │   Server    │
    └──────┬──────┘
           │
    ┌──────▼──────┐
    │  MongoDB    │  ← Cloud Database
    │   Atlas     │
    └─────────────┘
```

## ✅ What's Been Implemented

### Android App

1. **Updated Models** - Added sync fields to all models:
   - `serverId` - MongoDB _id
   - `lastModified` - Timestamp for conflict resolution
   - `syncStatus` - PENDING, SYNCED, or FAILED

2. **Network Layer**:
   - Retrofit client configured
   - API service interfaces
   - JSON models for network communication
   - Automatic request/response handling

3. **Sync Manager**:
   - Batch sync functionality
   - Conflict resolution (last-write-wins)
   - Individual entity sync methods
   - Network status checking

4. **Background Sync**:
   - WorkManager integration
   - Periodic sync (every 15 minutes)
   - Battery and network constraints
   - Retry logic for failed syncs

5. **UI Controls**:
   - Sync button on Home screen
   - Last sync time display
   - Sync status messages
   - Auto-sync toggle switch

6. **Database Updates**:
   - Updated DAOs with sync queries
   - Database version incremented to 6
   - Sync-related query methods

### Backend Server

1. **Node.js/Express Server**:
   - RESTful API endpoints
   - MongoDB integration
   - Batch sync endpoint
   - CORS enabled

2. **API Endpoints**:
   - `/api/sync/batch` - Sync all data at once
   - `/api/customers` - Customer CRUD operations
   - `/api/orders` - Order CRUD operations
   - `/api/measurements` - Measurement CRUD operations

3. **Features**:
   - Timestamp-based sync
   - Conflict resolution
   - Data validation
   - Error handling

---

## 🚀 Setting Up the Backend

### Option 1: MongoDB Atlas (Recommended - Free Cloud Database)

1. **Create MongoDB Atlas Account**:
   ```
   https://www.mongodb.com/cloud/atlas/register
   ```

2. **Create a Cluster**:
   - Click "Build a Database"
   - Select "M0 Free" tier
   - Choose a cloud provider and region
   - Click "Create Cluster"

3. **Create Database User**:
   - Go to "Database Access"
   - Click "Add New Database User"
   - Choose "Password" authentication
   - Username: `admin`
   - Password: (create a strong password)
   - Database User Privileges: "Atlas admin"
   - Click "Add User"

4. **Add IP Address**:
   - Go to "Network Access"
   - Click "Add IP Address"
   - Click "Allow Access from Anywhere" (0.0.0.0/0)
   - Click "Confirm"

5. **Get Connection String**:
   - Go to "Database" → "Connect"
   - Select "Connect your application"
   - Copy the connection string:
   ```
   mongodb+srv://admin:<password>@cluster0.xxxxx.mongodb.net/
   ```
   - Replace `<password>` with your actual password

### Option 2: Local MongoDB

1. **Install MongoDB**:
   ```bash
   # macOS
   brew install mongodb-community
   brew services start mongodb-community
   
   # Ubuntu
   sudo apt-get install mongodb
   sudo systemctl start mongodb
   
   # Windows
   Download from: https://www.mongodb.com/try/download/community
   ```

2. **Connection String**: `mongodb://localhost:27017`

### Install and Run Backend Server

1. **Install Node.js**:
   - Download from: https://nodejs.org/ (LTS version)
   - Verify installation: `node --version`

2. **Navigate to Backend Directory**:
   ```bash
   cd backend
   ```

3. **Install Dependencies**:
   ```bash
   npm install
   ```

4. **Configure Environment**:
   ```bash
   # Create .env file
   echo "MONGODB_URI=mongodb+srv://admin:YOUR_PASSWORD@cluster0.xxxxx.mongodb.net/" > .env
   echo "PORT=3000" >> .env
   ```

5. **Start the Server**:
   ```bash
   # Development mode (auto-restart on changes)
   npm run dev
   
   # OR Production mode
   npm start
   ```

6. **Verify Server is Running**:
   ```bash
   curl http://localhost:3000/api/health
   ```
   
   Should return:
   ```json
   {
     "status": "ok",
     "message": "Server is running",
     "timestamp": 1234567890
   }
   ```

---

## 📱 Configuring the Android App

### 1. Update Base URL

Edit: `/app/src/main/java/com/example/perfectfit/network/RetrofitClient.kt`

```kotlin
// For Android Emulator (server on same machine):
private const val BASE_URL = "http://10.0.2.2:3000/"

// For Real Device (same network):
// Find your computer's IP address:
// - Mac: ifconfig | grep inet
// - Windows: ipconfig
// - Use that IP:
private const val BASE_URL = "http://192.168.1.100:3000/"  // Replace with your IP

// For Production (deployed server):
private const val BASE_URL = "https://your-domain.com/"
```

### 2. Add Internet Permission

This is already added, but verify in: `/app/src/main/AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 3. Sync Gradle Files

In Android Studio:
1. Click "Sync Now" on the banner
2. Or: File → Sync Project with Gradle Files
3. Wait for sync to complete

### 4. Build and Run

1. Build the project: Build → Make Project
2. Run on emulator or device

---

## 🧪 Testing the Sync

### Step 1: Create Test Data

1. Open the app
2. Register a customer
3. Create an order for that customer
4. Add measurements

### Step 2: Trigger Sync

1. Go to Home screen
2. Click "☁️ Sync Now" button
3. Wait for success message
4. Check "Last sync" time updates

### Step 3: Verify on Server

```bash
# Check data in MongoDB
curl http://localhost:3000/api/customers
curl http://localhost:3000/api/orders
curl http://localhost:3000/api/measurements
```

### Step 4: Test Cross-Device Sync

1. Install app on another device/emulator
2. Click "Sync Now"
3. Verify data appears from first device

### Step 5: Test Background Sync

1. Enable "Auto-sync" switch on Home screen
2. Wait 15+ minutes (or force sync via WorkManager)
3. Create new data
4. Background sync should occur automatically

---

## ⚙️ How It Works

### Sync Flow

1. **Local Changes Tracking**:
   - When you create/update data, `syncStatus` is set to "PENDING"
   - `lastModified` timestamp is recorded

2. **Manual Sync**:
   - User clicks "Sync Now"
   - App collects all PENDING records
   - Sends to server via POST `/api/sync/batch`
   
3. **Server Processing**:
   - Server receives new/updated records
   - Inserts/updates in MongoDB
   - Returns records with MongoDB `_id`
   - Also returns any updates from other devices

4. **Local Update**:
   - App receives server response
   - Updates local records with `serverId`
   - Sets `syncStatus` to "SYNCED"
   - Inserts new records from other devices

5. **Background Sync**:
   - WorkManager runs every 15 minutes
   - Same sync process as manual
   - Only when network available and battery not low

### Conflict Resolution

**Strategy**: Last-Write-Wins

- Server timestamp is authoritative
- If two devices modify same record:
  - Device A syncs → Server has version A
  - Device B syncs → Server overwrites with version B
  - Device A syncs again → Gets version B from server

### Sync Status States

- **PENDING**: Local change not synced yet
- **SYNCED**: Successfully synced with server
- **FAILED**: Sync attempt failed (will retry)

---

## 🔧 Troubleshooting

### Android App Can't Connect to Server

**Problem**: "No network connection" or timeout errors

**Solutions**:

1. **Check BASE_URL**:
   - Emulator: Use `10.0.2.2:3000` not `localhost:3000`
   - Real device: Use your computer's IP, not `localhost`

2. **Verify Server is Running**:
   ```bash
   curl http://localhost:3000/api/health
   ```

3. **Check Firewall**:
   - Disable firewall temporarily to test
   - Or add rule to allow port 3000

4. **Check Network**:
   - Device and computer on same WiFi network
   - Computer not in "public network" mode

### MongoDB Connection Failed

**Problem**: Backend can't connect to MongoDB

**Solutions**:

1. **Check Connection String**:
   - Verify password has no special characters (or URL encode them)
   - Check username is correct
   
2. **MongoDB Atlas**:
   - Verify IP whitelist includes `0.0.0.0/0`
   - Check database user exists and has permissions

3. **Local MongoDB**:
   - Verify MongoDB is running: `ps aux | grep mongod`
   - Start if needed: `brew services start mongodb-community`

### Sync Shows "Failed"

**Problem**: Sync completes but shows "Failed" status

**Solutions**:

1. **Check Server Logs**:
   - Look at Node.js console for errors
   
2. **Verify Data Format**:
   - Check that all required fields are present
   - Verify date formats match

3. **Check Server Response**:
   - Use Postman or curl to test API directly
   - Verify response format matches expected

### Background Sync Not Working

**Problem**: Auto-sync doesn't run

**Solutions**:

1. **Battery Optimization**:
   - Disable battery optimization for the app
   - Settings → Apps → Perfect Fit → Battery → Unrestricted

2. **WorkManager Constraints**:
   - Ensure device is connected to network
   - Check battery is not in low-power mode

3. **Verify WorkManager**:
   - Check Logcat for "SyncWorker" logs
   - Confirm WorkManager is scheduled

### Database Version Mismatch

**Problem**: App crashes after updating database

**Solution**:
- Uninstall and reinstall the app
- Or increment database version again in `AppDatabase.kt`

---

## 📊 Monitoring Sync

### Check Sync Status in App

- Open Home screen
- Look at "Last sync" time
- Check sync status message

### Check Server Logs

```bash
# Watch logs in real-time
cd backend
npm run dev

# You'll see:
# - Sync requests
# - Data being processed
# - Any errors
```

### Query MongoDB Directly

```bash
# Install MongoDB Shell
npm install -g mongodb-shell

# Connect to MongoDB
mongosh "mongodb+srv://admin:PASSWORD@cluster0.xxxxx.mongodb.net/"

# View data
use perfectfit_db
db.customers.find()
db.orders.find()
db.measurements.find()
```

---

## 🚀 Production Deployment

### Deploy Backend

**Recommended Services**:

1. **Heroku** (Easy, free tier):
   ```bash
   cd backend
   heroku create your-app-name
   heroku config:set MONGODB_URI="your_mongodb_uri"
   git push heroku main
   ```

2. **Render** (Modern, free tier):
   - Create account at render.com
   - Connect GitHub repo
   - Set environment variables
   - Deploy

3. **DigitalOcean** (Full control):
   - Create Droplet
   - SSH and install Node.js
   - Run server with PM2

### Update Android App

1. Change `BASE_URL` to production URL
2. Build release APK
3. Test thoroughly
4. Distribute via Play Store

---

## 📚 Additional Resources

- **MongoDB Atlas Documentation**: https://docs.atlas.mongodb.com/
- **Retrofit Documentation**: https://square.github.io/retrofit/
- **WorkManager Guide**: https://developer.android.com/topic/libraries/architecture/workmanager
- **Room Database**: https://developer.android.com/training/data-storage/room

---

## 🎉 You're All Set!

Your Perfect Fit app now has full cloud sync capabilities with MongoDB! 

**Key Features**:
- ✅ Automatic background sync every 15 minutes
- ✅ Manual sync on demand
- ✅ Offline-first architecture
- ✅ Conflict resolution
- ✅ Cross-device synchronization
- ✅ Cloud backup with MongoDB

**Questions or Issues?**
- Check troubleshooting section above
- Review server logs for errors
- Test API endpoints with curl/Postman
- Verify network connectivity

Happy coding! 🚀

