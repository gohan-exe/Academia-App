package br.edu.fatecpg.academia.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.academia.dao.PlanoTreino
import br.edu.fatecpg.academia.databinding.ActivityEditarPlanoBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditarPlanoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPlanoBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura o binding da tela de edição
        binding = ActivityEditarPlanoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Recuperar o ID do plano
        val planoId = intent.getStringExtra("planoId") ?: return

        // Carregar os dados do plano
        firestore.collection("plano_de_Treino").document(planoId)
            .get()
            .addOnSuccessListener { document ->
                val plano = document.toObject(PlanoTreino::class.java)
                if (plano != null) {
                    // Preencher os campos com os dados do plano
                    binding.edtTituloPlano.setText(plano.titulo)
                    binding.edtDescricaoPlano.setText(plano.descricao)

                    // Atualizar o plano
                    binding.btnSalvarPlano.setOnClickListener {
                        val novoTitulo = binding.edtTituloPlano.text.toString().trim()
                        val novaDescricao = binding.edtDescricaoPlano.text.toString().trim()
                        val resultadoIntent = Intent()
                        resultadoIntent.putExtra("planoId", plano.id)  // Passa o ID do plano
                        resultadoIntent.putExtra("titulo", plano.titulo)  // Passa o título atualizado
                        resultadoIntent.putExtra("descricao", plano.descricao)  // Passa a descrição atualizada
                        setResult(RESULT_OK, resultadoIntent)
                        finish()

                        if (novoTitulo.isNotEmpty() && novaDescricao.isNotEmpty()) {
                            firestore.collection("plano_de_Treino")
                                .document(planoId)
                                .update("titulo", novoTitulo, "descricao", novaDescricao)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Plano atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                                    finish() // Fecha a tela de edição e volta para a anterior
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Erro ao atualizar plano: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                        }
                    }



                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar plano: ${it.message}", Toast.LENGTH_SHORT).show()
            }


    }
}
