package dk.aau.sw711e20.io.swagger.client

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.io.File
import kotlin.reflect.typeOf

class FileAdapter {

    @ToJson
    fun toJson(data: File): String = String(data.readBytes())

    @FromJson
    fun fromJson(data: String): File  {
        return File("PLSWORK");

    }

}
