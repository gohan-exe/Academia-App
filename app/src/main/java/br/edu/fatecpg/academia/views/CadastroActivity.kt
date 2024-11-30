package br.edu.fatecpg.academia.views

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.academia.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewBinding
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o FirebaseAuth e o Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.edtNome.text.toString()
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()

            // Verifica se o tipo de usuário foi selecionado
            val selectedRadioButtonId = binding.radioGroupTipoUsuario.checkedRadioButtonId
            val tipoUsuario = if (selectedRadioButtonId != -1) {
                val selectedRadioButton: RadioButton = findViewById(selectedRadioButtonId)
                selectedRadioButton.text.toString()
            } else {
                null
            }

            if (nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty() && tipoUsuario != null) {
                // Usando o e-mail e senha para criar o usuário
                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Se o cadastro for bem-sucedido, salvar no Firestore
                            val user = auth.currentUser
                            val userId = user?.uid

                            if (userId != null) {
                                // Cria um mapa com os dados que serão salvos
                                val userData = hashMapOf(
                                    "name" to nome,
                                    "email" to email,
                                    "tipo" to tipoUsuario
                                )

                                // Salva os dados na coleção 'usuarios' no Firestore
                                firestore.collection("usuarios")
                                    .document(userId) // O documento é identificado pelo userId
                                    .set(userData)
                                    .addOnCompleteListener { databaseTask ->
                                        if (databaseTask.isSuccessful) {
                                            // Dados salvos com sucesso
                                            Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()

                                            // Limpa os campos
                                            binding.edtNome.text.clear()
                                            binding.edtEmail.text.clear()
                                            binding.edtSenha.text.clear()

                                            // Redireciona para a tela de login
                                            val intent = Intent(this, LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()  // Fecha a tela de cadastro
                                        } else {
                                            // Se falhar ao salvar os dados no banco
                                            Toast.makeText(this, "Erro ao salvar dados no banco", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                // Se o ID do usuário for nulo
                                Toast.makeText(this, "Erro ao obter ID do usuário", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Se falhar ao criar o usuário
                            Toast.makeText(this, "Erro ao criar o usuário: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Se os campos não forem preenchidos corretamente
                Toast.makeText(this, "Por favor, preencha todos os campos e selecione o tipo de usuário!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
