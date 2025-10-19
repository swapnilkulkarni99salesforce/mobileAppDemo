package com.example.perfectfit.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit client singleton for API communication.
 * 
 * This object provides a configured Retrofit instance for making HTTP requests to the backend server.
 * It includes OkHttp configuration with logging, timeouts, and JSON parsing via Gson.
 * 
 * Architecture Pattern: Singleton
 * - Ensures a single Retrofit instance is shared across the app
 * - Reduces memory overhead and improves performance
 * - Thread-safe by using Kotlin object declaration
 * 
 * Network Configuration:
 * - Connect timeout: 30 seconds (time to establish connection)
 * - Read timeout: 30 seconds (time to read response data)
 * - Write timeout: 30 seconds (time to write request data)
 * - Logging: Full request/response body logging (debug only)
 * 
 * TODO - Production Improvements:
 * 1. Move BASE_URL to BuildConfig for environment-specific URLs:
 *    In build.gradle.kts, add:
 *    ```
 *    buildTypes {
 *        debug {
 *            buildConfigField("String", "API_BASE_URL", "\"http://192.168.1.16:3000/\"")
 *        }
 *        release {
 *            buildConfigField("String", "API_BASE_URL", "\"https://api.yourdomain.com/\"")
 *        }
 *    }
 *    buildFeatures {
 *        buildConfig = true
 *    }
 *    ```
 *    Then replace BASE_URL with: BuildConfig.API_BASE_URL
 * 
 * 2. Disable logging in release builds:
 *    Use: if (BuildConfig.DEBUG) { ... } around loggingInterceptor
 * 
 * 3. Add authentication interceptor for token-based auth:
 *    ```
 *    .addInterceptor { chain ->
 *        val request = chain.request().newBuilder()
 *            .addHeader("Authorization", "Bearer $token")
 *            .build()
 *        chain.proceed(request)
 *    }
 *    ```
 * 
 * 4. Add network connectivity check interceptor
 * 5. Implement certificate pinning for enhanced security
 * 
 * @see [ApiService] for available API endpoints
 */
object RetrofitClient {
    
    /**
     * Base URL for the backend API server.
     * 
     * IMPORTANT: Update this based on your environment:
     * 
     * Local Development:
     * - Physical device on same network: http://<YOUR_COMPUTER_IP>:3000/
     * - Android emulator: http://10.0.2.2:3000/ (10.0.2.2 is the host machine from emulator)
     * - DO NOT use localhost or 127.0.0.1 (these point to the Android device itself)
     * 
     * Production:
     * - Use your actual production server URL: https://api.yourdomain.com/
     * - ALWAYS use HTTPS in production for security
     * - Consider using BuildConfig for environment-specific URLs (see class documentation)
     * 
     * Current Setting: Local network testing with computer IP
     */
    private const val BASE_URL = "http://192.168.1.16:3000/"
    
    /**
     * HTTP logging interceptor for debugging network requests/responses.
     * 
     * Log Level: BODY
     * - Logs request and response lines, headers, and body
     * - Useful for debugging API issues
     * - WARNING: In production, use NONE or BASIC to avoid logging sensitive data
     * 
     * Best Practice: Disable body logging in release builds
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        // TODO: In production, use:
        // level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
    }
    
    /**
     * OkHttp client with custom configuration.
     * 
     * Configuration:
     * - Logging interceptor: Logs all network requests/responses
     * - Connect timeout: 30s (time to establish TCP connection)
     * - Read timeout: 30s (time between packets being received)
     * - Write timeout: 30s (time to send request data)
     * 
     * The timeouts are generous to accommodate:
     * - Slow network connections
     * - Large file uploads/downloads
     * - Backend processing time
     * 
     * Adjust timeouts based on your API's performance characteristics.
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    /**
     * Main Retrofit instance configured with:
     * - Base URL for all API endpoints
     * - Custom OkHttp client with logging and timeouts
     * - Gson converter for JSON serialization/deserialization
     * 
     * This instance is lazily initialized and reused for all API calls.
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    /**
     * Main API service instance.
     * 
     * This is the primary way to access the backend API in the app.
     * Retrofit generates the implementation at runtime based on the ApiService interface.
     * 
     * Usage:
     * ```
     * val response = RetrofitClient.apiService.syncCustomers(request)
     * ```
     */
    val apiService: ApiService = retrofit.create(ApiService::class.java)
    
    /**
     * Creates a new API service instance with a custom base URL.
     * 
     * Use Cases:
     * - Testing against different environments
     * - Switching between development/staging/production servers
     * - A/B testing different API versions
     * 
     * Note: This creates a NEW Retrofit instance each time. For most use cases,
     * prefer using the shared `apiService` instance with BuildConfig.
     * 
     * @param baseUrl The custom base URL to use
     * @return A new ApiService instance configured for the given URL
     * 
     * Example:
     * ```
     * val stagingService = RetrofitClient.createService("https://staging-api.yourdomain.com/")
     * ```
     */
    fun createService(baseUrl: String): ApiService {
        val newRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return newRetrofit.create(ApiService::class.java)
    }
}

