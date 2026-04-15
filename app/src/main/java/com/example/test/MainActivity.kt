package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image // 新增
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // 新增
import androidx.compose.ui.res.painterResource // 新增
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FancyDrinkOrderApp()
                }
            }
        }
    }
}

// --- 新增：統一處理圖示的組件 ---
@Composable
fun DrinkIcon(name: String, icon: String, size: Dp) {
    if (name == "勞大冰紅茶") {
        Image(
            painter = painterResource(id = R.drawable.kobe), // 💡 請確保 drawable 資料夾有 kobe.jpg
            contentDescription = "勞大",
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Text(text = icon, fontSize = (size.value * 0.8).sp)
    }
}

data class OrderRecord(
    val drink: String,
    val icon: String,
    val sugar: String,
    val ice: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FancyDrinkOrderApp() {
    var selectedDrink by remember { mutableStateOf("珍珠奶茶") }
    var selectedSugar by remember { mutableStateOf("半糖") }
    var selectedIce by remember { mutableStateOf("少冰") }

    val orderHistory = remember { mutableStateListOf<OrderRecord>() }

    val iconMap = mapOf("珍珠奶茶" to "🧋", "勞大冰紅茶" to "☕", "茉莉綠茶" to "🍵", "鮮奶拿鐵" to "🥛")
    val drinks = iconMap.toList()
    val sugarLevels = listOf("無糖", "微糖", "半糖", "全糖")
    val iceLevels = listOf("去冰", "微冰", "少冰", "正常冰")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("點餐機", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF7EFE5))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFBF5))
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 1️⃣ 飲料選擇
            item {
                SectionHeader("1. 選擇心儀飲品", "Choose your base")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(180.dp).padding(top = 8.dp) // 高度稍微增加一點給照片
                ) {
                    items(drinks) { (name, icon) ->
                        DrinkCard(name, icon, selectedDrink == name) { selectedDrink = name }
                    }
                }
            }

            // 2️⃣ 甜度與冰量
            item { OptionSection("2. 調整甜度", sugarLevels, selectedSugar) { selectedSugar = it } }
            item { OptionSection("3. 選擇冰量", iceLevels, selectedIce) { selectedIce = it } }

            // 3️⃣ 即時摘要與按鈕
            item {
                SectionHeader("4. 確認並送出", "Order Preview")
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEADBC8).copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 💡 這裡也改用 DrinkIcon
                        DrinkIcon(name = selectedDrink, icon = iconMap[selectedDrink] ?: "🥤", size = 45.dp)

                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = selectedDrink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = "$selectedSugar / $selectedIce", color = Color.Gray, fontSize = 14.sp)
                        }
                        Button(
                            onClick = {
                                val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                orderHistory.add(0, OrderRecord(selectedDrink, iconMap[selectedDrink]!!, selectedSugar, selectedIce, currentTime))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF674188)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("點餐")
                        }
                    }
                }
            }

            // 4️⃣ 歷史紀錄區塊
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    SectionHeader("歷史訂單紀錄", "Order History")
                }
            }

            if (orderHistory.isEmpty()) {
                item {
                    Text("目前尚無點餐紀錄", color = Color.LightGray, modifier = Modifier.padding(16.dp))
                }
            } else {
                items(orderHistory) { record ->
                    HistoryItem(record)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(record: OrderRecord) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 💡 歷史紀錄也使用照片判斷
            DrinkIcon(name = record.drink, icon = record.icon, size = 32.dp)

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(record.drink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${record.sugar} / ${record.ice}", fontSize = 12.sp, color = Color.Gray)
            }
            Text(record.time, fontSize = 12.sp, color = Color.LightGray)
        }
    }
}

@Composable
fun DrinkCard(name: String, icon: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(if (isSelected) Color(0xFFC3ACD0) else Color.White)
    val borderColor = if (isSelected) Color(0xFF674188) else Color(0xFFE0E0E0)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 💡 使用照片判斷邏輯
            DrinkIcon(name = name, icon = icon, size = 40.dp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

// ... OptionSection 與 SectionHeader 保持不變 ...
@Composable
fun OptionSection(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(title, "Preference")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            options.forEach { option ->
                val isSelected = selected == option
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFF674188) else Color(0xFFF2F2F2))
                        .clickable { onSelect(option) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = option, color = if (isSelected) Color.White else Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(main: String, sub: String) {
    Column {
        Text(main, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D2727))
        Text(sub, fontSize = 10.sp, color = Color.Gray)
    }
}