# Customer Uniqueness - Complete Reference

## 🔑 Primary Keys

### Android Room Database
```kotlin
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,              // Local primary key (auto-increment)
    val serverId: String? = null, // MongoDB _id reference
    // ... other fields
)
```

### MongoDB
```javascript
{
  _id: ObjectId("507f1f77bcf86cd799439011"), // Automatic primary key
  localId: 123,                               // Android Room id reference
  mobile: "9876543210",                       // Business unique identifier
  // ... other fields
}
```

## 🎯 Business Unique Identifier

### **Composite Key: firstName + lastName + mobile**
A **composite unique key** combining three fields serves as the business-level unique identifier for customers.

**Why composite key?**
- ✅ Allows family members to share same mobile (e.g., husband & wife)
- ✅ Allows different people with same name but different mobiles
- ✅ Prevents exact duplicate entries (same person registered twice)
- ✅ More flexible for real-world tailor shop scenarios

**Database Enforcement:**
```javascript
// MongoDB composite unique index (server.js lines 44-48)
await customersCollection.createIndex(
  { firstName: 1, lastName: 1, mobile: 1 }, 
  { unique: true }
);
```

**Examples:**
```
✅ ALLOWED:
  - Rahul Sharma (9876543210)
  - Priya Sharma (9876543210)  // Different first name, same mobile
  - Rahul Sharma (9988776655)  // Same name, different mobile

❌ REJECTED (Duplicate):
  - Rahul Sharma (9876543210)  // Already exists
```

This ensures:
- No two customers can have the exact combination of firstName + lastName + mobile
- Duplicate insert attempts will fail with a unique constraint violation
- Data integrity is maintained at the database level

## 📊 All Unique Identifiers Summary

| Entity | Unique Identifier | Purpose |
|--------|------------------|---------|
| **Customer** | `firstName` + `lastName` + `mobile` | Composite business identifier |
| **Order** | `customerId` + `orderDate` + `orderType` | Composite key |
| **Measurement** | `customerId` | One measurement per customer |

## 🔄 How Sync Uses These Identifiers

### 1. Initial Creation (Local)
```
User creates customer → Room assigns id=1 → syncStatus=PENDING
```

### 2. First Sync to Server
```
Android sends: { localId: 1, firstName: "Rahul", lastName: "Sharma", mobile: "9876543210", ... }
MongoDB creates: { _id: "abc123", firstName: "Rahul", lastName: "Sharma", mobile: "9876543210", ... }
Server returns: { _id: "abc123", localId: 1, ... }
Android updates: id=1, serverId="abc123", syncStatus=SYNCED
```

### 3. Subsequent Syncs
```
Server checks: Does firstName="Rahul" + lastName="Sharma" + mobile="9876543210" exist?
  ✅ Yes → Update existing record (prevent duplicate)
  ❌ No → Insert new record
```

## ⚠️ Duplicate Prevention Strategy

### Server Side (MongoDB)
1. **Unique index** on `mobile` prevents database-level duplicates
2. **Upsert logic** uses `_id` if provided, otherwise inserts
3. **Sync response** excludes already-processed records

### Client Side (Android Room)
1. Checks if record exists by `localId`
2. Checks if record exists by `serverId`
3. Only inserts if neither exists (truly new record)

## 🛡️ What Happens on Duplicate Composite Key?

### Scenario: User tries to register customer with existing firstName + lastName + mobile

**Current Behavior:**
- Android allows creation (no local validation)
- Sync attempt fails with MongoDB unique constraint error (E11000)
- Record remains unsynced (syncStatus=FAILED)

**Recommended Enhancement:**
Add validation on Android before saving:

```kotlin
// In RegisterCustomerFragment or ViewModel
suspend fun validateAndSaveCustomer(customer: Customer): Boolean {
    // Check locally first using composite key
    val existingLocal = customerDao.getCustomerByCompositeKey(
        customer.firstName,
        customer.lastName,
        customer.mobile
    )
    if (existingLocal != null) {
        return false // Duplicate customer in local DB
    }
    
    // Check server (if online)
    try {
        val response = apiService.checkCustomerExists(
            customer.firstName,
            customer.lastName,
            customer.mobile
        )
        if (response.exists) {
            return false // Duplicate customer on server
        }
    } catch (e: Exception) {
        // If offline, allow creation (will sync later)
    }
    
    // Save customer
    customerDao.insertCustomer(customer)
    return true
}
```

## 🔧 Required DAO Method (✅ Already Added)

Added to `CustomerDao.kt`:

```kotlin
@Query("SELECT * FROM customers WHERE firstName = :firstName AND lastName = :lastName AND mobile = :mobile LIMIT 1")
suspend fun getCustomerByCompositeKey(firstName: String, lastName: String, mobile: String): Customer?
```

## 📱 Recommended UI Changes

### RegisterCustomerFragment
1. Add real-time validation on all three fields
2. Show error if exact customer (firstName + lastName + mobile) already exists
3. Suggest viewing existing customer instead of creating duplicate

```kotlin
// Example validation
private fun validateCustomerUniqueness() {
    lifecycleScope.launch {
        val firstName = binding.firstNameEditText.text.toString().trim()
        val lastName = binding.lastNameEditText.text.toString().trim()
        val mobile = binding.mobileEditText.text.toString().trim()
        
        if (firstName.isNotEmpty() && lastName.isNotEmpty() && mobile.length >= 10) {
            val exists = customerDao.getCustomerByCompositeKey(firstName, lastName, mobile)
            if (exists != null) {
                binding.saveButton.isEnabled = false
                Toast.makeText(
                    requireContext(),
                    "Customer '$firstName $lastName' with mobile $mobile already exists!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                binding.saveButton.isEnabled = true
            }
        }
    }
}

// Call this when any of the three fields change
binding.firstNameEditText.addTextChangedListener { validateCustomerUniqueness() }
binding.lastNameEditText.addTextChangedListener { validateCustomerUniqueness() }
binding.mobileEditText.addTextChangedListener { validateCustomerUniqueness() }
```

## 🧪 Testing Uniqueness

### Test Case 1: Same name, same mobile (Exact duplicate)
```
1. Create customer "Rahul Sharma" with mobile "9876543210"
2. Try to create another "Rahul Sharma" with mobile "9876543210"
3. Expected: ❌ Validation error prevents creation
```

### Test Case 2: Different name, same mobile (Family members)
```
1. Create customer "Rahul Sharma" with mobile "9876543210"
2. Create customer "Priya Sharma" with mobile "9876543210"
3. Expected: ✅ Both allowed (different first names)
```

### Test Case 3: Same name, different mobile
```
1. Create customer "Rahul Sharma" with mobile "9876543210"
2. Create customer "Rahul Sharma" with mobile "9988776655"
3. Expected: ✅ Both allowed (different mobiles)
```

### Test Case 4: Sync duplicate to server
```
1. Create customer "Rahul Sharma" with mobile "9876543210"
2. Sync successfully
3. Clear app data and reinstall
4. Create same customer "Rahul Sharma" with mobile "9876543210"
5. Try to sync
6. Expected: ❌ MongoDB unique constraint error (E11000)
```

### Test Case 5: Case sensitivity check
```
1. Create customer "rahul sharma" with mobile "9876543210"
2. Try to create "Rahul Sharma" with mobile "9876543210"
3. Expected: ✅ Currently allowed (case-sensitive)
Note: Consider normalizing names to prevent this scenario
```

## 📝 Migration for Existing Duplicates

If you already have customers with duplicate composite keys:

```bash
# Step 1: Clean up duplicates (uses new composite key logic)
cd backend
node cleanup-duplicates.js

# Step 2: Restart server (applies unique constraint)
npm start

# Step 3: Verify no duplicates remain
mongo
> use perfectfit_db
> db.customers.aggregate([
    { 
      $group: { 
        _id: { 
          firstName: "$firstName", 
          lastName: "$lastName", 
          mobile: "$mobile" 
        }, 
        count: { $sum: 1 } 
      } 
    },
    { $match: { count: { $gt: 1 } } }
  ])
```

Should return empty array `[]` if no duplicates.

## 🎯 Best Practices

1. ✅ Always validate composite key uniqueness before saving
2. ✅ Show user-friendly errors for exact duplicate customers
3. ✅ Provide option to view/edit existing customer instead
4. ✅ Keep sync logic idempotent (safe to run multiple times)
5. ✅ Log duplicate detection for debugging
6. ✅ Handle sync errors gracefully (show user what failed)
7. ✅ Consider normalizing names (lowercase, trim) to avoid case-sensitivity issues
8. ✅ Allow family members to share mobile numbers (different names)

## 📚 Related Files

- **Customer Model**: `app/src/main/java/com/example/perfectfit/models/Customer.kt`
- **Customer DAO**: `app/src/main/java/com/example/perfectfit/database/CustomerDao.kt`
- **Sync Manager**: `app/src/main/java/com/example/perfectfit/sync/SyncManager.kt`
- **Backend Server**: `backend/server.js`
- **Cleanup Script**: `backend/cleanup-duplicates.js`

---

**Key Takeaway**: The **composite key of `firstName` + `lastName` + `mobile`** is the unique business identifier for customers. This allows flexibility for family members sharing mobile numbers while preventing exact duplicate entries. All duplicate prevention logic is built around this composite key.

