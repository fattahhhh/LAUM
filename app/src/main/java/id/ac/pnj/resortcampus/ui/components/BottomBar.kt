package id.ac.pnj.resortcampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import id.ac.pnj.resortcampus.ui.Routes
import id.ac.pnj.resortcampus.ui.navigateTab
import id.ac.pnj.resortcampus.ui.screens.ScanMode
import id.ac.pnj.resortcampus.ui.theme.Green900
import id.ac.pnj.resortcampus.ui.theme.GreenSoft
import id.ac.pnj.resortcampus.ui.theme.Line
import id.ac.pnj.resortcampus.ui.theme.Mint50
import id.ac.pnj.resortcampus.ui.theme.Muted

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val HOME_TAB = Tab(Routes.HOME, "Beranda", Icons.Filled.Home)
private val PROFILE_TAB = Tab(Routes.PROFILE, "Profil", Icons.Filled.Person)

/** Kerangka layar utama dengan bottom nav 3 item: Beranda | Scan | Profil. */
@Composable
fun MainScaffold(
    nav: NavHostController,
    current: String,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = Mint50,
        bottomBar = { BottomBar(nav, current) },
        content = content,
    )
}

@Composable
private fun BottomBar(nav: NavHostController, current: String) {
    Surface(color = Color.White) {
        Column(Modifier.navigationBarsPadding()) {
            HorizontalDivider(color = Line)
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NavItem(HOME_TAB, current == HOME_TAB.route, Modifier.weight(1f)) { nav.navigateTab(HOME_TAB.route) }
                ScanPill(Modifier.weight(1.4f)) {
                    nav.navigate(Routes.scan(ScanMode.WASTE)) { launchSingleTop = true }
                }
                NavItem(PROFILE_TAB, current == PROFILE_TAB.route, Modifier.weight(1f)) { nav.navigateTab(PROFILE_TAB.route) }
            }
        }
    }
}

@Composable
private fun ScanPill(modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = CircleShape,
        color = Green900,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Scan", color = Color.White, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun NavItem(tab: Tab, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val color = if (selected) Green900 else Muted
    Row(
        modifier
            .height(48.dp)
            .clip(CircleShape)
            .background(if (selected) GreenSoft else Color.Transparent)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(tab.icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(6.dp))
        Text(tab.label, color = color, style = MaterialTheme.typography.labelMedium, maxLines = 1)
    }
}