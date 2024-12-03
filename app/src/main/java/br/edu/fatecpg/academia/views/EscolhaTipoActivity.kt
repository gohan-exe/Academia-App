package br.edu.fatecpg.academia.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.academia.databinding.ActivityEscolhaTipoBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class EscolhaTipoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEscolhaTipoBinding
    private lateinit var barChart: BarChart  // Corrigir a declaração da variável

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEscolhaTipoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o gráfico
        barChart = binding.barChart  // Corrigir a inicialização do gráfico

        // Receber o tipo de usuário da Intent
        val userType = intent.getStringExtra("userType")

        if (userType == "Aluno") {
            // Mostrar apenas a opção de Aluno
            binding.btnAluno.visibility = View.VISIBLE
            binding.btnTreinador.visibility = View.INVISIBLE
            binding.txvTitulo.text = "Seu Redimento"
        } else if (userType == "Treinador") {
            // Mostrar apenas a opção de Treinador
            binding.btnTreinador.visibility = View.VISIBLE
            binding.btnAluno.visibility = View.INVISIBLE
            binding.txvTitulo.text = "Todas as atribuições"
        }

        // Configurar os botões
        binding.btnAluno.setOnClickListener {
            // Ir para a tela do Aluno
            val intent = Intent(this, AlunoActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnTreinador.setOnClickListener {
            // Ir para a tela do Treinador
            val intent = Intent(this, TreinadorActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Consultar Firestore e carregar o gráfico
        loadGraphData()
    }

    private fun loadGraphData() {
        val db = FirebaseFirestore.getInstance()
        val atribuicoesRef = db.collection("atribuicoes")

        // Obter o ID do usuário logado (supondo que você use FirebaseAuth)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Recuperar o tipo de usuário do Firestore (supondo que você tenha um documento de usuário com o campo "tipo")
        val userDocRef = db.collection("usuarios").document(userId!!)  // A coleção "usuarios" tem os dados do usuário

        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // Supondo que o campo "tipo" no Firestore seja "aluno" ou "treinador"
                val userType = documentSnapshot.getString("tipo") ?: "aluno" // Default é "aluno" caso não exista o campo

                // Se o usuário for 'treinador', ele verá todas as atribuições
                val query: Query = if (userType == "Treinador") {
                    // O treinador vê todas as atribuições (sem filtro de alunoId)
                    atribuicoesRef
                } else {
                    // O aluno vê apenas as atribuições que pertencem a ele (filtrando pelo alunoId)
                    atribuicoesRef.whereEqualTo("alunoId", userId)
                }

                // Agora, contamos os documentos com os estados "pendente" e "concluido"
                val queryPendente = query.whereEqualTo("status", "pendente")
                val queryConcluido = query.whereEqualTo("status", "concluido")

                // Contagem de atribuições "pendente"
                queryPendente.get().addOnSuccessListener { snapshotPendente ->
                    val pendenteCount = snapshotPendente.size()

                    // Contagem de atribuições "concluido"
                    queryConcluido.get().addOnSuccessListener { snapshotConcluido ->
                        val concluidoCount = snapshotConcluido.size()

                        // Preencher o gráfico com os dados
                        val entries = ArrayList<BarEntry>()
                        entries.add(BarEntry(0f, pendenteCount.toFloat()))
                        entries.add(BarEntry(1f, concluidoCount.toFloat()))

                        val barDataSet = BarDataSet(entries, "Atribuições")

                        // Definindo as cores das barras
                        val colors = ArrayList<Int>()
                        colors.add(Color.YELLOW)  // Cor para a barra "Pendente"
                        colors.add(Color.GREEN)   // Cor para a barra "Concluído"
                        barDataSet.colors = colors

                        val barData = BarData(barDataSet)

                        // Configuração do gráfico
                        barChart.data = barData

                        // Configurar a cor dos textos (labels) para branco
                        barChart.xAxis.textColor = Color.WHITE  // Cor dos textos no eixo X
                        barChart.axisLeft.textColor = Color.WHITE  // Cor dos textos no eixo Y
                        barChart.axisRight.textColor = Color.WHITE  // Cor dos textos no eixo Y direito (se existir)
                        barChart.legend.textColor = Color.WHITE  // Cor do texto da legenda

                        // Desabilitar as labels sobre as barras
                        barChart.xAxis.setDrawLabels(false)  // Isso remove os textos "Pendente" e "Concluído" no topo das barras

                        // Remover a descrição (label) do gráfico
                        barChart.description.isEnabled = false

                        // Configurar a legenda com os rótulos e cores
                        val labels = ArrayList<LegendEntry>()
                        labels.add(LegendEntry("Pendente", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.YELLOW))
                        labels.add(LegendEntry("Concluído", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.GREEN))

                        val legend = barChart.legend
                        legend.isEnabled = true  // Habilita a legenda
                        legend.setCustom(labels)  // Passando a lista de LegendEntry

                        // Configura a legenda para ser horizontal
                        legend.orientation = Legend.LegendOrientation.HORIZONTAL  // Alterando para horizontal
                        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM  // Colocando a legenda na parte inferior
                        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT  // Posicionando a legenda no centro

                        // Aumentar o espaço entre os itens da legenda para garantir que fiquem mais afastados
                        legend.xEntrySpace = 130f  // Espaçamento horizontal entre os itens da legenda

                        // Configurar os rótulos do eixo X
                        barChart.xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                            arrayOf("Pendente", "Concluído")
                        )

                        // Garantir que os números no eixo Y esquerdo sejam inteiros
                        barChart.axisLeft.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return value.toInt().toString()  // Convertendo para inteiro
                            }
                        }

                        // Garantir que os números no eixo Y direito sejam inteiros
                        barChart.axisRight.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return value.toInt().toString()  // Convertendo para inteiro
                            }
                        }

                        // Atualizar o gráfico
                        barChart.invalidate() // Atualizar o gráfico
                    }
                }
            }
        }
    }





}
