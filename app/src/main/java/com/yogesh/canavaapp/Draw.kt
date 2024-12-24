package com.yogesh.canavaapp

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorScreen() {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var undoStack by remember { mutableStateOf(listOf<TextFieldValue>()) }
    var redoStack by remember { mutableStateOf(listOf<TextFieldValue>()) }
    var fontSize by remember { mutableStateOf(16f) }
    var expanded by remember { mutableStateOf(false) }
    var selectedFont by remember { mutableStateOf("Default") }
    var position by remember { mutableStateOf(Offset(100f, 100f)) }
    var isBold by remember { mutableStateOf(false) }
    var isUnderlined by remember { mutableStateOf(false) }

    val fonts = listOf("Default", "Serif", "Monospace", "Cursive")

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Undo and Redo Buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = {
                                        if (undoStack.isNotEmpty()) {
                                            redoStack = listOf(textFieldValue) + redoStack
                                            textFieldValue = undoStack.first()
                                            undoStack = undoStack.drop(1)
                                        }
                                    },
                                    enabled = undoStack.isNotEmpty()
                                ) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Undo")
                                }
                                Text("Undo", textAlign = TextAlign.Center, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(1.dp))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = {
                                        if (redoStack.isNotEmpty()) {
                                            undoStack = listOf(textFieldValue) + undoStack
                                            textFieldValue = redoStack.first()
                                            redoStack = redoStack.drop(1)
                                        }
                                    },
                                    enabled = redoStack.isNotEmpty()
                                ) {
                                    Icon(Icons.Default.ArrowForward, contentDescription = "Redo")
                                }
                                Text("Redo", textAlign = TextAlign.Center, fontSize = 12.sp)
                            }
                        }

                        // Font Dropdown Menu
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Font Style", fontSize = 14.sp)
                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Font Options"
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    fonts.forEach { font ->
                                        DropdownMenuItem(
                                            text = { Text(font) },
                                            onClick = {
                                                selectedFont = font
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Font Size Control
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Font Size", fontSize = 14.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { if (fontSize > 12) fontSize -= 2f }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    contentDescription = "Decrease Font Size"
                                )
                            }

                            Text(
                                text = "${fontSize.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            IconButton(onClick = { if (fontSize < 32f) fontSize += 2f }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Increase Font Size"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bold and Underline Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Bold Button
                        ElevatedButton(onClick = {
                            val selection = textFieldValue.selection
                            if (!selection.collapsed) {
                                val start = selection.start
                                val end = selection.end
                                val newText = textFieldValue.text
                                val boldedText = newText.substring(0, start) +
                                        "${newText.substring(start, end)}" +
                                        newText.substring(end)
                                textFieldValue = textFieldValue.copy(
                                    text = boldedText,
                                    selection = TextRange(start, start + boldedText.length)
                                )
                                isBold = true
                            }
                        }) {
                            Text("Bold")
                        }

                        // Underline Button
                        ElevatedButton(onClick = {
                            val selection = textFieldValue.selection
                            if (!selection.collapsed) {
                                val start = selection.start
                                val end = selection.end
                                val newText = textFieldValue.text
                                val underlinedText = newText.substring(0, start) +
                                        "${newText.substring(start, end)}" +
                                        newText.substring(end)
                                textFieldValue = textFieldValue.copy(
                                    text = underlinedText,
                                    selection = TextRange(start, start + underlinedText.length)
                                )
                                isUnderlined = true
                            }
                        }) {
                            Text("Underline")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Text Input Field
                    TextField(
                        value = textFieldValue,
                        onValueChange = { newValue ->
                            undoStack = listOf(textFieldValue) + undoStack
                            textFieldValue = newValue
                            redoStack = listOf()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .padding(8.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = fontSize.sp,
                            fontFamily = when (selectedFont) {
                                "Serif" -> androidx.compose.ui.text.font.FontFamily.Serif
                                "Monospace" -> androidx.compose.ui.text.font.FontFamily.Monospace
                                "Cursive" -> androidx.compose.ui.text.font.FontFamily.Cursive
                                else -> androidx.compose.ui.text.font.FontFamily.Default
                            },
                            fontWeight = if (isBold) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
                            textDecoration = if (isUnderlined) androidx.compose.ui.text.style.TextDecoration.Underline else null
                        ),
                        placeholder = { Text("Enter Your Text") },
                        singleLine = false
                    )

                    // Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    position = position.copy(
                                        x = position.x + dragAmount.x,
                                        y = position.y + dragAmount.y
                                    )
                                }
                            }
                    ) {
                        val lines = textFieldValue.text.split("\n")
                        val lineHeight = fontSize.sp.toPx() + 4

                        lines.forEachIndexed { index, line ->
                            drawContext.canvas.nativeCanvas.drawText(
                                line,
                                position.x,
                                position.y + index * lineHeight,
                                Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    textSize = fontSize.sp.toPx()
                                    typeface = when (selectedFont) {
                                        "Serif" -> Typeface.SERIF
                                        "Monospace" -> Typeface.MONOSPACE
                                        "Cursive" -> Typeface.create("cursive", Typeface.NORMAL)
                                        else -> Typeface.DEFAULT
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add Text Button
                    ElevatedButton(
                        onClick = {
                            undoStack = listOf(textFieldValue) + undoStack
                            val currentText = textFieldValue.text
                            val newText = "$currentText\n"
                            textFieldValue = textFieldValue.copy(
                                text = newText,
                                selection = TextRange(newText.length)
                            )
                            redoStack = listOf()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Add Text")
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTextEditorScreen() {
    TextEditorScreen()
}

