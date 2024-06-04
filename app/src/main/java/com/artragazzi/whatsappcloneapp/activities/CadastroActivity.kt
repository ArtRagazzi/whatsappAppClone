package com.artragazzi.whatsappcloneapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.artragazzi.whatsappcloneapp.R
import com.artragazzi.whatsappcloneapp.databinding.ActivityCadastroBinding
import com.artragazzi.whatsappcloneapp.model.Usuario
import com.artragazzi.whatsappcloneapp.utils.exibirMensagemCurta
import com.artragazzi.whatsappcloneapp.utils.exibirMensagemLonga
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityCadastroBinding.inflate(layoutInflater)
    }
    private lateinit var nome:String
    private lateinit var email:String
    private lateinit var senha:String

    private val firebaseAuth by lazy{
        FirebaseAuth.getInstance()
    }
    private val firebaseDB by lazy{
        FirebaseFirestore.getInstance()
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
        iniciarToolbar()
        iniciarEventosClick()


    }

    private fun iniciarToolbar(){
        val toolbar = binding.includeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun validarCampos(): Boolean {
        nome = binding.tiNome.text.toString()
        email = binding.tiEmail.text.toString()
        senha = binding.tiSenha.text.toString()
        if(nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty()){
            return true
        }else{
            //Para tirar a mensagem de erro quando digitar novamente
            binding.tfNome.error=null
            binding.tfEmail.error=null
            binding.tfSenha.error=null
            if(nome.isEmpty()){
                binding.tfNome.error="Preencha seu Nome!"
            }
            if(email.isEmpty()){
                binding.tfEmail.error="Preencha seu Email!"
            }
            if(senha.isEmpty()){
                binding.tfSenha.error="Preencha sua Senha!"
            }
            return false
        }
    }

    private fun iniciarEventosClick(){
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()) {
                cadastrarUsuario(nome,email,senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {

        firebaseAuth.createUserWithEmailAndPassword(
            email,senha
        ).addOnCompleteListener{resultado->
            if(resultado.isSuccessful){
                val idUsuario = resultado.result.user?.uid
                if(idUsuario != null){
                    val usuario = Usuario(idUsuario,nome,email)
                    salvarUsuarioFirestore(usuario)
                }
            }
        }.addOnFailureListener { erro->
            try {
                throw erro
            }catch (erro:FirebaseAuthWeakPasswordException){
                exibirMensagemLonga("Senha muito fraca, Utilize Letras, Numeros e Caracteres Especiais")
            } catch (erro:FirebaseAuthInvalidCredentialsException){
                exibirMensagemLonga("E-mail invalido, digite um outro email")
            }catch (erro:FirebaseAuthUserCollisionException){
                exibirMensagemLonga("E-mail já cadastrado")
            }
        }
    }

    private fun salvarUsuarioFirestore(usuario: Usuario) {
        firebaseDB
            .collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                exibirMensagemCurta("Usuario cadastrado com sucesso!")
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }.addOnFailureListener {
                exibirMensagemLonga("Erro ao cadastrar Usuario!")
            }

    }
}