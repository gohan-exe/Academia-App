package br.edu.fatecpg.academia.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import br.edu.fatecpg.academia.dao.Usuario

class AtribuicaoPlanoViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()


    // Método para atribuir plano com planoId fornecido
    fun atribuirPlanoAoAluno(planoId: String, aluno: Usuario, callback: (String) -> Unit) {
        // Log para verificar o método sendo chamado
        Log.d("TESTE", "Atribuindo plano $planoId ao aluno: ${aluno.name}")

        // Atualiza ou adiciona o campo planoId no documento do aluno
        val alunoRef = firestore.collection("usuarios").document(aluno.id)
        alunoRef.set(mapOf("planoId" to planoId), SetOptions.merge())
            .addOnSuccessListener {
                callback("Plano atribuído com sucesso ao aluno ${aluno.name}.")
            }
            .addOnFailureListener { exception ->
                Log.e("AtribuicaoPlanoViewModel", "Erro ao atribuir plano", exception)
                callback("Erro ao atribuir plano: ${exception.message}")
            }

        // Adiciona o registro da atribuição em uma coleção separada (opcional)
        val atribuicao = hashMapOf(
            "planoId" to planoId,
            "alunoId" to aluno.id,
            "data" to System.currentTimeMillis(),
            "status" to "pendente"
        )

        firestore.collection("atribuicoes")
            .add(atribuicao)
            .addOnSuccessListener {
                Log.d("AtribuicaoPlanoViewModel", "Registro de atribuição criado com sucesso")
            }
            .addOnFailureListener { exception ->
                Log.e("AtribuicaoPlanoViewModel", "Erro ao registrar atribuição", exception)
            }
    }

    // Método para atribuir plano com planoId gerado automaticamente
    fun atribuirPlanoAoAluno(aluno: Usuario, callback: (String) -> Unit) {
        // Referência para a coleção de planos
        val planoRef = firestore.collection("planos").document()

        // Gera automaticamente o planoId
        val planoId = planoRef.id

        // Registra o plano no Firestore
        planoRef.set(mapOf("alunoId" to aluno.id, "dataCriacao" to System.currentTimeMillis()))
            .addOnSuccessListener {
                // Associa o plano ao aluno
                val alunoRef = firestore.collection("usuarios").document(aluno.id)
                alunoRef.set(mapOf("planoId" to planoId), SetOptions.merge())
                    .addOnSuccessListener {
                        callback("Plano atribuído com sucesso ao aluno ${aluno.name}.")
                    }
                    .addOnFailureListener { exception ->
                        callback("Erro ao atribuir plano: ${exception.message}")
                    }
            }
            .addOnFailureListener { exception ->
                callback("Erro ao criar plano: ${exception.message}")
            }
    }
}
