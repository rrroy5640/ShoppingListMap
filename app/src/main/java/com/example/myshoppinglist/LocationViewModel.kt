package com.example.myshoppinglist

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationsample.LocationUtils
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    private val _address = mutableStateOf(listOf<GeocodingResult>())
    val address: State<List<GeocodingResult>> = _address

    fun updateLocation(newLocation: LocationData) {
        _location.value = newLocation
    }


    fun fetchAddress(latlng: String) {
        try {
            Log.d("test", "test")
            viewModelScope.launch {
                Log.d("test", "${latlng}")
                val result = RetrofitClient.create().getGeocodingResponse(
                    latlng = latlng,
                    apikey = "AIzaSyD1YAj-8j8gMxbxh8Z7M8AP7FiriPFYDsQ"
                )
                Log.d("not yet", "${result.results[0]}")
                _address.value = result.results
                //Log.d("success", "${address.value[0]}")
            }
        } catch (e: Exception) {
            Log.d("res1", "${e.cause}: ${e.message}")
        }
    }
}