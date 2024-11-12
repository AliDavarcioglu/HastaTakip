package com.example.hastatakip

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                "ACİL" -> 0
                "ORTA RİSKLİ" -> 1
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
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Grid to display boxes
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(boxes.size) { index ->

                val box = boxes[index]

                val boxColor = when (box.riskStatus) {
                    "ACİL" -> Color.Red.copy(0.65f)
                    "ORTA RİSKLİ" -> Color.Yellow.copy(0.65f)
                    "NORMAL" -> Color.Green.copy(0.65f)
                    else -> Color.Gray.copy(0.6f)
                }

                BoxWithImageAndContent(
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
                    },
                    boxColor = boxColor,
                    imageResId = R.drawable.watch
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
                        "ACİL" -> 0
                        "ORTA RİSKLİ" -> 1
                        "NORMAL" -> 2
                        else -> 3
                    }
                }

                coroutineScope.launch {
                    saveBoxes(context, boxes)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(0.65f))
        ) {
            Text("Saat Ekle", color = Color.White)
        }



    }


}


@Composable
fun BoxWithImageAndContent(
    item: BoxItem,
    onLongPress: () -> Unit,
    onClick: () -> Unit,
    boxColor: Color,
    imageResId: Int // Eklenen resim ID'si
) {

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // Ekran boyutlarına göre dinamik boyutlandırma
    val boxSize = screenWidth * 0.55f // Ekran genişliğinin %45'i kadar kutu boyutu
    val boxHeight = screenHeight * 0.28f // Ekran yüksekliğinin %25'i kadar kutu yüksekliği
    val imagePadding = screenWidth * 0.01f // Ekran genişliğinin %1'i kadar padding


    Box(
        modifier = Modifier
            .size(boxSize,boxHeight)
            .padding(imagePadding)
            .clip(RoundedCornerShape(16.dp)) // Daha yumuşak köşeler
            .background(Color.Transparent), // Arka planı şeffaf yapıyoruz
        contentAlignment = Alignment.Center
    ) {
        // Resmi kutunun arka planı olarak yerleştirin
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
        )

        // BoxItemView içeriğini üst üste yerleştirin
        BoxItemView(
            item = item,
            onLongPress = onLongPress,
            onClick = onClick,
            boxColor = boxColor,
            contentPadding = PaddingValues(
                start = screenWidth * 0.035f,
                end = screenWidth * 0.045f,
                top = screenHeight * 0.006f,
                bottom = screenHeight * 0.02f
            )

        )
    }
}




@Composable
fun BoxItemView(item: BoxItem, onLongPress: () -> Unit, onClick: () -> Unit, boxColor: Color, contentPadding: PaddingValues) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp



    Box(
        modifier = Modifier
            .size(120.dp)
            .padding(contentPadding)
            .background(boxColor, RoundedCornerShape(18.dp))
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
                text = "NO: ${item.id}",
                color = MaterialTheme.colorScheme.onSurface, // Daha uyumlu bir renk
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = item.riskStatus,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Risk durumu için hafifçe farklı bir renk tonu
                style = MaterialTheme.typography.bodySmall,
                fontSize = 14.sp,
                textAlign = TextAlign.Center

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
