package com.josecorral.trabajofingradojosecorral.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey
    @PropertyName("id") val id: String = "",
    @PropertyName("nombre") val nombre: String = "",
    @PropertyName("precio") val precio: String = "",
    @PropertyName("imagenUrl") val imagenUrl: String = ""
)