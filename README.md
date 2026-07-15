# Komputus - Infrastructure (and more) as Kotlin

*computus: Latin for computation*

Komputus is an opinionated Kotlin library to model entities (like IT infrastructure) in code based on the
[ECS pattern](https://en.wikipedia.org/wiki/Entity_component_system). The primary goal is to run *systems* against the
(infrastructure) model and draw conclusions from it, such as its cost or its bandwidth use. The secondary goal (not
implemented yet) is to do CRUD operations with bundles (world, stack, rack, cluster, etc.) of infrastructure entities.

TODO: Write a section about the problems with calculating IT infrastructure costs, bandwidth, etc.
TODO: Write a section about the problems with Terraform.

## Primary features

- [Amounts](#amounts): numbers with a unit and a prefix, backed by `BigDecimal`.
    - [Prefixes](#prefixes-and-prefix-groups): SI, IEC, and the written scales for money, with rules for which prefix a
      unit may take.
    - [Alignment](#alignment): pick the unit and prefix a human would write ("1.5 min", not "90 s").
    - [Rates](#rates-dividing-and-multiplying-amounts): dividing one amount by another gives a rate, and multiplying it
      back cancels the rate.
- [Dimensions](#dimensions-and-units): time, binary information and money, plus the quotients of those.
    - [Conversion, comparison, and arithmetic](#conversion) within a dimension.
    - [A bridge to `java.time`](#bridging-to-javatime) for calendar arithmetic.
- [An ECS](#ecs): not ready yet.

### Amounts

The computational foundation of Komputus is the [Amount](src/main/kotlin/net/tegulis/komputus/amount/Amount.kt) class.
Nearly all values ("amounts") in Komputus are represented as amounts, and all entities must use amounts to represent
their quantities. Think of an amount as a generalisation of
[Duration](https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html) for any kind of quantity.

An amount has three parts:

- `magnitude`: the plain value, without any prefix, as a `BigDecimal`.
- `prefix`: display state, for example, kilo or mebi. It never changes the value, only how it is written.
- `unit`: the unit of measurement, for example, the second. The unit knows its `dimension`.

An amount also keeps its `baseMagnitude`: the same value expressed in the dimension's base unit. Comparisons and
conversions go through this, which is why 60 seconds and 1 minute are equal.

Amounts are immutable. Every operation returns a new amount, and an operation with no effect returns the same instance.

#### Creating amounts

```kotlin
Amount(1, SI.KILO)                   // magnitude 1000, written as "1 k"
Amount(1000, SI.KILO, raw = true)    // magnitude 1000, written as "1000 k"
Amount(90, unit = Time.Second)       // 90 s
Time.Second.amountOf(90)             // the same, from the unit

Amount.ofSeconds(90)                 // 90 s
Amount.ofBytes(1, IEC.TEBI)          // 1 TiB
Amount.ofMoney(19.99, Currency.EUR)  // 19.99 EUR

// Short forms
90.seconds()
250.GiB()
19.99.money(Currency.EUR)
```

The constructor scales the value with the prefix: `Amount(1, SI.KILO).magnitude == 1000`. Pass `raw = true` when the
value is already unscaled. Every convenience function has a `Number` and a `BigDecimal` version.

#### Prefixes and prefix groups

A [Prefix](src/main/kotlin/net/tegulis/komputus/prefixes/Prefix.kt) belongs to a `PrefixGroup`, which defines the
multiplier the prefix's `power` is raised to. Komputus ships four groups:

| Group        | Multiplier | Prefixes                                              | Used by                      |
|--------------|------------|-------------------------------------------------------|------------------------------|
| `SI`         | 10         | quecto (10⁻³⁰) ... quetta (10³⁰)                      | seconds, bits, decimal bytes |
| `IEC`        | 1024       | Ki … Qi (1024¹ ... 1024¹⁰)                            | binary bytes                 |
| `ShortScale` | 10         | thousand, million, billion ... octillion              | money                        |
| `LongScale`  | 10         | thousand, million, milliard, billion ... quadrilliard | money                        |

`NotScalingPrefixGroup` holds the single `NotScalingPrefix`, which is used by units that never scale.

Properties control which prefix an amount may take:

- `Prefix.isMajor` marks the prefixes that are normally used. In the SI prefix group, the major prefixes are the powers
  of three, so `align()` on an amount with the SI prefix group picks kilo but never hecto or deca.
- `UnitOfMeasurement.prefixFilter` says which prefixes are customary *for that unit*. This is convention, not maths:
  networking uses Mb/s, storage uses KiB, and there are no "kilominutes".

Each unit also has a `defaultPrefix`: the prefix it takes when it is set to a unit without an explicit prefix, for
example, after a conversion. The default prefix also decides which group the amount aligns in, and it must be allowed by
the unit's own `prefixFilter`.

A prefix group is a closed world for alignment: `alignPrefix()` only looks at prefixes of the group the amount is
currently in. So a unit that allows a group with no fitting prefix would strand the amount there. This is why `Second`
rejects every non-SI prefix, and why converting a time amount to seconds can then align down to milliseconds.

#### Alignment

`alignPrefix()` picks the largest prefix that still leaves a value of at least one, within the current prefix group and
the unit's `prefixFilter`.

`align()` asks the dimension, which may switch the unit as well. Time switches units (3600 seconds align to 1 hour);
binary information does not, because bits or bytes is the caller's choice, not a question of magnitude.

```kotlin
Amount.ofSeconds(90).align()     // 1.5 min
0.5.seconds().align()            // 500 ms
Amount.ofMinutes(0.001).align()  // 60 ms
2_500_000.money().align()        // 2.5 million¤ (uses the ShortScale.MILLION prefix, but currently ill-formatted)
```

#### Conversion

`convertTo(newUnit)` converts within a dimension, through the dimension's base unit. Each unit provides a `toBase` and a
`fromBase` function, so a dimension only needs one conversion pair per unit instead of one per unit pair.

```kotlin
Amount.ofSeconds(90).convertTo(Time.Minute)            // 1.5 min
Amount.ofBytes(1000).convertTo(BinaryInformation.Bit)  // 8000 b
```

The prefix follows the library's carry-over rule, which lives in `Amount.copy()` and applies to every operation that
changes the unit: the prefix is kept when the new unit's `prefixFilter` allows it, and reset to the new unit's
`defaultPrefix` otherwise.

Converting between dimensions throws an `IllegalArgumentException`.

Conversion and comparison across units are shown together in
[UnitConversions.kt](src/test/kotlin/net/tegulis/komputus/demos/UnitConversions.kt).

#### Comparison

Two amounts are equal when they have the same dimension and the same base magnitude, so `60.seconds()` equals
`1.minutes()`. `equals()` never throws: amounts of different dimensions are simply not equal. `compareTo()` is stricter
and throws an `IllegalArgumentException` for different dimensions, because there is no sensible order between them.

#### Arithmetic

```kotlin
1.hours() + 30.minutes()  // 1.5 h - the right side is converted to the left side's unit
30.minutes() + 1.hours()  // 90 min
1.hours() - 30.minutes()  // 0.5 h
30.minutes() - 1.hours()  // -30 min
90.seconds() * 2          // 180 s - a dimensionless factor
90.seconds() / 2          // 45 s
(-90).seconds()             // -90 s
```

Adding or subtracting amounts of different dimensions throws an `IllegalArgumentException`.

See [AmountArithmetics.kt](src/test/kotlin/net/tegulis/komputus/demos/AmountArithmetics.kt) for a runnable walk-through.

#### Rates: dividing and multiplying amounts

Dividing an amount by another amount gives a rate. A rate is an ordinary `Amount` whose unit is a `QuotientUnit`, so
everything above (alignment, conversion, comparison, formatting) works on it. Rates can be nested too.

```kotlin
500.Mb() / 1.seconds()  // 500 Mb/s
100.Mb() / 3.hours()    // 33.33 Mb/h
```

Multiplying a rate by an amount of its denominator dimension cancels the quotient, in either order. Dividing an amount
by a rate whose numerator matches the amount's dimension cancels it too:

```kotlin
val nightlyBackup = 250.GiB()
val uplink = 500.Mb() / 1.seconds()
val transferTime = nightlyBackup / uplink                    // 1.19 h - a Time amount
val monthlyTraffic = (nightlyBackup / 1.days()) * 30.days()  // a BinaryInformation amount
```

A product of two amounts that is not a cancellation throws an `IllegalArgumentException`. Komputus deliberately does not
model product dimensions – see the [comparison to JSR 385](#comparison-to-javas-units-of-measurement-api-jsr-385) below.

A denominator that is itself a product is written by currying. A bucket price per gigabyte per month is money per byte
per day, and it cancels one step at a time:

```kotlin
val price = BigDecimal("0.023").money() / 1.GB() / 30.days()  // (¤/B)/d
val cost = price * 30.days() * monthlyTraffic                 // ¤
```

Quotients are structural and nothing is simplified: `byte/byte` is not dimensionless, and a quotient dimension is never
equal to a named dimension that models the same physical quantity.

Note that a rate carries a single prefix, and it scales the numerator: an amount of 100 with `SI.MEGA` in bits per
second is written `100 Mb/s`.

[UnitArithmetics.kt](src/test/kotlin/net/tegulis/komputus/demos/UnitArithmetics.kt) works through building and
cancelling rates across units.

#### Formatting

**NOTE**: An AmountFormatter is coming, to replace `format()`.

`format()` (and `toString()`) writes the value with the prefix and unit the amount currently carries. It does not align
first, so call `align()` when you want the human-friendly form.

```kotlin
val disk = Amount.ofBytes(1, IEC.TEBI)
disk.format()                           // 1 TiB
disk.format(prefix = IEC.GIBI)          // 1,024 GiB
disk.format(prefix = SI.GIGA)           // 1,099.51 GB
```

The `prefix` parameter forces a prefix for display only, without creating a new amount and without asking the
`prefixFilter`, so any prefix is honoured – even one from another group. To force the unit as well, convert first: a
unit change is a real conversion, not a presentation.

The defaults live in `Amount.Companion` and can be changed:

- `defaultNumberFormatProvider` provides a fresh `NumberFormat` for each call (number formats are not thread-safe).
- `defaultPrefixAndUnitFormatString` is the pattern for value, prefix, and unit.

When a unit has no symbol, its `name` or `pluralName` is used, chosen by the value as it is *displayed*: `1.0001` with
two fraction digits displays as "1", so it reads "1 nibble".

See [ForcedFormatting.kt](src/test/kotlin/net/tegulis/komputus/demos/ForcedFormatting.kt) for a longer walk-through.

#### Numbers and rounding

Everything is a `BigDecimal`, and the policy is one and the same everywhere: exact whenever the conversion terminates.
When a division does not terminate, it is retried with `Amount.defaultNonTerminatingPrecision` (10 digits) and
`Amount.defaultRoundingMode` (`HALF_DOWN`). Rounding therefore only happens where it cannot be avoided, and it compounds
when an already rounded amount is converted again.

### Dimensions and units

A [Dimension](src/main/kotlin/net/tegulis/komputus/units/Dimension.kt) is a set of units with one base unit, and it
decides how its amounts align. A [UnitOfMeasurement](src/main/kotlin/net/tegulis/komputus/units/UnitOfMeasurement.kt)
knows its dimension, its names and symbol, its `prefixFilter`, its `defaultPrefix`, and its `toBase` and `fromBase`
conversions.

Base units are chosen so that `toBase` multiplies by a terminating decimal wherever possible, because comparisons go
through it.

#### Binary information

Bits, nibbles, octets, and bytes. Bytes are the base unit. Octets and bytes are separate units so that they can have
separate symbols. Bits and nibbles default to SI prefixes, as used in networking; octets and bytes default to IEC
prefixes, as used when measuring storage (but not when advertising it). Both groups are allowted for every unit, so a
disk can be shown in TB or in TiB.

There are shorthands for every unit and major prefix: `100.Mb()`, `250.GiB()`, `1.TB()`, `8.kb()`, and so on.

#### Time

Seconds, minutes, hours, days, and weeks, with the classic 60/60/24/7 multipliers. Seconds are the base unit. Seconds
take SI prefixes below one (milli, micro, nano), so they can align down to milliseconds. The other units take no prefix
at all – there are no kilominutes.

Months and years are deliberately **not** units: they are not fixed lengths (28 to 31 days, leap years). Calendar
arithmetic belongs to `java.time`, and the result bridges back through the `Duration` conversions.

#### Currency

Money is a dimension like any other, not a special case bolted onto the side.
[Currency](src/main/kotlin/net/tegulis/komputus/units/Currency.kt) carries the ISO 4217 units, and each one is an
ordinary unit of measurement. So an amount of money adds, subtracts, compares, converts, formats, and - the point of the
exercise - takes part in rates exactly like an amount of time or storage does. That is what makes an infrastructure cost
expressible as arithmetic instead of as a comment:

```kotlin
val hourly = 40.money(Currency.EUR) / 1.hours()  // 40 EUR/h
val cost = hourly * 3.hours()                    // 120 EUR
```

`Currency.MONEY` is the base unit: generic money, ISO 4217's `XXX`, written with the generic currency sign `¤`. It is
the default everywhere, so code that does not care which currency it is leaves it out.

```kotlin
100.money()                // 100 ¤
19.99.money(Currency.EUR)  // 19.99 EUR
```

Money scales the way money is written, with the `ShortScale` or the `LongScale` prefixes. SI is simply wrong here: it
writes 10^9 as "G", and nobody reports a budget as "1.5 G". Both scales are allowed and the short scale is the default,
so an amount that is deliberately put into the long scale keeps aligning there. The two are separate groups because they
disagree: a long scale billion is a thousand times a short scale billion, and 10^9 is a billion in the short scale but a
milliard in the long scale. Neither group has prefixes below one, so money never scales *down*: there are no
millidollars. Sub-unit precision is a formatting concern, not a scaling one.

```kotlin
2_500_000.money(Currency.EUR).align().prefix   // ShortScale.MILLION
```

Two limits are worth knowing before you hit them. **Exchange rates do not exist**: every currency converts to `MONEY`
through the identity, so currencies compare and convert 1:1, and adding dollars to euros silently treats them as the
same thing. Loading currency pairs is the planned fix. And **money does not format properly yet**: `format()` writes the
prefix symbol next to the unit symbol, which is right for "1.5 kb" but gives "1.5 millionEUR" instead of
"1.5 million EUR". A dedicated formatter will fix that, honour `CurrencyUnit.fractionDigits`, and translate the scale
words per locale.

##### Shorthands for your favourite currency

Komputus deliberately ships no per-currency helpers. There are 165 currencies, and giving each one a `Number.EUR()` and
a `BigDecimal.EUR()` would hang several hundred extension functions off `Number` and `BigDecimal` - the two most common
receivers in the library - and everyone importing the package would pay for that in autocomplete forever. The generic
`money(currency)` covers all 165 with four functions.

If `Currency.EUR` is not short enough, the cheapest shorthand costs nothing at all: import the unit.

```kotlin
import net.tegulis.komputus.units.Currency.EUR

val price = 19.99.money(EUR)
```

And if you want the real thing, write it yourself – in your own package, for the two or three currencies you actually
use:

```kotlin
package com.example.money

import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.units.Currency
import net.tegulis.komputus.units.ofMoney

fun Number.EUR(): Amount = Amount.ofMoney(this, Currency.EUR)

fun BigDecimal.EUR(): Amount = Amount.ofMoney(this, Currency.EUR)

val myMoney = 19.EUR()
```

Keeping them in a package of your own is the whole point: the pollution becomes opt-in, it is scoped to the currencies
you care about, and it is your namespace to spend.

#### Quotient dimensions

A `QuotientDimension` is the first-order quotient of two other dimensions, and a `QuotientUnit` is a unit of it, for
example bits per second. Both are data classes, so two quotients built independently from the same parts are equal and
convert into each other like any other unit. They are created
by [dividing amounts](#rates-dividing-and-multiplying-amounts);
there is rarely a reason to write one by hand.

#### No dimension

`NoDimension` and `NoUnit` are the defaults. `Amount(1, SI.KILO)` is a plain scaled number: it still aligns and formats,
it just has nothing to convert to.

### Bridging to `java.time`

```kotlin
Duration.ofMinutes(90).toAmount()             // 5400 s
someKotlinDuration.toAmount()                 // the same, from kotlin.time.Duration
Amount.ofMinutes(90).toJavaDuration()         // PT1H30M
Amount.ofMinutes(90).toKotlinDuration()       // 1h 30m
```

This is how calendar spans enter Komputus. Months and years are not time units, so compute the real span with
`java.time` and convert the resulting `Duration`:

```kotlin
val start = LocalDate.of(2026, 1, 1)
val january = Duration.between(start.atStartOfDay(), start.plus(Period.ofMonths(1)).atStartOfDay()).toAmount()
val januaryTraffic = dailyTraffic * january   // 31 nights, not 30
```

Conversions to a `Duration` go through the base magnitude, so they are exact for every time unit. Fractions below
nanosecond resolution are rounded, seconds that do not fit a `Long` throw an `ArithmeticException`, and a
`kotlin.time.Duration` adds its own limits on top (it loses sub-millisecond precision beyond ~146 years).

### ECS

Not ready yet. The `ecs` package holds an early sketch (entities, components, a registry) and the `old` package holds
the previous attempt. Neither is part of the API.

## Comparison to Java's Units of Measurement API (JSR 385)

[JSR 385](https://unitsofmeasurement.github.io/) (`javax.measure`, reference implementation
[Indriya](https://github.com/unitsofmeasurement/indriya)) is the fully general units library for the JVM. Komputus does
not try to replace it. It is an opinionated modelling library, and its amount system deliberately covers a smaller
space. Choose consciously.

### What JSR 385 has that Komputus does not

- **Full unit algebra**: products, powers, and roots of units (`kg·m/s²`), with dimensions reduced to base-dimension
  exponents, so equivalent composites are recognised automatically.
- **Non-linear conversions**: affine scales (Celsius, Fahrenheit) and logarithmic units (decibels). Komputus conversions
  are linear scalings through a dimension's base unit.
- **Compile-time quantity kinds**: a `Quantity<Length>` cannot be added to a `Quantity<Time>` - although the typing
  degrades to `Quantity<?>` plus runtime `asType()` checks exactly where the algebra is used.
- **Parsing** of unit expressions, large unit catalogues (imperial, US customary), and the maturity of a standard.

### What Komputus does instead

- **Presentation is a first-class concern.** A prefix is display state carried next to the value, units declare which
  prefixes are customary for them, and `align()` picks the unit and prefix a human would write ("1.5 min", not "90 s").
  In JSR 385, `KILO(METRE)` is simply a different unit, and nothing chooses a presentation for you.
- **A Kotlin-first API**: operators and extensions (`100.Mb() / 3.hours()`), immutable amounts with value semantics.
- **One predictable numeric policy**: `BigDecimal` everywhere, exact whenever conversions terminate, explicit rounding
  when they cannot.
- **First-order quotients instead of a full algebra.** Dividing an amount by an amount gives a structural rate, and
  multiplying by an amount of the denominator dimension cancels it. This covers the target domain:
    - Denominators that are products come from currying: a cost per gigabyte-month is `(money/binary information)/time`,
      a nested quotient.
    - Numerators that are products get named dimensions – the way SI itself names the newton, the joule, and the watt
      instead of spelling out the products.
    - Nothing is simplified, so a structural quotient never quietly turns into something else.
- **Calendar honesty**: months and years are not units, because they are not fixed lengths. Calendar arithmetic belongs
  to `java.time`, and amounts bridge to it through the `Duration` conversions.

### When to use which

Use JSR 385 for general scientific computing, for temperature or decibel scales, or for dimensional analysis of
arbitrary formulas. Use Komputus for modelling IT infrastructure, where the expressions are quotient-shaped (bandwidth,
capacity, cost rates) and consistent presentation matters more than algebraic completeness. Should Komputus ever need
the full algebra, the way to get it is a bridge to `javax.measure`, not a reimplementation.

## Not implemented yet

- **Exchange rates.** Currencies convert 1:1 through the identity.
- **A dedicated formatter.** `Amount.format()` cannot place a written prefix next to the value ("1.5 million EUR"),
  translate it per locale, or honour the ISO 4217 fraction digits.
- **`Period` conversion.** Only `Duration` bridges to an amount today.
- **Automatic alignment.** `Amount.autoAlign` and `Amount.autoAlignPrefix` are placeholders and do nothing.
- **More dimensions.** Length, mass, energy, and the rest are listed as TODOs in `Dimension.kt`.
- **The ECS and the infrastructure model**, and with them the CRUD operations on bundles of entities.

## Building and running

```shell
./gradlew build        # format, compile and test
./gradlew test         # tests only
./gradlew ktfmtFormat  # format the sources (this also runs before every compile)
./gradlew ktfmtCheck   # check the formatting instead of fixing it
```

The demos under [src/test/kotlin/net/tegulis/komputus/demos](src/test/kotlin/net/tegulis/komputus/demos) are plain
`main` functions. Run them from the IDE to see the library work end to end:

- [Amounts.kt](src/test/kotlin/net/tegulis/komputus/demos/Amounts.kt): SI against IEC prefixes, and the advertised
  capacity of a disk against its real one.
- [AmountArithmetics.kt](src/test/kotlin/net/tegulis/komputus/demos/AmountArithmetics.kt): adding, subtracting, and
  scaling amounts of the same dimension.
- [UnitArithmetics.kt](src/test/kotlin/net/tegulis/komputus/demos/UnitArithmetics.kt): dividing and multiplying amounts
  of different units to build and cancel rates.
- [UnitConversions.kt](src/test/kotlin/net/tegulis/komputus/demos/UnitConversions.kt): converting and comparing between
  different units of a dimension.
- [BucketTransfer.kt](src/test/kotlin/net/tegulis/komputus/demos/BucketTransfer.kt): transfer time, monthly traffic, and
  storage cost, computed with rates.
- [ForcedFormatting.kt](src/test/kotlin/net/tegulis/komputus/demos/ForcedFormatting.kt): forcing a unit and a prefix for
  display.

Tests are split by dimension and by concern (`TimeAlignmentTests`, `BinaryConversionTests`, `QuotientArithmeticTests`,
and so on). Cases that are not specific to a dimension live in the classes without a dimension in the name.
