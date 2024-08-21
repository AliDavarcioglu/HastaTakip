package com.example.hastatakip

import PatientInfoPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hastatakip.ui.theme.HastaTakipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            HastaTakipTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Pages()


                }
            }
        }
    }
}



@Composable
fun Pages() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "watches_page") {

        composable(route = "watches_page") {
            WatchesPage(navController)
        }

        composable("selection_page/{boxIds}") { backStackEntry ->
            SelectionPage(navController, backStackEntry.arguments?.getString("boxIds"))
        }

        composable("patient_info_page/{boxId},{ageGroup},{gender},{riskStatus}"){backStackEntry ->
            PatientInfoPage(backStackEntry.arguments?.getString("boxId"),
                backStackEntry.arguments?.getString("ageGroup"),
                backStackEntry.arguments?.getString("gender"),
                backStackEntry.arguments?.getString("riskStatus"),
                navController
            )

        }


        }
    }



    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        HastaTakipTheme {
            Pages()
        }
    }

