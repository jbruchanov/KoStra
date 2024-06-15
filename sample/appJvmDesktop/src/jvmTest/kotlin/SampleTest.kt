import com.jibru.kostra.KQualifiers
import com.sample.app.K
import com.sample.app.Resources
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SampleTest {
    @Test
    fun string() {
        assertEquals("Add", Resources.string.get(K.string.action_add, KQualifiers("en")))
        assertEquals("Přidat", Resources.string.get(K.string.action_add, KQualifiers("cs")))
    }
}
