import com.jibru.kostra.K
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Plurals
import com.jibru.kostra.Resources
import com.jibru.kostra.icu.FixedDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class SampleTest {
    companion object {
        private const val CS = "cs"
        private const val EN = "en"
    }

    @Test
    fun string() {
        assertEquals("Add", Resources.string.get(K.string.actionAdd, KQualifiers(EN)))
        assertEquals("Přidat", Resources.string.get(K.string.actionAdd, KQualifiers(CS)))
    }

    @Test
    fun plurals() {
        assertEquals("0 bugs", plural(K.plural.bugX, EN, 0))
        assertEquals("1 bug", plural(K.plural.bugX, EN, 1))
        assertEquals("1.5 bugs", plural(K.plural.bugX, EN, 1.5))
        assertEquals("2 bugs", plural(K.plural.bugX, EN, 2))

        assertEquals("0 brouků", plural(K.plural.bugX, CS, 0))
        assertEquals("1 brouk", plural(K.plural.bugX, CS, 1))
        assertEquals("1.5 brouku", plural(K.plural.bugX, CS, 1.5))
        assertEquals("2 brouci", plural(K.plural.bugX, CS, 2))
        assertEquals("3 brouci", plural(K.plural.bugX, CS, 3))
        assertEquals("10 brouků", plural(K.plural.bugX, CS, 10))
    }

    @Test
    fun ordinals() {
        assertEquals("0th day", ordinal(K.plural.dayX, EN, 0))
        assertEquals("1st day", ordinal(K.plural.dayX, EN, 1))
        assertEquals("1.5th day", ordinal(K.plural.dayX, EN, 1.5))
        assertEquals("2nd day", ordinal(K.plural.dayX, EN, 2))
        assertEquals("3rd day", ordinal(K.plural.dayX, EN, 3))
        assertEquals("10th day", ordinal(K.plural.dayX, EN, 10))
        assertEquals("21st day", ordinal(K.plural.dayX, EN, 21))
        assertEquals("22nd day", ordinal(K.plural.dayX, EN, 22))
        assertEquals("23rd day", ordinal(K.plural.dayX, EN, 23))

        assertEquals("0. den", ordinal(K.plural.dayX, CS, 0))
        assertEquals("1. den", ordinal(K.plural.dayX, CS, 1))
        assertEquals("1.5. den", ordinal(K.plural.dayX, CS, 1.5))
        assertEquals("2. den", ordinal(K.plural.dayX, CS, 2))
    }

    private fun plural(key: PluralResourceKey, locale: String, quantity: Number) =
        Resources.plural.get(key, KQualifiers(locale), FixedDecimal(quantity.toDouble()), Plurals.Type.Plurals, quantity)

    private fun ordinal(key: PluralResourceKey, locale: String, quantity: Number) =
        Resources.plural.get(key, KQualifiers(locale), FixedDecimal(quantity.toDouble()), Plurals.Type.Ordinals, quantity)
}
