package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.LongScale
import net.tegulis.komputus.prefixes.ShortScale
import net.tegulis.komputus.units.Currency.EUR
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CurrencyTests {

    private fun getUsNumberFormat(): NumberFormat =
        DecimalFormat.getInstance(Locale.ROOT).apply { maximumFractionDigits = 2 }

    @Test
    fun `MONEY amounts use ShortScale by default`() {
        val five = 5.money()
        assertThat(five.unit).isSameInstanceAs(Currency.MONEY)
        assertThat(five.prefix.prefixGroup).isSameInstanceAs(ShortScale)
        assertThat(five.prefix).isSameInstanceAs(ShortScale.NONE)
        assertThat(five.format(getUsNumberFormat())).isEqualTo("5 ¤")
    }

    @Test
    fun `money aligns to the short scale by default`() {
        assertThat(1500.money(EUR).align().prefix).isSameInstanceAs(ShortScale.THOUSAND)
        assertThat(2_500_000.money(EUR).align().prefix).isSameInstanceAs(ShortScale.MILLION)
        // In the short scale 10^9 is a billion
        assertThat(1_500_000_000.money(EUR).align().prefix).isSameInstanceAs(ShortScale.BILLION)
        // TODO: format() concatenates the written prefix with the unit symbol, and money wants the multiplier next to
        //   the value ("1.5 thousand EUR") - see the formatting TODO in Currency.kt
        assertThat(1500.money(EUR).align().format(getUsNumberFormat())).isEqualTo("1.5 thousandEUR")
        assertThat(1_500_000_000.money(EUR).align().format(getUsNumberFormat())).isEqualTo("1.5 billionEUR")
    }

    @Test
    fun `money aligns within the long scale when it is put there`() {
        // The very magnitude that is a billion in the short scale is a milliard in the long scale
        val milliard = Amount(1_500_000_000, LongScale.NONE, EUR).align()
        assertThat(milliard.prefix).isSameInstanceAs(LongScale.MILLIARD)
        assertThat(milliard.format(getUsNumberFormat())).isEqualTo("1.5 milliardEUR")
        // ...while a long scale billion is a thousand times bigger, at 10^12
        val billion = Amount(1_500_000_000_000L, LongScale.NONE, EUR).align()
        assertThat(billion.prefix).isSameInstanceAs(LongScale.BILLION)
        // The amount stays in the group it was put into instead of resetting to the ShortScale default
        assertThat(milliard.prefix.prefixGroup).isSameInstanceAs(LongScale)
        // Only the presentation differs: the value is the same as the short scale billion
        assertThat(milliard).isEqualTo(1_500_000_000.money(EUR))
    }

    @Test
    fun `money never scales down`() {
        // Neither scale has sub-one prefixes, so there are no millidollars: cents stay a fraction of a dollar
        val cents = BigDecimal("0.5").money(EUR).align()
        assertThat(cents.prefix).isSameInstanceAs(ShortScale.NONE)
        assertThat(cents.format(getUsNumberFormat())).isEqualTo("0.5 EUR")
        assertThat(185.money(EUR).align().prefix).isSameInstanceAs(ShortScale.NONE)
    }

    @Test
    fun `currency amounts do not equal or compare to other dimensions`() {
        assertThat(5.money()).isNotEqualTo(Amount.ofSeconds(5))
        assertThat(5.money()).isNotEqualTo(Amount.ofBytes(5))
        assertThat(5.money()).isNotEqualTo(Amount(5))
        assertThrows<IllegalArgumentException> { 5.money() < Amount.ofSeconds(5) }
    }

    @Test
    fun `a bucket price is a curried quotient of money, information, and time`() {
        val price = BigDecimal("0.023").money() / 1.GB() / 30.days()
        assertThat(price.unit.symbol).isEqualTo("(¤/B)/d")
        // 0.023 / 1e9 is exact, the division by 30 rounds to Amount.defaultNonTerminatingPrecision
        assertThat(price.magnitude).isEqualToIgnoringScale(BigDecimal("7.666666667E-13"))
        val storedBytes = 250.GiB() / 1.days() * 30.days()
        val monthlyCost = price * 30.days() * storedBytes
        assertThat(monthlyCost.unit).isSameInstanceAs(Currency.MONEY)
        assertThat(monthlyCost.magnitude).isEqualToIgnoringScale(BigDecimal("185.22046464805306368"))
        assertThat(monthlyCost.format(getUsNumberFormat())).isEqualTo("185.22 ¤")
    }

    @Test
    fun `MONEY is the generic base unit`() {
        assertThat(Currency.baseUnit).isSameInstanceAs(Currency.MONEY)
        assertThat(Currency.MONEY.amountOf(5, ShortScale.NONE).format(getUsNumberFormat())).isEqualTo("5 ¤")
    }

    @Test
    fun `currencies format with their ISO codes`() {
        // TODO: refactor to parametric test
        for (currency in Currency.units) {
            if (currency == Currency.MONEY) continue
            val amount = Amount(5, unit = currency)
            assertThat(amount.format(getUsNumberFormat())).isEqualTo("5 ${currency.code}")
        }
    }

    @Test
    fun `currencies convert with identity`() {
        val converted = 5.money().convertTo(EUR)
        assertThat(converted.unit).isSameInstanceAs(EUR)
        assertThat(converted.magnitude).isEqualToIgnoringScale(BigDecimal("5"))
        assertThat(5.money(EUR)).isEqualTo(EUR.amountOf(5))
        assertThat(5.money().baseMagnitude).isEqualToIgnoringScale(BigDecimal("5"))
    }
}
