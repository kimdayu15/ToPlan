package com.gems.toplan.ui.screen

import com.gems.toplan.ui.model.TodoViewModel
import android.app.DatePickerDialog
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.gems.toplan.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gems.toplan.ui.components.Importance
import com.gems.toplan.data.TodoItem
import com.gems.toplan.data.TodoWorkRequest
import com.gems.toplan.navigation.NavigationItems
import com.gems.toplan.ui.theme.ToPlanTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    navController: NavController, viewModel: TodoViewModel = TodoViewModel(), taskId: String
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val text = remember { mutableStateOf("") }
    val importanceS = remember { mutableStateOf(Importance.NONE) }
    val deadline: MutableState<Date?> = remember { mutableStateOf(null) }
    val createdAt: MutableState<Long> = remember { mutableLongStateOf(System.currentTimeMillis()) }
    val isDone = remember { mutableStateOf(false) }
    var taskId = taskId

    if (taskId != "") {
        viewModel.getTask(taskId) {
            text.value = it?.text ?: ""
            importanceS.value = it?.importance ?: Importance.NONE
            deadline.value = it?.deadline?.let { Date(it) }
            createdAt.value = it?.createdAt ?: Date().time
            taskId = it?.id ?: ""
            isDone.value = it?.done == true
        }
    }

    ToPlanTheme {
        Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
            TopAppBar(modifier = Modifier.padding(10.dp, 0.dp), title = {
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    Text(text = "Save", modifier = Modifier.clickable {
                        if (taskId != "") {
                            viewModel.refreshTaskId(
                                taskId,
                                TodoWorkRequest(
                                    status = "ok",
                                    taskElement = TodoItem.Task(
                                        id = taskId,
                                        text = text.value,
                                        importance = importanceS.value,
                                        deadline = deadline.value?.time,
                                        createdAt = createdAt.value,
                                        done = isDone.value,
                                        changedAt = System.currentTimeMillis(),
                                        lastUpdatedBy = ""
                                    )
                                )
                            )
                            navController.navigate(NavigationItems.DoScreen.route)
                        } else {
                            viewModel.addTask(
                                text.value,
                                importanceS.value,
                                deadline.value?.time
                            )
                            navController.navigate(NavigationItems.DoScreen.route)
                        }

                    })
                }
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.navigate(NavigationItems.DoScreen.route)
                }) {
                    Icon(
                        imageVector = Icons.Default.Close, contentDescription = null
                    )
                }
            })
        }) { values ->
            Column(
                modifier = Modifier
                    .padding(values)
                    .padding(10.dp)
            ) {
                OutlinedTextField(
                    value = text.value,
                    onValueChange = { text.value = it },
                    placeholder = { Text("Write your note here") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 130.dp),
                    maxLines = Int.MAX_VALUE,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    var oneSelected = remember { mutableStateOf(false) }
                    var twoSelected = remember { mutableStateOf(false) }
                    var threeSelected = remember { mutableStateOf(false) }
                    val color = remember { mutableStateOf(Color.Black) }
                    importanceUpdate(
                        importanceS.value,
                        colorImportance = color
                    )
                    Text("Importance")
                    Card(modifier = Modifier.height(40.dp), shape = RoundedCornerShape(47)) {
                        Row {
                            Button(
                                onClick = {
                                    oneSelected.value = true
                                    twoSelected.value = false
                                    threeSelected.value = false
                                    importanceS.value = Importance.LOW
                                    importanceUpdate(
                                        importanceS.value,
                                        colorImportance = color
                                    )

                                }, colors = ButtonDefaults.buttonColors(
                                    if (oneSelected.value) {
                                        Color.White
                                    } else {
                                        Color.Transparent
                                    }
                                )
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_low),
                                    contentDescription = null
                                )
                            }
                            Button(
                                onClick = {
                                    oneSelected.value = false
                                    twoSelected.value = true
                                    threeSelected.value = false
                                    importanceS.value = Importance.NONE
                                    importanceUpdate(
                                        importanceS.value,
                                        colorImportance = color
                                    )

                                }, colors = ButtonDefaults.buttonColors(
                                    if (twoSelected.value) {
                                        Color.White
                                    } else {
                                        Color.Transparent
                                    }
                                )
                            ) {
                                Text("NO", color = Color.Black)
                            }
                            Button(
                                onClick = {
                                    oneSelected.value = false
                                    twoSelected.value = false
                                    threeSelected.value = true
                                    importanceS.value = Importance.HIGH
                                    importanceUpdate(
                                        importanceS.value,
                                        colorImportance = color
                                    )

                                }, colors = ButtonDefaults.buttonColors(
                                    if (threeSelected.value) {
                                        Color.White
                                    } else {
                                        Color.Transparent
                                    }
                                )
                            ) {
                                Text(
                                    "!!", color = if (threeSelected.value) {
                                        Color.Red
                                    } else {
                                        Color.Gray
                                    }, fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
                val isToggled = remember { mutableStateOf(false) }
                val initDateSetter = remember { mutableStateOf(false) }
                val dateSelected = remember { mutableStateOf("") }
                val calendar = Calendar.getInstance()

                if (deadline.value != null) {
                    dateSelected.value = viewModel.simpleDateFormatter(deadline.value!!)
                    isToggled.value = true
                    initDateSetter.value = true
                }

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    LocalContext.current, { _, selectedYear, selectedMonth, selectedDay ->
                        calendar.set(selectedYear, selectedMonth, selectedDay)
                        dateSelected.value = viewModel.simpleDateFormatter(calendar.time)
                        deadline.value = calendar.time
                        isToggled.value = true
                        initDateSetter.value = true
                    }, year, month, day
                )

                LaunchedEffect(isToggled.value && !initDateSetter.value) {
                    if (isToggled.value && !initDateSetter.value) {
                        datePickerDialog.show()
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Deadline")
                        if (isToggled.value) {
                            Text(dateSelected.value, color = Color.Black)
                        }
                    }
                    Switch(checked = isToggled.value, onCheckedChange = {
                        if (it) {
                            isToggled.value = true
                            initDateSetter.value = false
                        } else {
                            isToggled.value = false
                            dateSelected.value = ""
                            deadline.value = null
                        }
                    })
                }
                val deleteColor = remember { mutableStateOf(Color.Gray) }
                if (text.value != "") {
                    deleteColor.value = Color.Red
                } else {
                    deleteColor.value = Color.Gray
                }
                Button(
                    onClick = {
                        viewModel.deleteTaskId(taskId)
                        navController.navigate(NavigationItems.DoScreen.route)
                    }, modifier = Modifier.fillMaxWidth().padding(3.dp), colors = ButtonDefaults.buttonColors(
                        Color.White)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = null,
                        tint = deleteColor.value
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Delete", color = deleteColor.value)
                }
            }
        }
    }
}


fun importanceUpdate(
    selectedImportance: Importance,
    colorImportance: MutableState<Color>
) {
    if (selectedImportance == Importance.NONE) {
        colorImportance.value = Color.Black
    } else if (selectedImportance == Importance.LOW) {
        colorImportance.value = Color.Black
    } else if (selectedImportance == Importance.HIGH) {
        colorImportance.value = Color.Red
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true)
@Composable
fun ItemPreview() {
    ItemScreen(rememberNavController(), taskId = "1")
}