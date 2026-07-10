package net.tegulis.komputus.units

import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.prefixes.Prefix

/**
 * Currency dimension and units.
 *
 * [MONEY] is the generic base unit. For now, all currencies convert to it with the identity defaults of
 * [UnitOfMeasurement.toBase] and [UnitOfMeasurement.fromBase], so amounts of different currencies compare and convert
 * 1:1. In the future, currency pairs and their exchange rates can be loaded to define real conversions.
 *
 * Currencies use [NotScalingPrefix]: money is not scaled with prefixes.
 *
 * TODO: All currencies have been imported from ISO 4217. A thorough review of the list is needed to ensure all
 *   currencies are correct and have proper naming and symbols.
 */
object Currency : Dimension {
    override val units: List<CurrencyUnit> by lazy {
        listOf(
            MONEY,
            AED,
            AFN,
            ALL,
            AMD,
            AOA,
            ARS,
            AUD,
            AWG,
            AZN,
            BAM,
            BBD,
            BDT,
            BHD,
            BIF,
            BMD,
            BND,
            BOB,
            BOV,
            BRL,
            BSD,
            BTN,
            BWP,
            BYN,
            BZD,
            CAD,
            CDF,
            CHE,
            CHF,
            CHW,
            CLF,
            CLP,
            CNY,
            COP,
            COU,
            CRC,
            CUP,
            CVE,
            CZK,
            DJF,
            DKK,
            DOP,
            DZD,
            EGP,
            ERN,
            ETB,
            EUR,
            FJD,
            FKP,
            GBP,
            GEL,
            GHS,
            GIP,
            GMD,
            GNF,
            GTQ,
            GYD,
            HKD,
            HNL,
            HTG,
            HUF,
            IDR,
            ILS,
            INR,
            IQD,
            IRR,
            ISK,
            JMD,
            JOD,
            JPY,
            KES,
            KGS,
            KHR,
            KMF,
            KPW,
            KRW,
            KWD,
            KYD,
            KZT,
            LAK,
            LBP,
            LKR,
            LRD,
            LSL,
            LYD,
            MAD,
            MDL,
            MGA,
            MKD,
            MMK,
            MNT,
            MOP,
            MRU,
            MUR,
            MVR,
            MWK,
            MXN,
            MXV,
            MYR,
            MZN,
            NAD,
            NGN,
            NIO,
            NOK,
            NPR,
            NZD,
            OMR,
            PAB,
            PEN,
            PGK,
            PHP,
            PKR,
            PLN,
            PYG,
            QAR,
            RON,
            RSD,
            RUB,
            RWF,
            SAR,
            SBD,
            SCR,
            SDG,
            SEK,
            SGD,
            SHP,
            SLE,
            SOS,
            SRD,
            SSP,
            STN,
            SVC,
            SYP,
            SZL,
            THB,
            TJS,
            TMT,
            TND,
            TOP,
            TRY,
            TTD,
            TWD,
            TZS,
            UAH,
            UGX,
            USD,
            USN,
            UYI,
            UYU,
            UYW,
            UZS,
            VED,
            VES,
            VND,
            VUV,
            WST,
            XAD,
            XAF,
            XAG,
            XAU,
            XBA,
            XBB,
            XBC,
            XBD,
            XCD,
            XCG,
            XDR,
            XOF,
            XPD,
            XPF,
            XPT,
            XSU,
            XTS,
            XUA,
            YER,
            ZAR,
            ZMW,
            ZWG,
        )
    }
    override val baseUnit: UnitOfMeasurement
        get() = MONEY

    sealed class CurrencyUnit(
        val code: String,
        val numericCode: String,
        override val name: String,
        override val pluralName: String,
        symbol: String? = null,
        val fractionDigits: Int? = null,
    ) : UnitOfMeasurement {
        override val symbol: String = symbol ?: code
        override val dimension: Dimension
            get() = Currency

        override val prefixFilter: (Prefix) -> Boolean = Prefix.notScalingPrefixFilter
    }

    /** Generic money and the base unit of the dimension; the symbol is the generic currency sign. */
    object MONEY : CurrencyUnit("XXX", "999", "money", "monies", "¤")

    object AED :
        CurrencyUnit("AED", "784", "United Arab Emirates dirham", "United Arab Emirates dirhams", fractionDigits = 2)

    object AFN : CurrencyUnit("AFN", "971", "Afghan afghani", "Afghan afghanis", fractionDigits = 2)

    object ALL : CurrencyUnit("ALL", "008", "Albanian lek", "Albanian leks", fractionDigits = 2)

    object AMD : CurrencyUnit("AMD", "051", "Armenian dram", "Armenian drams", fractionDigits = 2)

    object AOA : CurrencyUnit("AOA", "973", "Angolan kwanza", "Angolan kwanzas", fractionDigits = 2)

    object ARS : CurrencyUnit("ARS", "032", "Argentine peso", "Argentine pesos", fractionDigits = 2)

    object AUD : CurrencyUnit("AUD", "036", "Australian dollar", "Australian dollars", fractionDigits = 2)

    object AWG : CurrencyUnit("AWG", "533", "Aruban florin", "Aruban florins", fractionDigits = 2)

    object AZN : CurrencyUnit("AZN", "944", "Azerbaijani manat", "Azerbaijani manats", fractionDigits = 2)

    object BAM :
        CurrencyUnit(
            "BAM",
            "977",
            "Bosnia and Herzegovina convertible mark",
            "Bosnia and Herzegovina convertible marks",
            fractionDigits = 2,
        )

    object BBD : CurrencyUnit("BBD", "052", "Barbados dollar", "Barbados dollars", fractionDigits = 2)

    object BDT : CurrencyUnit("BDT", "050", "Bangladeshi taka", "Bangladeshi takas", fractionDigits = 2)

    object BHD : CurrencyUnit("BHD", "048", "Bahraini dinar", "Bahraini dinars", fractionDigits = 3)

    object BIF : CurrencyUnit("BIF", "108", "Burundian franc", "Burundian francs", fractionDigits = 0)

    object BMD : CurrencyUnit("BMD", "060", "Bermudian dollar", "Bermudian dollars", fractionDigits = 2)

    object BND : CurrencyUnit("BND", "096", "Brunei dollar", "Brunei dollars", fractionDigits = 2)

    object BOB : CurrencyUnit("BOB", "068", "Boliviano", "Bolivianos", fractionDigits = 2)

    object BOV :
        CurrencyUnit("BOV", "984", "Bolivian Mvdol (funds code)", "Bolivian Mvdol (funds code)s", fractionDigits = 2)

    object BRL : CurrencyUnit("BRL", "986", "Brazilian real", "Brazilian reals", fractionDigits = 2)

    object BSD : CurrencyUnit("BSD", "044", "Bahamian dollar", "Bahamian dollars", fractionDigits = 2)

    object BTN : CurrencyUnit("BTN", "064", "Bhutanese ngultrum", "Bhutanese ngultrums", fractionDigits = 2)

    object BWP : CurrencyUnit("BWP", "072", "Botswana pula", "Botswana pulas", fractionDigits = 2)

    object BYN : CurrencyUnit("BYN", "933", "Belarusian ruble", "Belarusian rubles", fractionDigits = 2)

    object BZD : CurrencyUnit("BZD", "084", "Belize dollar", "Belize dollars", fractionDigits = 2)

    object CAD : CurrencyUnit("CAD", "124", "Canadian dollar", "Canadian dollars", fractionDigits = 2)

    object CDF : CurrencyUnit("CDF", "976", "Congolese franc", "Congolese francs", fractionDigits = 2)

    object CHE : CurrencyUnit("CHE", "947", "WIR euro", "WIR euros", fractionDigits = 2)

    object CHF : CurrencyUnit("CHF", "756", "Swiss franc", "Swiss francs", fractionDigits = 2)

    object CHW : CurrencyUnit("CHW", "948", "WIR franc", "WIR francs", fractionDigits = 2)

    object CLF : CurrencyUnit("CLF", "990", "Unidad de Fomento (UF)", "Unidad de Fomentos (UF)", fractionDigits = 4)

    object CLP : CurrencyUnit("CLP", "152", "Chilean peso", "Chilean pesos", fractionDigits = 0)

    object CNY : CurrencyUnit("CNY", "156", "Renminbi", "Renminbis", fractionDigits = 2)

    object COP : CurrencyUnit("COP", "170", "Colombian peso", "Colombian pesos", fractionDigits = 2)

    object COU :
        CurrencyUnit("COU", "970", "Unidad de Valor Real (UVR)", "Unidad de Valor Real (UVR)s", fractionDigits = 2)

    object CRC : CurrencyUnit("CRC", "188", "Costa Rican colon", "Costa Rican colons", fractionDigits = 2)

    object CUP : CurrencyUnit("CUP", "192", "Cuban peso", "Cuban pesos", fractionDigits = 2)

    object CVE : CurrencyUnit("CVE", "132", "Cape Verdean escudo", "Cape Verdean escudos", fractionDigits = 2)

    object CZK : CurrencyUnit("CZK", "203", "Czech koruna", "Czech korunas", fractionDigits = 2)

    object DJF : CurrencyUnit("DJF", "262", "Djiboutian franc", "Djiboutian francs", fractionDigits = 0)

    object DKK : CurrencyUnit("DKK", "208", "Danish krone", "Danish krones", fractionDigits = 2)

    object DOP : CurrencyUnit("DOP", "214", "Dominican peso", "Dominican pesos", fractionDigits = 2)

    object DZD : CurrencyUnit("DZD", "012", "Algerian dinar", "Algerian dinars", fractionDigits = 2)

    object EGP : CurrencyUnit("EGP", "818", "Egyptian pound", "Egyptian pounds", fractionDigits = 2)

    object ERN : CurrencyUnit("ERN", "232", "Eritrean nakfa", "Eritrean nakfas", fractionDigits = 2)

    object ETB : CurrencyUnit("ETB", "230", "Ethiopian birr", "Ethiopian birrs", fractionDigits = 2)

    object EUR : CurrencyUnit("EUR", "978", "Euro", "Euros", fractionDigits = 2)

    object FJD : CurrencyUnit("FJD", "242", "Fiji dollar", "Fiji dollars", fractionDigits = 2)

    object FKP : CurrencyUnit("FKP", "238", "Falkland Islands pound", "Falkland Islands pounds", fractionDigits = 2)

    object GBP : CurrencyUnit("GBP", "826", "Pound sterling", "Pound sterlings", fractionDigits = 2)

    object GEL : CurrencyUnit("GEL", "981", "Georgian lari", "Georgian laris", fractionDigits = 2)

    object GHS : CurrencyUnit("GHS", "936", "Ghanaian cedi", "Ghanaian cedis", fractionDigits = 2)

    object GIP : CurrencyUnit("GIP", "292", "Gibraltar pound", "Gibraltar pounds", fractionDigits = 2)

    object GMD : CurrencyUnit("GMD", "270", "Gambian dalasi", "Gambian dalasis", fractionDigits = 2)

    object GNF : CurrencyUnit("GNF", "324", "Guinean franc", "Guinean francs", fractionDigits = 0)

    object GTQ : CurrencyUnit("GTQ", "320", "Guatemalan quetzal", "Guatemalan quetzals", fractionDigits = 2)

    object GYD : CurrencyUnit("GYD", "328", "Guyanese dollar", "Guyanese dollars", fractionDigits = 2)

    object HKD : CurrencyUnit("HKD", "344", "Hong Kong dollar", "Hong Kong dollars", fractionDigits = 2)

    object HNL : CurrencyUnit("HNL", "340", "Honduran lempira", "Honduran lempiras", fractionDigits = 2)

    object HTG : CurrencyUnit("HTG", "332", "Haitian gourde", "Haitian gourdes", fractionDigits = 2)

    object HUF : CurrencyUnit("HUF", "348", "Hungarian forint", "Hungarian forints", fractionDigits = 2)

    object IDR : CurrencyUnit("IDR", "360", "Indonesian rupiah", "Indonesian rupiahs", fractionDigits = 2)

    object ILS : CurrencyUnit("ILS", "376", "Israeli new shekel", "Israeli new shekels", fractionDigits = 2)

    object INR : CurrencyUnit("INR", "356", "Indian rupee", "Indian rupees", fractionDigits = 2)

    object IQD : CurrencyUnit("IQD", "368", "Iraqi dinar", "Iraqi dinars", fractionDigits = 3)

    object IRR : CurrencyUnit("IRR", "364", "Iranian rial", "Iranian rials", fractionDigits = 2)

    object ISK : CurrencyUnit("ISK", "352", "Icelandic króna", "Icelandic krónas", fractionDigits = 0)

    object JMD : CurrencyUnit("JMD", "388", "Jamaican dollar", "Jamaican dollars", fractionDigits = 2)

    object JOD : CurrencyUnit("JOD", "400", "Jordanian dinar", "Jordanian dinars", fractionDigits = 3)

    object JPY : CurrencyUnit("JPY", "392", "Japanese yen", "Japanese yens", fractionDigits = 0)

    object KES : CurrencyUnit("KES", "404", "Kenyan shilling", "Kenyan shillings", fractionDigits = 2)

    object KGS : CurrencyUnit("KGS", "417", "Kyrgyzstani som", "Kyrgyzstani soms", fractionDigits = 2)

    object KHR : CurrencyUnit("KHR", "116", "Cambodian riel", "Cambodian riels", fractionDigits = 2)

    object KMF : CurrencyUnit("KMF", "174", "Comoro franc", "Comoro francs", fractionDigits = 0)

    object KPW : CurrencyUnit("KPW", "408", "North Korean won", "North Korean wons", fractionDigits = 2)

    object KRW : CurrencyUnit("KRW", "410", "South Korean won", "South Korean wons", fractionDigits = 0)

    object KWD : CurrencyUnit("KWD", "414", "Kuwaiti dinar", "Kuwaiti dinars", fractionDigits = 3)

    object KYD : CurrencyUnit("KYD", "136", "Cayman Islands dollar", "Cayman Islands dollars", fractionDigits = 2)

    object KZT : CurrencyUnit("KZT", "398", "Kazakhstani tenge", "Kazakhstani tenges", fractionDigits = 2)

    object LAK : CurrencyUnit("LAK", "418", "Lao kip", "Lao kips", fractionDigits = 2)

    object LBP : CurrencyUnit("LBP", "422", "Lebanese pound", "Lebanese pounds", fractionDigits = 2)

    object LKR : CurrencyUnit("LKR", "144", "Sri Lankan rupee", "Sri Lankan rupees", fractionDigits = 2)

    object LRD : CurrencyUnit("LRD", "430", "Liberian dollar", "Liberian dollars", fractionDigits = 2)

    object LSL : CurrencyUnit("LSL", "426", "Lesotho loti", "Lesotho lotis", fractionDigits = 2)

    object LYD : CurrencyUnit("LYD", "434", "Libyan dinar", "Libyan dinars", fractionDigits = 3)

    object MAD : CurrencyUnit("MAD", "504", "Moroccan dirham", "Moroccan dirhams", fractionDigits = 2)

    object MDL : CurrencyUnit("MDL", "498", "Moldovan leu", "Moldovan leus", fractionDigits = 2)

    object MGA : CurrencyUnit("MGA", "969", "Malagasy ariary", "Malagasy ariarys", fractionDigits = 2)

    object MKD : CurrencyUnit("MKD", "807", "Macedonian denar", "Macedonian denars", fractionDigits = 2)

    object MMK : CurrencyUnit("MMK", "104", "Myanmar kyat", "Myanmar kyats", fractionDigits = 2)

    object MNT : CurrencyUnit("MNT", "496", "Mongolian tögrög", "Mongolian tögrögs", fractionDigits = 2)

    object MOP : CurrencyUnit("MOP", "446", "Macanese pataca", "Macanese patacas", fractionDigits = 2)

    object MRU : CurrencyUnit("MRU", "929", "Mauritanian ouguiya", "Mauritanian ouguiyas", fractionDigits = 2)

    object MUR : CurrencyUnit("MUR", "480", "Mauritian rupee", "Mauritian rupees", fractionDigits = 2)

    object MVR : CurrencyUnit("MVR", "462", "Maldivian rufiyaa", "Maldivian rufiyaas", fractionDigits = 2)

    object MWK : CurrencyUnit("MWK", "454", "Malawian kwacha", "Malawian kwachas", fractionDigits = 2)

    object MXN : CurrencyUnit("MXN", "484", "Mexican peso", "Mexican pesos", fractionDigits = 2)

    object MXV :
        CurrencyUnit(
            "MXV",
            "979",
            "Mexican Unidad de Inversion (UDI) (funds code)",
            "Mexican Unidad de Inversion (UDI) (funds code)s",
            fractionDigits = 2,
        )

    object MYR : CurrencyUnit("MYR", "458", "Malaysian ringgit", "Malaysian ringgits", fractionDigits = 2)

    object MZN : CurrencyUnit("MZN", "943", "Mozambican metical", "Mozambican meticals", fractionDigits = 2)

    object NAD : CurrencyUnit("NAD", "516", "Namibian dollar", "Namibian dollars", fractionDigits = 2)

    object NGN : CurrencyUnit("NGN", "566", "Nigerian naira", "Nigerian nairas", fractionDigits = 2)

    object NIO : CurrencyUnit("NIO", "558", "Nicaraguan córdoba", "Nicaraguan córdobas", fractionDigits = 2)

    object NOK : CurrencyUnit("NOK", "578", "Norwegian krone", "Norwegian krones", fractionDigits = 2)

    object NPR : CurrencyUnit("NPR", "524", "Nepalese rupee", "Nepalese rupees", fractionDigits = 2)

    object NZD : CurrencyUnit("NZD", "554", "New Zealand dollar", "New Zealand dollars", fractionDigits = 2)

    object OMR : CurrencyUnit("OMR", "512", "Omani rial", "Omani rials", fractionDigits = 3)

    object PAB : CurrencyUnit("PAB", "590", "Panamanian balboa", "Panamanian balboas", fractionDigits = 2)

    object PEN : CurrencyUnit("PEN", "604", "Peruvian sol", "Peruvian sols", fractionDigits = 2)

    object PGK : CurrencyUnit("PGK", "598", "Papua New Guinean kina", "Papua New Guinean kinas", fractionDigits = 2)

    object PHP : CurrencyUnit("PHP", "608", "Philippine peso[12]", "Philippine peso[12]s", fractionDigits = 2)

    object PKR : CurrencyUnit("PKR", "586", "Pakistani rupee", "Pakistani rupees", fractionDigits = 2)

    object PLN : CurrencyUnit("PLN", "985", "Polish złoty", "Polish złotys", fractionDigits = 2)

    object PYG : CurrencyUnit("PYG", "600", "Paraguayan guaraní", "Paraguayan guaranís", fractionDigits = 0)

    object QAR : CurrencyUnit("QAR", "634", "Qatari riyal", "Qatari riyals", fractionDigits = 2)

    object RON : CurrencyUnit("RON", "946", "Romanian leu", "Romanian leus", fractionDigits = 2)

    object RSD : CurrencyUnit("RSD", "941", "Serbian dinar", "Serbian dinars", fractionDigits = 2)

    object RUB : CurrencyUnit("RUB", "643", "Russian ruble", "Russian rubles", fractionDigits = 2)

    object RWF : CurrencyUnit("RWF", "646", "Rwandan franc", "Rwandan francs", fractionDigits = 0)

    object SAR : CurrencyUnit("SAR", "682", "Saudi riyal", "Saudi riyals", fractionDigits = 2)

    object SBD : CurrencyUnit("SBD", "090", "Solomon Islands dollar", "Solomon Islands dollars", fractionDigits = 2)

    object SCR : CurrencyUnit("SCR", "690", "Seychelles rupee", "Seychelles rupees", fractionDigits = 2)

    object SDG : CurrencyUnit("SDG", "938", "Sudanese pound", "Sudanese pounds", fractionDigits = 2)

    object SEK : CurrencyUnit("SEK", "752", "Swedish krona", "Swedish kronas", fractionDigits = 2)

    object SGD : CurrencyUnit("SGD", "702", "Singapore dollar", "Singapore dollars", fractionDigits = 2)

    object SHP : CurrencyUnit("SHP", "654", "Saint Helena pound", "Saint Helena pounds", fractionDigits = 2)

    object SLE :
        CurrencyUnit(
            "SLE",
            "925",
            "Sierra Leonean leone (new leone)[13][14][15]",
            "Sierra Leonean leone (new leone)[13][14][15]s",
            fractionDigits = 2,
        )

    object SOS : CurrencyUnit("SOS", "706", "Somalian shilling", "Somalian shillings", fractionDigits = 2)

    object SRD : CurrencyUnit("SRD", "968", "Surinamese dollar", "Surinamese dollars", fractionDigits = 2)

    object SSP : CurrencyUnit("SSP", "728", "South Sudanese pound", "South Sudanese pounds", fractionDigits = 2)

    object STN :
        CurrencyUnit("STN", "930", "São Tomé and Príncipe dobra", "São Tomé and Príncipe dobras", fractionDigits = 2)

    object SVC : CurrencyUnit("SVC", "222", "Salvadoran colón", "Salvadoran colóns", fractionDigits = 2)

    object SYP : CurrencyUnit("SYP", "760", "Syrian pound", "Syrian pounds", fractionDigits = 2)

    object SZL : CurrencyUnit("SZL", "748", "Swazi lilangeni", "Swazi lilangenis", fractionDigits = 2)

    object THB : CurrencyUnit("THB", "764", "Thai baht", "Thai bahts", fractionDigits = 2)

    object TJS : CurrencyUnit("TJS", "972", "Tajikistani somoni", "Tajikistani somonis", fractionDigits = 2)

    object TMT : CurrencyUnit("TMT", "934", "Turkmenistan manat", "Turkmenistan manats", fractionDigits = 2)

    object TND : CurrencyUnit("TND", "788", "Tunisian dinar", "Tunisian dinars", fractionDigits = 3)

    object TOP : CurrencyUnit("TOP", "776", "Tongan paʻanga", "Tongan paʻangas", fractionDigits = 2)

    object TRY : CurrencyUnit("TRY", "949", "Turkish lira", "Turkish liras", fractionDigits = 2)

    object TTD :
        CurrencyUnit("TTD", "780", "Trinidad and Tobago dollar", "Trinidad and Tobago dollars", fractionDigits = 2)

    object TWD : CurrencyUnit("TWD", "901", "New Taiwan dollar", "New Taiwan dollars", fractionDigits = 2)

    object TZS : CurrencyUnit("TZS", "834", "Tanzanian shilling", "Tanzanian shillings", fractionDigits = 2)

    object UAH : CurrencyUnit("UAH", "980", "Ukrainian hryvnia", "Ukrainian hryvnias", fractionDigits = 2)

    object UGX : CurrencyUnit("UGX", "800", "Ugandan shilling", "Ugandan shillings", fractionDigits = 0)

    object USD : CurrencyUnit("USD", "840", "US dollar", "US dollars", fractionDigits = 2)

    object USN : CurrencyUnit("USN", "997", "US dollar (next day)", "US dollars (next day)", fractionDigits = 2)

    object UYI :
        CurrencyUnit(
            "UYI",
            "940",
            "Uruguay Peso en Unidades Indexadas (URUIURUI)",
            "Uruguay Peso en Unidades Indexadass (URUIURUI)",
            fractionDigits = 0,
        )

    object UYU : CurrencyUnit("UYU", "858", "Uruguayan peso", "Uruguayan pesos", fractionDigits = 2)

    object UYW : CurrencyUnit("UYW", "927", "Unidad previsional", "Unidad previsionals", fractionDigits = 4)

    object UZS : CurrencyUnit("UZS", "860", "Uzbekistani sum", "Uzbekistani sums", fractionDigits = 2)

    object VED :
        CurrencyUnit("VED", "926", "Venezuelan digital bolívar", "Venezuelan digital bolívars", fractionDigits = 2)

    object VES :
        CurrencyUnit("VES", "928", "Venezuelan sovereign bolívar", "Venezuelan sovereign bolívars", fractionDigits = 2)

    object VND : CurrencyUnit("VND", "704", "Vietnamese đồng", "Vietnamese đồngs", fractionDigits = 0)

    object VUV : CurrencyUnit("VUV", "548", "Vanuatu vatu", "Vanuatu vatus", fractionDigits = 0)

    object WST : CurrencyUnit("WST", "882", "Samoan tala", "Samoan talas", fractionDigits = 2)

    object XAD : CurrencyUnit("XAD", "396", "Arab Accounting Dinar", "Arab Accounting Dinars", fractionDigits = 2)

    object XAF : CurrencyUnit("XAF", "950", "CFA franc", "CFA francs", fractionDigits = 0)

    object XAG : CurrencyUnit("XAG", "961", "Silver troy ounce", "Silver troy ounces")

    object XAU : CurrencyUnit("XAU", "959", "Gold troy ounce", "Gold troy ounces")

    object XBA : CurrencyUnit("XBA", "955", "European Composite Unit (EURCO)", "European Composite Unit (EURCO)s")

    object XBB : CurrencyUnit("XBB", "956", "European Monetary Unit (E.M.U.-6)", "European Monetary Unit (E.M.U.-6)s")

    object XBC :
        CurrencyUnit("XBC", "957", "European Unit of Account 9 (E.U.A.-9)", "European Unit of Account 9 (E.U.A.-9)s")

    object XBD :
        CurrencyUnit(
            "XBD",
            "958",
            "European Unit of Account 17 (E.U.A.-17)",
            "European Unit of Account 17 (E.U.A.-17)s",
        )

    object XCD : CurrencyUnit("XCD", "951", "East Caribbean dollar", "East Caribbean dollars", fractionDigits = 2)

    object XCG : CurrencyUnit("XCG", "532", "Caribbean guilder", "Caribbean guilders", fractionDigits = 2)

    object XDR : CurrencyUnit("XDR", "960", "Special drawing rights", "Special drawing rights")

    object XOF : CurrencyUnit("XOF", "952", "CFA franc", "CFA francs", fractionDigits = 0)

    object XPD : CurrencyUnit("XPD", "964", "Palladium troy ounce", "Palladium troy ounces")

    object XPF : CurrencyUnit("XPF", "953", "CFP franc", "CFP francs", fractionDigits = 0)

    object XPT : CurrencyUnit("XPT", "962", "Platinum troy ounce", "Platinum troy ounces")

    object XSU : CurrencyUnit("XSU", "994", "SUCRE", "SUCREs")

    object XTS : CurrencyUnit("XTS", "963", "RESERVED", "RESERVED")

    object XUA : CurrencyUnit("XUA", "965", "ADB Unit of Account", "ADB Unit of Account")

    // XXX is MONEY

    object YER : CurrencyUnit("YER", "886", "Yemeni rial", "Yemeni rials", fractionDigits = 2)

    object ZAR : CurrencyUnit("ZAR", "710", "South African rand", "South African rands", fractionDigits = 2)

    object ZMW : CurrencyUnit("ZMW", "967", "Zambian kwacha", "Zambian kwachas", fractionDigits = 2)

    object ZWG : CurrencyUnit("ZWG", "924", "Zimbabwe Gold", "Zimbabwe Gold", fractionDigits = 2)
}

//
// Convenience functions for creating currency amounts
//

// TODO: Generate this code with something like JavaPoet (for Kotlin)

fun Amount.Companion.ofCAD(value: Number): Amount = Currency.CAD.amountOf(value, NotScalingPrefix)

fun Amount.Companion.ofCAD(value: BigDecimal): Amount = Currency.CAD.amountOf(value, NotScalingPrefix)

fun Number.CAD() = Amount.ofCAD(this)

fun BigDecimal.CAD() = Amount.ofCAD(this)

fun Amount.Companion.ofEUR(value: Number): Amount = Currency.EUR.amountOf(value, NotScalingPrefix)

fun Amount.Companion.ofEUR(value: BigDecimal): Amount = Currency.EUR.amountOf(value, NotScalingPrefix)

fun Number.EUR() = Amount.ofEUR(this)

fun BigDecimal.EUR() = Amount.ofEUR(this)

fun Amount.Companion.ofGBP(value: Number): Amount = Currency.GBP.amountOf(value, NotScalingPrefix)

fun Amount.Companion.ofGBP(value: BigDecimal): Amount = Currency.GBP.amountOf(value, NotScalingPrefix)

fun Number.GBP() = Amount.ofGBP(this)

fun BigDecimal.GBP() = Amount.ofGBP(this)

fun Amount.Companion.ofUSD(value: Number): Amount = Currency.USD.amountOf(value, NotScalingPrefix)

fun Amount.Companion.ofUSD(value: BigDecimal): Amount = Currency.USD.amountOf(value, NotScalingPrefix)

fun Number.USD() = Amount.ofUSD(this)

fun BigDecimal.USD() = Amount.ofUSD(this)
