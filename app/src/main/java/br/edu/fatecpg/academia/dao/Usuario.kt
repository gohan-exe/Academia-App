package br.edu.fatecpg.academia.dao

data class Usuario(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var tipo: String = "", // Aluno ou Treinador
)

