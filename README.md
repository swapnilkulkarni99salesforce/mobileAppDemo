# Perfect Fit Android App

A Kotlin-based Android application for business management with customer and order tracking.

## Features

- **Bottom Navigation**: Easy navigation between Home, Customers, and Orders screens
- **Top App Bar**: Displays "Perfect Fit" branding
- **Home Screen**: Welcome dashboard with quick overview
- **Customers Screen**: List of customers with contact information
- **Orders Screen**: Order tracking with status and details

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Material Design Components
- **Architecture**: MVVM with ViewBinding
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/perfectfit/
│   │   ├── MainActivity.kt
│   │   ├── HomeFragment.kt
│   │   ├── CustomersFragment.kt
│   │   ├── OrdersFragment.kt
│   │   ├── adapters/
│   │   │   ├── CustomersAdapter.kt
│   │   │   └── OrdersAdapter.kt
│   │   └── models/
│   │       ├── Customer.kt
│   │       └── Order.kt
│   └── res/
│       ├── layout/
│       ├── menu/
│       ├── drawable/
│       └── values/
```

## Building the Project

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 8 or later
- Android SDK with API 34

### Build Steps

1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device

```bash
./gradlew build
```

### Run on Device/Emulator

1. Connect an Android device or start an emulator
2. Click "Run" in Android Studio or use:

```bash
./gradlew installDebug
```

## Screens

### Home
Welcome screen with app overview and quick stats dashboard.

### Customers
Displays a scrollable list of customers with:
- Customer name
- Email address
- Phone number

### Orders
Shows order list with:
- Order ID
- Customer name
- Order date
- Amount
- Status badge

## Dependencies

- AndroidX Core KTX
- Material Components
- ConstraintLayout
- Navigation Components
- Lifecycle Components
- RecyclerView

## Future Enhancements

- Add database persistence (Room)
- Implement add/edit/delete functionality
- Add search and filter features
- Integrate with backend API
- Add authentication
- Generate reports and analytics

## License

This is a demo application for learning purposes.

