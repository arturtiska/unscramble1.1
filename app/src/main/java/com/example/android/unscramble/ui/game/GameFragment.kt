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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


//Fragmento onde o jogo é jogado, contém a lógica do jogo.
class GameFragment : Fragment() {

    // Instância de objeto de associação com acesso aos modos de exibição no layout game_fragment.xml
    private lateinit var binding: GameFragmentBinding

    //Crie um ViewModel na primeira vez que o fragmento for criado.
    //Se o fragmento for recriado, ele receberá a mesma instância do GameViewModel criada pelo
    //primeiro fragmento.
    private val viewmodel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Inflar o arquivo XML de layout e retornar uma instância de objeto de vinculação
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Definir o viewModel para vinculação de dados - isso permite o acesso ao layout acoplado
        //a todos os dados no VieWModel
        binding.gameViewModel = viewmodel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        //Especifique a visualização de fragmento como o proprietário do ciclo de vida da associação.
        //Isso é usado para que a associação possa observar as atualizações do LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        //Configure um ouvinte de clique para os botões Enviar e Ignorar.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
    }

    //Verifica a palavra do usuário e atualiza a pontuação de acordo.
    //Exibe a próxima palavra embaralhada.
    //Após a última palavra, o usuário é mostrado um diálogo com a pontuação final.
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewmodel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewmodel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }

    }

    /*
     * Ignora a palavra atual sem alterar a pontuação.
     * Aumenta a contagem de palavras.
     * Após a última palavra, o usuário é mostrado um diálogo com a pontuação final.
     */
    private fun onSkipWord() {
        if (viewmodel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    //Cria e mostra um AlertDialog com pontuação final.
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewmodel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ -> exitGame() }
            .setPositiveButton(getString(R.string.play_again)) { _, _ -> restartGame() }
            .show()
    }

    /*
     * Reinicializa os dados no ViewModel e atualiza as visualizações com os novos dados, para
     * reinicie o jogo.
     */
    private fun restartGame() {
        viewmodel.reinitializeData()
        setErrorTextField(false)
    }

    //sai do jogo
    private fun exitGame() {
        activity?.finish()
    }

    //Define e redefine o status de erro do campo de texto.
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
