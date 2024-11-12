package com.example.hastatakip

import PatientInfoPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hastatakip.ui.theme.HastaTakipTheme
import kotlinx.coroutines.delay

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

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }

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



@Composable
fun SplashScreen(navController: NavController) {
    // Alpha değeri için animasyon durumu
    val alpha = remember { Animatable(0f) }

    // Animasyonu başlat
    LaunchedEffect(Unit) {
        // Alpha değerini 0'dan 1'e çıkararak fade-in efekti
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500) // 1.5 saniyede tamamla
        )
        // Bekleme süresi
        delay(1000) // 1.5 saniye bekle
        // Alpha değerini 1'den 0'a düşürerek fade-out efekti
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 1500) // 1.5 saniyede tamamla
        )
        // Geçişi gerçekleştir
        navController.navigate("watches_page") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Box yapısı, üst üste yerleştirilecek UI bileşenleri için uygun
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Arka plan rengi
    ) {
        // Üst kısımda yan yana resimler
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.cbu),
                contentDescription = "CBU Logo",
                modifier = Modifier.size(90.dp) // İsteğe göre boyutu ayarlayın
                    .alpha(alpha.value)
            )
            Image(
                painter = painterResource(id = R.drawable.mth),
                contentDescription = "MTH Logo",
                modifier = Modifier.size(90.dp) // İsteğe göre boyutu ayarlayın
                    .alpha(alpha.value)
            )
            Image(
                painter = painterResource(id = R.drawable.teknofest),
                contentDescription = "Teknofest Logo",
                modifier = Modifier.size(90.dp) // İsteğe göre boyutu ayarlayın
                    .alpha(alpha.value)
            )
        }

        // Ortada yer alacak logo
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.Center) // Ortaya hizala
                .size(1000.dp) // İsteğe göre boyutu ayarlayın
                .alpha(alpha.value) // Alpha değeri ile görünürlüğü ayarla
        )
    }
}



    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        HastaTakipTheme {
            Pages()
        }
    }

