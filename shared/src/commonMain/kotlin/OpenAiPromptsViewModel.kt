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

@Immutable
data class UiState(
    val loading: Boolean = false,
    val answer: String = "",
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
            header("Authorization", "Bearer api key")
        }
    }

    fun getStory(content: String) {
        viewModelScope.launch {
            _uiStateFlow.update { it.copy(loading = true) }
            val response  = getOpenApiPromptsResponse(content)
            _uiStateFlow.update { it.copy(answer = response.choices.first().message.content, loading = false) }
        }
    }

    private suspend fun getOpenApiPromptsResponse(content: String): ChatCompletion {
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
                                    content = content
                                )
                            ),
                            temperature = 0.7
                        )
                    )
                }.body<ChatCompletion>()
            println("TAG openAi response = $response")
            return response
        } catch (e: Exception) {
            println("TAG error receiving response = ${e.message}")
            _uiStateFlow.update { it.copy(error = true, loading = false) }
        }
        return ChatCompletion()
    }
}