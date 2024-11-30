package br.edu.fatecpg.academia.views

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.academia.adapter.AlunoAdapter
import br.edu.fatecpg.academia.dao.Usuario
import br.edu.fatecpg.academia.databinding.ActivityListaAlunosBinding
import br.edu.fatecpg.academia.viewModel.AtribuicaoPlanoViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ListaAlunosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaAlunosBinding
    private lateinit var adapter: AlunoAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var planoId: String

    private val atribuicaoPlanoViewModel: AtribuicaoPlanoViewModel by viewModels()  // O ViewModel permanece aqui

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaAlunosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        planoId = intent.getStringExtra("planoId") ?: "" // Pega o planoId passado pela Intent

        firestore = FirebaseFirestore.getInstance()

        // Configurar o RecyclerView
        adapter = AlunoAdapter(onClick = { aluno ->
            // Ao clicar no aluno, associar o plano a ele
            atribuirPlanoAoAluno(aluno)
        })
        binding.recyclerAlunos.layoutManager = LinearLayoutManager(this)
        binding.recyclerAlunos.adapter = adapter

        // Passar o ViewModel e o planoId para o Adapter
        adapter.setViewModel(atribuicaoPlanoViewModel, planoId)

        // Carregar lista de alunos
        carregarAlunos()
    }

    private fun carregarAlunos() {
        firestore.collection("usuarios")
            .whereEqualTo("tipo", "Aluno")
            .get()
            .addOnSuccessListener { result ->
                val alunos = result.map { document ->
                    val aluno = document.toObject(Usuario::class.java)
                    aluno.id = document.id
                    aluno
                }
                adapter.submitList(alunos) // Atualiza a lista de alunos no Adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar alunos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun atribuirPlanoAoAluno(aluno: Usuario) {
        if (planoId.isNotEmpty()) {
            // Chama o ViewModel para atribuir o plano ao aluno
            atribuicaoPlanoViewModel.atribuirPlanoAoAluno(planoId, aluno) { mensagem ->
                Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Plano inválido", Toast.LENGTH_SHORT).show()
        }
    }
}
