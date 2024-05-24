package com.josecorral.trabajofingradojosecorral.view.fragment

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.FragmentLoginBinding
import com.josecorral.trabajofingradojosecorral.viewmodels.LoginViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient= GoogleSignIn.getClient(requireActivity(), gso)
        // Inicializar el Listener del estado de autenticación
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            updateUI(user)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(authStateListener)

    }

    @SuppressLint("StringFormatInvalid")
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            binding.twBienv.text = getString(R.string.Bienvenido, user.displayName)
            binding.bntSignOut.visibility = View.VISIBLE
        } else {
            binding.twBienv.text = getString(R.string.Bienvenido, "")
            binding.bntSignOut.visibility = View.GONE
        }
    }
    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            updateUI(user)
        }
        binding.bntAcceder.setOnClickListener {
            val email = binding.correo.text.toString().trim()
            val password = binding.contra.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Correo y contraseña son requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.signIn(email, password).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_loginFragment_to_HomeFragment)
                } else {
                    val errorMessage = task.exception?.message ?: "Error desconocido al iniciar sesión"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.bntGoogle.setOnClickListener {
            singingGoogle()
        }
        binding.bntSignOut.setOnClickListener {
            singOut()
        }

        // Crear un SpannableString para aplicar el subrayado al TextView
        val content = SpannableString(getString(R.string.olvide_contra))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)

        // Aplicar el SpannableString al TextView en tu diseño (asegúrate de tener un TextView con el id textViewLink)
        binding.olvidarPass.text = content

        // Hacer el TextView cliclable
        binding.olvidarPass.setOnClickListener {
            onLinkClick(it)
        }
        binding.bntRegistro.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_RegistroFragment)
        }
        binding.twBienv.text = getString(R.string.Bienvenido, auth.currentUser)

        viewModel.userRole.observe(viewLifecycleOwner) { role ->
            if (role == "admin") {
                // Mostrar opciones de administrador
                binding.adminOptions.visibility = View.VISIBLE
            } else {
                // Ocultar opciones de administrador
                binding.adminOptions.visibility = View.GONE
            }
        }
        binding.adminOptions.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_adminFragment)
        }
    }

    private fun onLinkClick(view: View) {
        findNavController().navigate(R.id.action_loginFragment_to_correoOlvidadoFragment)
    }


    fun singingGoogle() {
        val signIntent = googleSignInClient.signInIntent
        launcher.launch(signIntent)

    }

    fun singOut() {
        auth.signOut()
        googleSignInClient.revokeAccess().addOnCompleteListener(requireActivity()) {
            updateUI(null)
            Toast.makeText(requireActivity(), "Signed out successfully", Toast.LENGTH_SHORT).show()
        }
    }


    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if(result.resultCode == AppCompatActivity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            manageResule(task)
        }
    }

    private fun manageResule(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account!=null){
                updateUi(account)
            }
        }
    }

    private fun updateUi(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                val firebaseUser = auth.currentUser
                val userName = firebaseUser?.displayName ?: ""
                saveUserNameToFirebase(userName)
                Toast.makeText(requireActivity(), "Registrado", Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun saveUserNameToFirebase(userName: String) {
        // Aquí puedes guardar el nombre de usuario en tu base de datos Firebase
        // Por ejemplo, usando Firestore:
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users").document(auth.currentUser?.uid ?: "")
        userDocRef.update("username", userName)
            .addOnSuccessListener {
                Log.d(TAG, "Nombre de usuario actualizado correctamente en Firestore")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al actualizar el nombre de usuario en Firestore", e)
            }
    }
    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }
}
