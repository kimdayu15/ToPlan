package com.gems.toplan.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.gems.toplan.R
import com.gems.toplan.data.TodoItem
import com.gems.toplan.ui.model.Importance
import com.gems.toplan.ui.model.TodoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoScreen(
    newTask: () -> Unit,
    navController: NavHostController,
    viewModel: TodoViewModel = TodoViewModel(),
    updateTask: (TodoItem.Task) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val snackMessage = viewModel.snackbarMessage.collectAsState()
    val isVisible = viewModel.isRefreshing.collectAsState()
    val checkedTasks = viewModel.checkedTasks.collectAsState()
    val tasksList = viewModel.tasks.collectAsState()
    val snackHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val createTask = remember { mutableStateOf(false) }

    LaunchedEffect(snackMessage.value) {
        snackMessage.value?.let {
            scope.launch {
                snackHostState.showSnackbar(
                    message = it,
                    actionLabel = "OK",
                )
            }
            viewModel.snackError()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.refreshTasks()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    viewModel.observeNetworkChanges(LocalContext.current)

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackHostState) },
            topBar = {
                LargeTopAppBar(
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Мои дела",
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (scrollBehavior.state.collapsedFraction < 0.5f) {
                                    Text(
                                        text = "Done - ${checkedTasks.value}",
                                        fontSize = 20.sp,
                                        color = Color(0x4D000000)
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.updateVisibility() }) {
                                val eyeIcon = if (isVisible.value) {
                                    R.drawable.ic_no_visibility
                                } else {
                                    R.drawable.ic_visibility
                                }
                                Icon(
                                    painter = painterResource(eyeIcon),
                                    contentDescription = "Visibility",
                                    tint = Color(0xFF007AFF)
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (!createTask.value) {
                            createTask.value = true
                            viewModel.refreshTasks()
                            newTask()
                        }
                    },
                    containerColor = Color(0xFF007AFF),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        ) { innerPadding ->
            Card(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(5.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(tasksList.value, key = { _, task -> task.id }) { _, task ->
                        if (!isVisible.value && !task.done || isVisible.value) {
                            EachTask(task, updateTask, viewModel, navController)
                        }
                    }
                }
            }
        }
}


@Composable
fun EachTask(
    task: TodoItem.Task,
    updateTask: (TodoItem.Task) -> Unit,
    viewModel: TodoViewModel,
    navController: NavHostController
) {
    val strike = if (task.done) TextDecoration.LineThrough else TextDecoration.None
    val textColor = when {
        task.done -> Color.Gray
        task.importance == Importance.HIGH -> Color.Red
        else -> Color.Black
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.done,
                onCheckedChange = { isChecked ->
                    val updatedTask = task.copy(done = isChecked)
                    updateTask(updatedTask)
                    viewModel.refreshTaskUi(updatedTask)
                },
                colors = CheckboxDefaults.colors(textColor)
            )

            Icon(
                painter = painterResource(
                    if (task.importance == Importance.HIGH) R.drawable.important else R.drawable.ic_low
                ),
                contentDescription = null,
                tint = if (task.importance == Importance.HIGH) Color.Red else Color.Black
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.text,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = strike,
                    color = textColor
                )

                task.deadline?.let {
                    Text(
                        text = "$it",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            IconButton(onClick = {
                navController.navigate("item_screen/${task.id}")
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = "Details",
                    tint = Color(0xFF313131)
                )
            }
        }
    }
}
