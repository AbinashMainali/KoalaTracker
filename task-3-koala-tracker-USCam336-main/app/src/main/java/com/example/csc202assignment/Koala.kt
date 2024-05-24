package com.example.csc202assignment

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//Here data class com.example.csc202assignment.Koala act as a model layer
//model layer purpose is to hold and manage data and have no knowledge of UI
@Entity
data class Koala(
    @PrimaryKey val id: UUID,
    val title: String,
    val place: String,
    val date: Date,
    val photoFileName: String? = null,
    var lat: String,
    var lon: String




)

