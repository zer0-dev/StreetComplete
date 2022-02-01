package de.westnordost.streetcomplete.measure

import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round

/** In which unit the measurement is displayed */
sealed interface MeasureDisplayUnit {
    fun format(distanceMeters: Double): String
}
/** Measurement displayed in meters rounded to the given number of [decimals] */
data class MeasureDisplayUnitMeter(val decimals: Int) : MeasureDisplayUnit {
    init {
        require(decimals >= 0)
    }

    override fun format(distanceMeters: Double): String {
        return "%.${decimals}f m".format(getRounded(distanceMeters))
    }

    /** Returns the given distance in meters rounded to the given precision */
    fun getRounded(distanceMeters: Double): Double {
        val num = 10.0.pow(decimals)
        return round(distanceMeters * num) / num
    }
}
/** Measurement displayed in feet+inch, inches rounded to nearest [inchStep]. Must be 1,2,3,4,6 or 12 */
data class MeasureDisplayUnitFeetInch(val inchStep: Int) : MeasureDisplayUnit {
    init {
        require(inchStep in arrayOf(1,2,3,4,6,12))
    }

    override fun format(distanceMeters: Double): String {
        val (feet, inches) = getRounded(distanceMeters)
        return if (inches < 10) "$feet′ $inches″" else "$feet′$inches″"
    }

    /** Returns the given distance in meters as feet + inch */
    fun getRounded(distanceMeters: Double): Pair<Int, Int> {
        val distanceFeet = distanceMeters / 0.3048
        var feet = floor(distanceFeet).toInt()
        val inches = (distanceFeet - feet) * 12
        var inchesStepped = round(inches / inchStep).toInt() * inchStep
        if (inchesStepped == 12) {
            ++feet
            inchesStepped = 0
        }
        return Pair(feet, inchesStepped)
    }
}
