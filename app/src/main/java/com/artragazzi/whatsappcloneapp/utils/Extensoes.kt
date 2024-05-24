package com.artragazzi.whatsappcloneapp.utils

import android.app.Activity
import android.widget.Toast

fun Activity.exibirMensagemCurta(msg:String){
    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
}
fun Activity.exibirMensagemLonga(msg:String){
    Toast.makeText(this,msg, Toast.LENGTH_LONG).show()
}