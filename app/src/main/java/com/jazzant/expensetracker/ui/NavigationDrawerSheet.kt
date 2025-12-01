package com.jazzant.expensetracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DrawerItem(
    val id: Int,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String = label,
    val onClick: () -> Unit,
)

@Composable
fun NavigationDrawerSheet(navDrawerItems: List<DrawerItem>, currentItemId: Int)
{
    ModalDrawerSheet{
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            DrawerHeader()
            navDrawerItems.forEach {
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = it.label,
                            style = TextStyle(fontSize = 18.sp),
                        )
                    },
                    selected = currentItemId == it.id,
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = it.contentDescription,
                        )
                    },
                    onClick = it.onClick
                )
            }
        }
    }
}

@Composable
private fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    ) {
        Text("Menu", fontSize = 32.sp)
    }
}