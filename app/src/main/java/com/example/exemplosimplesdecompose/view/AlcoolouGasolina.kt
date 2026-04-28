package com.example.exemplosimplesdecompose.view

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.R
import com.example.exemplosimplesdecompose.data.FuelStation
import com.example.exemplosimplesdecompose.data.FuelStationRepository
import com.example.exemplosimplesdecompose.location.LocationHelper
import com.example.exemplosimplesdecompose.util.toLocalizedDoubleOrNull
import java.util.UUID

@Composable
fun AlcoolGasolinaPreco(navController: NavHostController, check: Boolean) {
    val context = LocalContext.current
    val repository = remember { FuelStationRepository(context) }
    var alcohol by remember { mutableStateOf("") }
    var gasoline by remember { mutableStateOf("") }
    var stationName by remember { mutableStateOf("") }
    var checkedState by remember { mutableStateOf(check) }
    var resultMessage by remember { mutableStateOf(context.getString(R.string.result_initial)) }
    var showNameError by remember { mutableStateOf(false) }
    var showAlcoholError by remember { mutableStateOf(false) }
    var showGasolineError by remember { mutableStateOf(false) }

    val saveStation: (Boolean) -> Unit = saveAction@{ permissionDenied ->
        val parsedAlcohol = alcohol.toLocalizedDoubleOrNull()
        val parsedGasoline = gasoline.toLocalizedDoubleOrNull()
        showNameError = stationName.isBlank()
        showAlcoholError = parsedAlcohol == null || parsedAlcohol <= 0.0
        showGasolineError = parsedGasoline == null || parsedGasoline <= 0.0

        if (showNameError || showAlcoholError || showGasolineError) {
            Toast.makeText(context, context.getString(R.string.validation_fix_fields), Toast.LENGTH_SHORT)
                .show()
            return@saveAction
        }

        val location = if (permissionDenied) null else LocationHelper.getLastKnownLocation(context)
        repository.saveStation(
            FuelStation(
                id = UUID.randomUUID().toString(),
                name = stationName.trim(),
                alcoholPrice = parsedAlcohol!!,
                gasolinePrice = parsedGasoline!!,
                date = System.currentTimeMillis(),
                latitude = location?.first ?: 0.0,
                longitude = location?.second ?: 0.0
            )
        )

        val messageId = if (location == null) {
            R.string.station_saved_without_location
        } else {
            R.string.station_saved_with_location
        }
        Toast.makeText(context, context.getString(messageId), Toast.LENGTH_SHORT).show()
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        saveStation(!granted)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = alcohol,
                onValueChange = {
                    alcohol = it
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
                value = gasoline,
                onValueChange = {
                    gasoline = it
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
                value = stationName,
                onValueChange = {
                    stationName = it
                    showNameError = false
                },
                label = { Text(stringResource(R.string.station_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = showNameError,
                supportingText = {
                    if (showNameError) {
                        Text(stringResource(R.string.validation_name))
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.use_75_percent_rule),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    modifier = Modifier.semantics {
                        contentDescription = context.getString(R.string.switch_content_description)
                    },
                    checked = checkedState,
                    onCheckedChange = {
                        checkedState = it
                        saveConfig(context, checkedState)
                    }
                )
            }

            Button(
                onClick = {
                    val parsedAlcohol = alcohol.toLocalizedDoubleOrNull()
                    val parsedGasoline = gasoline.toLocalizedDoubleOrNull()
                    showAlcoholError = parsedAlcohol == null || parsedAlcohol <= 0.0
                    showGasolineError = parsedGasoline == null || parsedGasoline <= 0.0
                    if (showAlcoholError || showGasolineError) {
                        resultMessage = context.getString(R.string.validation_fix_fields)
                    } else {
                        val threshold = if (checkedState) 0.75 else 0.70
                        val ratio = parsedAlcohol!! / parsedGasoline!!
                        resultMessage = if (ratio <= threshold) {
                            context.getString(R.string.result_alcohol)
                        } else {
                            context.getString(R.string.result_gasoline)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.calculate_button))
            }

            Text(
                text = resultMessage,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_station_button))
            }

            Button(
                onClick = { navController.navigate("stations") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.view_stations_button))
            }
        }
    }
}

fun saveConfig(context: Context, switchState: Boolean) {
    val sharedFileName = "config_Alc_ou_Gas"
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(sharedFileName, Context.MODE_PRIVATE)
    sharedPreferences.edit().putBoolean("is_75_checked", switchState).apply()
}
