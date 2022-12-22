package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

// GameViewModel é subclasse do ViewModel (classe abstrata, então precisa
// ser estendido para ser usado no app.
class GameViewModel : ViewModel() {

    init {
        Log.d("GameFragment", "GameViewModel created!")
    }

    // O ViewModel é destruído quando o fragmento associado é desanexado ou
    // quando a atividade é concluída. Logo antes do ViewModel ser destruído,
    // o callback onCleared() é chamado.
    override fun onCleared() {
        super.onCleared()
        // log statement que monitora o ciclo de vida do GameViewModel
        Log.d("GameFragment", "GameViewModel destroyed!")
    }

    // geração de registros para compreender o ciclo de vida da ViewModel
    init {
        Log.d("GameFragment", "GameViewModel created!")
    }

    // Separar os dados de IU do app do controlador de IU (suas classes Activity / Fragment)
    // permite seguir o princípio de responsabilidade exclusiva. As atividades e fragmentos
    // são responsáveis por mostrar visualizações e dados na tela, enquanto o ViewModel
    // é responsável por armazenar e processar todos os dados necessários da IU.
    private var score = 0
    private var currentWordCount = 0
    // Dados são editáveis -> private e var. Fora do ViewModel, os dados precisam ser legíveis,
    // mas não editáveis, dados expostos como -> public e val

    // Propriedade de APOIO: currentScrambledWord com propriedade de apoio.
    // Agora a _currentScrambledWord pode ser acessada e editada somente no GameViewModel.
    // O controlador de IU GameFragment pode ler seu valor usando a propriedade
    // somente leitura, currentScrambledWord.
    private var _currentScrambledWord = "test"
    val currentScrambledWord: String
        get() = _currentScrambledWord
}