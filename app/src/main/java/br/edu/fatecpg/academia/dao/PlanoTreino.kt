package br.edu.fatecpg.academia.dao

import android.os.Parcel
import android.os.Parcelable

data class PlanoTreino(
    var id: String = "",
    val titulo: String = "",
    val descricao: String = "",
) : Parcelable {

    // Implementação obrigatória dos métodos de Parcelable
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        titulo = parcel.readString() ?: "",
        descricao = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(titulo)
        parcel.writeString(descricao)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlanoTreino> {
        override fun createFromParcel(parcel: Parcel): PlanoTreino {
            return PlanoTreino(parcel)
        }

        override fun newArray(size: Int): Array<PlanoTreino?> {
            return arrayOfNulls(size)
        }
    }
}
