package br.edu.fatecpg.academia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.academia.dao.PlanoTreino
import br.edu.fatecpg.academia.databinding.ItemPlanoAlunoBinding

class PlanoAlunoAdapter(
    private val onFabClick: (PlanoTreino) -> Unit // Função callback
) : RecyclerView.Adapter<PlanoAlunoAdapter.PlanoAlunoViewHolder>() {

    private var planos: List<PlanoTreino> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanoAlunoViewHolder {
        val binding = ItemPlanoAlunoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlanoAlunoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanoAlunoViewHolder, position: Int) {
        val plano = planos[position]
        holder.bind(plano)
    }

    override fun getItemCount(): Int = planos.size

    fun submitList(planos: List<PlanoTreino>) {
        this.planos = planos
        notifyDataSetChanged()
    }

    inner class PlanoAlunoViewHolder(private val binding: ItemPlanoAlunoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plano: PlanoTreino) {
            val id = plano.id
            binding.txtTituloPlano.text = plano.titulo
            binding.txtDescricaoPlano.text = plano.descricao

            // Defina o click no FAB
            binding.fabRegistrarProgresso.setOnClickListener {
                onFabClick(plano) // Chama a função de callback quando o FAB for clicado
            }
        }
    }
}
