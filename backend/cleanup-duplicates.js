/**
 * MongoDB Duplicate Cleanup Script
 * 
 * This script removes duplicate records from the PerfectFit database.
 * It keeps the most recent version of each record based on lastModified timestamp.
 * 
 * Usage:
 *   node cleanup-duplicates.js
 */

const { MongoClient, ObjectId } = require('mongodb');
require('dotenv').config();

const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017';
const DB_NAME = 'perfectfit_db';

async function cleanupDuplicates() {
  let client;
  
  try {
    // Connect to MongoDB
    client = await MongoClient.connect(MONGODB_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true
    });
    
    console.log('✅ Connected to MongoDB');
    const db = client.db(DB_NAME);
    
    // Cleanup customers
    await cleanupCustomerDuplicates(db);
    
    // Cleanup orders
    await cleanupOrderDuplicates(db);
    
    // Cleanup measurements
    await cleanupMeasurementDuplicates(db);
    
    console.log('\n✅ Cleanup completed successfully!');
    
  } catch (error) {
    console.error('❌ Error during cleanup:', error);
  } finally {
    if (client) {
      await client.close();
      console.log('📡 Disconnected from MongoDB');
    }
  }
}

async function cleanupCustomerDuplicates(db) {
  console.log('\n🔍 Checking customers for duplicates...');
  const customersCollection = db.collection('customers');
  
  // Find duplicates by mobile number (primary unique identifier)
  const pipeline = [
    {
      $group: {
        _id: '$mobile',
        count: { $sum: 1 },
        records: { $push: { id: '$_id', lastModified: '$lastModified', firstName: '$firstName', lastName: '$lastName' } }
      }
    },
    {
      $match: {
        count: { $gt: 1 }
      }
    }
  ];
  
  const duplicates = await customersCollection.aggregate(pipeline).toArray();
  
  if (duplicates.length === 0) {
    console.log('✅ No duplicate customers found');
    return;
  }
  
  console.log(`⚠️  Found ${duplicates.length} sets of duplicate customers`);
  
  let totalRemoved = 0;
  for (const dup of duplicates) {
    // Sort by lastModified descending to keep the most recent
    dup.records.sort((a, b) => b.lastModified - a.lastModified);
    
    const keepRecord = dup.records[0];
    const removeRecords = dup.records.slice(1);
    
    console.log(`  - Mobile: ${dup._id} (${dup.records[0].firstName} ${dup.records[0].lastName})`);
    console.log(`    Keeping: ${keepRecord.id} (${new Date(keepRecord.lastModified).toLocaleString()})`);
    console.log(`    Removing ${removeRecords.length} duplicate(s)`);
    
    // Delete duplicates
    const idsToRemove = removeRecords.map(r => new ObjectId(r.id));
    const result = await customersCollection.deleteMany({ _id: { $in: idsToRemove } });
    totalRemoved += result.deletedCount;
  }
  
  console.log(`✅ Removed ${totalRemoved} duplicate customer records`);
}

async function cleanupOrderDuplicates(db) {
  console.log('\n🔍 Checking orders for duplicates...');
  const ordersCollection = db.collection('orders');
  
  // Find duplicates by customerId + orderDate + orderType combination
  const pipeline = [
    {
      $group: {
        _id: {
          customerId: '$customerId',
          orderDate: '$orderDate',
          orderType: '$orderType'
        },
        count: { $sum: 1 },
        records: { $push: { id: '$_id', lastModified: '$lastModified', customerName: '$customerName' } }
      }
    },
    {
      $match: {
        count: { $gt: 1 }
      }
    }
  ];
  
  const duplicates = await ordersCollection.aggregate(pipeline).toArray();
  
  if (duplicates.length === 0) {
    console.log('✅ No duplicate orders found');
    return;
  }
  
  console.log(`⚠️  Found ${duplicates.length} sets of duplicate orders`);
  
  let totalRemoved = 0;
  for (const dup of duplicates) {
    // Sort by lastModified descending to keep the most recent
    dup.records.sort((a, b) => b.lastModified - a.lastModified);
    
    const keepRecord = dup.records[0];
    const removeRecords = dup.records.slice(1);
    
    console.log(`  - Order: ${dup._id.customerName} (${dup._id.orderType})`);
    console.log(`    Keeping: ${keepRecord.id} (${new Date(keepRecord.lastModified).toLocaleString()})`);
    console.log(`    Removing ${removeRecords.length} duplicate(s)`);
    
    // Delete duplicates
    const idsToRemove = removeRecords.map(r => new ObjectId(r.id));
    const result = await ordersCollection.deleteMany({ _id: { $in: idsToRemove } });
    totalRemoved += result.deletedCount;
  }
  
  console.log(`✅ Removed ${totalRemoved} duplicate order records`);
}

async function cleanupMeasurementDuplicates(db) {
  console.log('\n🔍 Checking measurements for duplicates...');
  const measurementsCollection = db.collection('measurements');
  
  // Find duplicates by customerId (each customer should have only one measurement)
  const pipeline = [
    {
      $group: {
        _id: '$customerId',
        count: { $sum: 1 },
        records: { $push: { id: '$_id', lastModified: '$lastModified' } }
      }
    },
    {
      $match: {
        count: { $gt: 1 }
      }
    }
  ];
  
  const duplicates = await measurementsCollection.aggregate(pipeline).toArray();
  
  if (duplicates.length === 0) {
    console.log('✅ No duplicate measurements found');
    return;
  }
  
  console.log(`⚠️  Found ${duplicates.length} sets of duplicate measurements`);
  
  let totalRemoved = 0;
  for (const dup of duplicates) {
    // Sort by lastModified descending to keep the most recent
    dup.records.sort((a, b) => b.lastModified - a.lastModified);
    
    const keepRecord = dup.records[0];
    const removeRecords = dup.records.slice(1);
    
    console.log(`  - CustomerId: ${dup._id}`);
    console.log(`    Keeping: ${keepRecord.id} (${new Date(keepRecord.lastModified).toLocaleString()})`);
    console.log(`    Removing ${removeRecords.length} duplicate(s)`);
    
    // Delete duplicates
    const idsToRemove = removeRecords.map(r => new ObjectId(r.id));
    const result = await measurementsCollection.deleteMany({ _id: { $in: idsToRemove } });
    totalRemoved += result.deletedCount;
  }
  
  console.log(`✅ Removed ${totalRemoved} duplicate measurement records`);
}

// Run the cleanup
cleanupDuplicates();

