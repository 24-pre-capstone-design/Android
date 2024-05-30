package com.capston2024.capstonapp.presentation.aimode

import android.content.Context
import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionMode
import com.aallam.openai.api.chat.ToolChoice
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.capston2024.capstonapp.presentation.aimode.data.CustomChatMessage
import com.capston2024.capstonapp.presentation.aimode.data.EmbeddingHistory
import com.capston2024.capstonapp.presentation.aimode.data.HistoryDbHelper
import com.capston2024.capstonapp.presentation.aimode.data.SlidingWindow
import com.capston2024.capstonapp.presentation.main.MainActivity
import com.capston2024.capstonapp.presentation.main.MainViewModel
import com.capston2024.capstonapp.presentation.main.foods.FoodsFragment

import kotlinx.serialization.json.jsonPrimitive



/** Uses OpenAI Kotlin lib to call chat model */
@OptIn(BetaOpenAI::class)
class OpenAIWrapper(val context: Context?,val aiViewModel: AIViewModel, val mainActivity: MainActivity, val mainViewModel: MainViewModel) {
    private val openAIToken: String = Constants.OPENAI_TOKEN
    private var conversation: MutableList<CustomChatMessage>
    private var openAI: OpenAI = OpenAI(openAIToken)
    private val dbHelper = HistoryDbHelper(context)
    val cartManager = CartManager(aiViewModel, mainActivity,mainViewModel)

    init {
        conversation = mutableListOf(
            CustomChatMessage(
                role = ChatRole.System,
                userContent = """너는 친절한 20대 여성이고 '한식다이어리'라는 음식점의 점원이야.
                    |항상 부드러운 말투와 정중한 표현을 사용해
                    |'야채비빔밥',' 육회비빔밥','김치찌개'같은 한국 음식이름은 foodName으로서 함수 호출시 사용할 수 있으니 제대로 인식해야해
                    |""".trimMargin()
            )
        )
        // TODO: use location services to determine latitude/longitude for current location
    }

    suspend fun chat(message: String): String {
        val relevantHistory = EmbeddingHistory.groundInHistory(openAI, dbHelper, message)

        // add the user's message to the chat history
        val userMessage = CustomChatMessage(
            role = ChatRole.User,
            grounding = relevantHistory,
            userContent = message
        )
        conversation.add(userMessage)

        // implement sliding window. hardcode 50 tokens used for the weather function definitions.
        val chatWindowMessages = SlidingWindow.chatHistoryToWindow(conversation, reservedForFunctionsTokens=50)

        // build the OpenAI network request
        val chatCompletionRequest = chatCompletionRequest {
            model = ModelId(Constants.OPENAI_CHAT_MODEL)
            messages = chatWindowMessages

            // hardcoding weather function every time (for now)
            functions {
                function {
                    name = cartManager.name()
                    description = cartManager.description()
                    parameters = cartManager.params()
                }
                function {
                    name = cartManager.FMFname()
                    description = cartManager.FMFdescription()
                    parameters = cartManager.FMFparams()
                }

            }
            functionCall = FunctionMode.Auto
        }
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val completionMessage = completion.choices.first().message ?: error("no response found!")

        // extract the response to show in the app
        var chatResponse = completionMessage.content ?: ""

        if (completionMessage.functionCall == null) {
            // no function, add the response to the conversation history
            val botResponse =CustomChatMessage(
                role = ChatRole.Assistant,
                userContent = chatResponse
            )
            conversation.add(botResponse)
            // add message pair to history database
            EmbeddingHistory.storeInHistory(openAI, dbHelper, userMessage, botResponse)
        } else { // handle function

            val function = completionMessage.functionCall
            Log.i("LLM", "Function ${function!!.name} was called")

            var functionResponse = ""
            var handled = true
            when (function.name) {
                cartManager.name()->{
                    val functionArgs = function.argumentsAsJson() ?: error("arguments field is missing")
                    val item = decodeIfNeeded(functionArgs.getValue("item").jsonPrimitive.content)
                    val quantity = decodeIfNeeded(functionArgs.getValue("quantity").jsonPrimitive.content)
                    Log.i("LLM-WK", "Argument $item $quantity")
                    functionResponse = cartManager.foodOrderFunction(item, quantity)
                }
                cartManager.FMFname()->{
                    val functionArgs = function.argumentsAsJson() ?: error("arguments field is missing")
                    Log.i("LLM-WK", "Argument nothing")
                    functionResponse = cartManager.foodMenuFunction()
                }

                else -> {
                    Log.i("LLM", "Function ${function!!.name} does not exist")
                    handled = false
                    chatResponse += " " + function.name + " " + function.arguments
                    conversation.add(
                        CustomChatMessage(
                            role = ChatRole.Assistant,
                            userContent = chatResponse
                        )
                    )
                }
            }

            if (handled)
            {
                // add the 'call a function' response to the history
                conversation.add(
                    CustomChatMessage(
                        role = completionMessage.role,
                        userContent = completionMessage.content ?: "", // required to not be empty in this case
                        functionCall = completionMessage.functionCall
                    )
                )
                // add the response to the 'function' call to the history
                conversation.add(
                    CustomChatMessage(
                        role = ChatRole.Function,
                        name = function.name,
                        userContent = functionResponse
                    )
                )

                // sliding window - with the function call messages,
                // we might need to remove more from the history
                val functionChatWindowMessages = SlidingWindow.chatHistoryToWindow(conversation, 50)

                // send the function request/response back to the model
                val functionCompletionRequest = chatCompletionRequest {
                    model = ModelId(Constants.OPENAI_CHAT_MODEL)
                    messages = functionChatWindowMessages }
                val functionCompletion: ChatCompletion = openAI.chatCompletion(functionCompletionRequest)
                // show the interpreted function response as chat completion
                chatResponse = functionCompletion.choices.first().message?.content!!
                val botResponse = CustomChatMessage(
                    role = ChatRole.Assistant,
                    userContent = chatResponse
                )
                conversation.add(botResponse)

                if (completionMessage.functionCall == null) {
                    // wasn't a function, add message pair to history database
                    // prevents historical answers from being used
                    // instead of calling the function again
                    // (ie returns old weather info)
                    EmbeddingHistory.storeInHistory(openAI, dbHelper, userMessage, botResponse)
                }
            }
        }

        return chatResponse
    }

    fun decodeUnicodeEscapes(encoded: String): String {
        return encoded.split("\\\\u".toRegex()) // 역슬래시와 u로 분리
            .filter { it.isNotEmpty() } // 빈 문자열 제거
            .joinToString(separator = "") { code ->
                if (code.length >= 4) { // 유효한 유니코드 코드포인트 길이 체크
                    val unicode = code.substring(0, 4)
                    val rest = code.substring(4)
                    val char = unicode.toInt(16).toChar()
                    "$char$rest"
                } else {
                    code
                }
            }
    }
    fun decodeIfNeeded(input: String): String {
        // 유니코드 이스케이프 시퀀스 패턴 감지
        val unicodePattern = "\\\\u[0-9A-Fa-f]{4}".toRegex()

        return if (unicodePattern.containsMatchIn(input)) {
            // 유니코드 이스케이프가 포함된 경우 디코딩 실행
            decodeUnicodeEscapes(input)
        } else {
            // 그렇지 않은 경우 원본 문자열 반환
            input
        }
    }
}