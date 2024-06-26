package com.artragazzi.whatsappcloneapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.artragazzi.whatsappcloneapp.R
import com.artragazzi.whatsappcloneapp.adapters.ViewPagerAdapter
import com.artragazzi.whatsappcloneapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy{
        FirebaseAuth.getInstance()
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
        iniciarNavegacaoAbas()
    }

    private fun iniciarNavegacaoAbas() {
        val tabLayout = binding.tlPrincipal
        val viewPager = binding.vpPrincipal

        //Adapter
        val abas = listOf("Conversas", "Contatos")
        viewPager.adapter = ViewPagerAdapter(abas, supportFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout, viewPager){aba, posicao->
            aba.text = abas[posicao]
        }.attach()

    }

    private fun iniciarToolbar(){
        val toolbar = binding.includeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Whatsapp"
        }
        addMenuProvider(
            object : MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId){
                        R.id.itemPerfil ->{
                            startActivity(Intent(applicationContext, PerfilActivity::class.java))
                        }
                        R.id.itemSair ->{
                            deslogarUsuario()
                        }
                    }
                    return true
                }

            }
        )
    }

    private fun deslogarUsuario() {
        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Não"){dialog, posicao ->}
            .setPositiveButton("Sim"){dialog, posicao->
                firebaseAuth.signOut()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
            .create().show()
    }

}