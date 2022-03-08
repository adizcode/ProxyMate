package model

import common.getCurrentDayOfWeek

data class ProcessedConfig(
    val url: String,
    val username: String,
    val password: String,
    val lectureDuration: MilitaryTime,
    val entryRelaxation: MilitaryTime,
    val currentTimeTable: LinkedHashMap<MilitaryTime, String>
) {
    constructor(config: Config) : this(url = config.url,
        username = config.credentials.username,
        password = config.credentials.password,
        lectureDuration = MilitaryTime(config.lectureDuration),
        entryRelaxation = MilitaryTime(config.entryRelaxation),
        currentTimeTable = config.timeTable[getCurrentDayOfWeek()]?.mapKeys { MilitaryTime(it.key) } as? LinkedHashMap
            ?: LinkedHashMap())

    companion object {
        fun readAndProcessConfig(): ProcessedConfig {
            val config = Config.readConfig()
            return ProcessedConfig(config)
        }
    }
}
