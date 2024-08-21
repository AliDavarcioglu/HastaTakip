package com.example.hastatakip

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import kotlinx.coroutines.launch



@Composable
fun SelectionPage(navController: NavController, boxIdsString: String?) {
    val scope = rememberCoroutineScope()

    var selectedAgeGroup by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var selectedRisk by remember { mutableStateOf("") }

    val context = LocalContext.current

    BackHandler {
        //Geri tuşu fonksiyonu
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { navController.navigate("watches_page") },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Geri", modifier = Modifier.size(32.dp))
        }

        Text(
            text = "Saat No: $boxIdsString",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2),
            modifier = Modifier.padding(vertical = 8.dp).offset(y = (-32).dp)
        )

        // Age Group Selection

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
                    .fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Yaş Grubu",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                    CheckboxWithLabel(
                        label = "ÇOCUK",
                        checked = selectedAgeGroup == "ÇOCUK",
                        onCheckedChange = { selectedAgeGroup = "ÇOCUK" }
                    )
                    CheckboxWithLabel(
                        label = "YETİŞKİN",
                        checked = selectedAgeGroup == "YETİŞKİN",
                        onCheckedChange = { selectedAgeGroup = "YETİŞKİN" }
                    )
                }
            }
        }



        // Gender Selection


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
                    .fillMaxWidth()
            ) {
                Column {
                    Text(text = "Cinsiyet", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                    CheckboxWithLabel(
                        label = "ERKEK",
                        checked = selectedGender == "ERKEK",
                        onCheckedChange = { selectedGender = "ERKEK" }
                    )
                    CheckboxWithLabel(
                        label = "KADIN",
                        checked = selectedGender == "KADIN",
                        onCheckedChange = { selectedGender = "KADIN" }
                    )
                }
            }
        }




        // Risk Status Selection


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
                    .fillMaxWidth()
            ) {
                Column {
                    Text(text = "RİSK DURUMU", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                    CheckboxWithLabel(
                        label = "KRİTİK",
                        checked = selectedRisk == "KRİTİK",
                        onCheckedChange = { selectedRisk = "KRİTİK" }
                    )
                    CheckboxWithLabel(
                        label = "RİSKLİ",
                        checked = selectedRisk == "RİSKLİ",
                        onCheckedChange = { selectedRisk = "RİSKLİ" }
                    )
                    CheckboxWithLabel(
                        label = "NORMAL",
                        checked = selectedRisk == "NORMAL",
                        onCheckedChange = { selectedRisk = "NORMAL" }
                    )
                }
            }
        }



        // Save Button
        Button(
            onClick = {

                if (selectedAgeGroup.isNotEmpty() && selectedGender.isNotEmpty() && selectedRisk.isNotEmpty()) {
                    // Tüm seçimler yapıldıysa işlemleri gerçekleştir
                    scope.launch {
                        saveBoxData(context, boxIdsString?.toInt() ?: 0, selectedAgeGroup, selectedGender, selectedRisk)
                        navController.navigate("patient_info_page/$boxIdsString,$selectedAgeGroup,$selectedGender,$selectedRisk")
                    }
                } else {
                    // Seçimler eksikse kullanıcıya uyarı vermek isteyebilirsiniz
                    // Örneğin bir Toast mesajı gösterilebilir
                    Toast.makeText(context, "Lütfen tüm seçimleri yapınız.", Toast.LENGTH_SHORT).show()
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top=16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(0.6f)),
        ) {
            Text(text = "KAYDET", color = Color.White)
        }
    }
}

@Composable
fun CheckboxWithLabel(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkmarkColor = Color.White)
        )
        Text(text = label, fontSize = 16.sp, color = Color.Black)
    }
}


suspend fun saveBoxData(context: Context, boxId: Int, ageGroup: String, gender: String, riskStatus: String) {
    context.dataStore.edit { preferences ->
        val riskStatusKey = stringPreferencesKey("box_${boxId}_riskStatus")
        val ageGroupKey = stringPreferencesKey("box_${boxId}_ageGroup")
        val genderKey = stringPreferencesKey("box_${boxId}_gender")

        preferences[riskStatusKey] = riskStatus
        preferences[ageGroupKey] = ageGroup
        preferences[genderKey] = gender
    }
}

