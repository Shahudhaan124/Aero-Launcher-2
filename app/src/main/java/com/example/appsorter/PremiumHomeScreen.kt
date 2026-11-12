package com.example.appsorter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap

@Composable
fun PremiumHomeScreen(appRepository: AppRepository) {
    var categorizedApps by remember { mutableStateOf>>(emptyMap()) }
    var expandedFolder by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        categorizedApps = appRepository.getInstalledApps()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .blur(if (expandedFolder != null) 24.dp else 0.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp, bottom = 120.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categorizedApps.keys.toList()) { category ->
                    val appsInFolder = categorizedApps[category] ?: emptyList()
                    FolderCard(
                        categoryName = category,
                        apps = appsInFolder,
                        onClick = { expandedFolder = category },
                        onAppClick = { packageName -> appRepository.launchApp(packageName) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    categorizedApps.values.flatten().take(4).forEach { app ->
                        DockIconItem(app = app, onClick = { appRepository.launchApp(app.packageName) })
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = expandedFolder != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expandedFolder = null },
                contentAlignment = Alignment.Center
            ) {
                expandedFolder?.let { category ->
                    val apps = categorizedApps[category] ?: emptyList()
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color.White.copy(alpha = 0.8f))
                            .padding(24.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(apps) { app ->
                                AppIconItem(app = app, textColor = Color.Black, onClick = {
                                    appRepository.launchApp(app.packageName)
                                    expandedFolder = null
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FolderCard(categoryName: String, apps: List<AppInfo>, onClick: () -> Unit, onAppClick: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.25f))
                .clickable { onClick() }
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MiniAppIcon(apps.getOrNull(0), onAppClick)
                    MiniAppIcon(apps.getOrNull(1), onAppClick)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MiniAppIcon(apps.getOrNull(2), onAppClick)
                    MiniAppIcon(apps.getOrNull(3), onAppClick)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = categoryName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MiniAppIcon(app: AppInfo?, onAppClick: (String) -> Unit) {
    if (app != null) {
        Image(
            bitmap = app.icon.toBitmap(config = android.graphics.Bitmap.Config.ARGB_8888).asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable { onAppClick(app.packageName) }
        )
    } else {
        Spacer(modifier = Modifier.size(28.dp))
    }
}

@Composable
fun AppIconItem(app: AppInfo, textColor: Color = Color.White, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            bitmap = app.icon.toBitmap(config = android.graphics.Bitmap.Config.ARGB_8888).asImageBitmap(),
            contentDescription = app.label,
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = app.label, color = textColor, fontSize = 11.sp,
            maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DockIconItem(app: AppInfo, onClick: () -> Unit) {
    Image(
        bitmap = app.icon.toBitmap(config = android.graphics.Bitmap.Config.ARGB_8888).asImageBitmap(),
        contentDescription = app.label,
        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick)
    )
}
