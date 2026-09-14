package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteItem
import com.example.data.model.PaymentGatewayConfig
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NotesScreen(
    viewModel: MainViewModel,
    notes: List<NoteItem>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val unlockedNoteIds by viewModel.unlockedNoteIds.collectAsState()
    val paymentConfig by viewModel.paymentGatewayConfig.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf("All") }
    var selectedSemester by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("All") }

    var selectedNoteForDetail by remember { mutableStateOf<NoteItem?>(null) }
    var noteToUnlock by remember { mutableStateOf<NoteItem?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }

    // Download state map for IDs currently downloading
    val downloadingMap = remember { mutableStateMapOf<Long, Float>() }

    val courses = listOf("All", "BCA", "BTech", "CS", "Math")
    val categories = listOf("All", "Exam Notes", "Hand Written", "Question Bank", "Cheat Sheet")

    val filteredNotes = notes.filter { note ->
        val matchesQuery = searchQuery.isBlank() ||
                note.title.contains(searchQuery, ignoreCase = true) ||
                note.subject.contains(searchQuery, ignoreCase = true) ||
                note.description.contains(searchQuery, ignoreCase = true)

        val matchesCourse = selectedCourse == "All" || note.course.equals(selectedCourse, ignoreCase = true)
        val matchesSem = selectedSemester == 0 || note.semester == selectedSemester
        val matchesCategory = selectedCategory == "All" || note.category.equals(selectedCategory, ignoreCase = true)

        matchesQuery && matchesCourse && matchesSem && matchesCategory
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showUploadDialog = true },
                containerColor = BrandBlue,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.CloudUpload, contentDescription = "Upload Note") },
                text = { Text("Upload / Sell Note", fontWeight = FontWeight.Bold) },
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("upload_note_fab")
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("notes_search_input"),
                placeholder = { Text("Search by subject, topic or semester...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // Course Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(courses) { course ->
                    FilterChip(
                        selected = selectedCourse == course,
                        onClick = { selectedCourse = course },
                        label = { Text(course) },
                        leadingIcon = if (selectedCourse == course) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            // Semester Selector
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    AssistChip(
                        onClick = { selectedSemester = 0 },
                        label = { Text("All Sem") },
                        colors = if (selectedSemester == 0)
                            AssistChipDefaults.assistChipColors(containerColor = BrandBlue.copy(alpha = 0.2f))
                        else
                            AssistChipDefaults.assistChipColors()
                    )
                }
                items((1..8).toList()) { sem ->
                    AssistChip(
                        onClick = { selectedSemester = sem },
                        label = { Text("Sem $sem") },
                        colors = if (selectedSemester == sem)
                            AssistChipDefaults.assistChipColors(containerColor = BrandBlue.copy(alpha = 0.2f))
                        else
                            AssistChipDefaults.assistChipColors()
                    )
                }
            }

            // Partner with Admin Banner Strip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Want to sell or share verified notes?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Upload directly or contact Admin Vishu on WhatsApp",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/919876543210?text=Hi%20Vishu,%20I%20want%20to%20share%20my%20BCA%20notes%20on%20Vishu%20Connect!"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Opening WhatsApp...", Toast.LENGTH_SHORT).show()
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Contact", fontSize = 11.sp, color = Color(0xFF25D366), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Results count header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${filteredNotes.size} Verified Notes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            // Notes List
            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No notes found matching criteria",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        val isUnlocked = viewModel.isNoteUnlocked(note)

                        NoteCardItem(
                            note = note,
                            isUnlocked = isUnlocked,
                            downloadProgress = downloadingMap[note.id],
                            onCardClick = { selectedNoteForDetail = note },
                            onSaveClick = { viewModel.toggleSaveNote(note) },
                            onDownloadClick = {
                                if (isUnlocked) {
                                    scope.launch {
                                        downloadingMap[note.id] = 0.1f
                                        delay(300)
                                        downloadingMap[note.id] = 0.6f
                                        delay(300)
                                        downloadingMap[note.id] = 1.0f
                                        delay(200)
                                        downloadingMap.remove(note.id)
                                        viewModel.downloadNote(note) {
                                            Toast.makeText(
                                                context,
                                                "Saved ${note.fileName} to Downloads folder!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } else {
                                    // Open Payment Gateway Checkout Dialog
                                    noteToUnlock = note
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Detail Dialog
    selectedNoteForDetail?.let { note ->
        val isUnlocked = viewModel.isNoteUnlocked(note)

        AlertDialog(
            onDismissRequest = { selectedNoteForDetail = null },
            title = {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandBlue.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${note.course} • Semester ${note.semester} • ${note.subject}",
                                color = BrandBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (note.isPaid && !isUnlocked) Color(0xFFD97706).copy(alpha = 0.2f) else BrandSuccess.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (!note.isPaid) "FREE" else if (isUnlocked) "UNLOCKED" else "₹${note.price.toInt()}",
                                color = if (note.isPaid && !isUnlocked) Color(0xFFD97706) else BrandSuccess,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = note.description,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                    if (note.contentSummary.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Topics Summary:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = note.contentSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Size: ${note.fileSize} (${note.fileType})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Downloads: ${note.downloadCount}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val n = note
                        selectedNoteForDetail = null
                        if (isUnlocked) {
                            scope.launch {
                                downloadingMap[n.id] = 0.1f
                                delay(300)
                                downloadingMap[n.id] = 1.0f
                                delay(200)
                                downloadingMap.remove(n.id)
                                viewModel.downloadNote(n) {
                                    Toast.makeText(context, "Downloaded ${n.fileName}!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            noteToUnlock = n
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUnlocked) BrandBlue else BrandAmber
                    )
                ) {
                    Icon(
                        if (isUnlocked) Icons.Default.Download else Icons.Default.LockOpen,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isUnlocked) "Download PDF" else "Download / Unlock")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedNoteForDetail = null }) {
                    Text("Close")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Payment Gateway Checkout Dialog
    noteToUnlock?.let { note ->
        PaymentCheckoutDialog(
            note = note,
            config = paymentConfig ?: PaymentGatewayConfig(),
            onDismiss = { noteToUnlock = null },
            onPaymentSuccess = {
                viewModel.unlockNote(note.id)
                noteToUnlock = null
                Toast.makeText(context, "Payment successful! Note is now unlocked.", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Upload Note Dialog with Paid/Price option
    if (showUploadDialog) {
        UploadNoteDialog(
            onDismiss = { showUploadDialog = false },
            onUpload = { title, subject, course, sem, category, desc, file, isPaid, price ->
                val error = viewModel.uploadUserNote(title, subject, course, sem, category, desc, file, isPaid, price)
                if (error != null) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                } else {
                    showUploadDialog = false
                    Toast.makeText(context, "Note submitted for admin approval!", Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}

@Composable
fun NoteCardItem(
    note: NoteItem,
    isUnlocked: Boolean,
    downloadProgress: Float?,
    onCardClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("note_card_${note.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandBlue.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${note.course} • Sem ${note.semester}",
                            color = BrandBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (!note.isPaid) BrandSuccess.copy(alpha = 0.15f)
                        else if (isUnlocked) BrandCyan.copy(alpha = 0.15f)
                        else BrandViolet.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (!note.isPaid) "FREE"
                            else if (isUnlocked) "UNLOCKED"
                            else "PREMIUM",
                            color = if (!note.isPaid) BrandSuccess
                            else if (isUnlocked) BrandCyan
                            else BrandViolet,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (note.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save Note",
                            tint = if (note.isSaved) BrandAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = note.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (downloadProgress != null) {
                LinearProgressIndicator(
                    progress = { downloadProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = BrandCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AttachFile,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${note.fileType} • ${note.fileSize}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        Icons.Default.Download,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${note.downloadCount}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onDownloadClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUnlocked) BrandBlue else BrandAmber
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        if (isUnlocked) Icons.Default.Download else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isUnlocked) "Download PDF" else "Download / Unlock",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentCheckoutDialog(
    note: NoteItem,
    config: PaymentGatewayConfig,
    onDismiss: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var transactionRef by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payment, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unlock Premium Note", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandSuccess.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                            Text(text = "Gateway: ${config.provider}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = "₹${note.price.toInt()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = BrandSuccess
                        )
                    }
                }

                Text(
                    text = "Merchant: ${config.merchantName}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Text(
                    text = "UPI ID: ${config.merchantUpiId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandBlue
                )

                Text(
                    text = config.instructions,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )

                OutlinedButton(
                    onClick = {
                        try {
                            val upiUri = Uri.parse("upi://pay?pa=${config.merchantUpiId}&pn=${Uri.encode(config.merchantName)}&am=${note.price}&cu=${config.currency}&tn=${Uri.encode("Unlock Note: ${note.title}")}")
                            val intent = Intent(Intent.ACTION_VIEW, upiUri)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Please transfer ₹${note.price.toInt()} to ${config.merchantUpiId}", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open UPI App (GPay/PhonePe/Paytm)", fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = transactionRef,
                    onValueChange = { transactionRef = it },
                    label = { Text("Enter 12-Digit UTR / Transaction ID") },
                    placeholder = { Text("e.g. 423984729183") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "Need help? Contact: ${config.supportContact}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPaymentSuccess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Verify & Unlock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun UploadNoteDialog(
    onDismiss: () -> Unit,
    onUpload: (String, String, String, Int, String, String, String, Boolean, Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("BCA") }
    var semester by remember { mutableIntStateOf(1) }
    var category by remember { mutableStateOf("Exam Notes") }
    var description by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("My_Semester_Notes.pdf") }
    var isPaid by remember { mutableStateOf(false) }
    var priceText by remember { mutableStateOf("49") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Upload Educational Notes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Note Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject / Topic") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = course,
                            onValueChange = { course = it },
                            label = { Text("Course") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = semester.toString(),
                            onValueChange = { semester = it.toIntOrNull() ?: 1 },
                            label = { Text("Semester") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    // Paid vs Free toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Monetize Note (Paid Note)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(
                                if (isPaid) "Students must pay to download" else "Free for all students",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(checked = isPaid, onCheckedChange = { isPaid = it })
                    }
                }
                if (isPaid) {
                    item {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Your Price in ₹ INR") },
                            leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Chapters") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
                item {
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("Attachment Name (e.g. OS_Unit1_Full.pdf)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category / Tags (e.g. Unit Notes, Drive PDF, Exam Prep)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && subject.isNotBlank()) {
                        val parsedPrice = if (isPaid) (priceText.toDoubleOrNull() ?: 49.0) else 0.0
                        onUpload(title, subject, course, semester, category, description, fileName, isPaid, parsedPrice)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
            ) {
                Text("Submit Note")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
