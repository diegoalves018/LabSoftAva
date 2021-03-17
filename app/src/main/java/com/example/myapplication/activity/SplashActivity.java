package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.myapplication.R;

public class SplashActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override public void run() {
                abrirAutenticacao();
            }
        }, 3000);
    }

    private void abrirAutenticacao() {
        Intent intent = new Intent(SplashActivity.this, AutenticacaoActivity.class);
        startActivity(intent);
        finish();
    }
}