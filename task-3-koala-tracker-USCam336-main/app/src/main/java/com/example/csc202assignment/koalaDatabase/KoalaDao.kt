package com.example.csc202assignment.koalaDatabase

import androidx.room.*
import com.example.csc202assignment.Koala
import kotlinx.coroutines.flow.Flow
import java.util.*

//Data Access object is an interface that contains functions for each database operation you want to perform

@Dao
interface KoalaDao {
    //The @Query annotation expects a string containing a SQL command as input.
    @Query("SELECT * FROM koala")
    fun getKoalas(): Flow<List<Koala>>

    @Query("SELECT * FROM koala WHERE id=(:id)")
    suspend fun getKoala(id: UUID): Koala

    @Update
    suspend fun updateKoala(koala: Koala)

    @Insert
    suspend fun addKoala(koala: Koala)

    @Delete
    suspend fun deleteKoala(koala: Koala)

}