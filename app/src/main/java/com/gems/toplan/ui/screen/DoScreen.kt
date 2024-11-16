package com.gems.toplan.ui.screen

import com.gems.toplan.ui.model.TodoViewModel
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
import androidx.navigation.compose.rememberNavController
import com.gems.toplan.R
import com.gems.toplan.data.TodoItem
import com.gems.toplan.navigation.NavigationItems
import com.gems.toplan.ui.theme.ToPlanTheme

@Composable
fun DoScreen(navController: NavHostController) {
//    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
//    val isRefreshing by viewModel.isRefreshing.collectAsState()
//
//    var visibleState by remember { mutableStateOf(true) }
//    val visibleTasks = if (visibleState) tasks else tasks.filter { it.done }
    ToPlanTheme {

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
                }
            ) {
                Icon(Icons.Default.Info, contentDescription = "Details")
            }
        }
    }
}




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainPreview(){
    DoScreen(rememberNavController())
}


