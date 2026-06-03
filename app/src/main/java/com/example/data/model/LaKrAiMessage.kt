package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "lakr_ai_messages")
data class LaKrAiMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderName: String, // "Kresley", "Larissa", or "LaKr IA"
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isUser: Boolean
) : Serializable
