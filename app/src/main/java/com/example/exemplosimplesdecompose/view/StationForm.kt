package com.example.exemplosimplesdecompose.view

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.R
import com.example.exemplosimplesdecompose.data.FuelStation
import com.example.exemplosimplesdecompose.data.FuelStationRepository
import com.example.exemplosimplesdecompose.location.LocationHelper
import com.example.exemplosimplesdecompose.util.toLocalizedDoubleOrNull
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationFormScreen(navController: NavHostController, stationId: String? = null) {
    val context = LocalContext.current
    val repository = remember { FuelStationRepository(context) }
    val existingStation = remember(stationId) {
        stationId?.takeIf { it.isNotBlank() }?.let(repository::getStationById)
    }

    var name by remember(existingStation) { mutableStateOf(existingStation?.name.orEmpty()) }
    var alcoholPrice by remember(existingStation) {
        mutableStateOf(existingStation?.alcoholPrice?.takeIf { it > 0.0 }?.toString().orEmpty())
    }
    var gasolinePrice by remember(existingStation) {
        mutableStateOf(existingStation?.gasolinePrice?.takeIf { it > 0.0 }?.toString().orEmpty())
    }
    var latitude by remember(existingStation) {
        mutableStateOf(existingStation?.latitude?.takeIf { it != 0.0 }?.toString().orEmpty())
    }
    var longitude by remember(existingStation) {
        mutableStateOf(existingStation?.longitude?.takeIf { it != 0.0 }?.toString().orEmpty())
    }
    var showNameError by remember { mutableStateOf(false) }
    var showAlcoholError by remember { mutableStateOf(false) }
    var showGasolineError by remember { mutableStateOf(false) }
    var showLatitudeError by remember { mutableStateOf(false) }
    var showLongitudeError by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(
                context,
                context.getString(R.string.station_saved_without_location),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val currentLocation = LocationHelper.getLastKnownLocation(context)
            if (currentLocation != null) {
                latitude = currentLocation.first.toString()
                longitude = currentLocation.second.toString()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.location_not_available),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (existingStation == null) {
                                R.string.station_form_create_title
                            } else {
                                R.string.station_form_edit_title
                            }
                        )
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    showNameError = false
                },
                label = { Text(stringResource(R.string.station_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = showNameError,
                supportingText = {
                    if (showNameError) {
                        Text(stringResource(R.string.validation_name))
                    }
                }
            )

            OutlinedTextField(
                value = alcoholPrice,
                onValueChange = {
                    alcoholPrice = it
                    showAlcoholError = false
                },
                label = { Text(stringResource(R.string.alcohol_price_label)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = showAlcoholError,
                supportingText = {
                    if (showAlcoholError) {
                        Text(stringResource(R.string.validation_price))
                    }
                }
            )

            OutlinedTextField(
                value = gasolinePrice,
                onValueChange = {
                    gasolinePrice = it
                    showGasolineError = false
                },
                label = { Text(stringResource(R.string.gasoline_price_label)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = showGasolineError,
                supportingText = {
                    if (showGasolineError) {
                        Text(stringResource(R.string.validation_price))
                    }
                }
            )

            OutlinedTextField(
                value = latitude,
                onValueChange = {
                    latitude = it
                    showLatitudeError = false
                },
                label = { Text(stringResource(R.string.latitude_label)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = showLatitudeError,
                supportingText = {
                    if (showLatitudeError) {
                        Text(stringResource(R.string.validation_latitude))
                    }
                }
            )

            OutlinedTextField(
                value = longitude,
                onValueChange = {
                    longitude = it
                    showLongitudeError = false
                },
                label = { Text(stringResource(R.string.longitude_label)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = showLongitudeError,
                supportingText = {
                    if (showLongitudeError) {
                        Text(stringResource(R.string.validation_longitude))
                    }
                }
            )

            Button(
                onClick = {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.use_current_location_button))
            }

            Button(
                onClick = {
                    val parsedAlcohol = alcoholPrice.toLocalizedDoubleOrNull()
                    val parsedGasoline = gasolinePrice.toLocalizedDoubleOrNull()
                    val parsedLatitude = latitude.toCoordinateValue()
                    val parsedLongitude = longitude.toCoordinateValue()

                    showNameError = name.isBlank()
                    showAlcoholError = parsedAlcohol == null || parsedAlcohol <= 0.0
                    showGasolineError = parsedGasoline == null || parsedGasoline <= 0.0
                    showLatitudeError =
                        latitude.isNotBlank() && (parsedLatitude == null || parsedLatitude < -90.0 || parsedLatitude > 90.0)
                    showLongitudeError =
                        longitude.isNotBlank() && (parsedLongitude == null || parsedLongitude < -180.0 || parsedLongitude > 180.0)

                    if (
                        showNameError ||
                        showAlcoholError ||
                        showGasolineError ||
                        showLatitudeError ||
                        showLongitudeError
                    ) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.validation_fix_fields),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val station = FuelStation(
                        id = existingStation?.id ?: UUID.randomUUID().toString(),
                        name = name.trim(),
                        alcoholPrice = parsedAlcohol!!,
                        gasolinePrice = parsedGasoline!!,
                        date = existingStation?.date ?: System.currentTimeMillis(),
                        latitude = parsedLatitude ?: 0.0,
                        longitude = parsedLongitude ?: 0.0
                    )

                    if (existingStation == null) {
                        repository.saveStation(station)
                        Toast.makeText(
                            context,
                            context.getString(R.string.station_created),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        repository.updateStation(station)
                        Toast.makeText(
                            context,
                            context.getString(R.string.station_updated),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    navController.popBackStack("stations", false)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_button))
            }
        }
    }
}

private fun String.toCoordinateValue(): Double? = replace(",", ".").toDoubleOrNull()
