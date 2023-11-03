package model

import kotlinx.serialization.Serializable

@Serializable
data class PromptRequest(val prompt: String, val max_tokens: Int = 150)

@Serializable
data class PromptResponse(val choices: List<Choice> = emptyList())

@Serializable
data class Choice(val content: String)