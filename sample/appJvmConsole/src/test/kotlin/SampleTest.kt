import com.jibru.kostra.K
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.Resources
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SampleTest {
    @Test
    fun string() {
        assertEquals("Add", Resources.string.get(K.string.actionAdd, KQualifiers("en")))
        assertEquals("Přidat", Resources.string.get(K.string.actionAdd, KQualifiers("cs")))
    }
}
