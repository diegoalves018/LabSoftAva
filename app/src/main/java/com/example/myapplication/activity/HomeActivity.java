package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.myapplication.R;
import com.example.myapplication.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu - Usuário");
        setSupportActionBar(toolbar);

        //Configuracao de objetos
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_sair :
                 deslogarUsuario();
                 finish();
                 startActivity(new Intent(getApplicationContext(),AutenticacaoActivity.class));
                break;
            case R.id.menu_configuracao :
                abrirConfiguracoes();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
         try {
             autenticacao.signOut();
         }catch (Exception e){
             e.printStackTrace();
         }
    }

    private void abrirConfiguracoes() {
        startActivity(new Intent(HomeActivity.this, ConfiguracoesUsuarioActivity.class));
    }
}