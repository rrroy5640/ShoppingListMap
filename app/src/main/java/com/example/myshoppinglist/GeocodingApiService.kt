package com.example.myshoppinglist

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {

    @GET("maps/api/geocode/json")
    suspend fun getGeocodingResponse(
        @Query("latlng") latlng: String,
        @Query("key") apikey: String
    ): GeocodingResponse
}