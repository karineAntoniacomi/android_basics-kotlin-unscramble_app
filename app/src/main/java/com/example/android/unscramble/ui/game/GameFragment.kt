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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Fragment where the game is played, contains the game logic. */
class GameFragment : Fragment() {
    // Create a ViewModel the first time the fragment is created. If the fragment is re-created,
    // it receives the same GameViewModel instance created by the first fragment.
    // Associa objeto ViewModel GameViewModel ao controlador de IU (atividade / fragmento) GameFragment
    // Inicializa GameViewModel usando o delegado da propriedade by viewModels() do Kotlin
    // O objeto viewModel é processado internamente pela classe delegada, viewModels, que criará o
    // objeto viewModel no primeiro acesso, manterá o valor dele durante as mudanças de
    // configuração e retornará o valor quando solicitado
    private val viewModel: GameViewModel by viewModels()

    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /** Inflate the layout XML file and return a binding object instance */
        // a instanciação da variável binding mudou para usar a vinculação de dados
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        // log statement para registrar a criação do fragmento. O callback onCreateView()
        // será acionado quando o fragmento for criado pela 1a vez e quando for recriado
        Log.d("GameFragment", "GameFragment created/re-created!")

        // registro para exibir os dados do app, a palavra, a pontuação e a contagem de palavras
        Log.d(
            "GameFragment", "Word: ${viewModel.currentScrambledWord} " +
                    "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}"
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the viewModel for data binding - this allows the bound layout access
        // to all the data in the VieWModel
        // inicializa as variáveis de layout gameViewModel e maxNoOfWords
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        // O LiveData é observável e compatível com o ciclo de vida, então  é necesário
        // transmitir o proprietário do ciclo de vida ao layout.
        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        /** Setup a click listener for the Submit and Skip buttons */
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
    }

    /** Checks the user's word, and updates the score accordingly.
     * Displays the next scrambled word.
     * After the last word, the user is shown a Dialog with the final score. */
    private fun onSubmitWord() {
        // Armazena a palavra do jogador extraindo-a do campo de texto da variável binding
        val playerWord = binding.textInputEditText.text.toString()
        // verifica a palavra do jogador
        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            // Se True, outra palavra estará disponível
            if (!viewModel.nextWord()) {
                // a partida terminou, então exibe a caixa de diálogo de aviso com a pontuação final
                showFinalScoreDialog()
            }
        } else {
            // se a palavra estiver incorreta mostra mensagem de erro
            setErrorTextField(true)
        }
    }

    /** Skips the current word without changing the score.
     * Increases the word count.
     * After the last word, the user is shown a Dialog with the final score. */
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            // Se true, exibe a palavra na tela e redefine o campo de texto
            setErrorTextField(false)
        } else {
            // Se false, e não houver mais palavras na partida, mostra diálogo com pontuação final
            showFinalScoreDialog()
        }
    }

    /** Creates and shows an AlertDialog with the final score. */
    private fun showFinalScoreDialog() {
        // classe MaterialAlertDialogBuilder para juntar cada parte da caixa de diálogo.
        // transmite o conteúdo usando o método requireContext() do fragmento.
        // O método requireContext() retorna um Context não nulo.
        MaterialAlertDialogBuilder(requireContext())
            // define o título na caixa de diálogo de aviso
            .setTitle(getString(R.string.congratulations))
            // define a mensagem para mostrar a pontuação final
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            // faz com que não seja possível cancelar a caixa de diálogo de aviso quando clicar em "Voltar"
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            // cria e exibe a caixa de diálogo de aviso
            .show()
    }

    /** Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.*/
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    /** Exits the game. */
    private fun exitGame() {
        activity?.finish()
    }

/**Sets and resets the text field error status. */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }
}
