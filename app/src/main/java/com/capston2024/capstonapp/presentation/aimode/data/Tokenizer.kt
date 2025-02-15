package com.capston2024.capstonapp.presentation.aimode.data

import com.capston2024.capstonapp.presentation.aimode.Constants.OPENAI_CHAT_TOKENIZER_MODEL
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.EncodingRegistry
import org.apache.http.annotation.Obsolete

/**
 * Count tokens so that we can understand how close we are
 * to the model's maximum input token limit
 *
 * https://community.openai.com/t/what-is-the-openai-algorithm-to-calculate-tokens/58237
 *
 * Using this Java package
 * https://github.com/knuddelsgmbh/jtokkit
 * linked from
 * https://github.com/openai/openai-cookbook/blob/main/examples/How_to_count_tokens_with_tiktoken.ipynb
 */
class Tokenizer {
    companion object {

        // expensive to create, keep one copy
        private val registry: EncodingRegistry = Encodings.newLazyEncodingRegistry()

        /**
         * Count the number of tokens in the input
         *
         * @param text User input OR past message/grounding content
         * @return number of tokens required for the `text`
         */
        fun countTokensIn (text: String?): Int {
            if (text == null) return 0

            // Get encoding via type-safe enum
            val encoding: Encoding = registry.getEncodingForModel(OPENAI_CHAT_TOKENIZER_MODEL)
            val tokenCount = encoding.countTokens(text)

            // Log.i("LLM-TK", "tokenCount: $tokenCount for $text")

            return tokenCount
        }

        /**
         * Trim the input text to be under the number of tokens specified
         *
         * @param text Input to be trimmed
         * @param tokenLimit Maximum number of tokens to be returned
         * @return substring of the text that's no longer than the
         * specified number of tokens
         */
        fun trimToTokenLimit (text: String?, tokenLimit: Int): String? {
            // https://jtokkit.knuddels.de/docs/getting-started/usage
            val encoding: Encoding = registry.getEncodingForModel(OPENAI_CHAT_TOKENIZER_MODEL)
            val encoded = encoding.encodeOrdinary(text, tokenLimit)
            if (encoded.isTruncated) {
                return encoding.decode(encoded.tokens)
            }
            return text // wasn't truncated
        }

        /**
         * Visualize the tokens in the string for debugging
         * i.e. comparing to the visualizer on
         * https://platform.openai.com/tokenizer
         */
        @Obsolete
        fun toDebugTokenString (text: String?): String? {
            if (text == null) return ""

            val encoding: Encoding = registry.getEncodingForModel(OPENAI_CHAT_TOKENIZER_MODEL)
            val encoded = encoding.encodeOrdinary(text)
            var debugString = ""
            for (token in encoded) {
                debugString += encoding.decode(listOf(token)) + "|"
            }
            return debugString
        }
    }
}