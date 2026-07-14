package net.tegulis.komputus

import java.math.BigDecimal
import java.math.MathContext
import java.text.NumberFormat
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.Prefix

/**
 * Convert to [BigDecimal] and provide [Amount.defaultMathContext] to the constructor.
 *
 * @see Amount.defaultMathContext
 * @see Amount.defaultPrecision
 * @see Amount.defaultRoundingMode
 */
fun Number.toBigDecimalWithMathContext(): BigDecimal = BigDecimal(this.toString(), Amount.defaultMathContext)

/**
 * Divide with the [Amount.defaultMathContext] while catching [ArithmeticException]s. If the reason is non-terminating
 * decimal expansion, re-try the division with [Amount.defaultNonTerminatingPrecision] and [Amount.defaultRoundingMode].
 *
 * @see BigDecimal.divide
 */
fun BigDecimal.divideWithMathContext(divisor: Number): BigDecimal =
    try {
        this.divide(divisor.toBigDecimalWithMathContext(), Amount.defaultMathContext)
    } catch (exception: ArithmeticException) {
        if (exception.message?.contains("Non-terminating") ?: false) {
            this.divide(
                divisor.toBigDecimalWithMathContext(),
                MathContext(Amount.defaultNonTerminatingPrecision, Amount.defaultRoundingMode),
            )
        } else {
            throw exception
        }
    }

fun BigDecimal.multiplyWithMathContext(multiplier: Number): BigDecimal =
    if (multiplier == 1) return this else this.multiply(multiplier.toBigDecimalWithMathContext())

/**
 * Multiply with the given [Prefix]'s multiplier value ([Prefix.value]) using [multiplyWithMathContext].
 *
 * @param prefix The prefix to multiply with.
 */
fun BigDecimal.multiplyWithPrefix(prefix: Prefix): BigDecimal =
    if (prefix.isNotScaling) return this else this.multiplyWithMathContext(prefix.value)

/**
 * Divide with the given [Prefix]'s multiplier value ([Prefix.value]) using [divideWithMathContext].
 *
 * @param prefix The prefix to multiply with.
 */
fun BigDecimal.divideWithPrefix(prefix: Prefix): BigDecimal =
    if (prefix.isNotScaling) return this else this.divideWithMathContext(prefix.value)

/** Convenience function to format a [BigDecimal] using [Amount.defaultNumberFormatProvider]. */
fun BigDecimal.format(numberFormat: NumberFormat = Amount.defaultNumberFormatProvider()): String =
    numberFormat.format(this)
