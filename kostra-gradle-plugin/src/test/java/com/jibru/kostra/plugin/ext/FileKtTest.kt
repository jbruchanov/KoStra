package com.jibru.kostra.plugin.ext

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class FileKtTest {
    @Test
    fun `relativeTo When Unix`() {
        assertThat(File("/usr/test/root/subfolder/item.xml").relativeTo(File("/usr/test/root"))).isEqualTo("subfolder/item.xml")
        assertThat(File("/usr/test/root/subfolder/item.xml").relativeTo(File("/usr/test/root/"))).isEqualTo("subfolder/item.xml")
    }

    @Test
    fun `relativeTo When Unix and ignoreCase`() {
        assertThat(File("/usr/test/ROOT/subfolder/item.xml").relativeTo(File("/USR/test/root"), ignoreCase = true)).isEqualTo("subfolder/item.xml")
        assertThat(File("/USR/test/root/subfolder/item.xml").relativeTo(File("/usr/TEST/root/"), ignoreCase = true)).isEqualTo("subfolder/item.xml")
    }

    @Test
    fun `relativeTo When Windows`() {
        assertThat(File("""C:\usr\test\root\subfolder\item.xml""").relativeTo(File("""C:\usr\test\root"""))).isEqualTo("subfolder/item.xml")
        assertThat(File("""C:\usr\test\root\subfolder\item.xml""").relativeTo(File("""C:\usr\test\root\"""))).isEqualTo("subfolder/item.xml")
    }

    @Test
    fun `relativeTo When Windows and ignoreCase`() {
        assertThat(File("""C:\USR\test\root\subfolder\item.xml""").relativeTo(File("""C:\usr\TEST\root"""), ignoreCase = true)).isEqualTo("subfolder/item.xml")
        assertThat(File("""C:\usr\TEST\root\subfolder\item.xml""").relativeTo(File("""C:\USR\test\root\"""), ignoreCase = true)).isEqualTo("subfolder/item.xml")
    }
}
