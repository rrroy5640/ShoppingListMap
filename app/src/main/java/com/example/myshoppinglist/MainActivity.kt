package com.example.myshoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.myshoppinglist.ui.theme.MyShoppingListTheme
import com.example.navigationsample.LocationUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val locationViewModel: LocationViewModel = viewModel()
            MyShoppingListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(viewModel = locationViewModel)
                }
            }
        }
    }
}

@Composable
fun App(viewModel: LocationViewModel) {
    val context = LocalContext.current
    val locationUtils = LocationUtils(context = context)
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "shoppinglistscreen") {
        composable("shoppinglistscreen") {
            MyShoppingList(
                viewModel = viewModel,
                locationUtils = locationUtils,
                navController = navController,
                context = context,
                address = viewModel.address.value.firstOrNull()?.formatted_address?:"No address"
            )
        }
        dialog("locationscreen"){
            backstack->
            viewModel.location.value?.let {
                LocationSelectionScreen(location = it, onLocationSelected = {
                    viewModel.fetchAddress("${it.latitude},${it.longitude}")
                    navController.popBackStack()
                })
            }
        }
    }

}