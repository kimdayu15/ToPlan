package com.gems.toplan.ui.screen

import TodoViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.gems.toplan.R
import com.gems.toplan.data.TodoItem
import com.gems.toplan.navigation.NavigationItems

@Composable
fun DoScreen(
    viewModel: TodoViewModel,
    navController: NavHostController
) {
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    var visibleState by remember { mutableStateOf(true) }
    val visibleTasks = if (visibleState) tasks else tasks.filter { it.done }

    Scaffold(
        topBar = {
            TaskTopAppBar(
                visibleState = visibleState,
                onVisibilityToggle = { visibleState = !visibleState },
                doneCount = tasks.count { it.done }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(NavigationItems.ItemScreen.createRoute(id = "new"))
                },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add a new task")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (tasks.isEmpty() && !isRefreshing) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(visibleTasks) { task ->
                        EachTask(task = task, navController) { updatedTask ->
                            viewModel.toggleTaskCompletion(updatedTask)
                        }
                    }
                }
            }

            if (isRefreshing) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
fun EachTask(
    task: TodoItem.Task,
    navController: NavHostController,
    onTaskCheckedChange: (TodoItem.Task) -> Unit
) {
    var checked by remember { mutableStateOf(task.done) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { isChecked ->
                    checked = isChecked
                    onTaskCheckedChange(task.copy(done = isChecked))
                }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.text,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                task.deadline?.let {
                    Text(
                        text = "Due: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            IconButton(
                onClick = {
                    navController.navigate(NavigationItems.ItemScreen.createRoute(task.id))
                }
            ) {
                Icon(Icons.Default.Info, contentDescription = "Details")
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "No tasks available. Add one!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTopAppBar(
    visibleState: Boolean,
    onVisibilityToggle: () -> Unit,
    doneCount: Int
) {
    LargeTopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "My Tasks",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Done - $doneCount",
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        painter = if (visibleState) {
                            painterResource(id = R.drawable.ic_visibility)
                        } else {
                            painterResource(id = R.drawable.ic_no_visibility)
                        },
                        contentDescription = "Toggle Visibility",
                        tint = Color(0xFF007AFF)
                    )
                }
            }
        }
    )
}
