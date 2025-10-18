const express = require('express');
const { MongoClient, ObjectId } = require('mongodb');
const cors = require('cors');
const bodyParser = require('body-parser');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// MongoDB Configuration
// Replace with your MongoDB connection string
// For local: mongodb://localhost:27017
// For MongoDB Atlas: mongodb+srv://username:password@cluster.mongodb.net/
const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017';
const DB_NAME = 'perfectfit_db';

let db;
let customersCollection;
let ordersCollection;
let measurementsCollection;

// Connect to MongoDB
async function connectToDatabase() {
  try {
    const client = await MongoClient.connect(MONGODB_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true
    });
    
    console.log('✅ Connected to MongoDB');
    db = client.db(DB_NAME);
    customersCollection = db.collection('customers');
    ordersCollection = db.collection('orders');
    measurementsCollection = db.collection('measurements');
    
    // Create indexes for better performance
    await customersCollection.createIndex({ lastModified: -1 });
    await customersCollection.createIndex({ mobile: 1 }); // Index for faster queries
    // Composite unique key: firstName + lastName + mobile
    await customersCollection.createIndex(
      { firstName: 1, lastName: 1, mobile: 1 }, 
      { unique: true }
    );
    await ordersCollection.createIndex({ lastModified: -1 });
    await ordersCollection.createIndex({ customerId: 1 });
    await measurementsCollection.createIndex({ lastModified: -1 });
    await measurementsCollection.createIndex({ customerId: 1 });
    
    console.log('✅ Database indexes created');
  } catch (error) {
    console.error('❌ MongoDB connection error:', error);
    process.exit(1); // Exit if can't connect to database
  }
}

// ============= HEALTH CHECK =============
app.get('/api/health', (req, res) => {
  res.json({
    status: 'ok',
    message: 'Server is running',
    timestamp: Date.now()
  });
});

// ============= BATCH SYNC ENDPOINT =============
app.post('/api/sync/batch', async (req, res) => {
  try {
    const { customers = [], orders = [], measurements = [], lastSyncTimestamp = 0 } = req.body;
    const serverTimestamp = Date.now();
    
    console.log(`Batch sync request: ${customers.length} customers, ${orders.length} orders, ${measurements.length} measurements`);
    
    // Track processed IDs to avoid duplicates
    const processedCustomerIds = new Set();
    const processedOrderIds = new Set();
    const processedMeasurementIds = new Set();
    
    // Process customers
    const processedCustomers = [];
    for (const customer of customers) {
      const result = await upsertCustomer(customer);
      processedCustomers.push(result);
      processedCustomerIds.add(result._id.toString());
    }
    
    // Get new/updated customers from server (excluding just processed ones)
    const updatedCustomers = await customersCollection
      .find({ 
        lastModified: { $gt: lastSyncTimestamp },
        _id: { $nin: Array.from(processedCustomerIds).map(id => new ObjectId(id)) }
      })
      .toArray();
    
    // Process orders
    const processedOrders = [];
    for (const order of orders) {
      const result = await upsertOrder(order);
      processedOrders.push(result);
      processedOrderIds.add(result._id.toString());
    }
    
    // Get new/updated orders from server (excluding just processed ones)
    const updatedOrders = await ordersCollection
      .find({ 
        lastModified: { $gt: lastSyncTimestamp },
        _id: { $nin: Array.from(processedOrderIds).map(id => new ObjectId(id)) }
      })
      .toArray();
    
    // Process measurements
    const processedMeasurements = [];
    for (const measurement of measurements) {
      const result = await upsertMeasurement(measurement);
      processedMeasurements.push(result);
      processedMeasurementIds.add(result._id.toString());
    }
    
    // Get new/updated measurements from server (excluding just processed ones)
    const updatedMeasurements = await measurementsCollection
      .find({ 
        lastModified: { $gt: lastSyncTimestamp },
        _id: { $nin: Array.from(processedMeasurementIds).map(id => new ObjectId(id)) }
      })
      .toArray();
    
    // Merge processed and updated data (no duplicates now)
    const allCustomers = [...processedCustomers, ...updatedCustomers];
    const allOrders = [...processedOrders, ...updatedOrders];
    const allMeasurements = [...processedMeasurements, ...updatedMeasurements];
    
    console.log(`Sync response: ${allCustomers.length} customers, ${allOrders.length} orders, ${allMeasurements.length} measurements`);
    
    res.json({
      success: true,
      customers: allCustomers.map(formatCustomer),
      orders: allOrders.map(formatOrder),
      measurements: allMeasurements.map(formatMeasurement),
      serverTimestamp
    });
    
  } catch (error) {
    console.error('Batch sync error:', error);
    res.status(500).json({
      success: false,
      message: error.message,
      serverTimestamp: Date.now()
    });
  }
});

// ============= CUSTOMER ENDPOINTS =============
app.get('/api/customers', async (req, res) => {
  try {
    const customers = await customersCollection.find({}).toArray();
    res.json(customers.map(formatCustomer));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.get('/api/customers/modified', async (req, res) => {
  try {
    const since = parseInt(req.query.since) || 0;
    const customers = await customersCollection
      .find({ lastModified: { $gt: since } })
      .toArray();
    res.json(customers.map(formatCustomer));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.post('/api/customers', async (req, res) => {
  try {
    const customer = req.body;
    const result = await upsertCustomer(customer);
    res.json(formatCustomer(result));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.post('/api/customers/batch', async (req, res) => {
  try {
    const { data, lastSyncTimestamp = 0 } = req.body;
    const processed = [];
    const processedIds = new Set();
    
    for (const customer of data) {
      const result = await upsertCustomer(customer);
      processed.push(result);
      processedIds.add(result._id.toString());
    }
    
    // Exclude just processed records to avoid duplicates
    const updated = await customersCollection
      .find({ 
        lastModified: { $gt: lastSyncTimestamp },
        _id: { $nin: Array.from(processedIds).map(id => new ObjectId(id)) }
      })
      .toArray();
    
    const all = [...processed, ...updated];
    
    res.json({
      success: true,
      data: all.map(formatCustomer),
      serverTimestamp: Date.now()
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: error.message,
      serverTimestamp: Date.now()
    });
  }
});

// ============= ORDER ENDPOINTS =============
app.get('/api/orders', async (req, res) => {
  try {
    const orders = await ordersCollection.find({}).toArray();
    res.json(orders.map(formatOrder));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.get('/api/orders/modified', async (req, res) => {
  try {
    const since = parseInt(req.query.since) || 0;
    const orders = await ordersCollection
      .find({ lastModified: { $gt: since } })
      .toArray();
    res.json(orders.map(formatOrder));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.post('/api/orders', async (req, res) => {
  try {
    const order = req.body;
    const result = await upsertOrder(order);
    res.json(formatOrder(result));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.post('/api/orders/batch', async (req, res) => {
  try {
    const { data, lastSyncTimestamp = 0 } = req.body;
    const processed = [];
    const processedIds = new Set();
    
    for (const order of data) {
      const result = await upsertOrder(order);
      processed.push(result);
      processedIds.add(result._id.toString());
    }
    
    // Exclude just processed records to avoid duplicates
    const updated = await ordersCollection
      .find({ 
        lastModified: { $gt: lastSyncTimestamp },
        _id: { $nin: Array.from(processedIds).map(id => new ObjectId(id)) }
      })
      .toArray();
    
    const all = [...processed, ...updated];
    
    res.json({
      success: true,
      data: all.map(formatOrder),
      serverTimestamp: Date.now()
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: error.message,
      serverTimestamp: Date.now()
    });
  }
});

// ============= MEASUREMENT ENDPOINTS =============
app.get('/api/measurements', async (req, res) => {
  try {
    const measurements = await measurementsCollection.find({}).toArray();
    res.json(measurements.map(formatMeasurement));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.post('/api/measurements/batch', async (req, res) => {
  try {
    const { data, lastSyncTimestamp = 0 } = req.body;
    const processed = [];
    const processedIds = new Set();
    
    for (const measurement of data) {
      const result = await upsertMeasurement(measurement);
      processed.push(result);
      processedIds.add(result._id.toString());
    }
    
    // Exclude just processed records to avoid duplicates
    const updated = await measurementsCollection
      .find({ 
        lastModified: { $gt: lastSyncTimestamp },
        _id: { $nin: Array.from(processedIds).map(id => new ObjectId(id)) }
      })
      .toArray();
    
    const all = [...processed, ...updated];
    
    res.json({
      success: true,
      data: all.map(formatMeasurement),
      serverTimestamp: Date.now()
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: error.message,
      serverTimestamp: Date.now()
    });
  }
});

// ============= HELPER FUNCTIONS =============
async function upsertCustomer(customer) {
  const { _id, localId, ...customerData } = customer;
  customerData.lastModified = Date.now();
  
  if (_id) {
    // Update existing
    await customersCollection.updateOne(
      { _id: new ObjectId(_id) },
      { $set: customerData }
    );
    return await customersCollection.findOne({ _id: new ObjectId(_id) });
  } else {
    // Insert new
    const result = await customersCollection.insertOne(customerData);
    return await customersCollection.findOne({ _id: result.insertedId });
  }
}

async function upsertOrder(order) {
  const { _id, localId, ...orderData } = order;
  orderData.lastModified = Date.now();
  
  if (_id) {
    await ordersCollection.updateOne(
      { _id: new ObjectId(_id) },
      { $set: orderData }
    );
    return await ordersCollection.findOne({ _id: new ObjectId(_id) });
  } else {
    const result = await ordersCollection.insertOne(orderData);
    return await ordersCollection.findOne({ _id: result.insertedId });
  }
}

async function upsertMeasurement(measurement) {
  const { _id, localId, ...measurementData } = measurement;
  measurementData.lastModified = Date.now();
  
  if (_id) {
    await measurementsCollection.updateOne(
      { _id: new ObjectId(_id) },
      { $set: measurementData }
    );
    return await measurementsCollection.findOne({ _id: new ObjectId(_id) });
  } else {
    const result = await measurementsCollection.insertOne(measurementData);
    return await measurementsCollection.findOne({ _id: result.insertedId });
  }
}

function formatCustomer(customer) {
  return {
    _id: customer._id.toString(),
    localId: customer.localId,
    firstName: customer.firstName,
    lastName: customer.lastName,
    address: customer.address || '',
    mobile: customer.mobile,
    alternateMobile: customer.alternateMobile || '',
    birthDate: customer.birthDate,
    lastModified: customer.lastModified
  };
}

function formatOrder(order) {
  return {
    _id: order._id.toString(),
    localId: order.localId,
    customerId: order.customerId,
    customerName: order.customerName,
    orderDate: order.orderDate,
    orderType: order.orderType,
    estimatedDeliveryDate: order.estimatedDeliveryDate,
    instructions: order.instructions || '',
    amount: order.amount,
    status: order.status,
    lastModified: order.lastModified
  };
}

function formatMeasurement(measurement) {
  return {
    _id: measurement._id.toString(),
    localId: measurement.localId,
    customerId: measurement.customerId,
    ...measurement,
    _id: measurement._id.toString() // Override to ensure string format
  };
}

// Start server after database connection
async function startServer() {
  await connectToDatabase();
  
  app.listen(PORT, () => {
    console.log(`✅ Server running on port ${PORT}`);
    console.log(`📡 API endpoint: http://localhost:${PORT}/api`);
    console.log(`🗄️  MongoDB: ${MONGODB_URI}`);
    console.log(`📊 Database: ${DB_NAME}`);
  });
}

startServer();

