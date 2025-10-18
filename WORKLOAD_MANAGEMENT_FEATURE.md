# Work Order Management Feature

## Overview
This document describes the new Work Order Management feature that enables automatic estimation of delivery dates based on workload configuration and pending orders.

## Feature Components

### 1. Workload Configuration
The workload configuration allows you to set up your work schedule and order completion times.

**Location**: Home Fragment → Actions Section → "⚙️ Workload Configuration" button

**Configuration Options**:
- **Time per Order**: How many hours it takes to complete a single order (default: 2.0 hours)
- **Weekly Working Hours**: Working hours for each day of the week:
  - Monday (default: 8.0 hours)
  - Tuesday (default: 8.0 hours)
  - Wednesday (default: 8.0 hours)
  - Thursday (default: 8.0 hours)
  - Friday (default: 8.0 hours)
  - Saturday (default: 4.0 hours)
  - Sunday (default: 0.0 hours)

### 2. Automatic Delivery Date Calculation
When creating a new order, the system automatically calculates and suggests an estimated delivery date based on:
- Number of pending orders (orders with status "Pending" or "In Progress")
- Configured time required to complete a single order
- Your weekly working schedule

**How it works**:
1. Counts all pending/in-progress orders in the system
2. Calculates total hours needed: (pending orders + 1 new order) × time per order
3. Starting from today, iterates through each day and deducts available working hours
4. Returns the date when all required hours are accounted for
5. Auto-populates the estimated delivery date field in the Create Order form

### 3. Database Schema

#### WorkloadConfig Entity
```kotlin
@Entity(tableName = "workload_config")
data class WorkloadConfig(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timePerOrderHours: Float = 2.0f,
    val mondayHours: Float = 8.0f,
    val tuesdayHours: Float = 8.0f,
    val wednesdayHours: Float = 8.0f,
    val thursdayHours: Float = 8.0f,
    val fridayHours: Float = 8.0f,
    val saturdayHours: Float = 4.0f,
    val sundayHours: Float = 0.0f
)
```

**Database Version**: Updated from v7 to v8

### 4. Files Created/Modified

#### New Files:
- `app/src/main/java/com/example/perfectfit/models/WorkloadConfig.kt`
- `app/src/main/java/com/example/perfectfit/database/WorkloadConfigDao.kt`
- `app/src/main/java/com/example/perfectfit/WorkloadConfigFragment.kt`
- `app/src/main/res/layout/fragment_workload_config.xml`

#### Modified Files:
- `app/src/main/java/com/example/perfectfit/database/AppDatabase.kt`
  - Added WorkloadConfig entity
  - Updated database version to 8
  - Added workloadConfigDao() method

- `app/src/main/java/com/example/perfectfit/HomeFragment.kt`
  - Added "Workload Configuration" button click listener
  - Added navigation method to WorkloadConfigFragment

- `app/src/main/res/layout/fragment_home.xml`
  - Added "Workload Configuration" button in Actions section

- `app/src/main/java/com/example/perfectfit/CreateOrderFragment.kt`
  - Added automatic delivery date calculation
  - Imports WorkloadConfig model
  - Calls calculateAndSetEstimatedDeliveryDate() on view creation

## Usage Instructions

### Setting Up Workload Configuration
1. Open the app and navigate to the Home screen
2. Scroll to the "Quick Actions" section
3. Click on "⚙️ Workload Configuration"
4. Enter your time per order (in hours)
5. Set your working hours for each day of the week
6. View the summary showing total weekly hours and orders per week
7. Click "💾 Save Configuration"

### Creating Orders with Auto-Calculated Delivery Dates
1. Navigate to a customer's detail page
2. Click "Create New Order"
3. The estimated delivery date will be automatically calculated and populated
4. The system shows a message like: "Your estimated delivery date is in X days on DD/MM/YYYY"
5. You can still manually change the date if needed by clicking the calendar icon

## Technical Details

### Delivery Date Calculation Algorithm
```kotlin
fun calculateDeliveryDate(pendingOrdersCount: Int, config: WorkloadConfig): Calendar {
    // Calculate total hours needed for all pending orders + this new order
    val totalOrdersToComplete = pendingOrdersCount + 1
    val totalHoursNeeded = totalOrdersToComplete * config.timePerOrderHours
    
    val currentDate = Calendar.getInstance()
    var hoursRemaining = totalHoursNeeded
    
    // Start from today and add working hours day by day
    while (hoursRemaining > 0) {
        val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)
        val workingHoursToday = config.getHoursForDay(dayOfWeek)
        
        // Deduct the working hours for this day
        hoursRemaining -= workingHoursToday
        
        // If we still have hours remaining, move to the next day
        if (hoursRemaining > 0) {
            currentDate.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    
    return currentDate
}
```

### Default Configuration
If no configuration is saved, the system uses these defaults:
- Time per order: 2.0 hours
- Monday-Friday: 8.0 hours each
- Saturday: 4.0 hours
- Sunday: 0.0 hours (holiday)

### Order Status Filtering
The system considers only orders with the following statuses as "pending":
- "Pending"
- "In Progress"

Orders with status "Completed", "Delivered", or "Closed" are not counted.

## Benefits

1. **Realistic Delivery Dates**: Based on actual workload and capacity
2. **Better Customer Communication**: Provide accurate delivery expectations
3. **Workload Management**: Visual summary of capacity (orders per week)
4. **Flexible Scheduling**: Configure different hours for different days
5. **Automatic Calculation**: No manual date calculation needed

## Future Enhancements (Suggestions)

1. **Holiday Management**: Add ability to mark specific dates as holidays
2. **Rush Orders**: Option to prioritize certain orders
3. **Multiple Workers**: Support for multiple tailors with different schedules
4. **Order Type Variations**: Different completion times for Blouse vs Kurti orders
5. **Historical Analytics**: Track actual vs estimated completion times
6. **Capacity Alerts**: Warn when workload exceeds capacity
7. **Calendar View**: Visual representation of scheduled orders

## Testing

To test the feature:
1. Configure your workload settings
2. Create multiple test orders and mark them as "Pending"
3. Create a new order and observe the auto-calculated delivery date
4. Verify the date considers working hours correctly
5. Test with different configurations (0 hours on certain days, etc.)
6. Test edge cases (all days with 0 hours, very large time per order, etc.)

## Troubleshooting

**Issue**: Delivery date not auto-populated
- **Solution**: Ensure workload configuration is saved (go to Workload Configuration and save)

**Issue**: Unrealistic delivery dates
- **Solution**: Check your workload configuration, especially time per order and weekly hours

**Issue**: Database error after update
- **Solution**: The app uses fallbackToDestructiveMigration, so data will be reset on version update

## Support

For issues or questions about this feature, please refer to the main README.md or contact the development team.

