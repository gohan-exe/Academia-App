package br.edu.fatecpg.academia.dao

data class Atribuicao(
    var data: String? = null,        // ID do documento no Firestore (opcional)
    var alunoId: String? = null,   // ID do aluno
    var planoId: String? = null,   // ID do plano de treino
    var status: String? = null     // Status da atribuição (pendente, concluído, etc.)
)
