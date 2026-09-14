package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.AnnouncementItem
import com.example.data.model.ClassItem
import com.example.data.model.NoteItem
import com.example.ui.MainViewModel
import com.example.ui.components.CreativeLiveMeetLogo
import com.example.ui.components.openExternalUrl
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    notes: List<NoteItem>,
    classes: List<ClassItem>,
    announcements: List<AnnouncementItem>,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val posts by viewModel.communityPosts.collectAsState()
    val banners by viewModel.carouselBanners.collectAsState()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Auto-advancing Carousel State (5-6 posters running in animation)
    var currentBannerIndex by remember { mutableIntStateOf(0) }
    val effectiveBanners = remember(banners) {
        if (banners.isNotEmpty()) banners
        else listOf(
            com.example.data.model.CarouselBannerItem(
                title = "Smart AI Tutor",
                subtitle = "Ask doubts, generate code & solve complex math",
                badgeText = "🤖 JIVAN COMPANION",
                actionTarget = "ai_assistant"
            ),
            com.example.data.model.CarouselBannerItem(
                title = "Live Google Meet Class",
                subtitle = "Join interactive live BCA & CS lectures with top faculty",
                badgeText = "🔴 LIVE CLASSROOM",
                actionTarget = "classes"
            ),
            com.example.data.model.CarouselBannerItem(
                title = "240 BCA STEM Solvers",
                subtitle = "Matrices, calculus, sorting, gates & subnetting calculators",
                badgeText = "⚡ 240 SOLVERS",
                actionTarget = "calculators"
            ),
            com.example.data.model.CarouselBannerItem(
                title = "Semester Notes Hub",
                subtitle = "Download & upload verified PDF notes and handwritten guides",
                badgeText = "📚 STUDY NOTES",
                actionTarget = "notes"
            ),
            com.example.data.model.CarouselBannerItem(
                title = "Coding & DSA Repository",
                subtitle = "C++, Java, Python, SQL programs with full explanations",
                badgeText = "💻 CODE REPO",
                actionTarget = "code_notes"
            ),
            com.example.data.model.CarouselBannerItem(
                title = "Community Doubt Solver",
                subtitle = "Ask doubts, discuss with peers and get verified answers",
                badgeText = "💬 COMMUNITY",
                actionTarget = "posts"
            )
        )
    }

    LaunchedEffect(effectiveBanners.size) {
        while (true) {
            kotlinx.coroutines.delay(4000)
            if (effectiveBanners.isNotEmpty()) {
                currentBannerIndex = (currentBannerIndex + 1) % effectiveBanners.size
            }
        }
    }

    val bannerGradients = listOf(
        listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4C1D95)),
        listOf(Color(0xFF881337), Color(0xFF9F1239), Color(0xFFBE123C), Color(0xFF4C0519)),
        listOf(Color(0xFF064E3B), Color(0xFF065F46), Color(0xFF047857), Color(0xFF022C22)),
        listOf(Color(0xFF1E3A8A), Color(0xFF1D4ED8), Color(0xFF2563EB), Color(0xFF172554)),
        listOf(Color(0xFF431407), Color(0xFF7C2D12), Color(0xFF9A3412), Color(0xFF2A0800)),
        listOf(Color(0xFF3B0764), Color(0xFF581C87), Color(0xFF6B21A8), Color(0xFF240046))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_content")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Hero Dynamic Animated Banner Carousel (5-6 Posters in Rotation)
            item {
                val activeBanner = effectiveBanners.getOrNull(currentBannerIndex) ?: effectiveBanners.first()
                val activeGradient = bannerGradients[currentBannerIndex % bannerGradients.size]

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(185.dp)
                            .clickable {
                                onNavigate(activeBanner.actionTarget.ifBlank { "ai_assistant" })
                            }
                    ) {
                        AnimatedContent(
                            targetState = Pair(activeBanner, activeGradient),
                            transitionSpec = {
                                fadeIn(androidx.compose.animation.core.tween(400)) togetherWith
                                        fadeOut(androidx.compose.animation.core.tween(400))
                            },
                            label = "carousel_banner_animation"
                        ) { (banner, gradient) ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.linearGradient(colors = gradient))
                                    .border(
                                        1.dp,
                                        Color.White.copy(alpha = 0.25f),
                                        RoundedCornerShape(22.dp)
                                    )
                            ) {
                                // Background Artwork Layer (Exact banner art for Jivan AI Tutor and custom URLs)
                                if (banner.imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(banner.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else if (banner.actionTarget == "ai_assistant" || banner.title.contains("Smart AI", ignoreCase = true) || banner.title.contains("Jivan", ignoreCase = true)) {
                                    AsyncImage(
                                        model = R.drawable.jivan_ai_hero_banner_1787306327123,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                // Dark Tint / Gradient Overlay to ensure crisp contrast of text and indicators
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color(0x77050B14),
                                                    Color(0xDD0A1128)
                                                )
                                            )
                                        )
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(18.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Top row: Pill badge + Arrow
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color.White.copy(alpha = 0.18f),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                Color.White.copy(alpha = 0.4f)
                                            )
                                        ) {
                                            Text(
                                                text = banner.badgeText,
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.5.sp,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowForward,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    // Middle text: Title and Subtitle
                                    Column {
                                        Text(
                                            text = banner.title,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = banner.subtitle,
                                            fontSize = 12.sp,
                                            color = Color(0xFFE2E8F0),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Bottom row: Dynamic Indicator Dots matching number of banners
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        effectiveBanners.indices.forEach { index ->
                                            val isSelected = index == currentBannerIndex
                                            Box(
                                                modifier = Modifier
                                                    .width(if (isSelected) 22.dp else 6.dp)
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(
                                                        if (isSelected) Color(0xFFF59E0B)
                                                        else Color.White.copy(alpha = 0.35f)
                                                    )
                                                    .clickable { currentBannerIndex = index }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Search & Quick Doubt Action Row
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Search BCA notes, C++ programs, 200+ formulas...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Quick Action Action Row (Ask Doubt for Students)
                    Button(
                        onClick = { onNavigate("posts") },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ask Student Doubt / Community", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 3. Live Class Alert Card (Pink container with red live indicator, exactly like screenshot)
            val liveClass = classes.firstOrNull { it.status.equals("Live", ignoreCase = true) } ?: classes.firstOrNull()
            if (liveClass != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (liveClass.status.equals("Live", ignoreCase = true))
                                Color(0xFFFEF2F2).copy(alpha = 0.95f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (liveClass.status.equals("Live", ignoreCase = true)) Color(0xFFFCA5A5) else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable {
                                openExternalUrl(context, liveClass.meetLink)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left Creative Animated Live Google Meet Logo
                            CreativeLiveMeetLogo(
                                size = 48.dp,
                                isLive = liveClass.status.equals("Live", ignoreCase = true)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Middle Info
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "● LIVE CLASS NOW",
                                        color = Color(0xFFDC2626),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = liveClass.time,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = liveClass.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0F172A),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${liveClass.subject} • ${liveClass.teacher}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Right Join Pill Button
                            Button(
                                onClick = { openExternalUrl(context, liveClass.meetLink) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Join", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 4. Section Title: Explore Learning Hub
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Explore Learning Hub",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "8 Modules",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 5. Four Primary Grid Cards (2x2) matching screenshot perfectly
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Row 1: Notes Hub & Code Notes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HubTileCard(
                            title = "Notes Hub",
                            subtitle = "BCA, B.Tech & CS",
                            icon = Icons.Default.MenuBook,
                            iconBgColor = Color(0xFF8B5CF6),
                            cardBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("notes") }
                        )

                        HubTileCard(
                            title = "Code Notes",
                            subtitle = "Programs & Syntax",
                            icon = Icons.Default.Code,
                            iconBgColor = Color(0xFF06B6D4),
                            cardBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("code_notes") }
                        )
                    }

                    // Row 2: 240 BCA Solvers & Jeevan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HubTileCard(
                            title = "240 BCA Solvers",
                            subtitle = "Sem 1-6 STEM Math",
                            icon = Icons.Default.Calculate,
                            iconBgColor = Color(0xFF10B981),
                            cardBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("calculators") }
                        )

                        HubTileCard(
                            title = "Jivan",
                            subtitle = "Smart AI Tutor",
                            icon = Icons.Default.AutoAwesome,
                            iconBgColor = Color(0xFFF59E0B),
                            cardBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("ai_assistant") }
                        )
                    }

                    // Row 3: Courses & Projects Hub
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HubTileCard(
                            title = "Courses & Syllabi",
                            subtitle = "Video Roadmaps",
                            icon = Icons.Default.WorkspacePremium,
                            iconBgColor = Color(0xFF6366F1),
                            cardBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("courses") }
                        )

                        HubTileCard(
                            title = "College Projects",
                            subtitle = "Source Code & Repos",
                            icon = Icons.Default.FolderSpecial,
                            iconBgColor = Color(0xFFEC4899),
                            cardBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("projects_apps") }
                        )
                    }
                }
            }

            // 6. Priority Announcements Bar
            if (announcements.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Campaign,
                                contentDescription = null,
                                tint = BrandAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Campus Announcements",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(announcements, key = { it.id }) { announcement ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (announcement.priority == "Urgent")
                                            BrandAmber.copy(alpha = 0.15f)
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    modifier = Modifier.width(280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = announcement.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = announcement.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Floating Notes Monetization Popup + Jivan Action Button (Bottom Right)
        var showNotesSellingPopup by remember { mutableStateOf(true) }

        // 30 seconds timer loop for popup appearance and disappearance
        LaunchedEffect(Unit) {
            while (true) {
                showNotesSellingPopup = true
                kotlinx.coroutines.delay(30000) // Show for 30 seconds
                showNotesSellingPopup = false
                kotlinx.coroutines.delay(20000) // Reappear after 20 seconds
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Animated Popup Bubble above Jivan
            AnimatedVisibility(
                visible = showNotesSellingPopup,
                enter = fadeIn(androidx.compose.animation.core.tween(400)) + slideInVertically { it / 2 },
                exit = fadeOut(androidx.compose.animation.core.tween(300)) + slideOutVertically { it / 2 }
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .widthIn(max = 290.dp)
                        .border(
                            1.dp,
                            BrandAmber.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BrandAmber.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "EARN / SELL",
                                        color = BrandAmber,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Share Notes with Admin",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(
                                onClick = { showNotesSellingPopup = false },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = "Want to share or sell clean notes? Contact Admin Vishu to set the price & get paid directly via UPI!",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    showNotesSellingPopup = false
                                    onNavigate("notes")
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Upload", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = {
                                    try {
                                        val intent = android.content.Intent(
                                            android.content.Intent.ACTION_VIEW,
                                            android.net.Uri.parse("https://wa.me/919876543210?text=Hello%20Admin%20Vishu,%20I%20have%20clean%20BCA/CS%20notes%20to%20sell/share%20on%20Vishu%20Connect!%20My%20UPI%20ID%20is%20ready.")
                                        )
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(context, "Opening WhatsApp...", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(
                                    Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Contact Admin",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Jivan Action Button
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = { onNavigate("ai_assistant") },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF7C3AED), Color(0xFF9333EA), Color(0xFFC026D3))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "Jivan",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Jivan",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HubTileCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    cardBgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

