package com.josecorral.trabajofingradojosecorral.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.FragmentPedidoBinding
import com.josecorral.trabajofingradojosecorral.model.data.AppDatabase
import com.josecorral.trabajofingradojosecorral.model.data.Producto
import com.josecorral.trabajofingradojosecorral.view.product.ProductoAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PedidoFragment : Fragment() {

    private lateinit var binding: FragmentPedidoBinding
    private lateinit var productoAdapter: ProductoAdapter
    private val productos = mutableListOf<Producto>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPedidoBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        binding.btnFinalizarCompra.setOnClickListener {
            finalizarCompra()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productoAdapter = ProductoAdapter(productos) { producto ->
            // Maneja clics en los productos si es necesario
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productoAdapter
        }

        cargarProductosDelCarrito()
    }
    private fun finalizarCompra() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // El usuario está autenticado, procede a guardar la compra
            guardarCompraEnFirestore(currentUser.uid)
        } else {
            Toast.makeText(requireContext(), "Todavia no estas registrado ", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_pedidoFragment_to_loginFragment)
        }
    }
    private fun guardarCompraEnFirestore(userId: String) {
        // Obtener los productos del carrito y guardarlos en Firestore con la referencia al usuario
        val db = FirebaseFirestore.getInstance()

        // Asegúrate de que productosEnCarrito es una lista de mapas o de un tipo que Firebase pueda serializar
        val productosEnCarrito = productos.map { producto ->
            mapOf(
                "id" to producto.id,
                "nombre" to producto.nombre,
                "precio" to producto.precio,
                "imagenUrl" to producto.imagenUrl
            )
        }

        val pedido = hashMapOf(
            "userId" to userId,
            "productos" to productosEnCarrito,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("pedidos")
            .add(pedido)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Compra finalizada exitosamente", Toast.LENGTH_SHORT).show()
                // Limpiar el carrito y navegar a la página de confirmación o home
                limpiarCarrito()
                findNavController().navigate(R.id.action_pedidoFragment_to_homeFragment)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al finalizar la compra: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun cargarProductosDelCarrito() {
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val productosCarrito = db.productoDao().obtenerTodosLosProductos()
            withContext(Dispatchers.Main) {
                productos.clear()
                productos.addAll(productosCarrito)
                productoAdapter.notifyDataSetChanged()
            }
        }
    }
    private fun limpiarCarrito() {
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            db.productoDao().eliminarTodosLosProductos()
            withContext(Dispatchers.Main) {
                productos.clear()
                productoAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Carrito limpiado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
