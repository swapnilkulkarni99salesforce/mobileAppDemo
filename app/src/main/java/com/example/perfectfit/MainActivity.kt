package com.example.perfectfit

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.perfectfit.databinding.ActivityMainBinding
import com.example.perfectfit.utils.NotificationHelper
import com.example.perfectfit.workers.BirthdayAlertWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Main Activity for the Perfect Fit tailoring application.
 * 
 * This is the single-activity host that manages navigation between different fragments
 * using a bottom navigation bar. The app follows the single-activity architecture pattern
 * where all screens are implemented as fragments.
 * 
 * Navigation Structure:
 * - Home: Dashboard with statistics, alerts, and quick actions
 * - Customers: List of all customers with search functionality
 * - Orders: List of all orders with filtering options
 * 
 * Features:
 * - Material Design 3 themed UI with custom toolbar
 * - Status bar color matching app theme
 * - Notification channels for order alerts and reminders
 * - Fragment transaction management with proper lifecycle handling
 * 
 * Note: This activity uses ViewBinding for type-safe view access, eliminating
 * the need for findViewById() calls.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding for type-safe view access
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Configure UI appearance
        setupStatusBar()
        setupToolbar()
        
        // Initialize system services
        initializeNotificationChannels()
        scheduleBirthdayAlertWorker()

        // Configure navigation
        setupBottomNavigation()

        // Load initial fragment (only on fresh start, not on configuration changes)
        if (savedInstanceState == null) {
            loadInitialFragment()
        }
    }
    
    /**
     * Configures the status bar appearance to match the app theme.
     * The status bar color is set to the primary theme color for visual consistency.
     */
    private fun setupStatusBar() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.md_theme_light_primary)
    }
    
    /**
     * Sets up the toolbar with custom configuration.
     * The title is hidden because we use a logo in the toolbar layout instead.
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)  // Hide text title, show logo instead
        }
    }
    
    /**
     * Initializes notification channels required for Android O and above.
     * Channels are used for order reminders, delivery alerts, and other notifications.
     */
    private fun initializeNotificationChannels() {
        NotificationHelper.createNotificationChannels(this)
    }
    
    /**
     * Schedules daily birthday alert worker to check for customer birthdays.
     * 
     * The worker runs once per day at 9:00 AM to:
     * - Check for customers with birthdays today
     * - Send WhatsApp birthday wishes
     * - Notify shop owner about birthdays
     * 
     * Uses KEEP policy to preserve existing schedule if already set.
     */
    private fun scheduleBirthdayAlertWorker() {
        // Calculate initial delay to run at 9:00 AM
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        
        // If 9 AM already passed today, schedule for tomorrow
        if (currentTime.after(targetTime)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis
        
        // Create periodic work request (runs daily)
        val birthdayWorkRequest = PeriodicWorkRequestBuilder<BirthdayAlertWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()
        
        // Enqueue the work (KEEP means don't replace if already scheduled)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            BirthdayAlertWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            birthdayWorkRequest
        )
    }

    /**
     * Configures the bottom navigation bar with item selection handling.
     * Uses setOnItemSelectedListener for Material 3 compatibility (replacing deprecated setOnNavigationItemSelectedListener).
     */
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_customers -> {
                    loadFragment(CustomersFragment())
                    true
                }
                R.id.navigation_orders -> {
                    loadFragment(OrdersFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    /**
     * Loads the initial fragment (Home) on app launch.
     * This is only called on fresh start, not on configuration changes like rotation.
     */
    private fun loadInitialFragment() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_home
    }

    /**
     * Loads a fragment into the main container, replacing any existing fragment.
     * 
     * This method performs a simple replace transaction without adding to back stack,
     * as bottom navigation tabs should not participate in back stack navigation.
     * 
     * Note: For nested navigation (e.g., detail screens), child fragments should
     * use addToBackStack(null) in their transactions.
     * 
     * @param fragment The fragment to load
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    /**
     * Handle back press behavior.
     * If we're not on the home fragment, navigate to home instead of exiting the app.
     */
    @Deprecated("Deprecated in Java", ReplaceWith("onBackPressedDispatcher.onBackPressed()"))
    override fun onBackPressed() {
        // Check if we're on the home fragment
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        
        if (currentFragment is HomeFragment) {
            // If on home, exit the app
            super.onBackPressed()
        } else {
            // Otherwise, navigate to home
            binding.bottomNavigation.selectedItemId = R.id.navigation_home
        }
    }
}

