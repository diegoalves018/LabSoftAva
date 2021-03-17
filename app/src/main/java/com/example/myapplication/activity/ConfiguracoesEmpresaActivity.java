package com.example.myapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.myapplication.R;
import com.example.myapplication.helper.ConfiguracaoFirebase;
import com.example.myapplication.helper.Permissoes;
import com.example.myapplication.helper.UsuarioFirebase;
import com.example.myapplication.model.Empresa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity
        implements View.OnClickListener{


    private EditText campoNome, campoDescricao, campoQuantidade, campoCategoria;
    private ImageView imagemEmpresa1, imagemEmpresa2;
    private CurrencyEditText campoPreco;
    private Empresa empresa;
    private StorageReference storage;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
            /* CASO USE A CAMERA DEPOIS => ,Manifest.permission.CAMERA*/
    };

    private List<String> listaFotosRecuperadasEmpresa = new ArrayList<>();
    private List<String> listaUrlFotosEmpresa = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        //configuracoes iniciais
        storage = ConfiguracaoFirebase.getFirebaseStorage();


        //validar permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        inicializarComponentes();


        /*configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Adicionando Empresa");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void salvarEmpresa(){

        //salvar imagem no STORAGE
        for (int i=0; i < listaFotosRecuperadasEmpresa.size(); i++){
            String urlImagem = listaFotosRecuperadasEmpresa.get(i);
            int tamanhoLista = listaFotosRecuperadasEmpresa.size();
            salvarFotosEmpresaStorage(urlImagem, tamanhoLista, i);

        }

    }

    private void salvarFotosEmpresaStorage (String urlString, int totalFotos, int contador){
        //criar nó no Storage
        StorageReference imagemEmpresa = storage.child("imagens").child("empresa")
                .child(empresa.getIdEmpresa())
                .child("imagem"+contador);

        //fazer upload do arquivo

        String nomeArquivo = UUID.randomUUID().toString();
        final StorageReference imagemRef = imagemEmpresa.child(nomeArquivo + ".jpeg");
        UploadTask uploadTask = imagemEmpresa.putFile(Uri.parse(urlString));

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();

                        //String urlConvertida = firebaseUrl.toString(); ?
                        String urlConvertida = url.toString();

                        listaUrlFotosEmpresa.add(urlConvertida);

                        if(totalFotos == listaUrlFotosEmpresa.size()){
                            empresa.setFotosEmpresas(listaUrlFotosEmpresa);
                            empresa.salvar();

                        }
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer o upload");
                Log.i("INFO","Falha ao fazer o upload" + e.getMessage());

            }
        });

    }

    private Empresa configurarEmpresa(){

        String nomeEmpresa = campoNome.getText().toString();
        String quantidadeEmpresa = campoQuantidade.getText().toString();
        String precoEmpresa = String.valueOf(campoPreco.getRawValue());
        String descricaoEmpresa = campoDescricao.getText().toString();
        String categoriaEmpresa = campoCategoria.getText().toString();

        Empresa empresa = new Empresa();
        empresa.setNome(nomeEmpresa);
        empresa.setQuantidadeEmpresa(quantidadeEmpresa);
        empresa.setPrecoEmpresa(precoEmpresa);
        empresa.setDescricaoEmpresa(descricaoEmpresa);
        empresa.setCategoriaEmpresa(categoriaEmpresa);

        return empresa;

    }

    public void validarDadosEmpresa (View view){
        empresa = configurarEmpresa();


        if (listaFotosRecuperadasEmpresa.size() != 0){
            if (!empresa.getNomeEmpresa().isEmpty()){
                if (!empresa.getCategoriaEmpresa().isEmpty()){
                    if (!empresa.getQuantidadeEmpresa().isEmpty()){
                        if (!empresa.getPrecoEmpresa().isEmpty() && !empresa.getPrecoEmpresa().equals("0")){
                            if (!empresa.getDescricaoEmpresa().isEmpty()){
                                empresa.salvar();
                                salvarEmpresa();

                            }else{
                                exibirMensagemErro("Preencha a descrição!");
                            }
                        }else{
                            exibirMensagemErro("Preencha o valor do empresa!");
                        }
                    }else{
                        exibirMensagemErro("Preencha a quantidade do empresa!");
                    }
                }else{
                    exibirMensagemErro("Preencha a categoria do empresa!");
                }
            }else{
                exibirMensagemErro("Digite o nome do empresa!");
            }
        }else{
            exibirMensagemErro("Selecione ao menos uma foto!");
        }

    }

    private void exibirMensagemErro (String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageEmpresa1:
                escolherImagemEmpresa(1);
                break;

            case R.id.imageEmpresa2:
                escolherImagemEmpresa(2);
                break;

        }
    }

    public void escolherImagemEmpresa(int requestCode){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == Activity.RESULT_OK){

            //RECUPERAR IMAGEM
            Uri imagemEmpresaSelecionada = data.getData();
            String caminhoImagemEmpresa = imagemEmpresaSelecionada.toString();

            //configura imagem no ImageView
            if (requestCode == 1){
                imagemEmpresa1.setImageURI(imagemEmpresaSelecionada);
            }else if (requestCode == 2){
                imagemEmpresa2.setImageURI(imagemEmpresaSelecionada);
            }
            listaFotosRecuperadasEmpresa.add(caminhoImagemEmpresa);
        }
    }

    private void inicializarComponentes(){
        campoNome = findViewById(R.id.editEmpresaNome);
        campoCategoria = findViewById(R.id.editEmpresaCategoria);
        campoQuantidade = findViewById(R.id.editEmpresaQuantidade);
        campoDescricao = findViewById(R.id.editEmpresaDescricao);
        campoPreco = findViewById(R.id.editEmpresaPreco);
        imagemEmpresa1 = findViewById(R.id.imageEmpresa1);
        imagemEmpresa2 = findViewById(R.id.imageEmpresa2);

        imagemEmpresa1.setOnClickListener(this);
        imagemEmpresa2.setOnClickListener(this);


        //configurar localidade pt-br
        Locale locale = new Locale("pt","BR");
        campoPreco.setLocale(locale);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissoesResultado : grantResults) {
            if (permissoesResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();

            }
        }
    }

    private void alertaValidacaoPermissao (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}