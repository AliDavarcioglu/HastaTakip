import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import com.example.hastatakip.HealthConnectManager
import com.example.hastatakip.dataStore
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope






private lateinit var database: DatabaseReference

@Composable
fun PatientInfoPage(boxId: String?, ageGroup: String?, gender: String?, riskStatus: String?, navController: NavController) {


    database = FirebaseDatabase.getInstance().getReference("health_data")



    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    val healthConnectManager = remember { HealthConnectManager(context) }

    var bpm by remember { mutableStateOf<String?>(null) }
    var temp by remember { mutableStateOf<String?>(null) }
    var diastolictension by remember { mutableStateOf<String?>(null) }
    var systolictension by remember { mutableStateOf<String?>(null) }
    var oxygenSaturation by remember { mutableStateOf<String?>(null) }



    suspend fun saveHealthDataToFirebase(userId: String,boxId: String?) {
        val heartRate = healthConnectManager.readHeartRateData(boxId)?.toIntOrNull() ?: 0
        val bodyTemperature = healthConnectManager.readBodyTemperatureData(boxId)?.toFloatOrNull() ?: 0f
        val oxygenSaturation = healthConnectManager.readOxygenSaturation(boxId)?.toFloatOrNull() ?: 0f
        val hypotension = healthConnectManager.readHypotensionData(boxId)?.toFloatOrNull() ?: 0f
        val hypertension = healthConnectManager.readHypertensionData(boxId)?.toFloatOrNull() ?: 0f

        saveHealthData(userId, heartRate, bodyTemperature, oxygenSaturation, hypotension, hypertension)
    }







    fun riskStatusColor(riskStatus: String?): Color {
        return when (riskStatus) {
            "ACİL" -> Color.Red
            "ORTA RİSKLİ" -> Color(0xFFFFBF00) // Sarı renk
            "NORMAL" -> Color(0xFF228B22) // Yeşil renk
            else -> Color.Black // Varsayılan renk
        }
    }



    fun bpmStatus(bpmValue: String?): String {
        var value = "--"
        val bpmvalue = bpm?.toIntOrNull()
        if (bpmvalue != null) {
            value = when {
                bpmvalue <= 59 || bpmvalue >= 101 -> "ACİL"
                bpmvalue in 60..64 -> "ORTA RİSKLİ"
                bpmvalue in 65..94 -> "NORMAL"
                bpmvalue in 95..100 -> "ORTA RİSKLİ"
                else -> "--"
            }
        }
        return value
    }

    fun bodyTempStatus(tempValue:String?):String{
        var value = "--"
        val tempValue = temp?.toDoubleOrNull()
        if (tempValue != null){
            value = when{
                tempValue<=35 || tempValue>=38.5 -> "ACİL"
                tempValue in 35.1 .. 36.0 -> "ORTA RİSKLİ"
                tempValue in 36.1 .. 37.3 -> "NORMAL"
                tempValue in 37.4 .. 38.4 -> "ORTA RİSKLİ"
                else -> "--"
            }
        }
        return value
    }

    fun systolicStatus(systolicValue:String?):String{
        var value = "--"
        val systolicValue = systolictension?.toIntOrNull()
        if (systolicValue != null){
            value = when{
                systolicValue<=110 || systolicValue>=160 -> "ACİL"
                systolicValue in 111 .. 119 -> "ORTA RİSKLİ"
                systolicValue in 120 .. 140 -> "NORMAL"
                systolicValue in 141 .. 159 -> "ORTA RİSKLİ"
                else -> "--"
            }
        }
        return value
    }

    fun diastolicStatus(diastolicValue:String?):String{
        var value = "--"
        val diastolicValue = diastolictension?.toIntOrNull()
        if (diastolicValue != null){
            value = when{
                diastolicValue<=70 || diastolicValue>=100 -> "ACİL"
                diastolicValue in 71 .. 79 -> "ORTA RİSKLİ"
                diastolicValue in 80 .. 90 -> "NORMAL"
                diastolicValue in 91 .. 99 -> "ORTA RİSKLİ"
                else -> "--"
            }
        }
        return value
    }

    fun oxygenSaturationStaus(oxygenSaturationValue:String?):String{
        var value = "--"
        val oxygenSaturationValue = oxygenSaturation?.toDoubleOrNull()
        if (oxygenSaturationValue != null){
            value = when{
                oxygenSaturationValue<=92.0 -> "ACİL"
                oxygenSaturationValue in 93.0 .. 94.0 -> "ORTA RİSKLİ"
                oxygenSaturationValue in 95.0 .. 100.0 -> "NORMAL"
                else -> "--"
            }
        }
        return value
    }





    LaunchedEffect(Unit) {

        while (true) {
            withContext(Dispatchers.IO) {
                bpm = healthConnectManager.readHeartRateData(boxId)
                temp = healthConnectManager.readBodyTemperatureData(boxId)
                diastolictension = healthConnectManager.readHypotensionData(boxId)
                systolictension = healthConnectManager.readHypertensionData(boxId)
                oxygenSaturation = healthConnectManager.readOxygenSaturation(boxId)

                saveHealthDataToFirebase("1",boxId)


                val currentRiskStatus = context.dataStore.data.map { preferences ->
                    preferences[stringPreferencesKey("box_${boxId}_riskStatus")] ?: "no status"
                }.first()


                if(currentRiskStatus!="PASİF"){

                    // Yeni risk durumunu hesapla
                    val bpmStatus = bpmStatus(bpm)
                    val tempStatus = bodyTempStatus(temp)
                    val systolicStatus = systolicStatus(systolictension)
                    val diastolicStatus = diastolicStatus(diastolictension)
                    val oxygenStatus = oxygenSaturationStaus(oxygenSaturation)

                    // Risk seviyelerini belirle
                    val riskLevels = listOf(bpmStatus, tempStatus, systolicStatus, diastolicStatus, oxygenStatus)


                    // En yüksek risk seviyesini seç
                    val newRiskStatus = when {
                        riskLevels.contains("ACİL") -> "ACİL"
                        riskLevels.contains("ORTA RİSKLİ") && currentRiskStatus != "ACİL" -> "ORTA RİSKLİ"
                        else -> currentRiskStatus
                    }


                    // Yeni risk durumu farklıysa, güncelle
                    if (newRiskStatus != currentRiskStatus) {
                        updateBoxRiskStatus(context, boxId, newRiskStatus)
                    }


                }

            }

            delay(3000)
        }
    }



    BackHandler {
        // Geri tuşu fonksiyonu
    }




    // UI part
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Box for boxId
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Hasta: $boxId",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Card for ageGroup, gender, and riskStatus
        Card(
            modifier = Modifier
                .padding(8.dp)
                .shadow(8.dp, RoundedCornerShape(8.dp)),
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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = "$ageGroup",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Box for gender
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Cinsiyet:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = "$gender",
                        fontSize = 18.sp,
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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = "$riskStatus",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = riskStatusColor(riskStatus),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(4.dp))


        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            // Table header row
            Row(
                modifier = Modifier
                    .background(
                        Color(0xFFBBDEFB),
                        RoundedCornerShape(10.dp)
                    ) // Arkaplan rengi için background
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ölçülen Değer",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "Referans",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "Sonuç",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "Durum",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BPM row
            Row(
                modifier = Modifier
                    .background(
                        Color(0xFFE3F2FD),
                        RoundedCornerShape(10.dp)
                    ) // Arkaplan rengi için background
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Nabız",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "65-94",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "$bpm",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = bpmStatus(bpm),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = riskStatusColor(bpmStatus(bpm)),
                    modifier = Modifier
                        .weight(1.1f)
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body Temperature row
            Row(
                modifier = Modifier
                    .background(
                        Color(0xFFE3F2FD),
                        RoundedCornerShape(10.dp)
                    ) // Arkaplan rengi için background
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Vücut Sıcaklığı",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "36.1°C - 37.3°C",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "$temp",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = bodyTempStatus(temp), // Durum rengi kırmızı olarak belirlenmiş
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = riskStatusColor(bodyTempStatus(temp)), // Renk kırmızı olarak ayarlandı
                    modifier = Modifier
                        .weight(1.1f)
                        .align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Sistolik Row
            Row(
                modifier = Modifier
                    .background(
                        Color(0xFFE3F2FD),
                        RoundedCornerShape(10.dp)
                    ) // Arkaplan rengi için background
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sistolik Tansiyon",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "120-140 mmHg",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "$systolictension",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = systolicStatus(systolictension),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = riskStatusColor(systolicStatus(systolictension)), // Renk kırmızı olarak ayarlandı
                    modifier = Modifier
                        .weight(1.1f)
                        .align(Alignment.CenterVertically)
                )
            }

            //Diastolik Row
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .background(
                        Color(0xFFE3F2FD),
                        RoundedCornerShape(10.dp)
                    ) // Arkaplan rengi için background
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Diastolik Tansiyon",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "80-90   mmHg",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "$diastolictension",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = diastolicStatus(diastolictension), // Durum rengi kırmızı olarak belirlenmiş
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = riskStatusColor(diastolicStatus(diastolictension)), // Renk kırmızı olarak ayarlandı
                    modifier = Modifier
                        .weight(1.1f)
                        .align(Alignment.CenterVertically)
                )
            }
            // Oksijen Satürasyonu Row
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .background(
                        Color(0xFFE3F2FD),
                        RoundedCornerShape(10.dp)
                    ) // Arkaplan rengi için background
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Oksijen Satürasyonu",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "%95-%100",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1.3f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "$oxygenSaturation",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = oxygenSaturationStaus(oxygenSaturation),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = riskStatusColor(oxygenSaturationStaus(oxygenSaturation)),
                    modifier = Modifier
                        .weight(1.1f)
                        .align(Alignment.CenterVertically)
                )
            }
        }



        Spacer(modifier = Modifier.height(6.dp))








    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // İçerik
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Row for the icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), // İkonlar arasında boşluk bırakır
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically // İkonların dikeyde ortalanmasını sağlar
            ) {
                // Silme tuşu
                IconButton(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier.size(50.dp) // İkonun boyutunu belirler
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Sil",
                        modifier = Modifier.size(35.dp), // İkonun boyutunu belirler
                        tint = Color.Red
                    )
                }

                if (showDialog) {
                    DeleteConfirmationDialog(
                        onConfirm = {
                            coroutineScope.launch {
                                boxId?.toIntOrNull()?.let { id ->
                                    deleteBox(context, id)
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
                    },
                    modifier = Modifier.size(50.dp) // İkonun boyutunu belirler
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Ana Sayfa",
                        modifier = Modifier.size(35.dp), // İkonun boyutunu belirler
                        tint = Color(0xFF1976D2)
                    )
                }
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


        preferences.remove(idKey)
        preferences.remove(riskStatusKey)
        preferences.remove(ageGroupKey)
        preferences.remove(genderKey)


        // Kutu durumunu pasif olarak ayarla
        preferences[stringPreferencesKey("box_${boxId}_status")] = "pasif"


    }
}

suspend fun updateBoxRiskStatus(context: Context, boxId: String?, newRiskStatus: String) {
    context.dataStore.edit { preferences ->
        val riskStatusKey = stringPreferencesKey("box_${boxId}_riskStatus")
        preferences[riskStatusKey] = newRiskStatus
    }
}



private fun saveHealthData(
    userId: String, heartRate: Int,
    bodyTemperature: Float,
    oxygenSaturation: Float,
    hypotension: Float,
    hypertension: Float) {
    val healthData = mapOf(
        "heartRate" to heartRate,
        "bodyTemperature" to bodyTemperature,
        "oxygenSaturation" to oxygenSaturation,
        "hypertension" to hypertension,
        "hypotension" to hypotension
    )
    val healthDataRef = database.child("health_data")
    healthDataRef.setValue(healthData)
        .addOnSuccessListener {

        }
        .addOnFailureListener {

        }
}


