package com.example.planit.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FirestoreRetrofitClient {
    private const val BASE_URL = "https://firestore.googleapis.com/v1/projects/base-6e3c9/databases/(default)/"

    val apiService: FirestoreApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirestoreApiService::class.java)
    }
}