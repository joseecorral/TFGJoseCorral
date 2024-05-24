package com.josecorral.trabajofingradojosecorral.view.product


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.ItemProductoBinding
import com.josecorral.trabajofingradojosecorral.model.data.Producto

class ProductoAdapter(
    private val productos: List<Producto>,
    private val onProductoClickListener: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto)
        holder.itemView.setOnClickListener {
            onProductoClickListener(producto)
        }
    }

    override fun getItemCount(): Int = productos.size

    inner class ProductoViewHolder(private val binding: ItemProductoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(producto: Producto) {
            binding.tvNombreProducto.text = producto.nombre
            binding.tvPrecioProducto.text = producto.precio
            Glide.with(binding.ivImagenProducto.context).load(producto.imagenUrl).into(binding.ivImagenProducto)
        }
    }
}