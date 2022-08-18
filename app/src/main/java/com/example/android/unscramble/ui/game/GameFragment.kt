/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {

    private val TAG = GameFragment::class.java.simpleName

    /**
     * Vì khai báo là val nên thuộc tính chỉ được đọc
     * Sử dụng tính năng ủy quyền sử dụng By để giao trách nhiệm setter và getter cho lớp viewModels()
     * Để tránh sự thay đổi cấu hình làm xóa trạng thái của tham chiếu viewmodel, viewModels() sẽ tạo và giữ
     * lại đối tượng khi thay đổi cấu hình
     * */
    private val viewModel: GameViewModel by viewModels()


    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same GameViewModel instance created by the
    // first fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG, "---onAttach")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "---onStart")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        Log.i(TAG, "---onCreateView")
        Log.d(
            TAG, "Word: ${viewModel.currentScrambledWord} " +
                    "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}"
        )
        binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "---onViewCreated")

        // Setup a click listener for the Submit and Skip buttons.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }

        // Update the UI


        //LiveData observe
        viewModel.currentScrambledWord.observe(viewLifecycleOwner) { newValue ->
            binding.textViewUnscrambledWord.text = newValue
        }

        viewModel.score.observe(viewLifecycleOwner) { newScore ->
            binding.score.text = getString(R.string.score, newScore)
        }

        viewModel.currentWordCount.observe(viewLifecycleOwner) { newCurrentWordCount ->
            binding.wordCount.text = getString(
                R.string.word_count, newCurrentWordCount, MAX_NO_OF_WORDS
            )
        }
    }

    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    /*
    * Sets and resets the text field error status.
    */
    private fun setErrorTextField(isError: Boolean) {
        if (isError) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            /**
             * clear text
             * */
            binding.textInputEditText.text = null
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "---onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "---onPause")
    }


    /**
     * Show dialog final
     * */
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }

    /*
     * Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.
     */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

}
