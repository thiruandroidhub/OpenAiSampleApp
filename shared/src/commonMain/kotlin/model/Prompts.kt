package model

import kotlinx.serialization.Serializable

@Serializable
data class PromptRequest(val prompt: String, val max_tokens: Int = 150)

@Serializable
data class ChatData(
    val model: String,
    val messages: List<Message>,
    val temperature: Double
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class ChatCompletion(
    val id: String = "",
    val `object`: String = "",
    val created: Long = 0L,
    val model: String = "",
    val choices: List<Choice> = emptyList(),
    val usage: Usage = Usage()
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

@Serializable
data class Usage(
    val prompt_tokens: Int = -1,
    val completion_tokens: Int = -1,
    val total_tokens: Int = -1
)

