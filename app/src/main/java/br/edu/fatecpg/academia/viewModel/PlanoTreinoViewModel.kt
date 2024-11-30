package br.edu.fatecpg.academia.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.edu.fatecpg.academia.dao.PlanoTreino
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PlanoTreinoViewModel : ViewModel() {

    private val _planos = MutableLiveData<List<PlanoTreino>>()
    val planos: LiveData<List<PlanoTreino>> get() = _planos
    private val _erro = MutableLiveData<String>()
    val erro: LiveData<String> get() = _erro

    private val firestore = FirebaseFirestore.getInstance()

    fun carregarPlanos() {
        firestore.collection("plano_de_Treino")
            .get()
            .addOnSuccessListener { result ->
                val listaDePlanos = mutableListOf<PlanoTreino>()
                for (document in result) {
                    val plano = document.toObject(PlanoTreino::class.java)
                    listaDePlanos.add(plano)
                }
                _planos.value = listaDePlanos
            }
            .addOnFailureListener { exception ->
                Log.e("PlanoTreinoViewModel", "Erro ao carregar planos", exception)
                _erro.value = "Erro ao carregar planos" // Atualiza o LiveData de erro
            }
    }


    fun editarPlano(plano: PlanoTreino) {
        firestore.collection("plano_de_Treino").document(plano.id)
            .set(plano)
            .addOnSuccessListener {
                // Atualiza a lista localmente sem precisar recarregar tudo
                _planos.value = _planos.value?.map { if (it.id == plano.id) plano else it }
            }
            .addOnFailureListener {
                Log.e("PlanoTreinoViewModel", "Erro ao editar plano", it)
            }
    }

    fun carregarPlanosAluno(idAluno: String) {
        firestore.collection("plano_de_Treino")
            .whereEqualTo("idAluno", idAluno)
            .get()
            .addOnSuccessListener { result ->
                val listaDePlanos = result.map { it.toObject(PlanoTreino::class.java) }
                _planos.value = listaDePlanos
            }
            .addOnFailureListener { exception ->
                Log.e("PlanoTreinoViewModel", "Erro ao carregar planos do aluno", exception)
            }
    }



}
