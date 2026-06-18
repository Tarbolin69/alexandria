package com.libreria.alexandria.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

// Un renderizador Markdown para la respuesta
// de DeepSeek AI

@Composable
fun MarkdownText(
    content: String,
    modifier: Modifier = Modifier
) {
    val lines = content.split("\n")
    val paragraphs = mutableListOf<List<String>>()
    var currentPara = mutableListOf<String>()

    for (line in lines) {
        if (line.isBlank()) {
            if (currentPara.isNotEmpty()) {
                paragraphs.add(currentPara.toList())
                currentPara = mutableListOf()
            }
        } else {
            currentPara.add(line)
        }
    }
    if (currentPara.isNotEmpty()) {
        paragraphs.add(currentPara.toList())
    }

    Column(modifier = modifier) {
        paragraphs.forEachIndexed { index, para ->
            para.forEach { line ->
                when {
                    line.trimStart().startsWith("# ") -> {
                        Text(
                            text = line.trimStart().removePrefix("# ").trim(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    line.trimStart().startsWith("## ") -> {
                        Text(
                            text = line.trimStart().removePrefix("## ").trim(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                        )
                    }
                    line.trimStart().startsWith("- ") || line.trimStart().startsWith("* ") -> {
                        val bulletText = line.trimStart().removePrefix("- ").removePrefix("* ")
                        Row(modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)) {
                            Text("\u2022")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(renderInlineMarkdown(bulletText))
                        }
                    }
                    line.trimStart().matches(Regex("^\\d+\\.\\s.*")) -> {
                        val match = Regex("^(\\d+\\.\\s)(.*)").find(line.trimStart())
                        if (match != null) {
                            val number = match.groupValues[1]
                            val rest = match.groupValues[2]
                            Row(modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)) {
                                Text(number)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(renderInlineMarkdown(rest))
                            }
                        }
                    }
                    else -> {
                        Text(
                            renderInlineMarkdown(line),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }
            if (index < paragraphs.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun renderInlineMarkdown(text: String) = buildAnnotatedString {
    var i = 0
    while (i < text.length) {
        when {
            text.startsWith("**", i) -> {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else {
                    append(text[i])
                    i++
                }
            }
            text.startsWith("*", i) && !text.startsWith("**", i) -> {
                val end = text.indexOf("*", i + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else {
                    append(text[i])
                    i++
                }
            }
            else -> {
                append(text[i])
                i++
            }
        }
    }
}
