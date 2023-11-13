import androidx.compose.runtime.Immutable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import model.ChatCompletion
import model.ChatData
import model.Message
import model.Story

@Immutable
data class UiState(
    val loading: Boolean = false,
    val answer: Story? = null,
    val error: Boolean = false
)

class OpenAiPromptsViewModel : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow: StateFlow<UiState> = _uiStateFlow.asStateFlow()

    private val openAiHttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        defaultRequest {
            header("Authorization", "Bearer xxx")
        }
    }

    fun getStory(content: String) {
        viewModelScope.launch {
            _uiStateFlow.update { it.copy(loading = true) }
            val story = getOpenApiPromptsResponse(content)
            val newPassage = story.passage
            _uiStateFlow.update {
                it.copy(
                    answer = story.copy(entirePassage = "$newPassage"),
                    loading = false
                )
            }
        }
    }

    fun getNextPartOfStory(entirePassage: String, question: String, optionSelected: String) {
        viewModelScope.launch {
            _uiStateFlow.update { it.copy(loading = true) }
            val story = getOpenAiNextPart(entirePassage, question, optionSelected)
            val currentPassage = _uiStateFlow.value.answer?.entirePassage ?: ""
            val newPassage = story.passage
            _uiStateFlow.update {
                it.copy(
                    answer = story.copy(entirePassage = "$currentPassage $newPassage"),
                    loading = false
                )
            }
        }
    }

    private suspend fun getOpenApiPromptsResponse(content: String): Story {
        val prompt =
            "Can you generate the first part of an interactive story in 50 words for the application. Your response should only contain json." +
                    "It is an interactive story because you will provide the user with options on what the character should do next." +
                    "So the json contain id, title, passage, question, answer1, answer2. All fields should be in String type." +
                    "Make the question as long as needed but ensure that the answer 1 and answer 2 will be one word answers. So the answer fits on a button." +
                    "You can just generate the first part of the story" +
                    "The story should be about $content. " +
                    "Your response can only have the precise json otherwise the app i have created will not be able to serialise it."
        println("TAG first prompt is = $prompt")
        try {
            val response =
                openAiHttpClient.post("https://api.openai.com/v1/chat/completions") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        ChatData(
                            model = "gpt-4",
                            messages = listOf(
                                Message(
                                    role = "user",
                                    content = prompt
                                )
                            ),
                            temperature = 0.7
                        )
                    )
                }.body<ChatCompletion>()
            println("TAG openAi response = $response")
            val json = Json { ignoreUnknownKeys = true }
            val data = response.choices.first().message.content
            println("TAG data is $data")
            val apiResponse = json.decodeFromString<Story>(data)
            println("TAG title " + apiResponse.title)
            return apiResponse
        } catch (e: Exception) {
            println("TAG error receiving response = ${e.message}")
            throw e
        }
    }

    private suspend fun getOpenAiNextPart(
        passage: String,
        question: String,
        optionSelected: String
    ): Story {
        val prompt =
            "The user has answered with an option for the question asked based on the passage shown. The passage is $passage. The question is $question. And the option selected is $optionSelected." +
                    "Can you generate the next part of the interactive story based on the option selected for the question asked in 50 words. Your response should only contain json." +
                    "The second part should also provide the user with options on what the character should do next." +
                    "So the json contain id, title, passage, question, answer1, answer2. All fields should be in String type." +
                    "Make the question as long as needed but ensure that the answer 1 and answer 2 will be one word answers. So the answer fits on a button." +
                    "Your response can only have the precise json otherwise the app i have created will not be able to serialise it."
        println("TAG passage next part is = $passage")
        try {
            val response =
                openAiHttpClient.post("https://api.openai.com/v1/chat/completions") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        ChatData(
                            model = "gpt-4",
                            messages = listOf(
                                Message(
                                    role = "user",
                                    content = prompt
                                )
                            ),
                            temperature = 0.7
                        )
                    )
                }.body<ChatCompletion>()
            println("TAG openAi next part of Story = $response")
            val json = Json { ignoreUnknownKeys = true }
            val stringEncodedJson: String = response.choices.first().message.content
            println("TAG how many choices is there " + response.choices.size)
            println("TAG data is $stringEncodedJson")
            val apiResponse = json.decodeFromString<Story>(stringEncodedJson)
            println("TAG title " + apiResponse.title)
            return apiResponse
        } catch (e: Exception) {
            println("TAG error receiving response = ${e.message}")
            throw e
        }
    }
}