package com.example.myshoppinglist

import android.Manifest
import android.content.Context
import android.net.InetAddresses
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.navigationsample.LocationUtils
import java.util.UUID
import kotlin.random.Random

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false,
    var address: String = ""
)

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color.Cyan),
                shape = RoundedCornerShape(percent = 20)
            )

    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Row {
                Text(text = item.name, modifier = Modifier.padding(8.dp))
                Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                Text(text = item.address)
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClicked) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "edit")
            }
            IconButton(onClick = onDeleteClicked) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
            }
        }
    }
}

@Composable
fun ItemEditor(item: ShoppingItem, onEditCompleted: (String, Int) -> Unit) {
    var editedName by remember {
        mutableStateOf(item.name)
    }
    var editedQuantity by remember {
        mutableStateOf(item.quantity.toString())
    }
    var isEditing by remember {
        mutableStateOf(item.isEditing)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
    )
    {
        Column {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentSize()
            )
            OutlinedTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentSize()
            )
        }
        Button(onClick = {
            onEditCompleted(editedName, editedQuantity.toIntOrNull() ?: 1)
            isEditing = false
        }) {
            Text(text = "Save")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyShoppingList(
    viewModel: LocationViewModel,
    locationUtils: LocationUtils,
    navController: NavController,
    context: Context,
    address: String

) {
    var sItems by remember {
        mutableStateOf(mutableListOf<ShoppingItem>())
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var itemName by remember {
        mutableStateOf("")
    }

    var itemQuantity by remember {
        mutableStateOf("")
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                locationUtils.requestLocationUpdate(viewModel)
            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationaleRequired) {
                    Toast.makeText(context, "Location required", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        context,
                        "Location required, enable in settings",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

    fun addItem() {
        if (itemName.isNotBlank() && itemQuantity.isNotBlank()) {
            val newItem = ShoppingItem(
                id = UUID.randomUUID().toString().hashCode(),
                name = itemName,
                quantity = itemQuantity.toInt()
            )
            sItems = (sItems + newItem).toMutableList()
            showDialog = false
        }
        itemName = ""
        itemQuantity = ""
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            items(sItems) { item ->
                if (item.isEditing) {
                    ItemEditor(
                        item = item,
                        onEditCompleted = { editedName, editedQuantity ->
                            sItems = sItems.map { it.copy(isEditing = false) }.toMutableList()
                            val editedItem = sItems.find { it.id == item.id }
                            editedItem?.let {
                                it.name = editedName
                                it.quantity = editedQuantity
                                it.address = address
                            }
                        })
                } else {
                    ShoppingListItem(item = item,
                        onEditClicked = {
                            sItems =
                                sItems.map { it.copy(isEditing = it.id == item.id) }.toMutableList()
                        },
                        onDeleteClicked = { sItems = (sItems - item).toMutableList() })
                }
            }
        }
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add item")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false; itemName = ""; itemQuantity = "" },
            confirmButton = {
                Button(onClick = { addItem() }) {
                    Text(text = "Add")
                }
            },
            title = { Text(text = "Add shopping item") },
            dismissButton = {
                Button(onClick = { showDialog = false; itemName = ""; itemQuantity = "" }) {
                    Text(text = "Cancel")
                }
            },
            text = {
                Column {
                    OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = {
                        Text(
                            text = "Item Name"
                        )
                    })
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        label = {
                            Text(
                                text = "Quantity"
                            )
                        })
                    Button(onClick = {
                        if (locationUtils.hasLocationPermission(context)) {
                            locationUtils.requestLocationUpdate(viewModel)
                            navController.navigate("locationscreen") {
                                this.launchSingleTop
                            }
                        } else {
                            requestPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            )
                        }
                    }) {
                        Text(text = "address")
                    }
                }
            }
        )
    }
}