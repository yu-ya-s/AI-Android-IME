package com.zeroconvert.ime

/**
 * Plain-text user dictionary backup: one "reading<TAB>word" entry per line.
 * Reading takes only the first two columns, so exports from Google Japanese Input / Mozc and
 * Gboard (reading, word, then part of speech or locale) can be imported as well.
 */
object UserDictionaryFile {
    const val MIME_TYPE = "text/plain"
    const val DEFAULT_FILE_NAME = "zeroconvert-user-dictionary.txt"

    fun encode(entries: List<UserDictEntry>): String = buildString {
        append("# ZeroConvert-IME user dictionary\n")
        for (entry in entries) {
            append(entry.reading.replace('\t', ' ').replace('\n', ' '))
            append('\t')
            append(entry.word.replace('\t', ' ').replace('\n', ' '))
            append('\n')
        }
    }

    // Files saved by Windows editors may start with a byte order mark.
    private val BYTE_ORDER_MARK = 0xFEFF.toChar().toString()

    fun decode(text: String): List<UserDictEntry> =
        text.removePrefix(BYTE_ORDER_MARK)
            .lineSequence()
            .map { it.trimEnd('\r') }
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .mapNotNull { line ->
                val columns = line.split('\t')
                if (columns.size < 2) return@mapNotNull null
                val reading = columns[0].trim()
                val word = columns[1].trim()
                if (reading.isEmpty() || word.isEmpty()) null else UserDictEntry(reading, word)
            }
            .toList()
}
