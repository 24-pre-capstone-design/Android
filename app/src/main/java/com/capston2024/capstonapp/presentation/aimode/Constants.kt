package com.capston2024.capstonapp.presentation.aimode


import com.capston2024.capstonapp.BuildConfig
import com.knuddels.jtokkit.api.ModelType

internal object Constants {
    internal const val OPENAI_TOKEN = BuildConfig.OPENAI_KEY

    /** Chat model: "gpt-4-32k" or "gpt-3.5-turbo-16k" "gpt-4o-2024-05-13"*/
    internal const val OPENAI_CHAT_MODEL = "gpt-4o-2024-05-13"

    /** Chat model: "gpt-4-32k" or "gpt-3.5-turbo-16k" */
    internal const val OPENAI_CHAT_MODEL = "gpt-4-1106-preview"


    /** Image model: "dall-e-3" or "dall-e-2" */
    internal const val OPENAI_IMAGE_MODEL = "dall-e-3"

    /** Image model: create on playground.openai.com */
    //internal const val OPENAI_ASSISTANT_ID = "asst_bykuslT6y2DWikNORnzl3ZTE"

    /** Tokenizer model: GPT_4_32K or GPT_3_5_TURBO_16K */
    internal val OPENAI_CHAT_TOKENIZER_MODEL = ModelType.GPT_4_32K // to match above

    /** Completion model: text-davinci-003 */
    internal const val OPENAI_COMPLETION_MODEL = "text-davinci-003"

    /** Maximum token limit for model: 4,096 for "gpt-3.5-turbo"
     * 16,384 for gpt-3.5-turbo-16k
     * 32,768 for gpt-4-32k
     * (used in Sliding Window calculations) */
    internal const val OPENAI_MAX_TOKENS = 32768

    /** Embedding model: text-embedding-ada-002 */
    internal const val OPENAI_EMBED_MODEL = "text-embedding-ada-002"

    internal const val WIKIPEDIA_USER_AGENT = ""

}