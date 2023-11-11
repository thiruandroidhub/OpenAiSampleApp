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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import model.ChatCompletion
import model.ChatData
import model.Message

@Immutable
data class UiState(
    val loading: Boolean = false,
    val answer: OpenAiPromptsViewModel.Story? = null,
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
            header("Authorization", "Bearer ")
        }
    }

    fun getStory(content: String) {
        viewModelScope.launch {
            _uiStateFlow.update { it.copy(loading = true) }
            val story  = getOpenApiPromptsResponse(content)
            _uiStateFlow.update { it.copy(answer = story, loading = false) }
        }
    }

    fun getNextPartOfStory(optionSelected: String){
        viewModelScope.launch {
            _uiStateFlow.update { it.copy(loading = true) }
            val story  = getOpenAiNextPart(optionSelected)
            _uiStateFlow.update { it.copy(answer = story, loading = false) }
        }
    }

    private suspend fun getOpenAiNextPart(optionSelected: String): Story {
        val prompt = "Now the user has selected " + optionSelected + " Your response should only contain json. " +
        "It is an interactive story because you will provide the user with options on what the character should do next. " +
                "So the object created should be like private data class Story( val title: String? val passage : String, val imagePrompt: String, val question: String," +
                "        val answer1: String," +
                "        val answer2: String," +
                "        val id: String" +
                "    ) " + "make the question as long as needed but ensure that the answer 1 and answer 2 will be one word answers. So the answer fits on a button " +
                " you can just generate the first part of the story. We will respond providing you with which option the user selected for you to generate the next part" +
                " remember your response can only have the precise json otherwise the app i have created will" +
                "not be able to serialise it"
        try {
            val response =
                openAiHttpClient.post("https://api.openai.com/v1/chat/completions") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        ChatData(
                            model = "gpt-3.5-turbo",
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

            println("TAG data is " + stringEncodedJson)


            val apiResponse = json.decodeFromString<Story>(stringEncodedJson)
            println("TAG title " + apiResponse.title)
            return apiResponse
        } catch (e: Exception) {
            println("TAG error receiving response = ${e.message}")
            throw e
            //   _uiStateFlow.update { it.copy(error = true, loading = false) }
        }
        // return ChatCompletion()
    }

    @Serializable
    data class Story(
        val title: String?,
        val passage : String,
        val imagePrompt: String,
        val question: String,
        val answer1: String,
        val answer2: String,
        val id: String

    )

    private suspend fun getOpenApiPromptsResponse(content: String): Story {
        val prompt = "Can you generate a 3 part interactive story for the application. Your response should only contain json. " +
                "It is an interactive story because you will provide the user with options on what the character should do next. " +
                "So the object created should be like private data class Story( val title: String? val passage : String, val imagePrompt: String, val question: String," +
                "        val answer1: String," +
                "        val answer2: String," +
                "        val id: String" +
                "    ) " + "make the question as long as needed but ensure that the answer 1 and answer 2 will be one word answers. So the answer fits on a button " +
                " you can just generate the first part of the story. We will respond providing you with which option the user selected for you to generate the next part" +
                "the story should be about" + content + " remember your response can only have the precise json otherwise the app i have created will" +
                "not be able to serialise it"
        try {
            val response =
                openAiHttpClient.post("https://api.openai.com/v1/chat/completions") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        ChatData(
                            model = "gpt-3.5-turbo",
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
            println("TAG data is " + data)
            val apiResponse = json.decodeFromString<Story>(data)
            println("TAG title " + apiResponse.title)
            return apiResponse
        } catch (e: Exception) {
            println("TAG error receiving response = ${e.message}")
           throw e
         //   _uiStateFlow.update { it.copy(error = true, loading = false) }
        }
       // return ChatCompletion()
    }
}