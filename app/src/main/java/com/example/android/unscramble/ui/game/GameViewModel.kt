package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.unscramble.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameViewModel : ViewModel() {
    private val _score = MutableLiveData(0)
    private val _currentWordCount = MutableLiveData(0)
    private val _currentScrambledWord = MutableLiveData<String>()
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }

    /**
     * _currentScrambledWord được chỉnh sửa bên trong ViewModel
     * currentScrambledWord Chỉ được đọc bên ngoài ViewModel
     * */
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    val score: LiveData<Int>
        get() = _score

    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        /**
         * while kiểm tra từ được xáo trộn giống từ ban đầu thì tiếp tục xáo trộn
         * */
        while (tempWord.toString() == currentWord) {
            tempWord.shuffle()
        }

        /**
         * List có tồn tại từ thì tiếp tục đệ qui để xáo trộn lấy từ mới, ngược lại
         * Tăng tổng số câu hỏi và thêm từ vào List
         * */
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            //sử dụng hàm Kotlin inc() để giá trị tăng thêm một bằng giá trị rỗng an toàn
            _currentWordCount.value = _currentWordCount.value?.inc()
            wordsList.add(currentWord)
        }
    }

    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    private fun increaseScore() {
        _score.value = _score.value?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }
}