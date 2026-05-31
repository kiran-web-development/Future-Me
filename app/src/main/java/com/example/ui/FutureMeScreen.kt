package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.api.FutureMeResponse
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FutureMeScreen(
    viewModel: FutureMeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Root background gradient matching custom brand guidelines for high luxury aura depth
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F12),
                        Color(0xFF13131A),
                        Color(0xFF1B1B25),
                        Color(0xFF0C0C0F)
                    )
                )
            )
            .drawBehind {
                // Subtle neon cyber-halo glowing orbs
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x180A84FF), Color.Transparent),
                        center = Offset(size.width * 0.15f, size.height * 0.25f),
                        radius = size.width * 0.7f
                    ),
                    center = Offset(size.width * 0.15f, size.height * 0.25f),
                    radius = size.width * 0.7f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x13BF5AF2), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.7f),
                        radius = size.width * 0.8f
                    ),
                    center = Offset(size.width * 0.85f, size.height * 0.7f),
                    radius = size.width * 0.8f
                )
            }
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "ScreenTransition"
        ) { state ->
            when (state) {
                is FutureMeUiState.Home -> {
                    HomeSection(viewModel = viewModel)
                }
                is FutureMeUiState.Form -> {
                    FormInputSection(viewModel = viewModel)
                }
                is FutureMeUiState.Loading -> {
                    LoadingSection(message = state.message)
                }
                is FutureMeUiState.Success -> {
                    ResultSection(
                        data = state.data,
                        viewModel = viewModel,
                        onStartChat = {
                            // Initiated by "Start Chat" click
                        }
                    )
                }
                is FutureMeUiState.Error -> {
                    ErrorSection(
                        message = state.errorMsg,
                        onBack = { viewModel.resetToForm() }
                    )
                }
            }
        }
    }
}

// ==========================================
// FORM FIELD AND INPUT DEFINITIONS
// ==========================================

@Composable
fun FormInputSection(viewModel: FutureMeViewModel) {
    val name by viewModel.name.collectAsState()
    val age by viewModel.age.collectAsState()
    val goal by viewModel.goal.collectAsState()
    val struggle by viewModel.struggle.collectAsState()
    val oneYearVision by viewModel.oneYearVision.collectAsState()
    val selectedTone by viewModel.tone.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateToHome() },
                    modifier = Modifier.testTag("back_to_home_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Workspace",
                        tint = AccentCyan
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Back to Workspace Home",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AccentCyan,
                    modifier = Modifier.clickable { viewModel.navigateToHome() }
                )
            }
        }

        item {
            HeaderSection()
        }

        item {
            GlassCard {
                Text(
                    text = "Reflect on Your Coordinate State",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CustomTextField(
                    value = name,
                    onValueChange = { viewModel.name.value = it },
                    label = "Your Name",
                    placeholder = "e.g., Kiran",
                    leadingIcon = Icons.Default.Info,
                    tag = "name_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                CustomTextField(
                    value = age,
                    onValueChange = { viewModel.age.value = it },
                    label = "Your Age",
                    placeholder = "e.g., 23",
                    leadingIcon = Icons.Default.Face,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    tag = "age_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                CustomTextField(
                    value = goal,
                    onValueChange = { viewModel.goal.value = it },
                    label = "Primary Dream / Core Goal",
                    placeholder = "e.g., Build a successful AI startup",
                    leadingIcon = Icons.Default.Star,
                    singleLine = false,
                    tag = "goal_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                CustomTextField(
                    value = struggle,
                    onValueChange = { viewModel.struggle.value = it },
                    label = "Biggest Daily Struggle / Friction",
                    placeholder = "e.g., Lack of consistency & overthinking",
                    leadingIcon = Icons.Default.Warning,
                    singleLine = false,
                    tag = "struggle_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                CustomTextField(
                    value = oneYearVision,
                    onValueChange = { viewModel.oneYearVision.value = it },
                    label = "One-Year Vision (Where are you?)",
                    placeholder = "e.g., Running a profitable AI company of my own",
                    leadingIcon = Icons.Default.Edit,
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    ),
                    tag = "union_input"
                )
            }
        }

        item {
            GlassCard {
                Text(
                    text = "Future Self Communication Tone",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                ToneSelectionGrid(
                    selectedTone = selectedTone,
                    options = viewModel.toneOptions,
                    onSelect = { viewModel.tone.value = it }
                )
            }
        }

        item {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = tween(150, easing = FastOutSlowInEasing),
                label = "Scale"
            )

            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.generateFutureMe()
                },
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .testTag("generate_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Generate My FutureMe",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun HeaderSection() {
    var launched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        launched = true
    }
    
    val rotateAnim by animateFloatAsState(
        targetValue = if (launched) 360f else 0f,
        animationSpec = tween(2000, easing = FastOutSlowInEasing),
        label = "IconRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(AccentBlue, Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .border(2.dp, AccentCyan, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier
                    .size(28.dp)
                    .rotate(rotateAnim)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Kiran Founder Labs",
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
            letterSpacing = 2.sp,
            color = AccentCyan,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(0.85f)
        )

        Text(
            text = "FutureMe",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "A direct bridge to the successful version of yourself.",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

// Custom abstraction for translucent modern text fields
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    tag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SlateDivider, RoundedCornerShape(14.dp))
                .testTag(tag),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x30000000),
                unfocusedContainerColor = Color(0x15000000),
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedPlaceholderColor = TextTertiary,
                unfocusedPlaceholderColor = TextTertiary
            ),
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(14.dp),
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else 3,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
    }
}

@Composable
fun ToneSelectionGrid(
    selectedTone: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { option ->
                    val isSelected = option == selectedTone
                    val borderAlpha by animateFloatAsState(if (isSelected) 1f else 0.2f)
                    val bgAlpha by animateFloatAsState(if (isSelected) 0.15f else 0.05f)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(
                                color = if (isSelected) AccentBlue.copy(alpha = bgAlpha) else Color.White.copy(alpha = bgAlpha),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) AccentBlue.copy(alpha = borderAlpha) else SlateDivider,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelect(option) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AccentCyan,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(end = 4.dp)
                                )
                            }
                            Text(
                                text = option,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// LOADING ANIMATED SECTION
// ==========================================

@Composable
fun LoadingSection(message: String) {
    // Premium loading states selector
    var stepMessage by remember { mutableStateOf("Mapping raw dimensional coordinates...") }
    val transitionMessages = listOf(
        "Interrogating absolute timelines and decision branches...",
        "Evaluating failure scenarios and struggle patterns...",
        "Locking in high-probability success habit profiles...",
        "Assembling personalized transmission telegram...",
        "Stabilizing quantum tunnel connection..."
    )

    LaunchedEffect(Unit) {
        var idx = 0
        while (true) {
            delay(2500)
            stepMessage = transitionMessages[idx % transitionMessages.size]
            idx++
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "RadarLoading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarRotation"
    )
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .drawBehind {
                    // Transparent cybernetic futuristic grid loop
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                AccentBlue.copy(alpha = 0.1f),
                                AccentCyan.copy(alpha = 0.5f),
                                AccentPurple.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        ),
                        radius = size.width / 2f
                    )
                }
                .rotate(rotation),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp * scalePulse)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(AccentBlue.copy(alpha = 0.4f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
                    .border(1.5.dp, AccentCyan.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = message,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedContent(
            targetState = stepMessage,
            transitionSpec = {
                slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
            },
            label = "StepMessage"
        ) { text ->
            Text(
                text = text,
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

// ==========================================
// RESULT / SUCCESS DASHBOARD VIEW
// ==========================================

@Composable
fun ResultSection(
    data: FutureMeResponse,
    viewModel: FutureMeViewModel,
    onStartChat: () -> Unit
) {
    var showChatMode by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showChatMode) {
        ChatSection(viewModel = viewModel, onBack = { showChatMode = false })
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Scrollable central report body
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TIMELINE REPORT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            letterSpacing = 3.sp
                        )
                        Text(
                            text = "Hello from 1 Year Ahead",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "A transcription of the person you are becoming.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 1. Identity Tag Glass Card
                item {
                    GlassCard(borderColor = AccentCyan.copy(alpha = 0.4f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Your Future Identity",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = data.futureIdentity,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                // 2. Primary Future Letter
                item {
                    GlassCard {
                        Text(
                            text = "Letter from Your Future Self",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            text = data.message,
                            fontSize = 15.sp,
                            color = TextPrimary.copy(alpha = 0.93f),
                            lineHeight = 24.sp,
                            fontStyle = FontStyle.Normal
                        )
                    }
                }

                // 3. Mantra Card (High tension highlight quote)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, GoldAmber.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0x20FFD60A)
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "YOUR TIMELINE MANTRA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAmber,
                                letterSpacing = 2.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "\"${data.mantra}\"",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                // 4. Next Moves Bullet timeline list
                item {
                    GlassCard {
                        Text(
                            text = "Next 3 High-Impact Steps",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        data.nextMoves.forEachIndexed { index, move ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(AccentBlue.copy(alpha = 0.2f), CircleShape)
                                        .border(1.dp, AccentBlue, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = AccentCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = move,
                                    fontSize = 14.sp,
                                    color = TextPrimary,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // 5. One Habit & Warning Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Crucial Habit
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SurfaceDarkGlass, RoundedCornerShape(20.dp))
                                .border(1.dp, SuccessGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Start Today",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = data.habit,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        // Danger Warning
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SurfaceDarkGlass, RoundedCornerShape(20.dp))
                                .border(1.dp, ErrorRed.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Mistake Warning",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ErrorRed,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = data.warning,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 6. Action Row Footer (Apple layout style)
            GlassCard(
                borderColor = SlateDivider,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.copyResultsToClipboard(context) },
                        modifier = Modifier
                            .background(SlateDivider, CircleShape)
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Report",
                            tint = TextPrimary
                        )
                    }

                    Button(
                        onClick = { viewModel.resetToForm() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SlateDivider, contentColor = TextPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Regenerate", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showChatMode = true },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("chat_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = TextPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Start Chat", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// CHAT WITH FUTUREME CONVERSATIONAL COMPONENT
// ==========================================

@Composable
fun ChatSection(
    viewModel: FutureMeViewModel,
    onBack: () -> Unit
) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val name by viewModel.name.collectAsState()
    val tone by viewModel.tone.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Autoscroll list whenever conversation extends
    LaunchedEffect(chatHistory.size, isChatLoading) {
        if (chatHistory.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(chatHistory.size)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(SlateDivider, CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to Report",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Chat with Future $name",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "System active • Mode: $tone",
                    fontSize = 11.sp,
                    color = AccentCyan
                )
            }
        }

        // Conversational Scroll Frame
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (chatHistory.isEmpty()) {
                // Conversational placeholder guide
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = AccentCyan.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Bridge Complete",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Ask your future self about details of the timeline, choices to make this week, or seek dynamic advice contextually.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(chatHistory) { message ->
                        ChatBubble(message = message)
                    }

                    if (isChatLoading) {
                        item {
                            ThinkingIndicator()
                        }
                    }
                }
            }
        }

        // Keyboard Row Input Footer
        GlassCard(
            borderColor = SlateDivider,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Ask your FutureMe anything...", fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedPlaceholderColor = TextTertiary,
                        unfocusedPlaceholderColor = TextTertiary
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (messageText.trim().isNotEmpty()) {
                                viewModel.sendChatMessage(messageText)
                                messageText = ""
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (messageText.trim().isNotEmpty() && !isChatLoading) {
                            viewModel.sendChatMessage(messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .background(if (messageText.trim().isNotEmpty() && !isChatLoading) AccentBlue else SlateDivider, CircleShape)
                        .size(44.dp)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Message",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "User"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val containerBg = if (isUser) AccentBlue else SlateDivider.copy(alpha = 0.5f)
    val shape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }
    val borderStroke = if (isUser) null else BorderStroke(1.dp, SlateDivider)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = containerBg,
            shape = shape,
            border = borderStroke,
            tonalElevation = 1.dp
        ) {
            Text(
                text = message.text,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
        Text(
            text = if (isUser) "You" else "FutureMe",
            fontSize = 10.sp,
            color = TextTertiary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ThinkingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "ThinkingAnim")
    val scalePulse1 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Dot1"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = SlateDivider.copy(alpha = 0.4f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateDivider)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("FutureMe is transmitting", fontSize = 12.sp, color = TextSecondary)
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(AccentCyan.copy(alpha = scalePulse1), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(AccentCyan.copy(alpha = (scalePulse1 + 0.3f).coerceAtMost(1f)), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(AccentCyan.copy(alpha = (scalePulse1 + 0.6f).coerceAtMost(1f)), CircleShape)
                )
            }
        }
    }
}

// ==========================================
// CENTRALIZED ERROR DISPLAY COMPONENT
// ==========================================

@Composable
fun ErrorSection(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(ErrorRed.copy(alpha = 0.15f), CircleShape)
                .border(2.dp, ErrorRed, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Timeline Interference",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            fontSize = 14.sp,
            color = TextSecondary,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = SlateDivider, contentColor = TextPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(44.dp).padding(horizontal = 16.dp)
        ) {
            Text("Re-establish Connection", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// STATIC TRANS-LUCENT GLASS CARD DEFINITION
// ==========================================

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = SlateDivider,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDarkGlass
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

// ==========================================
// THE HOME WORKSPACE & ABOUT COMPOSABLE
// ==========================================

@Composable
fun HomeSection(viewModel: FutureMeViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            HeaderSection()
        }

        // About FutureMe Card
        item {
            GlassCard(
                borderColor = AccentCyan.copy(alpha = 0.3f),
                modifier = Modifier.testTag("about_app_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About icon",
                        tint = AccentCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About FutureMe",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "FutureMe is an advanced temporal intelligence simulator that constructs a direct communication portal to your future self. By inputting your current state coordinates (your goals, obstacles, and dreams), Gemini synthesized models will forge an alternative path, yielding a customized future identity, recommended actions, warnings, and an interactive chat dialogue to communicate directly with your future self in real-time.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Key Operational Phases:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val stepsList = listOf(
                        "State Coordinates: Formulate goals, struggles, and long-term targets.",
                        "Select Tone Frequency: Align future persona frequencies (Brutally Honest, CEO Mode, Calm Mentor).",
                        "Sync Matrix: Compile and launch real-time dynamic conversational channel with Gemini models."
                    )
                    stepsList.forEachIndexed { index, stepText ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "${index + 1}. ",
                                color = AccentCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stepText,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Product Future Requirements Card
        item {
            GlassCard(
                borderColor = Color(0xFFBF5AF2).copy(alpha = 0.3f),
                modifier = Modifier.testTag("product_requirements_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Requirements icon",
                        tint = Color(0xFFBF5AF2),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Product Requirements",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "To preserve high-fidelity timeline accuracy, the FutureMe environment operates under the following functional specifications:",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                val requirements = listOf(
                    "Temporal Synthesis Engine" to "Form state coordinates validate input names, ages, primary goals, daily struggles, and one-year horizons.",
                    "API Direct Model Routing" to "Employs real-time secure communication with Gemini Flash models using structured JSON response formatting.",
                    "Alternative Timeline Prompts" to "Customized prompt injection with user-selected communication modalities (Motivational, CEO Mode, Calm Mentor, Brutally Honest).",
                    "Interactive Temporal Chat" to "Continuous state-tracked chat tracking back-and-forth communication dynamically using structured content parts.",
                    "Safe Navigation Flow" to "Local simulation of user state history with error safety limits."
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    requirements.forEach { (title, desc) ->
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFFBF5AF2), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(start = 14.dp, top = 2.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Action CTA Button
        item {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = tween(150, easing = FastOutSlowInEasing),
                label = "Scale"
            )

            Button(
                onClick = {
                    viewModel.navigateToForm()
                },
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .testTag("start_generator_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Generate My Future Profile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}


