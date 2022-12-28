package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

// GameViewModel é subclasse do ViewModel, classe abstrata, precisa ser estendido para ser usado no app.
/** ViewModel containing the app data and methods to process the data. */
class GameViewModel : ViewModel() {
    // Separar os dados de IU do app do controlador de IU (suas classes Activity / Fragment)
    // permite seguir o princípio de responsabilidade exclusiva. As atividades e fragmentos
    // são responsáveis por mostrar visualizações e dados na tela, enquanto o ViewModel
    // é responsável por armazenar e processar todos os dados necessários da IU.
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    // private e var: Fora do ViewModel, os dados precisam ser legíveis, mas não editáveis
    private val _currentWordCount = MutableLiveData(0)
    val  currentWordCount: LiveData<Int>
        get() = _currentWordCount

    // Agora a _currentScrambledWord pode ser acessada e editada somente no GameViewModel.
    // O controlador de IU GameFragment pode ler seu valor usando a propriedade
    // somente leitura (Propriedade de APOIO), currentScrambledWord.
    //private lateinit var _currentScrambledWord: String
    // LiveData e MutableLiveData são classes genéricas, então é necessário especificar o tipo de dados
    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
            // Uma string "spannable" é uma string com algumas informações extras anexadas a ela.
        } else {
            val scrambleWord = it.toString()
            val spannable: Spannable = SpannableString(scrambleWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambleWord).build(),
                0,
                scrambleWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    // List of words used in the game
    // variável de classe do tipo MutableList<String> p/ armazenar lista de palavras
    private var wordList: MutableList<String> = mutableListOf()

    // variável de classe inicializada posteriormente, p/ armazenar a palavra advinhada pelo jogador
    private lateinit var currentWord: String

    // usado para inicializar propriedades lateinit na classe, como a palavra atual
    init {
        // geração de registros para compreender o ciclo de vida da ViewModel
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }

    /** Updates currentWord and currentScrambledWord with the next word. */
    private fun getNextWord() {
        // Acessa palavra aleatória da allWordsList e atribua à currentWord
        currentWord = allWordsList.random()
        // converte a string currentWord em uma matriz de caracteres
        val tempWord = currentWord.toCharArray()
        //Embaralha caracteres da matriz
        tempWord.shuffle()

        // embaralha a palavra p/ continuar a repetição até que a palavra seja diferente da original
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        // verifica se a palavra já foi usada. Se a wordsList contiver currentWord,
        // chama getNextWord()
        if (wordList.contains(currentWord)) {
            getNextWord()
        } else {
            Log.d("Unscramble", "currentWord= $currentWord")
            // atualiza o valor da _currentScrambledWord com a palavra recém embaralhada,
            // Para acessar os dados em um objeto LiveData, usa-se a propriedade value
            _currentScrambledWord.value = String(tempWord)
            // aumenta a contagem de palavras e adiciona a nova palavra à wordsList
            // função inc() para aumentar o valor em um com segurança de tipo nulo
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordList.add(currentWord)
        }
    }

    /** Re-initializes the game data to restart the game. */
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordList.clear()
        getNextWord()
    }

    /** Increases the game score if the player's word is correct. */
    private fun increaseScore() {
        // função plus() para aumentar o valor de _score, que executa a adição com segurança de tipo nulo
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    // método auxiliar que retorna true se a palavra do jogador estiver correta e aumenta a pontuação
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

/**  Returns true if the current word count is less than MAX_NO_OF_WORDS. */
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}