package com.example.exemplosimplesdecompose

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.exemplosimplesdecompose.ui.theme.ExemploSimplesDeComposeTheme
import com.example.exemplosimplesdecompose.view.AlcoolGasolinaPreco
import com.example.exemplosimplesdecompose.view.ListofGasStations
import com.example.exemplosimplesdecompose.view.StationDetailScreen
import com.example.exemplosimplesdecompose.view.StationFormScreen
import com.example.exemplosimplesdecompose.view.Welcome

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val switchChecked = loadConfig(this)
        setContent {
            ExemploSimplesDeComposeTheme {
                val navController: NavHostController = rememberNavController()
                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") { Welcome(navController) }
                    composable("mainalcgas") { AlcoolGasolinaPreco(navController, switchChecked) }
                    composable("stations") { ListofGasStations(navController) }
                    composable("stationDetail/{stationId}") { backStackEntry ->
                        val stationId = backStackEntry.arguments?.getString("stationId").orEmpty()
                        StationDetailScreen(navController, stationId)
                    }
                    composable("stationForm") { StationFormScreen(navController) }
                    composable("stationForm/{stationId}") { backStackEntry ->
                        val stationId = backStackEntry.arguments?.getString("stationId").orEmpty()
                        StationFormScreen(navController, stationId)
                    }
                }
            }
        }
    }

    private fun loadConfig(context: Context): Boolean {
        val sharedFileName = "config_Alc_ou_Gas"
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(sharedFileName, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_75_checked", false)
    }
}
