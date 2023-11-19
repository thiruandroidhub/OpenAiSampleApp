import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class BuilderScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            var question by remember { mutableStateOf("Harry Potter fighting Freddy Kruger and Bowser but then he is helped out by Mario and Luuigi") }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                    TextField(
                        value = "Harry Potter fighting Freddy Kruger and Bowser but then he is helped out by Mario and Luuigi",
                        onValueChange = {
                            question = it
                        },
                        label = { Text("Ask a story to create") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    Button(
                        onClick = { navigator.push(StoryScreen(question)) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text("Create")
                    }
                }
            }

        }


}