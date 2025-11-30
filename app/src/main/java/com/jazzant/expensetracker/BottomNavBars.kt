package com.jazzant.expensetracker

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
    val floating: Boolean = false,
    val onNavButtonClick: () -> Unit = {}
)

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier,
        actions = {
            items.forEach {
                if(it.floating) { return@forEach }
                val selected = it.route == currentRoute
                IconButton(
                    onClick = {
                        it.onNavButtonClick()
                        onItemClick(it)
                    },
                    enabled = !selected
                ) {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.name,
                        tint = if (selected) { Color.Red } else { Color.Gray }
                    )
                }
            }
        },
        floatingActionButton = {
            items.forEach {
                if (!it.floating){ return@forEach }
                FloatingActionButton(
                    onClick = {
                        it.onNavButtonClick()
                        onItemClick(it)
                    },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.name,
                    )
                }
            }
        }
    )
}