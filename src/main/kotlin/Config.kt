// Credentials
const val USERNAME = "ENTER_YOUR_USERNAME"
const val PASSWORD = "ENTER_YOUR_PASSWORD"

// Subject Names
const val PP = "Python Programming"
const val CA = "Computing Aptitude"
const val APT = "Aptitude - 1"
const val SS = "Soft Skills -1"
const val AIP = "Advanced Internet Programming"
const val AIP_LAB = "Advanced Internet Programming Lab"

val MONDAY_TIME_TABLE = linkedMapOf(
    MilitaryTime(9, 40) to CA,
    MilitaryTime(10, 30) to CA,
    MilitaryTime(11, 20) to PP,
    MilitaryTime(12, 10) to PP,
)

val TUESDAY_TIME_TABLE = linkedMapOf(
    MilitaryTime(9, 40) to SS,
    MilitaryTime(10, 30) to SS,
    MilitaryTime(11, 20) to APT,
    MilitaryTime(12, 10) to APT,
    MilitaryTime(14, 0) to AIP,
    MilitaryTime(14, 50) to PP,
)

val WEDNESDAY_TIME_TABLE = linkedMapOf(
    MilitaryTime(9, 40) to SS,
    MilitaryTime(10, 30) to SS,
    MilitaryTime(11, 20) to AIP,
    MilitaryTime(13, 10) to PP,
)

val THURSDAY_TIME_TABLE = linkedMapOf(
    MilitaryTime(9, 40) to AIP_LAB,
    MilitaryTime(10, 30) to AIP_LAB,
    MilitaryTime(11, 20) to AIP,
    MilitaryTime(13, 10) to PP,
    MilitaryTime(14, 0) to PP
)

val FRIDAY_TIME_TABLE = linkedMapOf(
    MilitaryTime(9, 40) to AIP_LAB,
    MilitaryTime(10, 30) to AIP_LAB,
    MilitaryTime(11, 20) to CA,
    MilitaryTime(12, 10) to CA,
    MilitaryTime(14, 0) to APT,
    MilitaryTime(14, 50) to APT,
)

// Time Table
val TIME_TABLE = mapOf(
    MON to MONDAY_TIME_TABLE,
    TUE to TUESDAY_TIME_TABLE,
    WED to WEDNESDAY_TIME_TABLE,
    THU to THURSDAY_TIME_TABLE,
    FRI to FRIDAY_TIME_TABLE,
)

// TODO: Update the index before running the program
val TODAY_TIME_TABLE = TIME_TABLE[WED]!!

val ENTRY_RELAXATION = MilitaryTime(0, 10)

val CLASS_DURATION = MilitaryTime(0, 40)