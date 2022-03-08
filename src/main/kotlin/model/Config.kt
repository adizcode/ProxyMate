package model

import com.sksamuel.hoplite.ConfigLoader
import java.io.File

data class Config(
    val url: String,
    val credentials: Credentials,
    val lectureDuration: String,
    val entryRelaxation: String,
    val subjects: List<String>,
    val timeTable: Map<String, LinkedHashMap<String, String>>
) {
    companion object {
        fun readConfig(): Config = ConfigLoader().loadConfigOrThrow(File("config.yaml"))
    }
}

data class Credentials(val username: String, val password: String)