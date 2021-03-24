package com.example.myapplication.model;


import com.example.myapplication.helper.ConfiguracaoFirebase;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Produto {

    private String idProduto;
    private String nomeProduto;
    private String categoriaProduto;
    private String quantidadeProduto;
    private String precoProduto;
    private String descricaoProduto;
    private List<String> fotosProdutos;

    public Produto() {
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_produtos");
        setIdProduto( produtoRef.push().getKey());
    }

    public void salvar(){
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_produtos");

        produtoRef.child(idUsuario).child(getIdProduto()).setValue(this);

    }

    public void remover() {
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_produtos");

        produtoRef.child(idUsuario).child(getIdProduto()).removeValue();

    }



    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getCategoriaProduto() {
        return categoriaProduto;
    }

    public void setCategoriaProduto(String categoriaProduto) {
        this.categoriaProduto = categoriaProduto;
    }

    public String getQuantidadeProduto() {
        return quantidadeProduto;
    }

    public void setQuantidadeProduto(String quantidadeProduto) {
        this.quantidadeProduto = quantidadeProduto;
    }

    public String getPrecoProduto() {
        return precoProduto;
    }

    public void setPrecoProduto(String precoProduto) {
        this.precoProduto = precoProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public List<String> getFotosProdutos() {
        return fotosProdutos;
    }

    public void setFotosProdutos(List<String> fotosProdutos) {
        this.fotosProdutos = fotosProdutos;
    }
}
