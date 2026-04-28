package com.example.exemplosimplesdecompose.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.UUID

class FuelStationRepository(context: Context) {
    private val appContext = context.applicationContext
    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val listType: Type = object : TypeToken<List<FuelStation>>() {}.type

    fun getAllStations(): List<FuelStation> {
        val savedJson = sharedPreferences.getString(KEY_STATIONS, null)
        if (savedJson.isNullOrBlank()) {
            return migrateLegacyStation()?.let { listOf(it) } ?: emptyList()
        }
        return gson.fromJson(savedJson, listType) ?: emptyList()
    }

    fun getStationById(id: String): FuelStation? {
        return getAllStations().firstOrNull { it.id == id }
    }

    fun saveStation(station: FuelStation) {
        val stations = getAllStations().toMutableList()
        stations.removeAll { it.id == station.id }
        stations.add(station)
        persist(stations)
    }

    fun updateStation(station: FuelStation) {
        saveStation(station)
    }

    fun deleteStation(id: String) {
        val stations = getAllStations().filterNot { it.id == id }
        persist(stations)
    }

    private fun persist(stations: List<FuelStation>) {
        sharedPreferences.edit().putString(KEY_STATIONS, gson.toJson(stations)).apply()
    }

    private fun migrateLegacyStation(): FuelStation? {
        val legacyPreferences =
            appContext.getSharedPreferences(LEGACY_PREF_FILE, Context.MODE_PRIVATE)
        val legacyJson = legacyPreferences.getString(LEGACY_KEY, null).orEmpty()
        if (legacyJson.isBlank()) {
            return null
        }

        val legacyObject = runCatching { JSONObject(legacyJson) }.getOrNull() ?: return null
        val station = FuelStation(
            id = UUID.randomUUID().toString(),
            name = legacyObject.optString("name", ""),
            alcoholPrice = 0.0,
            gasolinePrice = 0.0,
            date = System.currentTimeMillis(),
            latitude = legacyObject.optDouble("lat", 0.0),
            longitude = legacyObject.optDouble("lgt", 0.0)
        )
        persist(listOf(station))
        return station
    }

    private companion object {
        const val PREF_FILE = "fuel_station_repository"
        const val KEY_STATIONS = "stations_json"
        const val LEGACY_PREF_FILE = "lastGasStationJSON"
        const val LEGACY_KEY = "gasJSON"
    }
}
