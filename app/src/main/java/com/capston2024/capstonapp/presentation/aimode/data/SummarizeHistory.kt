package com.capston2024.capstonapp.presentation.aimode.data

import android.util.Log
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.completion.completionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.capston2024.capstonapp.presentation.aimode.Constants

class SummarizeHistory {

    companion object {
        private const val openAIToken: String = Constants.OPENAI_TOKEN
        private var openAI: OpenAI = OpenAI(openAIToken)

        /**
         * Use the OpenAI completion endpoint to summarize the
         * conversation history into a topic-dense string
         */
        suspend fun summarize(history: String): String {
            if (history.isNullOrEmpty()) return ""

            val summarizePrompt = """
                |Extract all the session names from this discussion:
                |#####
                |$history
                |#####""".trimMargin()

            // send the function request/response back to the model
            val completionRequest = completionRequest {
                model = ModelId(Constants.OPENAI_COMPLETION_MODEL)
                prompt = summarizePrompt
                maxTokens = 500
            }
            val textCompletion: TextCompletion =
                openAI.completion(completionRequest)
            // show the interpreted function response as chat completion
            val response = textCompletion.choices.first().text
            Log.i("LLM", "summarize: $response")
            return response
        }
    }
}