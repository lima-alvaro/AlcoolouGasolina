package com.example.exemplosimplesdecompose.view

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.R
import com.example.exemplosimplesdecompose.data.FuelStationRepository
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailScreen(navController: NavHostController, stationId: String) {
    val context = LocalContext.current
    val repository = remember { FuelStationRepository(context) }
    val station = remember(stationId) { repository.getStationById(stationId) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.station_detail_title)) })
        }
    ) { innerPadding ->
        if (station == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.invalid_station_message))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = station.name)
            Text(
                text = stringResource(
                    R.string.detail_price_alcohol,
                    station.alcoholPrice
                )
            )
            Text(
                text = stringResource(
                    R.string.detail_price_gasoline,
                    station.gasolinePrice
                )
            )
            Text(
                text = "${stringResource(R.string.date_label)}: ${
                    DateFormat.getDateTimeInstance().format(Date(station.date))
                }"
            )
            Text(
                text = if (station.hasLocation()) {
                    "${stringResource(R.string.location_label)}: ${
                        context.getString(
                            R.string.detail_location,
                            station.latitude,
                            station.longitude
                        )
                    }"
                } else {
                    "${stringResource(R.string.location_label)}: ${
                        stringResource(R.string.location_not_available)
                    }"
                }
            )

            Button(
                onClick = { navController.navigate("stationForm/${station.id}") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.edit_button))
            }

            Button(
                onClick = {
                    repository.deleteStation(station.id)
                    Toast.makeText(context, context.getString(R.string.station_deleted), Toast.LENGTH_SHORT)
                        .show()
                    navController.popBackStack("stations", false)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.delete_button))
            }

            Button(
                onClick = {
                    if (!station.hasLocation()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.location_not_available),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val geoUri = Uri.parse(
                        "geo:${station.latitude},${station.longitude}?q=${station.latitude},${station.longitude}"
                    )
                    val intent = Intent(Intent.ACTION_VIEW, geoUri)
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.map_not_available),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.open_map_button))
            }
        }
    }
}
