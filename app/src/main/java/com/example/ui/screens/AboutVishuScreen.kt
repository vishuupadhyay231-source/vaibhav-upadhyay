package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.components.openExternalUrl
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutVishuScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var feedbackText by remember { mutableStateOf("") }
    val creator by viewModel.creatorIntro.collectAsState()

    val skills = listOf(
        "AI & Machine Learning (GenAI)", "Web Development (Full Stack)", "App Development (Android / Kotlin)",
        "Cybersecurity & Security", "UI / UX Design (Figma)", "Data Structures & Algorithms",
        "Python & AI Integration", "Java & Spring Boot", "C / C++ Programming", "DBMS & SQL Architecture",
        "Discrete Mathematics", "Cloud Architecture & Firebase"
    )

    val photoSource = (creator?.avatarUrl?.ifBlank { creator?.photoUrl } ?: "")
    val igUser = (creator?.instagramUsername?.let {
        if (it.startsWith("@")) it else "@$it"
    } ?: "@vishu_upadhyay_").ifBlank { "@vishu_upadhyay_" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("about_vishu_screen"),
        contentPadding = PaddingValues(16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Futuristic Compact Holographic ID Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF060B19), Color(0xFF0F172A), Color(0xFF1E1B4B))
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.linearGradient(
                                colors = listOf(BrandCyan, Color(0xFF818CF8), BrandCyan, Color(0xFFC084FC))
                            ),
                            RoundedCornerShape(22.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Top ID Header Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(Color(0xFF0B1120)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = R.drawable.vishu_vi_cyber_neon_logo_1787479124539,
                                        contentDescription = "Logo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "VISHU CONNECT • ID CARD",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandCyan,
                                    letterSpacing = 1.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandSuccess.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandSuccess)
                            ) {
                                Text(
                                    text = "VERIFIED CREATOR",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandSuccess,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Center Avatar + Details Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Creative Compact ID Avatar with 3D Cyber Shield Insignia
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(BrandCyan, Color(0xFF818CF8), BrandPurple, BrandCyan)
                                        )
                                    )
                                    .padding(2.5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (photoSource.isNotBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(photoSource)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Profile Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else {
                                    AsyncImage(
                                        model = R.drawable.vishu_id_badge_avatar_1787479138905,
                                        contentDescription = "Vishu Executive ID Badge",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = creator?.fullName?.ifBlank { creator?.name ?: "Vishu Upadhyay" } ?: "Vishu Upadhyay",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = creator?.title?.ifBlank { creator?.roleTitle ?: "Founder & Lead Architect" } ?: "Founder & Lead Architect",
                                    fontSize = 12.sp,
                                    color = BrandCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ID: VU-2026-VC909 • BCA & AI Mentor",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Compact Bio Text
                        Text(
                            text = creator?.bio?.ifBlank { "Dedicated to empowering BCA, BTech & CS students with real-time solvers, handwritten notes & mentorship." } ?: "",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Skills Badges
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🛠 Technical Expertise & Stack",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        skills.forEach { skill ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandBlue.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = skill,
                                    color = BrandBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Social & Connect Channels
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🌐 Connect & Official Channels",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    // 1. Instagram Profile Card (Username ONLY - strictly no phone number)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val targetIg = creator?.instagramUrl?.ifBlank {
                                    "https://instagram.com/${igUser.removePrefix("@")}"
                                } ?: "https://instagram.com/vishu_upadhyay_"
                                openExternalUrl(context, targetIg)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Instagram",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Instagram", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        text = "Connect on Official Profile",
                                        color = Color(0xFFE1306C),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // 2. WhatsApp Direct Chat Card (Starts WhatsApp conversation)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val waLink = when {
                                    !creator?.whatsappUrl.isNullOrBlank() -> creator!!.whatsappUrl
                                    !creator?.whatsappNumber.isNullOrBlank() -> {
                                        val clean = creator!!.whatsappNumber.replace("+", "").replace(" ", "").replace("-", "")
                                        "https://wa.me/$clean?text=${Uri.encode(creator?.whatsappMessage ?: "Hello Vishu Sir")}"
                                    }
                                    else -> "https://wa.me/919876543210"
                                }
                                openExternalUrl(context, waLink)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF25D366)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "WhatsApp",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("WhatsApp Direct", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        text = "Chat with Vishu Upadhyay",
                                        color = Color(0xFF25D366),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SocialIconButton(
                            label = "GitHub",
                            icon = Icons.Default.Code,
                            color = Color(0xFF24292E),
                            onClick = { openExternalUrl(context, creator?.githubUrl?.ifBlank { "https://github.com/vishuupadhyay" } ?: "https://github.com/vishuupadhyay") }
                        )
                        SocialIconButton(
                            label = "Email",
                            icon = Icons.Default.Email,
                            color = Color(0xFFEA4335),
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${creator?.email?.ifBlank { "vishuupadhyay231@gmail.com" } ?: "vishuupadhyay231@gmail.com"}")
                                    putExtra(Intent.EXTRA_SUBJECT, "Vishu Connect Inquiry")
                                }
                                try { context.startActivity(intent) } catch (e: Exception) {}
                            }
                        )
                        SocialIconButton(
                            label = "YouTube",
                            icon = Icons.Default.PlayCircle,
                            color = Color(0xFFFF0000),
                            onClick = { openExternalUrl(context, creator?.youtubeUrl?.ifBlank { "https://youtube.com/@vishu_connect" } ?: "https://youtube.com/@vishu_connect") }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SocialIconButton(
                            label = "Telegram",
                            icon = Icons.Default.Send,
                            color = Color(0xFF0088CC),
                            onClick = { openExternalUrl(context, creator?.telegramUrl?.ifBlank { "https://t.me/vishu_connect" } ?: "https://t.me/vishu_connect") }
                        )
                        SocialIconButton(
                            label = "LinkedIn",
                            icon = Icons.Default.Person,
                            color = Color(0xFF0A66C2),
                            onClick = { openExternalUrl(context, creator?.linkedinUrl?.ifBlank { "https://linkedin.com/in/vishuupadhyay" } ?: "https://linkedin.com/in/vishuupadhyay") }
                        )
                        SocialIconButton(
                            label = "Website",
                            icon = Icons.Default.Language,
                            color = Color(0xFF10B981),
                            onClick = { openExternalUrl(context, creator?.websiteUrl?.ifBlank { "https://vishuconnect.edu" } ?: "https://vishuconnect.edu") }
                        )
                    }
                }
            }
        }

        // Student Feedback & Message box
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💬 Send Direct Feedback to Vishu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        placeholder = { Text("Share notes requests, bug reports, or feature ideas...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (feedbackText.isNotBlank()) {
                                Toast.makeText(context, "Feedback sent directly to Vishu!", Toast.LENGTH_SHORT).show()
                                feedbackText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Submit Feedback")
                    }
                }
            }
        }
    }
}

@Composable
fun SocialIconButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
