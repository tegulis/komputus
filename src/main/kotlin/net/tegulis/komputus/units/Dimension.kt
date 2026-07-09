package net.tegulis.komputus.units

import net.tegulis.komputus.amount.Amount

// TODO:
//  Time: second, minute, hour, day
//  Length: meter, kilometer, inch, foot, mile
//  Area: m², hectare, acre
//  Volume: m³, liter, milliliter, gallon
//  Angle: radian, degree, arcminute, arcsecond
//  Mass: gram, kilogram, tonne, pound
//  Electric charge: coulomb, ampere-hour
//  Energy: joule, calorie, electronvolt, watt-hour
//  Power: watt, horsepower
//  Pressure: pascal, bar, atmosphere, mmHg
//  Frequency: hertz, rpm
//  Speed: m/s, km/h, mph
//  Acceleration: m/s², gal
//  Force: newton, dyne, pound-force
//  Torque: N·m, lbf·ft
//  Electric potential: volt, statvolt
//  Electric current: ampere, milliampere
//  Capacitance: farad, microfarad
//  Resistance: ohm, kiloohm
//  Conductance: siemens, mho
//  Magnetic flux: weber, maxwell
//  Magnetic field strength: tesla, gauss
//  Luminous intensity: candela, candlepower
//  Luminous flux: lumen, phot
//  Radioactivity: becquerel, curie
//  Absorbed dose: gray, rad
//  Equivalent dose: sievert, rem
//  Amount of substance: mole, kilomole

interface Dimension {
    val units: List<UnitOfMeasurement>
    val baseUnit: UnitOfMeasurement

    /**
     * Align [amount] to the fitting [UnitOfMeasurement] within this [Dimension]. By default, only the prefix is aligned
     * (with [Amount.alignPrefix]). Dimensions may override this to switch units as well (e.g. [Time] aligns 3600
     * seconds to 1 hour).
     *
     * @throws IllegalArgumentException if [amount] does not belong to this dimension
     */
    fun align(amount: Amount): Amount {
        require(amount.unit.dimension == this) { "Cannot align an amount of ${amount.unit.dimension} with $this" }
        return amount.alignPrefix()
    }
}

object NoDimension : Dimension {
    override val units = listOf<UnitOfMeasurement>(NoUnit)
    override val baseUnit = NoUnit
}
