package br.edu.fatecpg.academia.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.academia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar ações dos botões
        binding.btnLogin.setOnClickListener {
            // Redireciona para a tela de login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnCadastro.setOnClickListener {
            // Redireciona para a tela de cadastro
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }
}
