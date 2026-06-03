package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "couple_config")
data class CoupleConfig(
    @PrimaryKey val id: Int = 1,
    val partner1Name: String = "Kresley",
    val partner2Name: String = "Larissa",
    val anniversaryDate: Long = 1702512000000L, // Default: December 14, 2023
    val vaultPin: String = "1214",
    val currentActiveUser: String = "Kresley",
    val partner1Phone: String = "+55 (11) 99999-1214",
    val partner2Phone: String = "+55 (11) 98888-1214",
    val partner1Email: String = "kresley@lakr.love",
    val partner2Email: String = "larissa@lakr.love"
) : Serializable

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String? = null
) : Serializable

@Entity(tableName = "shared_photos")
data class SharedPhoto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uploaderName: String,
    val title: String,
    val caption: String,
    val category: String, // e.g., "Viagem", "Festa", "Romântico", "Dia-a-dia"
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "couple_goals")
data class CoupleGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val note: String,
    val creatorName: String,
    val status: Int = 0, // 0 = No Início, 1 = Em Andamento, 2 = Concluída!
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "special_dates")
data class SpecialDate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val eventDate: Long, // timestamp milis
    val description: String,
    val category: String = "Aniversário", // e.g., "Aniversário", "Viagem", "Outro"
    val colorHex: String = "#8A3FFC"
) : Serializable

@Entity(tableName = "memory_pins")
data class MemoryPin(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val latitude: Double, // local 2D X simulator percent for easy custom drawing
    val longitude: Double, // local 2D Y simulator percent
    val uploaderName: String,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "private_letters")
data class PrivateLetter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderName: String,
    val recipientName: String,
    val title: String,
    val content: String,
    val isLocked: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
