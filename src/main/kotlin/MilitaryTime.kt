data class MilitaryTime(val hours: Int, val minutes: Int) {

    init {
        assert(hours in 0..23 && minutes in 0..59)
    }

    constructor(timeString: String) : this(timeString.substring(0, 2).toInt(), timeString.substring(2).toInt())

    val time: String
        get() =
            hours.toString().padStart(2, '0') +
                    minutes.toString().padStart(2, '0')

    // Add two MilitaryTime instances
    fun add(timeToAdd: MilitaryTime): MilitaryTime {

        val totalMinutes = minutes + timeToAdd.minutes
        val carry = if (totalMinutes >= 60) 1 else 0
        val newMinutes = if (carry == 1) totalMinutes - 60 else totalMinutes

        val totalHours = hours + timeToAdd.hours + carry
        val newHours = if (totalHours >= 24) totalHours - 24 else totalHours

        return MilitaryTime(newHours, newMinutes)
    }

    // Subtract a MilitaryTime instance from the current instance
    fun subtract(timeToSubtract: MilitaryTime): MilitaryTime {
        assert(hours > timeToSubtract.hours || (hours == timeToSubtract.hours && minutes > timeToSubtract.minutes))

        val differenceMinutes = minutes - timeToSubtract.minutes

        val negCarry = if (differenceMinutes < 0) 1 else 0
        val newMinutes = if (negCarry == 1) 60 + differenceMinutes else differenceMinutes

        val newHours = hours - timeToSubtract.hours - negCarry

        return MilitaryTime(newHours, newMinutes)
    }
}