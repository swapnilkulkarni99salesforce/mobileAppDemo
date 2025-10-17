package com.example.perfectfit.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Batch sync - sync all data in one request
    @POST("api/sync/batch")
    suspend fun batchSync(@Body request: BatchSyncRequest): Response<BatchSyncResponse>
    
    // Individual entity endpoints
    
    // Customers
    @GET("api/customers")
    suspend fun getAllCustomers(): Response<List<ApiCustomer>>
    
    @GET("api/customers/modified")
    suspend fun getModifiedCustomers(@Query("since") timestamp: Long): Response<List<ApiCustomer>>
    
    @POST("api/customers")
    suspend fun createCustomer(@Body customer: ApiCustomer): Response<ApiCustomer>
    
    @PUT("api/customers/{id}")
    suspend fun updateCustomer(@Path("id") id: String, @Body customer: ApiCustomer): Response<ApiCustomer>
    
    @POST("api/customers/batch")
    suspend fun syncCustomers(@Body request: SyncRequest<ApiCustomer>): Response<SyncResponse<ApiCustomer>>
    
    // Orders
    @GET("api/orders")
    suspend fun getAllOrders(): Response<List<ApiOrder>>
    
    @GET("api/orders/modified")
    suspend fun getModifiedOrders(@Query("since") timestamp: Long): Response<List<ApiOrder>>
    
    @POST("api/orders")
    suspend fun createOrder(@Body order: ApiOrder): Response<ApiOrder>
    
    @PUT("api/orders/{id}")
    suspend fun updateOrder(@Path("id") id: String, @Body order: ApiOrder): Response<ApiOrder>
    
    @POST("api/orders/batch")
    suspend fun syncOrders(@Body request: SyncRequest<ApiOrder>): Response<SyncResponse<ApiOrder>>
    
    // Measurements
    @GET("api/measurements")
    suspend fun getAllMeasurements(): Response<List<ApiMeasurement>>
    
    @GET("api/measurements/modified")
    suspend fun getModifiedMeasurements(@Query("since") timestamp: Long): Response<List<ApiMeasurement>>
    
    @POST("api/measurements")
    suspend fun createMeasurement(@Body measurement: ApiMeasurement): Response<ApiMeasurement>
    
    @PUT("api/measurements/{id}")
    suspend fun updateMeasurement(@Path("id") id: String, @Body measurement: ApiMeasurement): Response<ApiMeasurement>
    
    @POST("api/measurements/batch")
    suspend fun syncMeasurements(@Body request: SyncRequest<ApiMeasurement>): Response<SyncResponse<ApiMeasurement>>
    
    // Health check
    @GET("api/health")
    suspend fun healthCheck(): Response<Map<String, String>>
}

