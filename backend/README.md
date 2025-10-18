# Perfect Fit Backend API

Backend API server for the Perfect Fit Android application with MongoDB integration.

## Features

- RESTful API endpoints for Customers, Orders, and Measurements
- MongoDB database for cloud storage
- Batch sync endpoint for efficient data synchronization
- CORS enabled for cross-origin requests
- Timestamp-based sync for conflict resolution

## Prerequisites

1. **Node.js** (v14 or higher)
   - Download from: https://nodejs.org/
   
2. **MongoDB** 
   - **Option A - Local MongoDB**: Download from https://www.mongodb.com/try/download/community
   - **Option B - MongoDB Atlas** (Free cloud database): Sign up at https://www.mongodb.com/cloud/atlas

## Installation

1. Navigate to the backend directory:
```bash
cd backend
```

2. Install dependencies:
```bash
npm install
```

3. Create `.env` file:
```bash
cp .env.example .env
```

4. Edit `.env` and set your MongoDB connection string:
   - For local MongoDB: `MONGODB_URI=mongodb://localhost:27017`
   - For MongoDB Atlas: `MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/`

## Running the Server

### Development mode (with auto-reload):
```bash
npm run dev
```

### Production mode:
```bash
npm start
```

The server will start on `http://localhost:3000`

## API Endpoints

### Health Check
```
GET /api/health
```

### Batch Sync (Recommended)
```
POST /api/sync/batch
Body: {
  customers: [...],
  orders: [...],
  measurements: [...],
  lastSyncTimestamp: 1234567890
}
```

### Customers
```
GET    /api/customers              - Get all customers
GET    /api/customers/modified     - Get modified customers since timestamp
POST   /api/customers              - Create/update customer
POST   /api/customers/batch        - Batch sync customers
```

### Orders
```
GET    /api/orders                 - Get all orders
GET    /api/orders/modified        - Get modified orders since timestamp
POST   /api/orders                 - Create/update order
POST   /api/orders/batch           - Batch sync orders
```

### Measurements
```
GET    /api/measurements           - Get all measurements
POST   /api/measurements/batch     - Batch sync measurements
```

## Testing the API

Use curl, Postman, or any HTTP client:

```bash
# Health check
curl http://localhost:3000/api/health

# Get all customers
curl http://localhost:3000/api/customers

# Create a customer
curl -X POST http://localhost:3000/api/customers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","mobile":"1234567890","birthDate":"01/01/1990","lastModified":1234567890}'
```

## Connecting Android App

1. **For Android Emulator**:
   - Use `http://10.0.2.2:3000/` (points to host machine's localhost)
   
2. **For Real Device** (on same network):
   - Find your computer's IP address:
     - Mac/Linux: `ifconfig | grep inet`
     - Windows: `ipconfig`
   - Use `http://YOUR_IP:3000/` (e.g., `http://192.168.1.100:3000/`)
   
3. **Update Android App**:
   - Edit `/app/src/main/java/com/example/perfectfit/network/RetrofitClient.kt`
   - Update `BASE_URL` constant with your server URL

## MongoDB Atlas Setup (Free Cloud Database)

1. Create account at https://www.mongodb.com/cloud/atlas/register
2. Create a new cluster (M0 Free tier)
3. Go to Database Access → Add New Database User
4. Go to Network Access → Add IP Address (allow from anywhere: `0.0.0.0/0` for testing)
5. Go to Database → Connect → Connect your application
6. Copy the connection string and paste in `.env` file
7. Replace `<password>` with your database user password

## Maintenance

### Clean Up Duplicate Records

If you have duplicate records in your MongoDB database from previous sync issues, use the cleanup script:

```bash
node cleanup-duplicates.js
```

**What it does:**
- Identifies duplicate records in customers, orders, and measurements collections
- Keeps the most recent version (based on `lastModified` timestamp)
- Removes older duplicates
- Shows a detailed report of what was cleaned up

**Duplicate Detection:**
- **Customers**: Same mobile number
- **Orders**: Same customerId + orderDate + orderType
- **Measurements**: Same customerId (one measurement per customer)

⚠️ **Important**: Always backup your database before running cleanup operations!

## Database Structure

The API automatically creates these collections:

### Customers Collection
```javascript
{
  _id: ObjectId,
  localId: Number,
  firstName: String,
  lastName: String,
  address: String,
  mobile: String,
  alternateMobile: String,
  birthDate: String,
  lastModified: Number (timestamp)
}
```

### Orders Collection
```javascript
{
  _id: ObjectId,
  localId: Number,
  customerId: Number,
  customerName: String,
  orderDate: String,
  orderType: String,
  estimatedDeliveryDate: String,
  instructions: String,
  amount: Number,
  status: String,
  lastModified: Number (timestamp)
}
```

### Measurements Collection
```javascript
{
  _id: ObjectId,
  localId: Number,
  customerId: Number,
  // ... measurement fields
  lastModified: Number (timestamp)
}
```

## Troubleshooting

1. **Port already in use**:
   - Change PORT in `.env` file
   - Or kill the process using port 3000

2. **MongoDB connection failed**:
   - Check if MongoDB is running (local)
   - Verify connection string in `.env`
   - For Atlas: Check network access and user credentials

3. **Android app can't connect**:
   - Verify server is running
   - Check firewall settings
   - Use correct IP address for real devices
   - For emulator, use `10.0.2.2` instead of `localhost`

## Development

- The API uses timestamps (`lastModified`) for sync conflict resolution
- Server always wins conflicts (last-write-wins strategy)
- Batch sync is more efficient than individual requests
- All sync operations are idempotent

## Production Deployment

For production deployment, consider:

1. Deploy to services like:
   - Heroku (https://www.heroku.com/)
   - DigitalOcean (https://www.digitalocean.com/)
   - AWS (https://aws.amazon.com/)
   - Render (https://render.com/)

2. Update Android app's `BASE_URL` to your production URL

3. Enable HTTPS for secure communication

4. Add authentication/authorization middleware

5. Set up proper error logging and monitoring

## License

MIT

