package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "virtual_apps")
data class VirtualApp(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val packageName: String,
    val iconName: String,
    val category: String,
    val isUserAdded: Boolean,
    val version: String = "1.0.0",
    val engineType: String = "Unity" // "Unity", "Unreal", "Native"
)
