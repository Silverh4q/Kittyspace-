package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.VirtualApp
import kotlinx.coroutines.flow.Flow

@Dao
interface VirtualAppDao {
    @Query("SELECT * FROM virtual_apps ORDER BY isUserAdded ASC, id ASC")
    fun getAllApps(): Flow<List<VirtualApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: VirtualApp)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<VirtualApp>)

    @Delete
    suspend fun deleteApp(app: VirtualApp)

    @Query("SELECT COUNT(*) FROM virtual_apps")
    suspend fun getCount(): Int
}
