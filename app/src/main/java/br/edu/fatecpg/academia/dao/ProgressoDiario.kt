package br.edu.fatecpg.academia.dao

data class ProgressoDiario(
    val id: String = "",
    val idPlano: String = "", // Relaciona com o plano de treino
    val data: String = "",   // Data do progresso (yyyy-MM-dd)
    val progresso: String = "" // Descrição do progresso (ex.: "Concluído", "Parcial", etc.)
)

