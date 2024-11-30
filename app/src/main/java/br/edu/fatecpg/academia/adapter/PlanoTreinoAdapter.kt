package br.edu.fatecpg.academia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.academia.dao.PlanoTreino
import br.edu.fatecpg.academia.databinding.ItemPlanoBinding

class PlanoTreinoAdapter(
    private val onEditClick: (PlanoTreino) -> Unit,
    private val onAtribuirClick: (PlanoTreino) -> Unit, // Passando o parâmetro PlanoTreino aqui
    private val onDeleteClick: (PlanoTreino) -> Unit // Novo parâmetro
) : ListAdapter<PlanoTreino, PlanoTreinoAdapter.PlanoTreinoViewHolder>(PlanoTreinoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanoTreinoViewHolder {
        val binding = ItemPlanoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanoTreinoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanoTreinoViewHolder, position: Int) {
        val plano = getItem(position)
        holder.bind(plano)
    }

    inner class PlanoTreinoViewHolder(private val binding: ItemPlanoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plano: PlanoTreino) {
            binding.txtTitulo.text = plano.titulo
            binding.txtDescricao.text = plano.descricao

            // Configura o clique para o botão de editar
            binding.btnEditar.setOnClickListener {
                onEditClick(plano)
            }

            // Configura o clique para o botão de atribuir
            binding.btnAtribuir.setOnClickListener {
                onAtribuirClick(plano)  // Passa o plano para o onAtribuirClick
            }

            // Configura o clique do FAB de deletar
            binding.fabDeletar.setOnClickListener {
                onDeleteClick(plano) // Passa o plano para o onDeleteClick
            }
        }
    }

    class PlanoTreinoDiffCallback : DiffUtil.ItemCallback<PlanoTreino>() {
        override fun areItemsTheSame(oldItem: PlanoTreino, newItem: PlanoTreino): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlanoTreino, newItem: PlanoTreino): Boolean {
            return oldItem == newItem
        }
    }
}
