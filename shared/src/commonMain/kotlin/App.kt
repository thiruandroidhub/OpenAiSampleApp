import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import model.Story

@Composable
fun App() {
    MaterialTheme {
        val openAiViewModel = getViewModel(Unit, viewModelFactory { OpenAiPromptsViewModel() })

        val uiState by openAiViewModel.uiStateFlow.collectAsState()

        var question by remember { mutableStateOf("Harry Potter") }
        var answer by remember {
            mutableStateOf(
                Story(
                    passage = "",
                    question = "",
                    answer1 = "",
                    answer2 = "",
                    id = "2",
                    imagePrompt = "",
                    title = ""
                )
            )
        }



        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (uiState.answer == null) {
                    TextField(
                        value = question,
                        onValueChange = {
                            question = it
                        },
                        label = { Text("Ask a story to create") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    )

                    Button(
                        onClick = { openAiViewModel.getStory(content = question) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text("Create")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            val scrollState = rememberScrollState()
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    when {
                        uiState.loading -> LoadingIndicator()
                        uiState.answer != null -> {
                            Text(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                text = uiState.answer?.title ?: "",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState.answer?.passage ?: "",
                                modifier = Modifier.fillMaxWidth()
                            )


                        }
                    }
                }

                // Buttons aligned to the bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (uiState.answer != null) {

                        Text(
                            fontWeight = FontWeight.Bold,
                            text = uiState.answer?.question ?: "",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                openAiViewModel.getNextPartOfStory(
                                    entirePassage = uiState.answer?.entirePassage ?: "",
                                    question = uiState.answer?.question ?: "",
                                    optionSelected = uiState.answer?.answer1 ?: ""
                                )
                            }
                        ) {
                            Text(text = uiState.answer?.answer1 ?: "")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                openAiViewModel.getNextPartOfStory(
                                    entirePassage = uiState.answer?.entirePassage ?: "",
                                    question = uiState.answer?.question ?: "",
                                    optionSelected = uiState.answer?.answer2 ?: ""
                                )
                            }
                        ) {
                            Text(text = uiState.answer?.answer2 ?: "")
                        }
                    }
                }
            }


        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

expect fun getPlatformName(): String

