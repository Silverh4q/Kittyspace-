package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dump_history")
data class DumpHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val packageName: String,
    val engineType: String, // "Unity" or "Unreal"
    val timestamp: Long = System.currentTimeMillis(),
    val status: String, // "SUCCESS" or "FAILED"
    val scannedSoFiles: String,
    val dumpPath: String,
    val dumpSummary: String // e.g. "Classes: 4122 | Methods: 24701 | Offsets: 18402"
)
