package com.capston2024.capstonapp.presentation.aimode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.chat.ChatRole
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Message
import com.capston2024.capstonapp.data.MessageType
import com.capston2024.capstonapp.databinding.FragmentAiBinding
import com.capston2024.capstonapp.extension.FoodState
import com.capston2024.capstonapp.presentation.aimode.data.ChatAdapter
import com.capston2024.capstonapp.presentation.aimode.data.CustomChatMessage
import com.capston2024.capstonapp.presentation.main.MainActivity
import com.capston2024.capstonapp.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@AndroidEntryPoint
class AIFragment : Fragment() {
    private var _binding: FragmentAiBinding? = null
    private val binding get() = _binding!!
    private lateinit var aiViewModel: AIViewModel

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: MutableList<CustomChatMessage>
    private lateinit var mainViewModel: MainViewModel
    private lateinit var openAIWrapper: OpenAIWrapper

    private lateinit var speechRecognizer: SpeechRecognizer
    private var textToSpeech: TextToSpeech? = null
    var isTTSReady = false // TTS 준비 상태 플래그

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
        Log.d("aifragment", "new aiframgent!!")

        //권한 설정
        requestPermission()
        //tts 객체 초기화
        resetTTS()


        //addChatItem(requireContext().getString(R.string.ai_explain), MessageType.AI_CHAT)
        //clickButton()
    }

    private fun setting() {
        //aiAdapter=AIAdapter(messages)
        //binding.rvAi.adapter = AIAdapter(messages)
        messageList = mutableListOf()
        chatAdapter = ChatAdapter(messageList)
        mainViewModel= ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        aiViewModel = ViewModelProvider(requireActivity()).get(AIViewModel::class.java)
        openAIWrapper = OpenAIWrapper(requireContext(), aiViewModel, requireActivity() as MainActivity, mainViewModel)

        with(binding) {
            rvAi.layoutManager = LinearLayoutManager(requireContext())
            rvAi.adapter = chatAdapter
            btnInput.setOnClickListener {
                clickButton()
            }
        }

        aiViewModel.getData()
        lifecycleScope.launch {
            aiViewModel.allFoodState.collect { foodState ->
                when (foodState) {
                    is FoodState.Success -> {
                        //Toast.makeText(activity, "정보 가져오기 성공", Toast.LENGTH_SHORT).show()
                        aiViewModel.updateFoodsList(foodState.foodList)
                    }

                    is FoodState.Error -> {
                        //Toast.makeText(activity, "정보 가져오기 실패", Toast.LENGTH_SHORT).show()
                        Log.e("error", "foodstate is error: ${foodState.message}")
                    }

                    is FoodState.Loading -> {
                        //Toast.makeText(activity, "로딩중", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun clickButton() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(recognitionListener)
            // RecognizerIntent 생성
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    requireActivity().packageName
                ) //여분의 키
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            }
            // 여기에 startListening 호출 추가
            startListening(intent)
        }
    }

    /*private fun clickButton() {
        handleUserMessage(binding.etMessage.text.toString())
        messageList.add(
            CustomChatMessage(
                ChatRole.User,
                userContent = binding.etMessage.text.toString()
            )
        )
        chatAdapter.notifyDataSetChanged()
        binding.etMessage.setText("") //초기화
    }*/

    private fun handleUserMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = openAIWrapper.chat(message) ?: "Error: Wrapper is null"
            withContext(Dispatchers.Main) {
                updateChatWithResponse(message, response)
            }
        }
    }

    private fun updateChatWithResponse(userMessage: String, response: String) {
        //messageList.add(CustomChatMessage(ChatRole.User, userContent = userMessage))
        messageList.add(CustomChatMessage(ChatRole.Assistant, userContent = response))
        chatAdapter.notifyDataSetChanged()
        binding.rvAi.scrollToPosition(messageList.size - 1)

        speakResponse(response)
    }

    private fun requestPermission() {
        // 버전 체크, 권한 허용했는지 체크
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO), 0
            )
        }
    }

    private fun resetTTS() {
        // TTS 객체 초기화
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 언어 데이터가 없거나 지원하지 않는 언어일 때 처리
                    Log.e("aifragment", "Language is not supported")
                    Toast.makeText(context, "TTS 언어 데이터가 필요합니다. Google TTS 앱에서 데이터를 설치해주세요.", Toast.LENGTH_LONG).show()
                    // 사용자를 Google TTS 앱 또는 설정 페이지로 안내할 수 있는 인텐트 실행
                    val installIntent = Intent()
                    installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                    context?.startActivity(installIntent)
                } else {
                    isTTSReady = true // TTS가 준비되었음을 표시
                    textToSpeech?.setSpeechRate(5.0f) // TTS 속도 설정
                    speakInitialMessage() // 초기 메시지 음성 출력
                }
            } else {
                // TTS 초기화 실패 처리
                Log.e("aifragment", "Initialization failed")
                Toast.makeText(context, "TTS 초기화에 실패하였습니다. 앱 설정에서 TTS 엔진을 확인해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun speakInitialMessage() {
        if (isTTSReady) {
            // 예제 메시지를 TTS로 말하기
            textToSpeech?.speak(
                "말씀하세요!",
                TextToSpeech.QUEUE_FLUSH,
                null,
                ""
            )
            Log.d("aifragment","tts is ready")
        }
    }

    private fun speakResponse(text:String){
        if (isTTSReady) {
            textToSpeech?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                ""
            )
        } else {
            Log.d("aifragment", "TTS is not ready")
        }
    }

    /*private fun addChatItem(message:String, type:MessageType){
        messages.add(Message(message, type))
        aiAdapter.notifyItemInserted(messages.size-1)
        binding.rvAi.scrollToPosition(messages.size-1)
    }*/


    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
            Toast.makeText(context, "이제 말씀하세요!", Toast.LENGTH_SHORT).show()
            binding.tvState.text = "이제 말씀하세요!"
        }

        override fun onBeginningOfSpeech() {
            binding.tvState.text = "잘 듣고 있어요."
        }

        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}

        // 말을 시작하고 인식이 된 단어를 buffer에 담음
        override fun onBufferReceived(buffer: ByteArray) {}

        // 말하기를 중지하면 호출
        override fun onEndOfSpeech() {
            binding.tvState.text = "끝!"
            CoroutineScope(Dispatchers.Main).launch {
                /*delay(500)
                addChatItem(
                    requireContext().getString(R.string.ai_explain),
                    MessageType.AI_CHAT
                )*/

                binding.tvState.text = "상태체크"
            }
        }

        // 오류 발생했을 때 호출
        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            binding.tvState.text = "에러 발생: $message"
        }

        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val text = matches[0] // 첫 번째 인식 결과를 사용
                //messages.add(Message(text,MessageType.USER_INPUT)) // 인식된 텍스트를 messages 리스트에 추가
                messageList.add(
                    CustomChatMessage(
                        ChatRole.User,
                        userContent =text
                    )
                )
                chatAdapter.notifyDataSetChanged()
                handleUserMessage(text)

                Log.d("ChatFragment", "인식된 메시지: $text")
            }
        }

        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {}

        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        //sst destroy
        if (this::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }

        // TTS 객체 정리
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}