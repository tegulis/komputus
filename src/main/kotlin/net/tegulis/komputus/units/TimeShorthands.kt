// Generated file - DO NOT edit by hand!
package net.tegulis.komputus.units

import java.math.BigDecimal
import kotlin.Number
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.prefixes.Prefix
import net.tegulis.komputus.prefixes.SI

fun Amount.Companion.ofSeconds(seconds: Number, prefix: Prefix = SI.defaultNotScalingPrefix): Amount = Time.Second.amountOf(seconds, prefix)

fun Amount.Companion.ofSeconds(seconds: BigDecimal, prefix: Prefix = SI.defaultNotScalingPrefix): Amount = Time.Second.amountOf(seconds, prefix)

fun Amount.Companion.ofMinutes(minutes: Number, prefix: Prefix = NotScalingPrefix): Amount = Time.Minute.amountOf(minutes, prefix)

fun Amount.Companion.ofMinutes(minutes: BigDecimal, prefix: Prefix = NotScalingPrefix): Amount = Time.Minute.amountOf(minutes, prefix)

fun Amount.Companion.ofHours(hours: Number, prefix: Prefix = NotScalingPrefix): Amount = Time.Hour.amountOf(hours, prefix)

fun Amount.Companion.ofHours(hours: BigDecimal, prefix: Prefix = NotScalingPrefix): Amount = Time.Hour.amountOf(hours, prefix)

fun Amount.Companion.ofDays(days: Number, prefix: Prefix = NotScalingPrefix): Amount = Time.Day.amountOf(days, prefix)

fun Amount.Companion.ofDays(days: BigDecimal, prefix: Prefix = NotScalingPrefix): Amount = Time.Day.amountOf(days, prefix)

fun Amount.Companion.ofWeeks(weeks: Number, prefix: Prefix = NotScalingPrefix): Amount = Time.Week.amountOf(weeks, prefix)

fun Amount.Companion.ofWeeks(weeks: BigDecimal, prefix: Prefix = NotScalingPrefix): Amount = Time.Week.amountOf(weeks, prefix)

fun Number.seconds(): Amount = Amount.ofSeconds(this)

fun BigDecimal.seconds(): Amount = Amount.ofSeconds(this)

fun Number.minutes(): Amount = Amount.ofMinutes(this)

fun BigDecimal.minutes(): Amount = Amount.ofMinutes(this)

fun Number.hours(): Amount = Amount.ofHours(this)

fun BigDecimal.hours(): Amount = Amount.ofHours(this)

fun Number.days(): Amount = Amount.ofDays(this)

fun BigDecimal.days(): Amount = Amount.ofDays(this)

fun Number.weeks(): Amount = Amount.ofWeeks(this)

fun BigDecimal.weeks(): Amount = Amount.ofWeeks(this)
