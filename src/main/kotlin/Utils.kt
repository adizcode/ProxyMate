import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getCurrentTime(): String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"))