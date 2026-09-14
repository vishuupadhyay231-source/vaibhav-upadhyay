package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBlue

/**
 * Creative Animated Live Google Meet Logo with Pulsing Broadcast Radar & Blinking REC Studio Light
 */
@Composable
fun CreativeLiveMeetLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    isLive: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "creative_live_logo_anim")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_pulse_alpha"
    )

    val recBlink by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rec_blink"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // 1. Radar wave pulse rings (Only if currently Live)
        if (isLive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseScale)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE11D48).copy(alpha = pulseAlpha))
            )
        }

        // 2. Main Stylized Glassmorphic Squircle Container
        val primaryGradients = if (isLive) {
            listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF31103F))
        } else {
            listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0C4A6E))
        }

        val borderGradient = if (isLive) {
            listOf(Color(0xFFF43F5E), Color(0xFFA855F7), Color(0xFF38BDF8))
        } else {
            listOf(Color(0xFF38BDF8), Color(0xFF3B82F6))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(13.dp))
                .background(Brush.linearGradient(primaryGradients))
                .border(
                    BorderStroke(1.5.dp, Brush.linearGradient(borderGradient)),
                    RoundedCornerShape(13.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Stylized Videocam Icon with Google Meet aesthetic
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "Live Google Meet Camera",
                tint = if (isLive) Color.White else Color(0xFF38BDF8),
                modifier = Modifier.size(size * 0.58f)
            )

            // Top-right Red Studio Recording Beacon (REC ●)
            if (isLive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .size(size * 0.22f)
                        .clip(CircleShape)
                        .background(Color(0xFFFF0055).copy(alpha = recBlink))
                        .border(0.7.dp, Color.White, CircleShape)
                )
            }
        }
    }
}

/**
 * Creative Full-Width or Compact Join Google Meet Button
 */
@Composable
fun CreativeLiveJoinButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLive: Boolean = true,
    isCompact: Boolean = false,
    text: String = if (isLive) "Join Meet Live" else "Open Meet Room"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "btn_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val liveGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFE11D48),
            Color(0xFF9333EA),
            Color(0xFF2563EB)
        )
    )

    val normalGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF1D4ED8),
            Color(0xFF0284C7)
        )
    )

    if (isCompact) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
                .clickable { onClick() }
                .testTag("creative_join_meet_compact_btn"),
            color = Color.Transparent,
            border = BorderStroke(
                1.dp,
                if (isLive) Color(0xFFF43F5E).copy(alpha = glowAlpha) else Color(0xFF38BDF8)
            )
        ) {
            Box(
                modifier = Modifier
                    .background(if (isLive) liveGradient else normalGradient)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CreativeLiveMeetLogo(
                        size = 20.dp,
                        isLive = isLive
                    )
                    Text(
                        text = text,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    } else {
        // Full Hero Join Button with Subtitle and Arrow
        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = if (isLive) 6.dp else 2.dp,
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp)
                .clickable { onClick() }
                .testTag("join_google_meet_hero_button"),
            color = Color.Transparent,
            border = BorderStroke(
                1.5.dp,
                if (isLive) Color(0xFFFDA4AF).copy(alpha = glowAlpha) else Color(0xFFBAE6FD)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isLive) liveGradient else normalGradient)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CreativeLiveMeetLogo(
                            size = 32.dp,
                            isLive = isLive
                        )

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isLive) "JOIN LIVE CLASSROOM" else "OPEN CLASSROOM",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    letterSpacing = 0.5.sp
                                )
                                if (isLive) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color.White.copy(alpha = 0.25f)
                                    ) {
                                        Text(
                                            text = "HD MEET",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Google Meet • Interactive Audio & Screen Share",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Enter Meet",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
