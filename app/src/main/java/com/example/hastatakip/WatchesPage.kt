package com.example.hastatakip

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "boxes")

@Composable
fun WatchesPage(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var boxes by remember { mutableStateOf(listOf<BoxItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedBoxId by remember { mutableStateOf(-1) }

    LaunchedEffect(Unit) {
        boxes = loadBoxes(context).sortedBy { box->
            when(box.riskStatus){
                "KRİTİK" -> 0
                "RİSKLİ" -> 1
                "NORMAL" -> 2
                else -> 3
            }

        }


    }

    BackHandler {
        // Geri tuşu işlevi ekleyin eğer gerekiyorsa
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Grid to display boxes
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(boxes.size) { index ->

                val box = boxes[index]

                val boxColor = when (box.riskStatus) {
                    "KRİTİK" -> Color.Red.copy(0.65f)
                    "RİSKLİ" -> Color.Yellow.copy(0.65f)
                    "NORMAL" -> Color.Green.copy(0.65f)
                    else -> Color.Gray.copy(0.6f)
                }
                BoxItemView(
                    item = box,
                    onLongPress = {
                        selectedBoxId = box.id
                        showDialog = true
                    },
                    onClick = {

                        if (box.riskStatus == "PASİF") {
                            navController.navigate("selection_page/${box.id}")
                        } else {
                            navController.navigate("patient_info_page/${box.id},${box.ageGroup},${box.gender},${box.riskStatus}")
                        }

                        //val boxIdString = boxes[index].id.toString()
                        //navController.navigate("selection_page/$boxIdString")
                    },
                    boxColor = boxColor
                )
            }
        }

        // Add Button
        Button(
            onClick = {
                val newId = if (boxes.isNotEmpty()) {
                    boxes.maxOf { it.id } + 1
                } else {
                    1
                }

                boxes = (boxes + BoxItem(newId)).sortedBy { box ->
                    when (box.riskStatus) {
                        "KRİTİK" -> 0
                        "RİSKLİ" -> 1
                        "NORMAL" -> 2
                        else -> 3
                    }
                }


                //boxes = boxes + BoxItem(newId)
                coroutineScope.launch {
                    saveBoxes(context, boxes)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(0.65f))
        ) {
            Text("Saat Ekle", color = Color.White)
        }
    }


}

@Composable
fun BoxItemView(item: BoxItem, onLongPress: () -> Unit, onClick: () -> Unit, boxColor: Color) {
    Box(
        modifier = Modifier
            .size(120.dp) // Boyutu biraz artırdık
            .padding(8.dp)
            .background(boxColor, RoundedCornerShape(16.dp)) // Daha yumuşak köşeler
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress()
                    },
                    onTap = {
                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Saat No: ${item.id}",
                color = MaterialTheme.colorScheme.onSurface, // Daha uyumlu bir renk
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = item.riskStatus,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Risk durumu için hafifçe farklı bir renk tonu
                style = MaterialTheme.typography.bodySmall
            )
        }

    }
}


data class BoxItem(val id: Int, val riskStatus: String = "PASİF",val ageGroup: String = "Unknown", val gender: String = "Unknown")

suspend fun loadBoxes(context: Context): List<BoxItem> {
    val preferences = context.dataStore.data.first()
    val count = preferences[intPreferencesKey("count")] ?: 0
    return List(count) { index ->
        val idKey = intPreferencesKey("box_${index + 1}_id")
        val id = preferences[idKey] ?: index + 1
        val riskStatusKey = stringPreferencesKey("box_${id}_riskStatus")
        val riskStatus = preferences[riskStatusKey] ?: "PASİF"
        val ageGroupKey = stringPreferencesKey("box_${id}_ageGroup")
        val ageGroup = preferences[ageGroupKey] ?: "Unknown"
        val genderKey = stringPreferencesKey("box_${id}_gender")
        val gender = preferences[genderKey] ?: "Unknown"
        BoxItem(id, riskStatus, ageGroup, gender)
    }
}

suspend fun saveBoxes(context: Context, boxes: List<BoxItem>) {
    context.dataStore.edit { preferences ->
        preferences[intPreferencesKey("count")] = boxes.size
        boxes.forEach { box ->
            val idKey = intPreferencesKey("box_${box.id}_id")
            val riskStatusKey = stringPreferencesKey("box_${box.id}_riskStatus")
            preferences[idKey] = box.id
            preferences[riskStatusKey] = box.riskStatus
            val ageGroupKey = stringPreferencesKey("box_${box.id}_ageGroup")
            val genderKey = stringPreferencesKey("box_${box.id}_gender")
            preferences[idKey] = box.id
            preferences[riskStatusKey] = box.riskStatus
            preferences[ageGroupKey] = box.ageGroup
            preferences[genderKey] = box.gender
        }
    }
}
