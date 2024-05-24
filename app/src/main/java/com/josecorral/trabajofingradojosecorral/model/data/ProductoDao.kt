package com.josecorral.trabajofingradojosecorral.model.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: Producto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<Producto>)

    @Query("SELECT * FROM productos")
    suspend fun obtenerTodosLosProductos(): List<Producto>

    @Query("DELETE FROM productos WHERE id = :productoId")
    suspend fun eliminarProducto(productoId: String)
}