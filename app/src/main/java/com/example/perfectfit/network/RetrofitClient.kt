package com.example.perfectfit.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // TODO: Replace with your actual server URL
    // For local testing: Use your computer's IP (not localhost/127.0.0.1 from Android)
    // Example: http://192.168.1.100:3000/
    // For production: https://your-domain.com/
    // For Android emulator: http://10.0.2.2:3000/
    // For real device on same network: Use your backend server machine IP
    private const val BASE_URL = "http://192.168.1.16:3000/" // Backend server IP address
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
    
    /**
     * Update the base URL dynamically (for testing or different environments)
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

