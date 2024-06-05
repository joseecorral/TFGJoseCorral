package com.josecorral.trabajofingradojosecorral.view.product


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.josecorral.trabajofingradojosecorral.databinding.ItemProductoBinding
import com.josecorral.trabajofingradojosecorral.model.data.Producto

class ProductoAdapter(
    private val productos: List<Producto>,
    private val onProductoClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount(): Int = productos.size

    inner class ProductoViewHolder(private val binding: ItemProductoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(producto: Producto) {
            binding.tvNombreProducto.text = producto.nombre
            binding.tvPrecioProducto.text = "${producto.precio}€"
            Glide.with(binding.ivImagenProducto.context)
                .load(producto.imagenUrl)
                .into(binding.ivImagenProducto)

            binding.root.setOnClickListener {
                onProductoClick(producto)
            }
            binding.cvLock.isVisible = producto.bloqueado
        }
    }
}