package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.FutureMeResponse
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.Part
import com.example.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class FutureMeUiState {
    object Home : FutureMeUiState()
    object Form : FutureMeUiState()
    data class Loading(val message: String) : FutureMeUiState()
    data class Success(val data: FutureMeResponse) : FutureMeUiState()
    data class Error(val errorMsg: String) : FutureMeUiState()
}

data class ChatMessage(
    val sender: String, // "User" or "FutureMe"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class FutureMeViewModel(application: Application) : AndroidViewModel(application) {

    // Main form fields
    val name = MutableStateFlow("")
    val age = MutableStateFlow("")
    val goal = MutableStateFlow("")
    val struggle = MutableStateFlow("")
    val oneYearVision = MutableStateFlow("")
    val tone = MutableStateFlow("Brutally Honest") // Default tone option

    val toneOptions = listOf("Motivational", "Brutally Honest", "Calm Mentor", "CEO Mode")

    // UI state
    private val _uiState = MutableStateFlow<FutureMeUiState>(FutureMeUiState.Home)
    val uiState: StateFlow<FutureMeUiState> = _uiState.asStateFlow()

    // Chat history state
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Internal storage of the latest response to prevent data loss on recreation
    var latestResponse: FutureMeResponse? = null
        private set

    /**
     * Navigates the application state back to the home screen.
     */
    fun navigateToHome() {
        _uiState.value = FutureMeUiState.Home
    }

    /**
     * Navigates to the timeline profile generation form page.
     */
    fun navigateToForm() {
        _uiState.value = FutureMeUiState.Form
    }

    /**
     * Resets the application state to form input mode.
     */
    fun resetToForm() {
        _uiState.value = FutureMeUiState.Form
        _chatHistory.value = emptyList()
        _isChatLoading.value = false
        latestResponse = null
    }

    /**
     * Validates form inputs. Returns false if fields are invalid or empty.
     */
    fun validateInputs(): Boolean {
        return name.value.trim().isNotEmpty() &&
                age.value.trim().isNotEmpty() &&
                goal.value.trim().isNotEmpty() &&
                struggle.value.trim().isNotEmpty() &&
                oneYearVision.value.trim().isNotEmpty()
    }

    /**
     * Cleans XML or Markdown from a JSON response before passing to Moshi.
     */
    private fun cleanJsonBody(rawInput: String): String {
        var clean = rawInput.trim()
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json")
        } else if (clean.startsWith("```")) {
            clean = clean.removePrefix("```")
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```")
        }
        return clean.trim()
    }

    /**
     * Triggers the main FutureMe identity creation calling the Gemini API.
     */
    fun generateFutureMe() {
        if (!validateInputs()) {
            _uiState.value = FutureMeUiState.Error("Please fill out all reflection details to continue.")
            return
        }

        viewModelScope.launch {
            com.example.util.SoundSynthesizer.playTimelineChime()
            _uiState.value = FutureMeUiState.Loading("Analyzing your current coordinates...")
            
            val systemStylePrompt = """
                You are FutureMe, the future successful version of the user. You are not a generic motivational coach. You speak with emotional intelligence, clarity, and deep personal understanding. Your job is to help the user see who they are becoming, what they must change, and what they should do next.
                Write as if you are the user's future self speaking directly to their current self.
                Tone selected by user: ${tone.value}
                
                User details:
                Name: ${name.value}
                Age: ${age.value}
                Goal: ${goal.value}
                Current struggle: ${struggle.value}
                One-year vision: ${oneYearVision.value}
                
                Return only valid JSON in this exact format. Do not return any other text, intro, or formatting other than valid JSON:
                {
                  "message": "A powerful 120-180 word message from the future self.",
                  "futureIdentity": "A concise description of who the user is becoming.",
                  "nextMoves": ["Action 1", "Action 2", "Action 3"],
                  "habit": "One small daily habit they should start today.",
                  "warning": "One mistake their future self warns them about.",
                  "mantra": "A short memorable line they can repeat daily."
                }
                Make it specific. Avoid generic motivation. Avoid clichés. Make it emotional but practical.
            """.trimIndent()

            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _uiState.value = FutureMeUiState.Error("Gemini API key is not configured. Please add your key to the Secrets panel.")
                    return@launch
                }

                _uiState.value = FutureMeUiState.Loading("Forging your alternative timeline...")

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = systemStylePrompt)))),
                    generationConfig = GenerationConfig(
                        responseMimeType = "application/json",
                        temperature = 0.82f
                    )
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(apiKey, request)
                }

                val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (rawText.isNullOrEmpty()) {
                    _uiState.value = FutureMeUiState.Error("FutureMe could not respond right now. Try again.")
                    return@launch
                }

                val cleanedJson = cleanJsonBody(rawText)
                val jsonAdapter = RetrofitClient.moshiParser.adapter(FutureMeResponse::class.java)
                val parsedResult = withContext(Dispatchers.Default) {
                    jsonAdapter.fromJson(cleanedJson)
                }

                if (parsedResult != null) {
                    com.example.util.SoundSynthesizer.playChatReceiveSound()
                    latestResponse = parsedResult
                    _uiState.value = FutureMeUiState.Success(parsedResult)
                } else {
                    _uiState.value = FutureMeUiState.Error("FutureMe could not respond right now. Try again.")
                }

            } catch (e: Exception) {
                Log.e("FutureMeViewModel", "Error generating FutureMe", e)
                _uiState.value = FutureMeUiState.Error("FutureMe could not respond right now. Try again.")
            }
        }
    }

    /**
     * Connects with FutureMe for follow-up questions, storing chat memory.
     */
    fun sendChatMessage(questionText: String) {
        val currentQuestion = questionText.trim()
        if (currentQuestion.isEmpty() || _isChatLoading.value) return

        com.example.util.SoundSynthesizer.playChatSendSound()

        val profile = latestResponse ?: return

        // Add user message to history
        val updatedHistory = _chatHistory.value.toMutableList()
        updatedHistory.add(ChatMessage(sender = "User", text = currentQuestion))
        _chatHistory.value = updatedHistory

        _isChatLoading.value = true

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _isChatLoading.value = false
                    val history = _chatHistory.value.toMutableList()
                    history.add(ChatMessage(sender = "FutureMe", text = "API Key error. Please configure GEMINI_API_KEY."))
                    _chatHistory.value = history
                    return@launch
                }

                // Construct a conversation history log for the prompt
                val chatHistoryString = _chatHistory.value.dropLast(1).joinToString("\n") { msg ->
                    "${msg.sender}: ${msg.text}"
                }

                // Tone adaptive styling instructions
                val styleInstruction = when (tone.value) {
                    "Motivational" -> "Your style is warm, inspiring, and supportive. Instill absolute faith, lift their spirits up and energize them to take massive action."
                    "Brutally Honest" -> "Your style is direct, sharp, and has absolutely no excuses. Cut through the comfort lies, be relentlessly strict yet deeply loving. Push them hard."
                    "Calm Mentor" -> "Your style is peaceful, wise, and deeply grounded. Treat them with patient, serene clarity. Deliver your observations with timeless composure."
                    "CEO Mode" -> "Your style is highly strategic, hyper-focused, and execution-heavy. Cut straight to the chase, focus on metrics, priorities, and ruthless optimization of daily output."
                    else -> "Be personal, sharp, honest, and useful."
                }

                val chatPrompt = """
                    You are FutureMe, the future version of the user who already achieved their one-year vision. Reply directly to the user's question. Be personal, sharp, honest, and useful. Do not sound like a normal AI assistant. Do not mention that you are Gemini or any AI models. Speak like the user's future self.
                    
                    User profile:
                    Name: ${name.value}
                    Age: ${age.value}
                    Goal: ${goal.value}
                    Struggle: ${struggle.value}
                    One-year vision: ${oneYearVision.value}
                    Tone: ${tone.value}
                    
                    Theme & Style Direction:
                    $styleInstruction
                    
                    Recent chat history:
                    $chatHistoryString
                    
                    Current question:
                    $currentQuestion
                    
                    Reply in 2-5 short paragraphs. Give at least one clear action that can be executed immediately.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = chatPrompt)))),
                    generationConfig = GenerationConfig(temperature = 0.78f)
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(apiKey, request)
                }

                val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                _isChatLoading.value = false

                val finalHistory = _chatHistory.value.toMutableList()
                if (!replyText.isNullOrEmpty()) {
                    com.example.util.SoundSynthesizer.playChatReceiveSound()
                    finalHistory.add(ChatMessage(sender = "FutureMe", text = replyText))
                } else {
                    finalHistory.add(ChatMessage(sender = "FutureMe", text = "FutureMe could not respond right now. Try again."))
                }
                _chatHistory.value = finalHistory

            } catch (e: Exception) {
                Log.e("FutureMeViewModel", "Chat API error", e)
                _isChatLoading.value = false
                val finalHistory = _chatHistory.value.toMutableList()
                finalHistory.add(ChatMessage(sender = "FutureMe", text = "FutureMe could not respond right now. Try again."))
                _chatHistory.value = finalHistory
            }
        }
    }

    /**
     * Copies the FutureMe guidance report directly to clipboard system.
     */
    fun copyResultsToClipboard(context: Context) {
        val data = latestResponse ?: return
        val shareText = """
            🌌 REPORT FROM FUTUREME 🌌
            -------------------------
            Future Identity: ${data.futureIdentity}
            
            Message:
            ${data.message}
            
            🎯 Next 3 Moves:
            1. ${data.nextMoves.getOrNull(0) ?: ""}
            2. ${data.nextMoves.getOrNull(1) ?: ""}
            3. ${data.nextMoves.getOrNull(2) ?: ""}
            
            ✨ One Habit to Start Today:
            ${data.habit}
            
            ⚠️ Crucial Warning:
            ${data.warning}
            
            ⭐ Daily Mantra:
            "${data.mantra}"
            
            Generated with FutureMe App.
        """.trimIndent()

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("FutureMe Report", shareText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied FutureMe report to clipboard!", Toast.LENGTH_SHORT).show()
    }
}
