package com.artragazzi.whatsappcloneapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.artragazzi.whatsappcloneapp.databinding.ActivityLoginBinding
import com.artragazzi.whatsappcloneapp.utils.exibirMensagemCurta
import com.artragazzi.whatsappcloneapp.utils.exibirMensagemLonga
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var email:String
    private lateinit var senha:String

    private val firebaseAuth by lazy{
        FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    //Verifica se o usuario ja esta logado, se sim, manda para tela principal
    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if(usuarioAtual != null){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        inicializarEventosClick()

    }

    private fun inicializarEventosClick(){
        binding.txtCadastro.setOnClickListener {
            startActivity(Intent(this,CadastroActivity::class.java))
        }

        binding.btnLogar.setOnClickListener {
            if(validarCampos()){
                logarUsuario()
            }
        }
    }
    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(email,senha).addOnSuccessListener {
            exibirMensagemCurta("Logado com sucesso!")
            startActivity(Intent(this,MainActivity::class.java))
        }.addOnFailureListener { erro->
            try {
                throw erro
            }catch (erro: FirebaseAuthInvalidUserException){
                exibirMensagemLonga("Email não cadastrado")
            } catch (erro: FirebaseAuthInvalidCredentialsException){
                exibirMensagemLonga("E-mail ou senha invalido")
            }
        }
    }
    private fun validarCampos(): Boolean {
        email = binding.tiEmail.text.toString()
        senha = binding.tiSenha.text.toString()
        if(email.isNotEmpty() && senha.isNotEmpty()){
            return true
        }else{
            //Para tirar a mensagem de erro quando digitar novamente
            binding.tfEmail.error=null
            binding.tfSenha.error=null
            if(email.isEmpty()){
                binding.tfEmail.error="Preencha seu Email!"
            }
            if(senha.isEmpty()){
                binding.tfSenha.error="Preencha sua Senha!"
            }
            return false
        }
    }
}