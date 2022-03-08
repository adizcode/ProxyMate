package common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getCurrentTimeString(): String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"))

fun getCurrentDayOfWeek(): String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE"))