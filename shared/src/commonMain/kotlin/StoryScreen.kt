import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

class StoryScreen(private val content: String) : Screen {

    @Composable
    override fun Content() {
        val openAiViewModel = getViewModel(Unit, viewModelFactory { OpenAiPromptsViewModel() })
        val uiState: UiState by openAiViewModel.uiStateFlow.collectAsState()
        LaunchedEffect(key1 = Unit) {
            openAiViewModel.getStory(content = content)
        }
        DisplayStory(uiState) { answer ->
            when (answer) {
                "A" -> {
                    openAiViewModel.getNextPartOfStory(
                        entirePassage = uiState.answer?.entirePassage ?: "",
                        question = uiState.answer?.question ?: "",
                        optionSelected = uiState.answer?.answer1 ?: ""
                    )
                }

                else -> {
                    openAiViewModel.getNextPartOfStory(
                        entirePassage = uiState.answer?.entirePassage ?: "",
                        question = uiState.answer?.question ?: "",
                        optionSelected = uiState.answer?.answer2 ?: ""
                    )
                }
            }
        }
    }

    @Composable
    fun DisplayStory(uiState: UiState, userAction: (String) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {


            Spacer(modifier = Modifier.height(8.dp))
            val scrollState = rememberScrollState()
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .fillMaxWidth()
                        .padding(8.dp)
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
                            KamelImage(
                                asyncPainterResource(uiState.answer?.imagePrompt ?: ""),
                                "",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.fillMaxWidth().aspectRatio(1.0f)
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                userAction("A")
                            }
                        ) {
                            Text(text = uiState.answer?.answer1 ?: "")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                userAction("B")
                            }
                        ) {
                            Text(text = uiState.answer?.answer2 ?: "")
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
}
