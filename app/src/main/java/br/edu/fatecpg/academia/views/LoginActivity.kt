package br.edu.fatecpg.academia.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.academia.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o FirebaseAuth e Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Ação de login
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Realizar o login com o email e a senha fornecidos
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            if (currentUser != null) {
                                // Log para verificar se o usuário foi autenticado
                                Log.d("LoginActivity", "Usuário autenticado com sucesso: ${currentUser.uid}")

                                // Recupera o tipo de usuário do Firestore
                                firestore.collection("usuarios").document(currentUser.uid)
                                    .get()
                                    .addOnCompleteListener { userTask ->
                                        if (userTask.isSuccessful) {
                                            val userType = userTask.result?.getString("tipo")

                                            if (userType != null) {
                                                // Redireciona para a tela de transição passando o tipo de usuário
                                                val intent = Intent(this, EscolhaTipoActivity::class.java)
                                                intent.putExtra("userType", userType)
                                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                startActivity(intent)
                                                finish()  // Fecha a LoginActivity
                                            } else {
                                                Toast.makeText(this, "Tipo de usuário desconhecido", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(this, "Erro ao recuperar tipo de usuário", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        } else {
                            // Caso o login falhe
                            Toast.makeText(this, "Credenciais inválidas: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            Log.e("LoginActivity", "Erro no login: ${task.exception?.message}")
                        }
                    }
            } else {
                // Se os campos de email ou senha estiverem vazios
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }

        // Ação do link de cadastro
        binding.txvCadastrar.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }
}
