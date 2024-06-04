package com.artragazzi.whatsappcloneapp.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.artragazzi.whatsappcloneapp.R
import com.artragazzi.whatsappcloneapp.databinding.ActivityCadastroBinding
import com.artragazzi.whatsappcloneapp.databinding.ActivityMensagensBinding
import com.artragazzi.whatsappcloneapp.model.Usuario
import com.artragazzi.whatsappcloneapp.utils.Constantes
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityMensagensBinding.inflate(layoutInflater)
    }

    private var dadosDestinatario: Usuario? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recuperarDadosUsuarioDestinatario()
        iniciarToolbar()
    }

    private fun recuperarDadosUsuarioDestinatario() {
        val extras = intent.extras
        if(extras!=null){
            val origim = extras.getString("origem")
            if(origim == Constantes.ORIGEM_CONTATO){

                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("dadosDestinatario", Usuario::class.java)
                }else{
                    extras.getParcelable("dadosDestinatario")
                }

            }else if(origim == Constantes.ORIGEM_CONVERSA){
                //Recuperar os dados da conversa

            }
        }
    }

    private fun iniciarToolbar(){
        val toolbar = binding.tbMensagens
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if(dadosDestinatario != null){
                binding.txtNome.text = dadosDestinatario!!.nome
                Picasso.get()
                    .load(dadosDestinatario!!.foto)
                    .into(binding.ivPerfil)
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

}