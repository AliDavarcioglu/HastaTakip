import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import com.example.hastatakip.HealthConnectManager
import com.example.hastatakip.dataStore
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun PatientInfoPage(boxId: String?, ageGroup: String?, gender: String?, riskStatus: String?, navController: NavController) {

    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    val healthConnectManager = remember { HealthConnectManager(context) }

    var bpm by remember { mutableStateOf<String?>(null) }
    var temp by remember { mutableStateOf<String?>(null) }
    var hypotension by remember { mutableStateOf<String?>(null) }
    var hypertension by remember { mutableStateOf<String?>(null) }
    var oxygenSaturation by remember { mutableStateOf<String?>(null) }





    LaunchedEffect(Unit) {
        while (true) {
            bpm = withContext(Dispatchers.IO) {
                healthConnectManager.readHeartRateData()
            }
            temp = withContext(Dispatchers.IO){
                healthConnectManager.readBodyTemperatureData()
            }
            hypotension = withContext(Dispatchers.IO){
                healthConnectManager.readHypotensionData()
            }
            hypertension = withContext(Dispatchers.IO){
                healthConnectManager.readHypertensionData()
            }
            oxygenSaturation = withContext(Dispatchers.IO){
                healthConnectManager.readOxygenSaturation()
            }

            delay(5000) // Adjust the delay as needed (e.g., 5 seconds)
        }
    }








    BackHandler {
        // Geri tuşu fonksiyonu
    }

    // UI part
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Box for boxId
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                //.border(width = 2.dp,color= Color.Gray, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Saat No: $boxId",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Card for ageGroup, gender, and riskStatus
        Card(
            modifier = Modifier
                .padding(8.dp)
                .shadow(8.dp, RoundedCornerShape(8.dp)), // Gölge efekti ve köşe yuvarlama
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFFE3F2FD)) // Arkaplan rengi için background
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                // Box for ageGroup
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Yaş Grubu:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = "$ageGroup",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp)) // Alanlar arasında boşluk ekler

                // Box for gender
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Cinsiyet:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = "$gender",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp)) // Alanlar arasında boşluk ekler

                // Box for riskStatus
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Risk Durumu:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = "$riskStatus",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(4.dp))


        // Box for BPM
        Card(
            modifier = Modifier
                .padding(8.dp)
                .shadow(8.dp, RoundedCornerShape(8.dp)), // Gölge efekti ve köşe yuvarlama
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFE3F2FD)) // Arkaplan rengi için background
                    .padding(12.dp)
                    .height(30.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "BPM:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "$bpm",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )

            }
        }

        Spacer(modifier = Modifier.height(4.dp))


        // Box for Body Temperature

        Card(
            modifier = Modifier
                .padding(8.dp)
                .shadow(8.dp, RoundedCornerShape(8.dp)), // Gölge efekti ve köşe yuvarlama
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFE3F2FD)) // Arkaplan rengi için background
                    .padding(12.dp)
                    .height(30.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Vücut Sıcaklığı:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd) // Align the entire row to the end
                ) {
                    Text(
                        text = "$temp",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(2.dp)) // Optional: Add some spacing between the two texts
                    Text(
                        text = "°C",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(top = 4.dp) // Adjust the padding to move mmHg downwards
                    )
                }

            }
        }


        Spacer(modifier = Modifier.height(4.dp))


        // Box for Tansiyon
        Card(
            modifier = Modifier
                .padding(8.dp)
                .shadow(8.dp, RoundedCornerShape(8.dp)), // Gölge efekti ve köşe yuvarlama
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFFE3F2FD)) // Arkaplan rengi için background
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        text = "Hipertansiyon:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd) // Align the entire row to the end
                    ) {
                        Text(
                            text = "$hypertension",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(2.dp)) // Optional: Add some spacing between the two texts
                        Text(
                            text = "mmHg",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(top = 4.dp) // Adjust the padding to move mmHg downwards
                        )
                    }



                }
                Spacer(modifier = Modifier.height(8.dp)) // İki metin arasına boşluk ekler
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Hipotansiyon:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd) // Align the entire row to the end
                    ) {
                        Text(
                            text = "$hypotension",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(2.dp)) // Optional: Add some spacing between the two texts
                        Text(
                            text = "mmHg",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(top = 4.dp) // Adjust the padding to move mmHg downwards
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))


        // Box for Oksijen satürasyonu

        Card(
            modifier = Modifier
                .padding(8.dp)
                .shadow(8.dp, RoundedCornerShape(8.dp)), // Gölge efekti ve köşe yuvarlama
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFE3F2FD)) // Arkaplan rengi için background
                    .padding(12.dp)
                    .height(30.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Oksijen satürasyonu:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "$oxygenSaturation",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )

            }
        }


        // Spacer to push the icon to the bottom
        Spacer(modifier = Modifier.weight(1f))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {


            // Silme tuşu
            IconButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Red
                )
            }

            if (showDialog) {
                DeleteConfirmationDialog(
                    onConfirm = {
                        coroutineScope.launch {
                            boxId?.toIntOrNull()?.let {id ->
                                deleteBox(context,id)
                                navController.navigate("watches_page")

                            }
                        }


                        showDialog = false


                    },
                    onDismiss = {
                        showDialog = false
                    }
                )
            }


            Spacer(modifier = Modifier.width(150.dp))

            // Home icon button
            IconButton(
                onClick = {
                    navController.navigate("watches_page")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Ana Sayfa",
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFF1976D2)
                )
            }



        }



    }
}



@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp), // Daha yuvarlak köşeler
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hasta Silinsin mi?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground // Renk uyumu
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Evet", color = Color.White)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Hayır", color = Color.White)
                    }
                }
            }
        }
    }
}
suspend fun deleteBox(context: Context, boxId: Int) {
    context.dataStore.edit { preferences ->
        val idKey = intPreferencesKey("box_${boxId}_id")
        val riskStatusKey = stringPreferencesKey("box_${boxId}_riskStatus")
        val ageGroupKey = stringPreferencesKey("box_${boxId}_ageGroup")
        val genderKey = stringPreferencesKey("box_${boxId}_gender")


        preferences.remove(riskStatusKey)
        preferences.remove(ageGroupKey)
        preferences.remove(genderKey)


        // Kutu durumunu pasif olarak ayarla
        preferences[stringPreferencesKey("box_${boxId}_status")] = "pasif"


    }
}


