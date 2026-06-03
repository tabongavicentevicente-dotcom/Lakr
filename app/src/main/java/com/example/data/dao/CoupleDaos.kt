package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoupleConfigDao {
    @Query("SELECT * FROM couple_config WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<CoupleConfig?>

    @Query("SELECT * FROM couple_config WHERE id = 1 LIMIT 1")
    suspend fun getConfigDirect(): CoupleConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: CoupleConfig)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessage(id: Int)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()
}

@Dao
interface SharedPhotoDao {
    @Query("SELECT * FROM shared_photos ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<SharedPhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: SharedPhoto)

    @Query("DELETE FROM shared_photos WHERE id = :id")
    suspend fun deletePhoto(id: Int)
}

@Dao
interface CoupleGoalDao {
    @Query("SELECT * FROM couple_goals ORDER BY timestamp DESC")
    fun getAllGoals(): Flow<List<CoupleGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: CoupleGoal)

    @Update
    suspend fun updateGoal(goal: CoupleGoal)

    @Query("DELETE FROM couple_goals WHERE id = :id")
    suspend fun deleteGoalById(id: Int)
}

@Dao
interface SpecialDateDao {
    @Query("SELECT * FROM special_dates ORDER BY eventDate ASC")
    fun getAllDates(): Flow<List<SpecialDate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDate(date: SpecialDate)

    @Query("DELETE FROM special_dates WHERE id = :id")
    suspend fun deleteDateById(id: Int)
}

@Dao
interface MemoryPinDao {
    @Query("SELECT * FROM memory_pins ORDER BY timestamp DESC")
    fun getAllPins(): Flow<List<MemoryPin>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPin(pin: MemoryPin)

    @Query("DELETE FROM memory_pins WHERE id = :id")
    suspend fun deletePinById(id: Int)
}

@Dao
interface PrivateLetterDao {
    @Query("SELECT * FROM private_letters ORDER BY timestamp DESC")
    fun getAllLetters(): Flow<List<PrivateLetter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: PrivateLetter)

    @Query("DELETE FROM private_letters WHERE id = :id")
    suspend fun deleteLetterById(id: Int)
}

@Dao
interface LaKrAiMessageDao {
    @Query("SELECT * FROM lakr_ai_messages ORDER BY timestamp ASC")
    fun getAllAiMessages(): Flow<List<LaKrAiMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiMessage(msg: LaKrAiMessage)

    @Query("DELETE FROM lakr_ai_messages WHERE id = :id")
    suspend fun deleteAiMessage(id: Int)

    @Query("DELETE FROM lakr_ai_messages")
    suspend fun clearAiChat()
}

