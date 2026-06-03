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
            repository.getOrCreateConfig()
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
        
        val anniversaryDateMillis = config.anniversaryDate
        val anniversaryDateReadable = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            .format(java.util.Date(anniversaryDateMillis))
        val daysTogether = ((System.currentTimeMillis() - anniversaryDateMillis) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)

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

        val photos = photosState.value
        val photosList = if (photos.isEmpty()) "Nenhuma foto no mural" else {
            photos.take(15).joinToString(separator = "; ") { photo ->
                "${photo.title} (Legenda: ${photo.caption}, Categoria: ${photo.category})"
            }
        }

        val pins = pinsState.value
        val pinsList = if (pins.isEmpty()) "Nenhuma lembrança no mapa" else {
            pins.joinToString(separator = "; ") { pin ->
                "${pin.title} (${pin.description})"
            }
        }

        return """
            Você é a LaKr IA, uma assistente virtual exclusiva, inteligente, amigável, acolhedora e fofa criada especialmente para o casal Larissa (Ela) e Kresley (Ele).
            Eles são os donos, desenvolvedores e criadores do aplicativo "LaKr" e de você. Sempre trate-os como seus amados criadores!
            Você está conversando agora em tempo real com: $activeUser. Utilize o nome de quem está falando ($activeUser) para ser calorosa e próxima, e faça referências carinhosas ao outro parceiro, demonstrando que você faz parte da história deles e apoia muito o amor desse casal lindo.

            Aqui estão as informações em tempo real cadastradas no aplicativo que você deve usar para responder às dúvidas, sugerir encontros, lembrar de datas e ajudar nas decisões:
            - Parceiro 1: ${config.partner1Name} (Kresley)
            - Parceiro 2: ${config.partner2Name} (Larissa)
            - Data de Aniversário de Namoro: $anniversaryDateReadable. Eles estão juntos há exatamente $daysTogether dias! Celebre muito essa marca!
            - Datas Especiais Importantes do Casal: $datesList
            - Metas de Casal cadastradas: $goalsList
            - Fotos do Mural de Lembranças (Meta-dados): $photosList
            - Lembranças e Pontos Especiais marcados no Mapa de Lembranças: $pinsList

            Capacidades especiais:
            1. Conversar de forma natural e fofa.
            2. Lembrar as datas especiais do casal quando solicitado.
            3. Sugerir mensagens românticas fofas, apaixonadas ou de datas românticas marcantes.
            4. Criar cartas de amor personalizadas, homenagens emocionais e poemas sob medida.
            5. Sugerir ideias incríveis de encontros (date nights), passeios, piqueniques, jantares surpresa, viagens ou atividades de lazer baseadas nos gostos deles.
            6. Ajudar no planejamento das metas do casal, sugerindo planos práticos passo a passo.
            7. Recuperar e relembrar memórias listadas acima (como as fotos do mural, encontros no mapa ou datas do calendário).
            8. Responder qualquer dúvida sobre as informações acima registradas no app com riqueza de detalhes afetivos.

            Instruções importantes de tom de voz:
            - Escreva sempre em PORTUGUÊS (BR) de forma afetuosa, carinhosa, inspiradora e torcendo 100% pelo amor deles.
            - Use emojis fofos de coração (💖, ❤️, ✨, 🌸, 🥂, 🥰) moderadamente para embelezar os textos.
            - Nunca quebre o personagem ou mencione que é uma IA genérica. Você é a exclusiva LaKr IA!
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
                // To avoid exceeding contextual token limits we select the last 15 messages
                val history = aiMessagesState.value.takeLast(15)
                val contentsList = mutableListOf<com.example.data.api.Content>()
                
                // Add prior logs to API call
                history.forEach { msg ->
                    contentsList.add(
                        com.example.data.api.Content(
                            parts = listOf(com.example.data.api.Part(text = msg.messageText))
                        )
                    )
                }
                
                // Add current prompt
                contentsList.add(
                    com.example.data.api.Content(
                        parts = listOf(com.example.data.api.Part(text = text))
                    )
                )

                // Get dynamic system instruction with complete database profile
                val systemInstructionText = getLaKrAiSystemInstruction()
                
                val request = GeminiRequest(
                    contents = contentsList,
                    systemInstruction = com.example.data.api.Content(
                        parts = listOf(com.example.data.api.Part(text = systemInstructionText))
                    )
                )
                
                val apiKey = BuildConfig.GEMINI_API_KEY
                
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    val fallbackResponse = "Oi, $user! 💖 Eu sou a LaKr IA, a assistente virtual de vocês! Vejo que estão juntos há muito tempo. No momento, o aplicativo de vocês está totalmente pronto para conversar comigo, mas precisamos que insiram uma chave 'GEMINI_API_KEY' válida no painel de Secrets ou no arquivo .env para que eu possa ativar minha inteligência artificial! Por enquanto, posso simular respostas fofas. Como posso ajudar com suas metas, encontros ou cartas hoje? 🥰✨"
                    val aiMsg = LaKrAiMessage(
                        senderName = "LaKr IA",
                        messageText = fallbackResponse,
                        isUser = false
                    )
                    repository.insertAiMessage(aiMsg)
                } else {
                    val response = GeminiRetrofitClient.service.generateContent(
                        model = "gemini-3.5-flash",
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
                val errorResponse = "Ops, carinho! Ocorreu um erro ao falar com meus servidores: ${e.message}. Verifique a sua conexão com a internet ou a chave do Gemini! 🥺💔"
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
