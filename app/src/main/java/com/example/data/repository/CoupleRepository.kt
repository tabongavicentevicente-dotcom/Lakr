package com.example.data.repository

import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class CoupleRepository(
    private val configDao: CoupleConfigDao,
    private val chatDao: ChatMessageDao,
    private val photoDao: SharedPhotoDao,
    private val goalDao: CoupleGoalDao,
    private val dateDao: SpecialDateDao,
    private val pinDao: MemoryPinDao,
    private val letterDao: PrivateLetterDao,
    private val laKrAiDao: LaKrAiMessageDao
) {
    // Configuration
    val configFlow: Flow<CoupleConfig?> = configDao.getConfig()

    suspend fun getOrCreateConfig(): CoupleConfig {
        val existing = configDao.getConfigDirect()
        if (existing != null) {
            return existing
        }
        val default = CoupleConfig()
        configDao.saveConfig(default)
        return default
    }

    suspend fun saveConfig(config: CoupleConfig) {
        configDao.saveConfig(config)
    }

    // Chat Messages
    val messagesFlow: Flow<List<ChatMessage>> = chatDao.getAllMessages()

    suspend fun insertMessage(msg: ChatMessage) {
        chatDao.insertMessage(msg)
    }

    suspend fun deleteMessage(id: Int) {
        chatDao.deleteMessage(id)
    }

    suspend fun clearChat() {
        chatDao.clearChat()
    }

    // Photo Album
    val photosFlow: Flow<List<SharedPhoto>> = photoDao.getAllPhotos()

    suspend fun insertPhoto(photo: SharedPhoto) {
        photoDao.insertPhoto(photo)
    }

    suspend fun deletePhoto(id: Int) {
        photoDao.deletePhoto(id)
    }

    // Couple Goals
    val goalsFlow: Flow<List<CoupleGoal>> = goalDao.getAllGoals()

    suspend fun insertGoal(goal: CoupleGoal) {
        goalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: CoupleGoal) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(id: Int) {
        goalDao.deleteGoalById(id)
    }

    // Special Dates
    val datesFlow: Flow<List<SpecialDate>> = dateDao.getAllDates()

    suspend fun insertDate(date: SpecialDate) {
        dateDao.insertDate(date)
    }

    suspend fun deleteDate(id: Int) {
        dateDao.deleteDateById(id)
    }

    // Memory Pins
    val pinsFlow: Flow<List<MemoryPin>> = pinDao.getAllPins()

    suspend fun insertPin(pin: MemoryPin) {
        pinDao.insertPin(pin)
    }

    suspend fun deletePin(id: Int) {
        pinDao.deletePinById(id)
    }

    // Private Letters
    val lettersFlow: Flow<List<PrivateLetter>> = letterDao.getAllLetters()

    suspend fun insertLetter(letter: PrivateLetter) {
        letterDao.insertLetter(letter)
    }

    suspend fun deleteLetter(id: Int) {
        letterDao.deleteLetterById(id)
    }

    // LaKr AI Messages
    val aiMessagesFlow: Flow<List<LaKrAiMessage>> = laKrAiDao.getAllAiMessages()

    suspend fun insertAiMessage(msg: LaKrAiMessage) {
        laKrAiDao.insertAiMessage(msg)
    }

    suspend fun deleteAiMessage(id: Int) {
        laKrAiDao.deleteAiMessage(id)
    }

    suspend fun clearAiChat() {
        laKrAiDao.clearAiChat()
    }
}
