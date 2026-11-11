
package com.aerolauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AppItem(
    val name: String,
    val packageName: String,
    val icon: ImageBitmap
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AeroHome(
                apps = loadApps(),
                launchApp = { pkg ->
                    packageManager
                        .getLaunchIntentForPackage(pkg)
                        ?.let { startActivity(it) }
                }
            )
        }
    }

    private fun loadApps(): List<AppItem> {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)

        return packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .mapNotNull { info ->
                val name = info.loadLabel(packageManager).toString()
                val pkg = info.activityInfo.packageName

                val bitmap = drawableToBitmap(
                    info.loadIcon(packageManager)
                )

                AppItem(name, pkg, bitmap.asImageBitmap())
            }
            .distinctBy { it.packageName }
            .sortedBy { it.name.lowercase() }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            96, 96, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, 96, 96)
        drawable.draw(canvas)
        return bitmap
    }
}

@Composable
fun AeroHome(
    apps: List<AppItem>,
    launchApp: (String) -> Unit
) {
    var search by remember { mutableStateOf("") }
    var selectedFolder by remember { mutableStateOf<String?>(null) }

    val folders = apps.groupBy { category(it) }

    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(
                    Color(0xFF182E53),
                    Color(0xFF527FA8),
                    Color(0xFFB7D0E8)
                )
            )
        )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {

            Text(
                "AERO",
                color = Color.White,
                letterSpacing = 5.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(38.dp))

            Text(
                "Good evening",
                color = Color.White.copy(.75f),
                fontSize = 18.sp
            )

            Text(
                "Your space.",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Light
            )

            Spacer(Modifier.height(25.dp))

            Surface(
                color = Color.White.copy(.20f),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, null, tint = Color.White)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Search apps",
                        color = Color.White.copy(.85f),
                        modifier = Modifier.clickable {
                            selectedFolder = "All Apps"
                        }
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                "Smart App Library",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(15.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(folders.keys.toList()) { folder ->

                    val items = folders[folder].orEmpty()

                    Surface(
                        color = Color.White.copy(.16f),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedFolder = folder
                            }
                    ) {
                        Column(Modifier.padding(14.dp)) {

                            Text(
                                folder,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(Modifier.height(12.dp))

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(7.dp)
                            ) {
                                items.take(4).forEach { app ->
                                    Image(
                                        app.icon,
                                        app.name,
                                        Modifier.size(34.dp)
                                            .clip(RoundedCornerShape(9.dp))
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "${items.size} apps  ›",
                                color = Color.White.copy(.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Surface(
                color = Color.White.copy(.20f),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(15.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        Icons.Default.Phone,
                        Icons.Default.Message,
                        Icons.Default.CameraAlt,
                        Icons.Default.GridView
                    ).forEach { icon ->
                        Icon(
                            icon,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(27.dp)
                        )
                    }
                }
            }
        }

        if (selectedFolder != null) {
            Surface(
                Modifier.fillMaxSize(),
                color = Color(0xFF142238)
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { selectedFolder = null }
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                null,
                                tint = Color.White
                            )
                        }

                        Text(
                            selectedFolder ?: "",
                            color = Color.White,
                            fontSize = 25.sp
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        verticalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        val folderApps =
                            if (selectedFolder == "All Apps") apps
                            else folders[selectedFolder].orEmpty()

                        items(folderApps) { app ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    launchApp(app.packageName)
                                }
                            ) {
                                Image(
                                    app.icon,
                                    app.name,
                                    Modifier.size(55.dp)
                                )

                                Text(
                                    app.name,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun category(app: AppItem): String {
    val text = (app.name + app.packageName).lowercase()

    return when {
        listOf(
            "whatsapp", "instagram", "telegram",
            "facebook", "messenger", "discord"
        ).any { it in text } -> "Social"

        listOf(
            "camera", "gallery", "photos",
            "youtube", "netflix", "spotify"
        ).any { it in text } -> "Photos & Media"

        listOf(
            "game", "minecraft", "roblox",
            "pubg", "clash"
        ).any { it in text } -> "Games"

        listOf(
            "shop", "amazon", "shopping"
        ).any { it in text } -> "Shopping"

        listOf(
            "bank", "wallet", "finance", "pay"
        ).any { it in text } -> "Finance"

        listOf(
            "calendar", "calculator", "files",
            "drive", "docs", "maps"
        ).any { it in text } -> "Productivity"

        listOf(
            "phone", "dialer", "contact", "message"
        ).any { it in text } -> "Communication"

        else -> "Utilities"
    }
}
