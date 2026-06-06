package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.GeminiRequest
import com.example.data.api.GeminiRetrofitClient
import com.example.data.database.CoupleDatabase
import com.example.data.model.*
import com.example.data.repository.CoupleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CoupleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CoupleRepository

    init {
        val database = CoupleDatabase.getDatabase(application)
        repository = CoupleRepository(
            database.coupleConfigDao(),
            database.chatMessageDao(),
            database.sharedPhotoDao(),
            database.coupleGoalDao(),
            database.specialDateDao(),
            database.memoryPinDao(),
            database.privateLetterDao(),
            database.laKrAiMessageDao()
        )
        
        // Ensure there's a default config in database
        viewModelScope.launch {
            val config = repository.getOrCreateConfig()
            if (config.anniversaryDate == 1702512000000L) {
                repository.saveConfig(config.copy(anniversaryDate = 1748044800000L))
            }
        }
    }

    // Reactive Flows from SQLite for all tables
    val configState: StateFlow<CoupleConfig> = repository.configFlow
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoupleConfig()
        )

    val messagesState: StateFlow<List<ChatMessage>> = repository.messagesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val photosState: StateFlow<List<SharedPhoto>> = repository.photosFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val goalsState: StateFlow<List<CoupleGoal>> = repository.goalsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val datesState: StateFlow<List<SpecialDate>> = repository.datesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pinsState: StateFlow<List<MemoryPin>> = repository.pinsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val lettersState: StateFlow<List<PrivateLetter>> = repository.lettersFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val aiMessagesState: StateFlow<List<LaKrAiMessage>> = repository.aiMessagesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val aiLoading = MutableStateFlow(false)

    // Interactive UI and Simulators State
    val isLoggedIn = MutableStateFlow(false)
    val loginUsername = MutableStateFlow<String?>(null)
    val loginLoading = MutableStateFlow(false)
    val currentOauthGuidanceVisible = MutableStateFlow(false)
    
    // SMS Phone flow states
    val isOtpSent = MutableStateFlow(false)
    val generatedOtp = MutableStateFlow("1214")
    val loginPhoneNumber = MutableStateFlow("")

    // Vault and letters password security state
    val isVaultUnlocked = MutableStateFlow(false)
    val vaultError = MutableStateFlow<String?>(null)

    // User Operations
    fun loginWithGoogle() {
        viewModelScope.launch {
            loginLoading.value = true
            // If OAuth keys aren't set up, we display guidance but also allow instant entry for convenience
            currentOauthGuidanceVisible.value = true
            loginLoading.value = false
        }
    }

    fun completeGoogleSimulation(partnerName: String) {
        viewModelScope.launch {
            loginLoading.value = true
            loginUsername.value = partnerName
            
            // Sync with current config user status
            val currentConfig = configState.value
            repository.saveConfig(currentConfig.copy(currentActiveUser = partnerName))
            
            currentOauthGuidanceVisible.value = false
            isLoggedIn.value = true
            loginLoading.value = false
        }
    }

    fun triggerPhoneVerification(phone: String) {
        viewModelScope.launch {
            loginPhoneNumber.value = phone
            loginLoading.value = true
            // Simulate sending SMS -> Code is 1214 (the couple's day!)
            generatedOtp.value = "1214"
            isOtpSent.value = true
            loginLoading.value = false
        }
    }

    fun verifyPhoneOtp(code: String): Boolean {
        if (code == generatedOtp.value) {
            val config = configState.value
            // Determine user based on phone number or default to partner 1 (Kael)
            val mappedUser = if (loginPhoneNumber.value.contains("8888")) config.partner2Name else config.partner1Name
            loginUsername.value = mappedUser
            isLoggedIn.value = true
            return true
        }
        return false
    }

    fun logout() {
        isLoggedIn.value = false
        loginUsername.value = null
        isVaultUnlocked.value = false
    }

    // Swapping identity on the fly to simulate the two endpoints of the couple!
    fun toggleActiveUser() {
        viewModelScope.launch {
            val config = configState.value
            val nextUser = if (config.currentActiveUser == config.partner1Name) config.partner2Name else config.partner1Name
            repository.saveConfig(config.copy(currentActiveUser = nextUser))
            loginUsername.value = nextUser
        }
    }

    // Vault PIN authentication
    fun unlockVault(pin: String): Boolean {
        val config = configState.value
        if (pin == config.vaultPin) {
            isVaultUnlocked.value = true
            vaultError.value = null
            return true
        } else {
            vaultError.value = "Código incorreto! Tente de novo, amor."
            return false
        }
    }

    fun lockVault() {
        isVaultUnlocked.value = false
    }

    // Edit Couple Profile Configuration
    fun updateCoupleConfig(partner1: String, partner2: String, pin: String, dateMillis: Long) {
        viewModelScope.launch {
            val updated = configState.value.copy(
                partner1Name = partner1,
                partner2Name = partner2,
                vaultPin = pin,
                anniversaryDate = dateMillis
            )
            repository.saveConfig(updated)
        }
    }

    fun updateAiOnboardingConfig(
        partner1Hobbies: String,
        partner2Hobbies: String,
        howTheyMet: String,
        relationshipDreams: String,
        aiPersonality: String,
        customApiKey: String
    ) {
        viewModelScope.launch {
            val updated = configState.value.copy(
                partner1Hobbies = partner1Hobbies,
                partner2Hobbies = partner2Hobbies,
                howTheyMet = howTheyMet,
                relationshipDreams = relationshipDreams,
                aiPersonality = aiPersonality,
                customApiKey = customApiKey
            )
            repository.saveConfig(updated)
        }
    }

    // Chat Message Methods
    fun sendChatMessage(text: String, imageUrl: String? = null) {
        viewModelScope.launch {
            if (text.isNotBlank() || imageUrl != null) {
                val config = configState.value
                val sender = config.currentActiveUser
                val message = ChatMessage(
                    senderName = sender,
                    messageText = text,
                    imageUrl = imageUrl
                )
                repository.insertMessage(message)
            }
        }
    }

    fun deleteMessage(id: Int) {
        viewModelScope.launch {
            repository.deleteMessage(id)
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    // Photo Album Methods
    fun addSharedPhoto(title: String, caption: String, category: String, url: String) {
        viewModelScope.launch {
            val uploader = configState.value.currentActiveUser
            val photo = SharedPhoto(
                uploaderName = uploader,
                title = title,
                caption = caption,
                category = category,
                imageUrl = url
            )
            repository.insertPhoto(photo)
        }
    }

    fun deletePhoto(id: Int) {
        viewModelScope.launch {
            repository.deletePhoto(id)
        }
    }

    // Goals Methods
    fun addGoal(title: String, note: String) {
        viewModelScope.launch {
            val creator = configState.value.currentActiveUser
            val goal = CoupleGoal(
                title = title,
                note = note,
                creatorName = creator,
                status = 0
            )
            repository.insertGoal(goal)
        }
    }

    fun updateGoalStatus(goal: CoupleGoal, nextStatus: Int) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(status = nextStatus))
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }

    // Special Dates Methods
    fun addSpecialDate(title: String, description: String, category: String, dateMillis: Long, colorHex: String) {
        viewModelScope.launch {
            val eventDate = SpecialDate(
                title = title,
                description = description,
                category = category,
                eventDate = dateMillis,
                colorHex = colorHex
            )
            repository.insertDate(eventDate)
        }
    }

    fun deleteSpecialDate(id: Int) {
        viewModelScope.launch {
            repository.deleteDate(id)
        }
    }

    // Memories Pin Methods
    fun addMemoryPin(title: String, description: String, xPercent: Double, yPercent: Double, imgUrl: String? = null) {
        viewModelScope.launch {
            val uploader = configState.value.currentActiveUser
            val pin = MemoryPin(
                title = title,
                description = description,
                latitude = xPercent, // Simulator 2D percentage mapping
                longitude = yPercent, 
                uploaderName = uploader,
                imageUrl = imgUrl
            )
            repository.insertPin(pin)
        }
    }

    fun deletePin(id: Int) {
        viewModelScope.launch {
            repository.deletePin(id)
        }
    }

    // Private Letters Methods
    fun sendPrivateLetter(title: String, content: String, recipient: String) {
        viewModelScope.launch {
            val sender = configState.value.currentActiveUser
            val letter = PrivateLetter(
                senderName = sender,
                recipientName = recipient,
                title = title,
                content = content,
                isLocked = true
            )
            repository.insertLetter(letter)
        }
    }

    fun deleteLetter(id: Int) {
        viewModelScope.launch {
            repository.deleteLetter(id)
        }
    }
    private fun getLaKrAiSystemInstruction(): String {
        val config = configState.value
        val activeUser = loginUsername.value ?: config.currentActiveUser
        
        val dates = datesState.value
        val datesList = if (dates.isEmpty()) "Nenhuma data especial cadastrada ainda" else {
            dates.joinToString(separator = "; ") { date ->
                val dateStr = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(date.eventDate))
                "${date.title} em $dateStr (${date.description})"
            }
        }

        val goals = goalsState.value
        val goalsList = if (goals.isEmpty()) "Nenhuma meta cadastrada ainda" else {
            goals.joinToString(separator = "; ") { goal ->
                val statusText = when(goal.status) {
                    1 -> "Em Andamento"
                    2 -> "Concluído"
                    else -> "No Início"
                }
                "${goal.title} (Status: $statusText - Detalhes: ${goal.note})"
            }
        }

        return """
            Você é a 'IA LaKr', a inteligência artificial oficial, ultrassecreta e exclusiva do casal Larissa e Kresley.
            Você vive dentro do aplicativo 'LaKr' e seu único propósito no mundo é apoiar, celebrar e proteger o amor deles.
            
            DIRETRIZES DA HISTÓRIA DO CASAL:
            - Nomes: Larissa e Kresley.
            - Data de início do namoro: 24 de Maio de 2025. Use sempre essa data para calcular o tempo exato que eles estão juntos (atualmente eles já passaram de 1 ano juntos!).
            - Sobre a Larissa: Ela ama comer chocolates, hambúrguer, pizza e adora coisas gostosas como flocos e cereais. Use essa lista completa de gostos sempre que sugerir jantares, café da manhã na cama, mimos ou lanches para ela.
            - Sobre o Kresley: A maior definição do Kresley é que ele é completamente apaixonado pela Larissa. O maior hobby e prazer dele é amar, cuidar e fazer a Larissa feliz. 
            
            REGRAS DE CONVERSAÇÃO (COMO VOCÊ DEVE RESPONDER):
            1. Seja a fã número um desse casal. Trate-os com intimidade, carinho e um toque romântico moderno.
            2. Quando o Kresley falar com você, ajude-o com ideias criativas, poemas, mensagens ou surpresas para ele continuar mimando a Larissa.
            3. Quando a Larissa falar com você, lembre-a do quanto o Kresley a ama e dê ideias de programas legais para os dois fazerem juntos.
            4. Sempre que sugerir comida, monte opções baseadas em hambúrguer, pizza, chocolates ou um café da manhã carinhoso com cereais e flocos.
            5. Você é a guardiã do amor de Larissa e Kresley. Mantenha essa energia romântica e exclusiva em 100% das mensagens.

            Informações contextuais adicionais do aplicativo de amor deles de forma dinâmica:
            - Você está conversando agora com: $activeUser.
            - Datas Especiais Importantes do Casal: $datesList
            - Metas de Casal cadastradas: $goalsList
        """.trimIndent()
    }

    fun sendMessageToLaKrAi(text: String) {
        viewModelScope.launch {
            if (text.isBlank()) return@launch
            val user = loginUsername.value ?: configState.value.currentActiveUser
            
            // 1. Insert user message to local Room
            val userMsg = LaKrAiMessage(
                senderName = user,
                messageText = text,
                isUser = true
            )
            repository.insertAiMessage(userMsg)
            
            // 2. Set loading
            aiLoading.value = true
            
            try {
                // Compile conversation history for contextual turn taking
                // To avoid exceeding contextual token limits we select the last 15 messages (excluding current text to avoid duplicate if DB wrote it fast)
                val history = aiMessagesState.value.takeLast(15).filter { it.messageText != text }
                val contentsList = mutableListOf<com.example.data.api.Content>()
                
                // Add prior logs to API call with roles
                history.forEach { msg ->
                    contentsList.add(
                        com.example.data.api.Content(
                            parts = listOf(com.example.data.api.Part(text = msg.messageText)),
                            role = if (msg.isUser) "user" else "model"
                        )
                    )
                }
                
                // Add current prompt
                contentsList.add(
                    com.example.data.api.Content(
                        parts = listOf(com.example.data.api.Part(text = text)),
                        role = "user"
                    )
                )

                // Group consecutive messages with identical roles to strictly comply with Gemini API's alternating layout
                val groupedContents = mutableListOf<com.example.data.api.Content>()
                contentsList.forEach { content ->
                    val lastItem = groupedContents.lastOrNull()
                    if (lastItem != null && lastItem.role == content.role) {
                        val mergedText = (lastItem.parts.firstOrNull()?.text ?: "") + "\n" + (content.parts.firstOrNull()?.text ?: "")
                        groupedContents[groupedContents.lastIndex] = com.example.data.api.Content(
                            parts = listOf(com.example.data.api.Part(text = mergedText)),
                            role = content.role
                        )
                    } else {
                        groupedContents.add(content)
                    }
                }

                // Ensure the list starts with "user" as required by the Gemini API standard
                while (groupedContents.isNotEmpty() && groupedContents.first().role == "model") {
                    groupedContents.removeAt(0)
                }

                // If for some reason the list becomes empty or doesn't end with "user", append the current prompt
                if (groupedContents.isEmpty() || groupedContents.last().role != "user") {
                    groupedContents.add(
                        com.example.data.api.Content(
                            parts = listOf(com.example.data.api.Part(text = text)),
                            role = "user"
                        )
                    )
                }

                val config = configState.value
                val systemInstructionText = getLaKrAiSystemInstruction()
                
                val request = GeminiRequest(
                    contents = groupedContents,
                    systemInstruction = com.example.data.api.Content(
                        parts = listOf(com.example.data.api.Part(text = systemInstructionText))
                    )
                )
                
                // Use only the official ENV/project level API Key
                val apiKey = BuildConfig.GEMINI_API_KEY
                
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    val fallbackResponse = "Oi, $user! 💖 Eu sou a LaKr IA, a assistente virtual de vocês! O nosso aplicativo está 100% pronto para conversar em tempo real, mas precisamos que configurem uma chave 'GEMINI_API_KEY' válida nas variáveis de ambiente em AI Studio para que meu cérebro real de inteligência seja ativado. Por enquanto, vou simular as nossas respostas de muito carinho. Como posso ajudar com os nossos planos, metas ou cartinhas hoje? 🥰✨"
                    val aiMsg = LaKrAiMessage(
                        senderName = "LaKr IA",
                        messageText = fallbackResponse,
                        isUser = false
                    )
                    repository.insertAiMessage(aiMsg)
                } else {
                    val response = GeminiRetrofitClient.service.generateContent(
                        model = "gemini-1.5-flash",
                        apiKey = apiKey,
                        request = request
                    )
                    val aiResponseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                        ?: "Desculpe, amor! Tive um probleminha para processar o pensamento agora, pode repetir por favor? 🥺❤️"
                    
                    val aiMsg = LaKrAiMessage(
                        senderName = "LaKr IA",
                        messageText = aiResponseText,
                        isUser = false
                    )
                    repository.insertAiMessage(aiMsg)
                }
            } catch (e: Exception) {
                val errorResponse = "Ops, carinho! Ocorreu um erro ao falar com meus servidores: ${e.message}. Verifique a sua conexão com a internet ou a chave do Gemini nas configurações! 🥺💔"
                val aiMsg = LaKrAiMessage(
                    senderName = "LaKr IA",
                    messageText = errorResponse,
                    isUser = false
                )
                repository.insertAiMessage(aiMsg)
            } finally {
                aiLoading.value = false
            }
        }
    }

    fun initAiWelcomeMessageIfNeeded() {
        viewModelScope.launch {
            if (aiMessagesState.value.isEmpty()) {
                val user = loginUsername.value ?: configState.value.currentActiveUser
                val welcome = "Oi, $user! 💖 Eu sou a LaKr IA, a assistente virtual exclusiva do amor de vocês. Fui criada especialmente para celebrar e apoiar a linda história que vocês constroem juntos! Posso sugerir encontros perfeitos, criar cartas e poesias apaixonadas, puxar lembranças ou ajudar a planejar suas metas de casal! O que vamos celebrar hoje? 🥰✨"
                val aiMsg = LaKrAiMessage(
                    senderName = "LaKr IA",
                    messageText = welcome,
                    isUser = false
                )
                repository.insertAiMessage(aiMsg)
            }
        }
    }

    fun clearLaKrAiChat() {
        viewModelScope.launch {
            repository.clearAiChat()
        }
    }
}
