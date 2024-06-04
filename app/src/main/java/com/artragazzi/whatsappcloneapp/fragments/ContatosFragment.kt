package com.artragazzi.whatsappcloneapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.artragazzi.whatsappcloneapp.R
import com.artragazzi.whatsappcloneapp.activities.MensagensActivity
import com.artragazzi.whatsappcloneapp.adapters.ContatosAdapter
import com.artragazzi.whatsappcloneapp.databinding.FragmentContatosBinding
import com.artragazzi.whatsappcloneapp.model.Usuario
import com.artragazzi.whatsappcloneapp.utils.Constantes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage


class ContatosFragment : Fragment() {

    private lateinit var binding:FragmentContatosBinding
    private lateinit var eventoSnapshot:ListenerRegistration
    private lateinit var  contatosAdapter:ContatosAdapter

    private val firebaseAuth by lazy{
        FirebaseAuth.getInstance()
    }
    private val firebaseDB by lazy{
        FirebaseFirestore.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentContatosBinding.inflate(inflater, container,false)

        contatosAdapter = ContatosAdapter{usuario->
            val intent = Intent(context, MensagensActivity::class.java)
            intent.putExtra("dadosDestinatario", usuario)
            intent.putExtra("origem", Constantes.ORIGEM_CONTATO)
            startActivity(intent)
        }
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(
            DividerItemDecoration(
                context,LinearLayoutManager.VERTICAL
            )
        )


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {
        eventoSnapshot = firebaseDB
            .collection("usuarios")
            .addSnapshotListener{querySnapshot, erro->
                val listaContatos = mutableListOf<Usuario>()
                val documentos = querySnapshot?.documents
                documentos?.forEach { documentSnapshot ->
                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    if(usuario!=null){
                        //Para nao adicionar o usuario logado na lista de contatos
                        val idUsuarioLogado = firebaseAuth.currentUser?.uid
                        if(idUsuarioLogado!=null && idUsuarioLogado!=usuario.id){
                            listaContatos.add(usuario)
                        }
                    }

                }

                if(listaContatos.isNotEmpty()){
                    contatosAdapter.adicionarLista(listaContatos)
                }


            }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }

}