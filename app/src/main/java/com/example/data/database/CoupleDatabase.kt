package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.model.*

@Database(
    entities = [
        CoupleConfig::class,
        ChatMessage::class,
        SharedPhoto::class,
        CoupleGoal::class,
        SpecialDate::class,
        MemoryPin::class,
        PrivateLetter::class,
        LaKrAiMessage::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CoupleDatabase : RoomDatabase() {
    abstract fun coupleConfigDao(): CoupleConfigDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun sharedPhotoDao(): SharedPhotoDao
    abstract fun coupleGoalDao(): CoupleGoalDao
    abstract fun specialDateDao(): SpecialDateDao
    abstract fun memoryPinDao(): MemoryPinDao
    abstract fun privateLetterDao(): PrivateLetterDao
    abstract fun laKrAiMessageDao(): LaKrAiMessageDao

    companion object {
        @Volatile
        private var INSTANCE: CoupleDatabase? = null

        fun getDatabase(context: Context): CoupleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoupleDatabase::class.java,
                    "lakr_couple_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
