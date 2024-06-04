package com.artragazzi.whatsappcloneapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.artragazzi.whatsappcloneapp.R
import com.artragazzi.whatsappcloneapp.databinding.ActivityPerfilBinding
import com.artragazzi.whatsappcloneapp.utils.exibirMensagemCurta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityPerfilBinding.inflate(layoutInflater)
    }
    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false
    //Adicionado por conta da versao do Android
    private var temPermissaoStorage = false

    private val firebaseAuth by lazy{
        FirebaseAuth.getInstance()
    }
    private val storage by lazy{
        FirebaseStorage.getInstance()
    }
    private val firebaseDB by lazy{
        FirebaseFirestore.getInstance()
    }




    private val gerenciadorGaleria = registerForActivityResult(ActivityResultContracts.GetContent()){
        uri->
        if(uri!=null){
            binding.ivPerfil.setImageURI(uri)
            uploadImagemStorage(uri)
        }else{
            exibirMensagemCurta("Nenhuma imagem selecionada")
        }
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciaisUsuario()
    }

    private fun recuperarDadosIniciaisUsuario() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if(idUsuario!=null){
            firebaseDB
                .collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener { documentSnapshot->
                    val dadosUsuario = documentSnapshot.data
                    if(dadosUsuario!=null){
                        val nome = dadosUsuario["nome"] as String
                        val foto = dadosUsuario["foto"] as String
                        binding.editNomePerfil.setText(nome)
                        if(foto.isNotEmpty()){
                            Picasso.get()
                                .load(foto)
                                .into(binding.ivPerfil)
                        }
                    }
                }

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
        iniciarToolbar()
        solicitarPermissoes()
        inicializarEventosClique()
    }

    private fun uploadImagemStorage(uri: Uri) {
        val idUsuario = firebaseAuth.currentUser?.uid
        if(idUsuario!=null){
            storage
                .getReference("fotos")
                .child("usuarios")
                .child(idUsuario)
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener {task->
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener {uri->
                        val dados = mapOf("foto" to uri.toString())
                        atualizarDadosPerfil(idUsuario,dados)
                    }

                    exibirMensagemCurta("Sucesso ao fazer upload da imagem") }
                .addOnFailureListener{exibirMensagemCurta("Erro ao fazer upload da Imagem")}
        }
    }

    private fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>){

        firebaseDB
            .collection("usuarios")
            .document(idUsuario)
            .update(dados)
            .addOnSuccessListener {  exibirMensagemCurta("Sucesso ao atualizar perfil do usuario")}
            .addOnFailureListener { exibirMensagemCurta("Erro ao atualizar perfil do usuario") }
    }

    private fun inicializarEventosClique() {
        binding.fabSelecionarImg.setOnClickListener {
            if(temPermissaoGaleria || temPermissaoStorage){
                gerenciadorGaleria.launch("image/*")
            }else{
                exibirMensagemCurta("Não tem permissão para acessar galeria")
                solicitarPermissoes()
            }
        }

        binding.btnAtualizar.setOnClickListener {
            val nomeUsuario = binding.editNomePerfil.text.toString()
            if(nomeUsuario.isNotEmpty()){
                val idUsuario = firebaseAuth.currentUser?.uid
                if(idUsuario!=null){
                    val dados = mapOf(
                        "nome" to nomeUsuario
                    )
                    atualizarDadosPerfil(idUsuario,dados)
                }

            }else{
                exibirMensagemCurta("Preecha o nome para atualizar")
            }
        }
    }

    private fun solicitarPermissoes() {

        //Verifico se usuário já tem permissão
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        //LISTA DE PERMISSÕES NEGADAS
        val listaPermissoesNegadas = mutableListOf<String>()
        if( !temPermissaoCamera )
            listaPermissoesNegadas.add( Manifest.permission.CAMERA )
        if( !temPermissaoGaleria )
            listaPermissoesNegadas.add( Manifest.permission.READ_MEDIA_IMAGES )
        if( !temPermissaoStorage )
            listaPermissoesNegadas.add( Manifest.permission.READ_EXTERNAL_STORAGE )

        if( listaPermissoesNegadas.isNotEmpty() ){

            //Solicitar multiplas permissões
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){ permissoes ->

                temPermissaoCamera = permissoes[Manifest.permission.CAMERA]
                    ?: temPermissaoCamera

                temPermissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES]
                    ?: temPermissaoGaleria

                temPermissaoStorage = permissoes[Manifest.permission.READ_EXTERNAL_STORAGE]
                    ?: temPermissaoStorage

            }
            gerenciadorPermissoes.launch( listaPermissoesNegadas.toTypedArray() )

        }

    }

    private fun iniciarToolbar(){
        val toolbar = binding.includeToolbarPerfil.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}