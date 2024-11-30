package br.edu.fatecpg.academia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.academia.dao.Usuario
import br.edu.fatecpg.academia.databinding.ItemUsuarioBinding
import br.edu.fatecpg.academia.viewModel.AtribuicaoPlanoViewModel

class AlunoAdapter(private val onClick: (Usuario) -> Unit) :
    RecyclerView.Adapter<AlunoAdapter.AlunoViewHolder>() {

    private var alunos: List<Usuario> = listOf()
    private lateinit var viewModel: AtribuicaoPlanoViewModel
    private lateinit var planoId: String

    fun setViewModel(viewModel: AtribuicaoPlanoViewModel, planoId: String) {
        this.viewModel = viewModel
        this.planoId = planoId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlunoViewHolder {
        val binding = ItemUsuarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlunoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlunoViewHolder, position: Int) {
        val aluno = alunos[position]
        holder.bind(aluno)
    }

    override fun getItemCount(): Int = alunos.size

    fun submitList(alunos: List<Usuario>) {
        this.alunos = alunos
        notifyDataSetChanged()
    }

    inner class AlunoViewHolder(private val binding: ItemUsuarioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(aluno: Usuario) {
            binding.txtNomeAluno.text = aluno.name
            binding.txtEmailAluno.text = aluno.email

            binding.btnAtribuirPlano.setOnClickListener {
                if (::viewModel.isInitialized && planoId.isNotEmpty()) {
                    // Atribui o plano ao aluno através do ViewModel
                    viewModel.atribuirPlanoAoAluno(planoId, aluno) { mensagem ->
                        Toast.makeText(binding.root.context, mensagem, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(binding.root.context, "Plano inválido", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
