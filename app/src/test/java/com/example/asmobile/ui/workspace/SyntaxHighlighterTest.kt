package com.example.asmobile.ui.workspace

import androidx.compose.material3.lightColorScheme
import org.junit.Assert.assertEquals
import org.junit.Test

class SyntaxHighlighterTest {

    @Test
    fun testKotlinHighlighting() {
        val code = "package com.example\nfun main() { val x = 10 }"
        val colorScheme = lightColorScheme()
        val highlighted = SyntaxHighlighter.highlight(code, "kt", colorScheme)
        
        // Check if keywords are highlighted
        // Note: AnnotatedString.spanStyles returns a list of ranges
        // "package" is at index 0, length 7
        // "fun" is at index 20, length 3
        // "val" is at index 33, length 3
        
        val keywordSpans = highlighted.spanStyles.filter { it.item.fontWeight == androidx.compose.ui.text.font.FontWeight.Bold }
        assertEquals(3, keywordSpans.size)
    }

    @Test
    fun testJavaHighlighting() {
        val code = "public class Test { int x = 1; }"
        val colorScheme = lightColorScheme()
        val highlighted = SyntaxHighlighter.highlight(code, "java", colorScheme)
        
        val keywordSpans = highlighted.spanStyles.filter { it.item.fontWeight == androidx.compose.ui.text.font.FontWeight.Bold }
        assertEquals(3, keywordSpans.size) // public, class, int
    }

    @Test
    fun testXmlHighlighting() {
        val code = "<LinearLayout android:id=\"@+id/main\" />"
        val colorScheme = lightColorScheme()
        val highlighted = SyntaxHighlighter.highlight(code, "xml", colorScheme)
        
        // Check total spans
        assertEquals("Should have at least one span for the tag", 1, highlighted.spanStyles.size)
        
        // Tags are usually primary color
        val tagSpans = highlighted.spanStyles.filter { it.item.color == colorScheme.primary }
        assertEquals(1, tagSpans.size)
    }
}
