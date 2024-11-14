package com.gems.toplan.ui.screen

import TodoViewModel
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gems.toplan.data.TodoItem
import com.gems.toplan.navigation.NavigationItems
import java.util.*

@Composable
fun ItemScreen(
    navController: NavController,
    viewModel: TodoViewModel,
    taskId: String? = null
) {
    var noteText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedDate by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    var selectedImportance by remember { mutableStateOf(Importance.NONE) }

    // Fetch task data if taskId is provided
    LaunchedEffect(taskId) {
        taskId?.let {
            val task = viewModel.getTaskById(it)
            task?.let {
                noteText = TextFieldValue(task.note)
                selectedDate = task.deadline?.let { formatDate(it) } ?: ""
                selectedImportance = Importance.values()[task.importance]
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Task Details") },
            actions = {
                // Save or Update task
                IconButton(onClick = {
                    if (noteText.text.isNotEmpty()) {
                        val task = TodoItem.Task(
                            id = taskId ?: UUID.randomUUID().toString(),
                            text = noteText.text,
                            importance = selectedImportance.ordinal,
                            deadline = if (selectedDate.isNotEmpty()) formatDate(selectedDate) else null,
                            done = false,
                            createdAt = System.currentTimeMillis(),
                            lastUpdatedBy = System.currentTimeMillis().toString()
                        )

                        if (taskId == null) {
                            viewModel.addTask(task)
                        } else {
                            viewModel.updateTask(task)
                        }

                        navController.navigate(NavigationItems.DoScreen.route)
                    }
                }) {
                    Text("Save")
                }
            }
        )
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                placeholder = { Text("Write your note here") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 130.dp),
                maxLines = Int.MAX_VALUE
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Importance")
                ImportanceSelector(
                    selectedImportance = selectedImportance,
                    onImportanceSelected = { selectedImportance = it }
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Deadline")
                if (selectedDate.isNotEmpty()) {
                    Text("Selected Deadline: $selectedDate", fontSize = 16.sp)
                }
                Switch(
                    checked = checked,
                    onCheckedChange = { isChecked ->
                        checked = isChecked
                        if (isChecked) {
                            showDatePickerDialog(navController.context) { date ->
                                selectedDate = date
                            }
                        } else {
                            selectedDate = ""
                        }
                    }
                )
            }
        }
    }
}

enum class Importance { NONE, LOW, HIGH }

@Composable
fun ImportanceSelector(
    selectedImportance: Importance,
    onImportanceSelected: (Importance) -> Unit
) {
    Row {
        Button(onClick = { onImportanceSelected(Importance.LOW) }) {
            Text("Low", color = if (selectedImportance == Importance.LOW) Color.White else Color.Black)
        }
        Button(onClick = { onImportanceSelected(Importance.NONE) }) {
            Text("None", color = if (selectedImportance == Importance.NONE) Color.White else Color.Black)
        }
        Button(onClick = { onImportanceSelected(Importance.HIGH) }) {
            Text("High", color = if (selectedImportance == Importance.HIGH) Color.White else Color.Red)
        }
    }
}


fun formatDate(date: Date): String {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
}

private fun showDatePickerDialog(
    context: android.content.Context, onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context, { _, selectedYear, selectedMonth, selectedDay ->
            val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(date)
        }, year, month, day
    )
    datePickerDialog.show()
}
