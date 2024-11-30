package br.edu.fatecpg.academia.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.academia.adapter.PlanoAlunoAdapter
import br.edu.fatecpg.academia.dao.Atribuicao
import br.edu.fatecpg.academia.dao.PlanoTreino
import br.edu.fatecpg.academia.databinding.ActivityAlunoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AlunoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlunoBinding
    private lateinit var adapter: PlanoAlunoAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlunoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        configurarRecyclerView()
        carregarPlanosAtribuidos()

        binding.btnVoltar.setOnClickListener {
            val intent = Intent(this, EscolhaTipoActivity::class.java)
            intent.putExtra("userType", "Aluno") // Enviar o tipo novamente
            startActivity(intent)
            finish() // Fecha a tela atual sem deslogar o usuário
        }
    }

    private fun configurarRecyclerView() {
        adapter = PlanoAlunoAdapter { plano ->
            registrarProgresso(plano)
        }
        binding.recyclerPlanosAluno.layoutManager = LinearLayoutManager(this)
        binding.recyclerPlanosAluno.adapter = adapter
    }

    private fun carregarPlanosAtribuidos() {
        val alunoId = auth.currentUser?.uid

        if (alunoId.isNullOrEmpty()) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("atribuicoes")
            .whereEqualTo("alunoId", alunoId)
            .whereEqualTo("status", "pendente")
            .get()
            .addOnSuccessListener { atribuicoesResult ->
                val planoIds = atribuicoesResult.documents.mapNotNull { it.getString("planoId") }

                if (planoIds.isEmpty()) {
                    Toast.makeText(this, "Nenhum plano atribuído encontrado.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                firestore.collection("plano_de_Treino")
                    .whereIn("id", planoIds)
                    .get()
                    .addOnSuccessListener { planosResult ->
                        val planos = planosResult.documents.map { document ->
                            document.toObject(PlanoTreino::class.java)!!.apply {
                                id = document.id
                            }
                        }

                        if (planos.isEmpty()) {
                            Toast.makeText(this, "Planos atribuídos não encontrados.", Toast.LENGTH_SHORT).show()
                        } else {
                            adapter.submitList(planos)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao carregar detalhes dos planos.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar atribuições.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun registrarProgresso(plano: PlanoTreino) {
        val alunoId = auth.currentUser?.uid

        if (alunoId.isNullOrEmpty()) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("atribuicoes")
            .whereEqualTo("alunoId", alunoId)
            .whereEqualTo("planoId", plano.id)
            .whereEqualTo("status", "pendente")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "Plano já foi concluído ou não encontrado.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val documento = result.documents.first()

                // Atualizar o status diretamente no Firestore
                firestore.collection("atribuicoes")
                    .document(documento.id) // Usa o ID do documento no Firestore
                    .update("status", "concluido")
                    .addOnSuccessListener {
                        Toast.makeText(this, "Progresso registrado com sucesso!", Toast.LENGTH_SHORT).show()
                        carregarPlanosAtribuidos()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao registrar progresso.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar atribuição.", Toast.LENGTH_SHORT).show()
            }
    }

}
