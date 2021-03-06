package com.example.myapplication.model;

import com.example.myapplication.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Empresa {

    private String idEmpresa;
    private String urlImagem;
    private String nome;
    private String tempo;
    private String categoria;
    private String precoEntrega;

    public Empresa() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresas")
                .child( getIdUsuario() );
        empresaRef.setValue(this);

    }

    public String getIdUsuario() {
        return idEmpresa;
    }

    public void setIdUsuario(String idUsuario) {
        this.idEmpresa = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrecoEntrega() {
        return precoEntrega;
    }

    public void setPrecoEntrega(String precoEntrega) {
        this.precoEntrega = precoEntrega;
    }
}


