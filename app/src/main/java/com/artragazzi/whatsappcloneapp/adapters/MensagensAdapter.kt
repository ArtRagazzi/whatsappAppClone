package com.artragazzi.whatsappcloneapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.artragazzi.whatsappcloneapp.databinding.ItemContatosBinding
import com.artragazzi.whatsappcloneapp.databinding.ItemMensagemRemetenteBinding
import com.artragazzi.whatsappcloneapp.databinding.ItemMensagensDestinatarioBinding
import com.artragazzi.whatsappcloneapp.model.Mensagem
import com.artragazzi.whatsappcloneapp.model.Usuario
import com.artragazzi.whatsappcloneapp.utils.Constantes
import com.google.firebase.auth.FirebaseAuth

class ConversasAdapter : Adapter<ViewHolder>() {


    private var listaMensagens = emptyList<Mensagem>()
    fun adicionarLista(lista:List<Mensagem>){
        listaMensagens = lista
        notifyDataSetChanged()
    }
    class MensagensRemententesViewHolder(
        private val binding: ItemMensagemRemetenteBinding
    ) : ViewHolder(binding.root){


        fun bind(mensagem: Mensagem){
            binding.txtMensagemRemetente.text = mensagem.mensagem
        }
        companion object{
            fun inflarLayout(parent: ViewGroup):MensagensRemententesViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMensagemRemetenteBinding.inflate(inflater, parent, false)
                return MensagensRemententesViewHolder(itemView)
            }
        }

    }


    class MensagensDestinatarioViewHolder(
        private val binding: ItemMensagensDestinatarioBinding
    ) : ViewHolder(binding.root){

        fun bind(mensagem: Mensagem){
            binding.txtMensagemDestinatario.text = mensagem.mensagem
        }
        companion object{
            fun inflarLayout(parent: ViewGroup):MensagensDestinatarioViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMensagensDestinatarioBinding.inflate(inflater, parent, false)
                return MensagensDestinatarioViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val mensagem = listaMensagens[position]
        val idUsuarioLogado = FirebaseAuth.getInstance().currentUser?.uid.toString()

        return if(idUsuarioLogado == mensagem.idUsuario){
            Constantes.TIPO_REMETENTE
        }else{
            Constantes.TIPO_DESTINATARIO
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType == Constantes.TIPO_REMETENTE){
           return MensagensRemententesViewHolder.inflarLayout(parent)
        }else{
            return MensagensDestinatarioViewHolder.inflarLayout(parent)
        }
    }



    override fun getItemCount(): Int {
        return listaMensagens.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mensagem = listaMensagens[position]
        when(holder){
            is MensagensRemententesViewHolder -> holder.bind(mensagem)
            is MensagensDestinatarioViewHolder -> holder.bind(mensagem)
        }


    }
}