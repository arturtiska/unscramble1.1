package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

//ViewModel que contém os dados do aplicativo e métodos para processar os dados
class GameViewModel : ViewModel() {

    private var _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private var _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    //Lista de palavras usadas no jogo
    private var wordList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    init {
        getNextWord()
    }

    //Atualiza currentWord e currentScrambledWord com a próxima palavra.
    fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        if (wordList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = _currentWordCount.value?.inc()
            wordList.add(currentWord)
        }
    }

    //Reinicializa os dados do jogo para reiniciar o jogo
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordList.clear()
        getNextWord()
    }

    //Aumenta a pontuação do jogo se a palavra do jogador estiver correta.
    private fun increaseScore() {
        _score.value = _score.value?.plus(SCORE_INCREASE)
    }

    //Retorna true se a palavra do jogador estiver correta.
    //    * Aumenta a pontuação de acordo
    fun isUserWordCorrect(playWord: String): Boolean {
        if (playWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    //Retorna true se a contagem de palavras atual for menor que MAX_NO_OF_WORDS
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}