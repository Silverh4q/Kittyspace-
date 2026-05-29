package com.example.data.repository

import com.example.data.local.VirtualAppDao
import com.example.data.local.DumpHistoryDao
import com.example.data.model.VirtualApp
import com.example.data.model.DumpHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class KittyRepository(
    private val virtualAppDao: VirtualAppDao,
    private val dumpHistoryDao: DumpHistoryDao
) {
    val allApps: Flow<List<VirtualApp>> = virtualAppDao.getAllApps()
        .onStart {
            prepopulateAppsIfEmpty()
        }

    val allDumps: Flow<List<DumpHistory>> = dumpHistoryDao.getAllDumps()

    suspend fun insertApp(app: VirtualApp) {
        virtualAppDao.insertApp(app)
    }

    suspend fun deleteApp(app: VirtualApp) {
        virtualAppDao.deleteApp(app)
    }

    suspend fun insertDump(dump: DumpHistory) {
        dumpHistoryDao.insertDump(dump)
    }

    suspend fun deleteDump(dump: DumpHistory) {
        dumpHistoryDao.deleteDump(dump)
    }

    private suspend fun prepopulateAppsIfEmpty() {
        // App workspace begins empty. User adds packages manually from installed applications list.
    }
}
