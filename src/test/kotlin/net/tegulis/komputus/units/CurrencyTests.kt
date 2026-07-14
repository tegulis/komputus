package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.prefixes.NotScalingPrefixGroup
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CurrencyTests {

    private fun getUsNumberFormat(): NumberFormat =
        DecimalFormat.getInstance(Locale.US).apply { maximumFractionDigits = 2 }

    @Test
    fun `MONEY amounts use NotScalingPrefixGroup`() {
        val five = 5.money()
        assertThat(five.unit).isSameInstanceAs(Currency.MONEY)
        assertThat(five.prefix.prefixGroup).isSameInstanceAs(NotScalingPrefixGroup)
        assertThat(five.prefix).isSameInstanceAs(NotScalingPrefix)
        assertThat(five.format(getUsNumberFormat())).isEqualTo("5 ¤")
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
        assertThat(Currency.MONEY.amountOf(5, NotScalingPrefix).format(getUsNumberFormat())).isEqualTo("5 ¤")
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
        val converted = 5.money().convertTo(Currency.EUR)
        assertThat(converted.unit).isSameInstanceAs(Currency.EUR)
        assertThat(converted.magnitude).isEqualToIgnoringScale(BigDecimal("5"))
        assertThat(5.money()).isEqualTo(5.EUR())
        assertThat(5.money().baseMagnitude).isEqualToIgnoringScale(BigDecimal("5"))
    }
}
