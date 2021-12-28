// Credentials
const val USERNAME = "ENTER_YOUR_USERNAME"
const val PASSWORD = "ENTER_YOUR_PASSWORD"

// Subject Names
const val ACN = "ADVANCED COMPUTER NETWORKS"
const val ADBMS = "ADVANCED DATABASE MANAGEMENT SYSTEM"
const val AOS = "ADVANCED OPERATING SYSTEM"
const val DAA = "DESIGN AND ANALYSIS OF ALGORITHMS"
const val DAA_LAB = "DESIGN AND ANALYSIS OF ALGORITHMS LAB"
const val LA_LAB = "LINUX ADMINISTRATION LAB"
const val PLSQL_LAB = "PL/SQL LAB"
const val SPM = "SOFTWARE PROJECT MANAGEMENT"

val MONDAY_TIME_TABLE = linkedMapOf(
    MilitaryTime(9, 40) to PLSQL_LAB,
    MilitaryTime(10, 30) to PLSQL_LAB,
    MilitaryTime(11, 20) to LA_LAB,
    MilitaryTime(12, 10) to LA_LAB,
    MilitaryTime(13, 50) to LA_LAB,
    MilitaryTime(14, 40) to LA_LAB,
)

val TUESDAY_TIME_TABLE = linkedMapOf(
    MilitaryTime(9, 40) to DAA_LAB,
    MilitaryTime(10, 30) to DAA_LAB,
    MilitaryTime(11, 20) to ADBMS,
)

val WEDNESDAY_TIME_TABLE = linkedMapOf(
    MilitaryTime(9, 40) to PLSQL_LAB,
    MilitaryTime(10, 30) to PLSQL_LAB,
    MilitaryTime(11, 20) to DAA_LAB,
    MilitaryTime(12, 10) to DAA_LAB,
    MilitaryTime(13, 50) to AOS,
)

val THURSDAY_TIME_TABLE = linkedMapOf(
    MilitaryTime(9, 40) to ACN,
    MilitaryTime(10, 30) to SPM,
    MilitaryTime(11, 20) to DAA,
    MilitaryTime(13, 0) to ADBMS,
    MilitaryTime(13, 50) to DAA,
    MilitaryTime(14, 45) to ACN,
)

val FRIDAY_TIME_TABLE = linkedMapOf(
    MilitaryTime(9, 40) to AOS,
    MilitaryTime(10, 30) to DAA,
    MilitaryTime(11, 20) to ADBMS,
    MilitaryTime(13, 0) to SPM,
    MilitaryTime(13, 50) to AOS,
    MilitaryTime(14, 45) to ACN,
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
val TODAY_TIME_TABLE = TIME_TABLE[TUE]!!