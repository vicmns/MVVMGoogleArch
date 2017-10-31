package com.lonelystudios.palantir.db

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * Created by vicmns on 10/25/17.
 */
class EnumConverter {
    val gson: Gson = Gson()

    @TypeConverter
    fun sortByEnumsFromString(value: String): List<String> {
        return gson.fromJson<List<String>>(value, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun sortByEnumsAsString(cl: List<String>): String {
        return gson.toJson(cl)
    }

}