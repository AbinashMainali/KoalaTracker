package com.example.csc202assignment.koalaDatabase

import androidx.room.TypeConverter
import java.util.*

//Type converters tell Room how to convert a specific type to format it needs to store in the database.
class KoalaTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }


    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }
}