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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import model.PromptRequest
import model.PromptResponse

class OpenAiPromptsViewModel : ViewModel() {

    private val openAiHttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        defaultRequest {
            header("api-Authorization", "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")
        }
    }

    fun getPrompts() {
        viewModelScope.launch {
            getOpenApiPromptsResponse()
        }
    }

    private suspend fun getOpenApiPromptsResponse(): PromptResponse {
        try {
            val response =
                openAiHttpClient.post("https://api.openai.com/v1/engines/davinci/completions") {
                    contentType(ContentType.Application.Json)
                    setBody(PromptRequest("hi hello how are you?"))
                }
                    .body<PromptResponse>()
            println("TAG openAi response = $response")
            return response
        } catch (e: Exception) {
            println("TAG error receiving response = ${e.message}")
        }
        return PromptResponse()
    }
}