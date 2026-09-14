package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiAiService
import com.example.data.model.ChatMessage
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiAssistantScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val chatMessages by viewModel.chatMessages.collectAsState()
    val isGenerating by viewModel.isAiGenerating.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var showKeyDialog by remember { mutableStateOf(false) }
    var apiKeyInput by remember { mutableStateOf("") }
    var isTestingKey by remember { mutableStateOf(false) }
    var testKeyFeedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val quickChips = listOf(
        "Explain Dijkstra's Algorithm in C++",
        "DBMS Normalization 1NF to BCNF",
        "Solve Quadratic: 2x² - 7x + 3 = 0",
        "Binary to Hex conversion method",
        "BCA Semester 3 Syllabus roadmap",
        "What is Banker's Deadlock algorithm?"
    )

    LaunchedEffect(showKeyDialog) {
        if (showKeyDialog) {
            apiKeyInput = GeminiAiService.getActiveApiKey()
        }
    }

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_assistant_screen")
    ) {
        // Jivan Header
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(BrandPurple, BrandCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Jivan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandSuccess.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "ONLINE",
                                    color = BrandSuccess,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "BCA • BTech • CS Academic AI Mentor",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showKeyDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Key,
                            contentDescription = "Gemini Key",
                            tint = BrandAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.clearAiChat() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickChips) { chip ->
                SuggestionChip(
                    onClick = {
                        viewModel.sendAiPrompt(chip)
                    },
                    label = { Text(chip, fontSize = 12.sp) },
                    icon = {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            tint = BrandAmber,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages, key = { it.id }) { message ->
                ChatBubble(
                    message = message,
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Jivan", message.text))
                        Toast.makeText(context, "Copied response to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = BrandCyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Jivan is formulating your academic solution...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Prompt Input Field
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask Jivan any study or code question...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("jivan_ai_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isGenerating) {
                            val prompt = inputText
                            inputText = ""
                            viewModel.sendAiPrompt(prompt)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) BrandBlue else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("jivan_ai_send_button"),
                    enabled = inputText.isNotBlank() && !isGenerating
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send prompt",
                        tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Gemini API Key Dialog for Direct Setup
        if (showKeyDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showKeyDialog = false
                    testKeyFeedback = null
                },
                icon = {
                    Icon(Icons.Default.Key, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(28.dp))
                },
                title = {
                    Text("Jivan AI Configuration", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Jivan can answer any coding or academic question using Google's Gemini AI. Enter your API key below:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { 
                                apiKeyInput = it
                                testKeyFeedback = null
                            },
                            label = { Text("Gemini API Key") },
                            placeholder = { Text("AIzaSy...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Test Button & Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (apiKeyInput.isNotBlank()) {
                                        isTestingKey = true
                                        testKeyFeedback = null
                                        scope.launch {
                                            val res = GeminiAiService.testApiKey(apiKeyInput.trim())
                                            testKeyFeedback = res
                                            isTestingKey = false
                                        }
                                    } else {
                                        testKeyFeedback = Pair(false, "Please enter an API key first.")
                                    }
                                },
                                enabled = !isTestingKey && apiKeyInput.isNotBlank(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isTestingKey) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Testing...", fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Test Key", fontSize = 12.sp)
                                }
                            }

                            if (GeminiAiService.getActiveApiKey().isNotBlank()) {
                                TextButton(
                                    onClick = {
                                        GeminiAiService.setCustomApiKey("")
                                        apiKeyInput = ""
                                        testKeyFeedback = Pair(true, "Key removed. Reverted to offline mode.")
                                        Toast.makeText(context, "API Key cleared", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text("Clear Key", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }

                        // Test result feedback banner
                        testKeyFeedback?.let { (success, msg) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (success) BrandSuccess.copy(alpha = 0.12f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = (if (success) "✅ " else "❌ ") + msg,
                                    fontSize = 12.sp,
                                    color = if (success) BrandSuccess else MaterialTheme.colorScheme.error,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        Text(
                            text = "Tip: Free Gemini API key is available in 10 seconds from Google AI Studio (aistudio.google.com). Once saved, it stays active on your phone forever!",
                            fontSize = 11.sp,
                            color = BrandBlue,
                            lineHeight = 15.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (apiKeyInput.isNotBlank()) {
                                GeminiAiService.setCustomApiKey(apiKeyInput.trim())
                                Toast.makeText(context, "Gemini API Key Saved & Active!", Toast.LENGTH_SHORT).show()
                            }
                            showKeyDialog = false
                            testKeyFeedback = null
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save & Apply")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showKeyDialog = false
                        testKeyFeedback = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onCopy: () -> Unit
) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BrandPurple)
                    .padding(top = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            color = if (isUser) BrandBlue else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy text",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
