package com.josecorral.trabajofingradojosecorral.view.fragment

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
import com.google.android.gms.common.api.ApiException
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
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(requireActivity(), gso)

        // Inicializar el Listener del estado de autenticación
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            updateUI(user)
        }
    }
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            binding.twBienv.text = getString(R.string.Bienvenido, user.displayName)
            binding.bntSignOut.visibility = View.VISIBLE
        } else {
            binding.twBienv.text = getString(R.string.Bienvenido, "")
            binding.bntSignOut.visibility = View.GONE
        }
    }

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
            signInWithGoogle()
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

    fun signInWithGoogle() {
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


    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        } else {
            Log.e(TAG, "Google Sign-In failed with result code: ${result.resultCode}")
            Toast.makeText(requireActivity(), "Google Sign-In failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                Log.d(TAG, "Google Sign-In successful, account: ${account.email}")
                firebaseAuthWithGoogle(account.idToken!!)
            }
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode, e)
            Toast.makeText(requireActivity(), "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            updateUI(null)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithCredential:success")
                val firebaseUser = auth.currentUser
                val userName = firebaseUser?.displayName ?: ""
                saveUserNameToFirebase(userName)
                Toast.makeText(requireActivity(), "Registrado", Toast.LENGTH_SHORT).show()
                updateUI(firebaseUser)
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                Toast.makeText(requireActivity(), "Authentication Failed.", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }
    private fun saveUserNameToFirebase(userName: String) {
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

    companion object {
        private const val TAG = "LoginFragment"
    }
}