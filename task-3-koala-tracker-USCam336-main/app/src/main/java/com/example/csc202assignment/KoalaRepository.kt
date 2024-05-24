package com.example.csc202assignment

import android.content.Context
import androidx.room.Room
import com.example.csc202assignment.koalaDatabase.KoalaDatabase
import com.example.csc202assignment.koalaDatabase.migration_1_2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*


/*A repository class encapsulates the logic for accessing data from a single source or a set of sources.
 It determines how to fetch and store a particular set of data,
 whether locally in a database or from a remote server. */

private const val DATABASE_NAME = "koala-database"

class KoalaRepository private  constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
){

    /*Room.databaseBuilder() creates a concrete implementation of
    abstract CrimeDatabase using three parameters.*/
    private val database: KoalaDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            KoalaDatabase::class.java,
            DATABASE_NAME
        )
        .addMigrations(
            migration_1_2
        )
        .build()

    fun  getKoalas(): Flow<List<Koala>> = database.koalaDao().getKoalas()

    suspend fun getKoala(id: UUID): Koala = database.koalaDao().getKoala(id)

    fun updateKoala(koala: Koala) {
            coroutineScope.launch {
            database.koalaDao().updateKoala(koala)
        }
    }

    suspend fun addKoala(koala: Koala) {
        database.koalaDao().addKoala(koala)
    }

    suspend fun deleteKoala(koala: Koala){
        database.koalaDao().deleteKoala(koala)
    }

    companion object {
        private var INSTANCE: KoalaRepository? = null

        fun initialize (context: Context) {
            if (INSTANCE==null){
                INSTANCE = KoalaRepository(context)
            }
        }

        fun get(): KoalaRepository{
            return INSTANCE?:
            throw IllegalStateException("KoalaRepository must be initialized")
        }
    }
}