package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DumpHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface DumpHistoryDao {
    @Query("SELECT * FROM dump_history ORDER BY timestamp DESC")
    fun getAllDumps(): Flow<List<DumpHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDump(dump: DumpHistory)

    @Delete
    suspend fun deleteDump(dump: DumpHistory)
}
