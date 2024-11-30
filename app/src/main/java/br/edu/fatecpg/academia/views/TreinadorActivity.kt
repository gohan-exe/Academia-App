package br.edu.fatecpg.academia.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.academia.adapter.PlanoTreinoAdapter
import br.edu.fatecpg.academia.dao.PlanoTreino
import br.edu.fatecpg.academia.databinding.ActivityTreinadorBinding
import br.edu.fatecpg.academia.viewModel.PlanoTreinoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class TreinadorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTreinadorBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: PlanoTreinoAdapter

    private val viewModel: PlanoTreinoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTreinadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Inicializa o adapter do RecyclerView com as ações de clique
        adapter = PlanoTreinoAdapter(
            onEditClick = { plano ->
                abrirTelaDeEdicao(plano) // Ação para editar plano
            },
            onAtribuirClick = { plano ->
                irParaListaAlunos(plano) // Ação para atribuir plano
            },
            onDeleteClick = { plano ->
                deletarPlano(plano) // Ação para deletar plano
            }
        )

        binding.btnVoltar.setOnClickListener {
            val intent = Intent(this, EscolhaTipoActivity::class.java)
            intent.putExtra("userType", "Treinador") // Enviar o tipo novamente
            startActivity(intent)
            finish()
        }


        binding.recyclerPlanos.layoutManager = LinearLayoutManager(this)
        binding.recyclerPlanos.adapter = adapter

        // Carregar planos ao abrir a tela
        viewModel.carregarPlanos()

        // Observar os planos e atualizar a lista
        viewModel.planos.observe(this, Observer { planosList ->
            adapter.submitList(planosList)
        })

        binding.btnSalvar.setOnClickListener {
            val titulo = binding.edtTitulo.text.toString().trim()
            val descricao = binding.edtDescricao.text.toString().trim()
            val idTreinador = auth.currentUser?.uid

            if (titulo.isNotEmpty() && descricao.isNotEmpty() && idTreinador != null) {
                val planoId = UUID.randomUUID().toString()

                val plano = PlanoTreino(
                    id = planoId,
                    titulo = titulo,
                    descricao = descricao,
                )

                firestore.collection("plano_de_Treino")
                    .document(planoId)
                    .set(plano)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Plano salvo com sucesso!", Toast.LENGTH_SHORT).show()
                        viewModel.carregarPlanos()
                        binding.edtTitulo.text.clear()
                        binding.edtDescricao.text.clear()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao salvar plano: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarPlanos()
    }

    private fun abrirTelaDeEdicao(plano: PlanoTreino) {
        val intent = Intent(this, EditarPlanoActivity::class.java)
        intent.putExtra("planoId", plano.id)
        intent.putExtra("planoTitulo", plano.titulo)
        intent.putExtra("planoDescricao", plano.descricao)
        startActivityForResult(intent, EDITAR_PLANO_REQUEST_CODE)
    }

    private fun irParaListaAlunos(plano: PlanoTreino) {
        val intent = Intent(this, ListaAlunosActivity::class.java)
        intent.putExtra("planoId", plano.id)
        startActivity(intent)
    }

    private fun deletarPlano(plano: PlanoTreino) {
        firestore.collection("plano_de_Treino")
            .document(plano.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Plano deletado com sucesso!", Toast.LENGTH_SHORT).show()
                viewModel.carregarPlanos()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao deletar plano: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDITAR_PLANO_REQUEST_CODE && resultCode == RESULT_OK) {
            val planoEditado = data?.getParcelableExtra<PlanoTreino>("planoEditado")
            planoEditado?.let {
                viewModel.editarPlano(it)
            }
        }
    }

    companion object {
        private const val EDITAR_PLANO_REQUEST_CODE = 1
    }
}
