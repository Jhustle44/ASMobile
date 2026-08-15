package com.example.asmobile.ui.workspace

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import java.util.regex.Pattern

object SyntaxHighlighter {
    private val KOTLIN_KEYWORDS = listOf(
        "as", "as?", "break", "class", "continue", "do", "else", "false", "for", "fun",
        "if", "in", "interface", "is", "null", "object", "package", "return", "super",
        "this", "throw", "true", "try", "typealias", "val", "var", "when", "while",
    )

    private val JAVA_KEYWORDS = listOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
        "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
        "finally", "float", "for", "goto", "if", "implements", "import", "instanceof",
        "int", "interface", "long", "native", "new", "package", "private", "protected",
        "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized",
        "this", "throw", "throws", "transient", "try", "void", "volatile", "while",
    )

    fun highlight(text: String, extension: String, colorScheme: ColorScheme): AnnotatedString {
        return buildAnnotatedString {
            when (extension.lowercase()) {
                "kt", "kts" -> highlightCode(text, KOTLIN_KEYWORDS, colorScheme)
                "java" -> highlightCode(text, JAVA_KEYWORDS, colorScheme)
                "xml" -> highlightXml(text, colorScheme)
                else -> append(text)
            }
        }
    }

    private fun AnnotatedString.Builder.highlightCode(text: String, keywords: List<String>, colorScheme: ColorScheme) {
        val keywordPattern = "\\b(?:${keywords.joinToString("|")})\\b"
        val stringPattern = "\"[^\"]*\""
        val commentPattern = "//.*|/\\*.*?\\*/"
        val numberPattern = "\\b\\d+\\b"

        val combinedPattern = Pattern.compile(
            "($keywordPattern)|($stringPattern)|($commentPattern)|($numberPattern)"
        )
        val matcher = combinedPattern.matcher(text)

        var lastEnd = 0
        while (matcher.find()) {
            append(text.substring(lastEnd, matcher.start()))
            
            when {
                matcher.group(1) != null -> { // Keyword
                    withStyle(SpanStyle(color = colorScheme.primary, fontWeight = FontWeight.Bold)) {
                        append(matcher.group())
                    }
                }
                matcher.group(2) != null -> { // String
                    withStyle(SpanStyle(color = colorScheme.tertiary)) {
                        append(matcher.group())
                    }
                }
                matcher.group(3) != null -> { // Comment
                    withStyle(SpanStyle(color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f))) {
                        append(matcher.group())
                    }
                }
                matcher.group(4) != null -> { // Number
                    withStyle(SpanStyle(color = colorScheme.secondary)) {
                        append(matcher.group())
                    }
                }
            }
            lastEnd = matcher.end()
        }
        append(text.substring(lastEnd))
    }

    private fun AnnotatedString.Builder.highlightXml(text: String, colorScheme: ColorScheme) {
        val tagPattern = "<[^>]+>"
        val attributePattern = "\\s+[a-zA-Z0-9:-]+="
        val stringPattern = "\"[^\"]*\""
        val commentPattern = "<!--.*?-->"

        val combinedPattern = Pattern.compile(
            "($tagPattern)|($attributePattern)|($stringPattern)|($commentPattern)"
        )
        val matcher = combinedPattern.matcher(text)

        var lastEnd = 0
        while (matcher.find()) {
            append(text.substring(lastEnd, matcher.start()))
            
            when {
                matcher.group(1) != null -> { // Tag
                    val tag = matcher.group()
                    if (tag.startsWith("<!--")) {
                        withStyle(SpanStyle(color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f))) {
                            append(tag)
                        }
                    } else {
                        withStyle(SpanStyle(color = colorScheme.primary)) {
                            append(tag)
                        }
                    }
                }
                matcher.group(2) != null -> { // Attribute
                    withStyle(SpanStyle(color = colorScheme.secondary)) {
                        append(matcher.group())
                    }
                }
                matcher.group(3) != null -> { // String
                    withStyle(SpanStyle(color = colorScheme.tertiary)) {
                        append(matcher.group())
                    }
                }
                matcher.group(4) != null -> { // Comment
                    withStyle(SpanStyle(color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f))) {
                        append(matcher.group())
                    }
                }
            }
            lastEnd = matcher.end()
        }
        append(text.substring(lastEnd))
    }
}
