package com.smsgateway24.nosignalclub.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smsgateway24.nosignalclub.R
import com.smsgateway24.nosignalclub.ui.theme.*

@Composable
fun AboutScreen(nav: NavController) {
    val ctx = LocalContext.current
    val packageInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)

    Box(modifier = Modifier.fillMaxSize().background(DeepBlack)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 56.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { nav.popBackStack() },
                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceCard).border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(stringResource(R.string.about_title), color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("NoSignalClub", color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(NeonGreen.copy(alpha = 0.08f), SurfaceCard)))
                    .border(1.dp, NeonGreen.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(NeonGreen.copy(alpha = 0.1f)).border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("NoSignalClub", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(2.dp))
                        Text("v${packageInfo.versionName}", color = NeonGreen, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
                        Text(stringResource(R.string.app_subtitle), color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            Text(stringResource(R.string.section_resources), color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp, modifier = Modifier.padding(top = 4.dp))

            LinkRow(icon = Icons.Default.Code, title = stringResource(R.string.github_repo_title), subtitle = stringResource(R.string.github_repo_subtitle),
                onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pushkadev/NoSignalClub"))) })

            LinkRow(icon = Icons.Default.BugReport, title = stringResource(R.string.github_issues_title), subtitle = stringResource(R.string.github_issues_subtitle),
                onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pushkadev/NoSignalClub/issues"))) })
        }
    }
}

@Composable
private fun LinkRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceCard).border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))) {
        TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(4.dp), shape = RoundedCornerShape(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceDark), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(subtitle, color = TextSecondary, fontSize = 11.sp)
                }
                Icon(Icons.Default.Launch, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
            }
        }
    }
}
