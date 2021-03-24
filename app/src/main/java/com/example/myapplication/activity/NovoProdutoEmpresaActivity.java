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
import com.example.myapplication.model.Produto;
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

public class NovoProdutoEmpresaActivity extends AppCompatActivity
                                        implements View.OnClickListener{


    private EditText campoNome, campoDescricao, campoQuantidade, campoCategoria;
    private ImageView imagemProduto1, imagemProduto2;
    private CurrencyEditText campoPreco;
    private Produto produto;
    private StorageReference storage;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
            /* CASO USE A CAMERA DEPOIS => ,Manifest.permission.CAMERA*/
    };

    private List<String> listaFotosRecuperadasProduto = new ArrayList<>();
    private List<String> listaUrlFotosProduto = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        //configuracoes iniciais
        storage = ConfiguracaoFirebase.getFirebaseStorage();


        //validar permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        inicializarComponentes();


        /*configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Adicionando Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void salvarProduto(){

        //salvar imagem no STORAGE
        for (int i=0; i < listaFotosRecuperadasProduto.size(); i++){
            String urlImagem = listaFotosRecuperadasProduto.get(i);
            int tamanhoLista = listaFotosRecuperadasProduto.size();
            salvarFotosProdutoStorage(urlImagem, tamanhoLista, i);

        }

    }

    private void salvarFotosProdutoStorage (String urlString, int totalFotos, int contador){
        //criar nó no Storage
        StorageReference imagemProduto = storage.child("imagens").child("produto")
                .child(produto.getIdProduto())
                .child("imagem"+contador);

        //fazer upload do arquivo

        String nomeArquivo = UUID.randomUUID().toString();
        final StorageReference imagemRef = imagemProduto.child(nomeArquivo + ".jpeg");
        UploadTask uploadTask = imagemProduto.putFile(Uri.parse(urlString));

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();


                        String urlConvertida = url.toString();

                        listaUrlFotosProduto.add(urlConvertida);

                        if(totalFotos == listaUrlFotosProduto.size()){
                            produto.setFotosProdutos(listaUrlFotosProduto);
                            produto.salvar();

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

    private Produto configurarProduto(){

        String nomeProduto = campoNome.getText().toString();
        String quantidadeProduto = campoQuantidade.getText().toString();
        String precoProduto = campoPreco.getText().toString();
        String descricaoProduto = campoDescricao.getText().toString();
        String categoriaProduto = campoCategoria.getText().toString();

        Produto produto = new Produto();
        produto.setNomeProduto(nomeProduto);
        produto.setQuantidadeProduto(quantidadeProduto);
        produto.setPrecoProduto(precoProduto);
        produto.setDescricaoProduto(descricaoProduto);
        produto.setCategoriaProduto(categoriaProduto);

        return produto;

    }

    public void validarDadosProduto (View view){
         produto = configurarProduto();
        String precoProduto = String.valueOf(campoPreco.getRawValue());


         if (listaFotosRecuperadasProduto.size() != 0){
               if (!produto.getNomeProduto().isEmpty()){
                   if (!produto.getCategoriaProduto().isEmpty()){
                       if (!produto.getQuantidadeProduto().isEmpty()){
                           if (!precoProduto.isEmpty() && !precoProduto.equals("0")){
                               if (!produto.getDescricaoProduto().isEmpty()){
                                   produto.salvar();
                                    salvarProduto();

                               }else{
                                   exibirMensagemErro("Preencha a descrição!");
                               }
                           }else{
                               exibirMensagemErro("Preencha o valor do produto!");
                           }
                       }else{
                           exibirMensagemErro("Preencha a quantidade do produto!");
                       }
                   }else{
                       exibirMensagemErro("Preencha a categoria do produto!");
                   }
               }else{
                   exibirMensagemErro("Digite o nome do produto!");
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
            case R.id.imageProduto1:
                escolherImagemProduto(1);
                break;

            case R.id.imageProduto2:
                escolherImagemProduto(2);
                break;

        }
    }

    public void escolherImagemProduto(int requestCode){
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
            Uri imagemProdutoSelecionada = data.getData();
            String caminhoImagemProduto = imagemProdutoSelecionada.toString();

            //configura imagem no ImageView
            if (requestCode == 1){
                imagemProduto1.setImageURI(imagemProdutoSelecionada);
            }else if (requestCode == 2){
                imagemProduto2.setImageURI(imagemProdutoSelecionada);
            }
            listaFotosRecuperadasProduto.add(caminhoImagemProduto);
        }
    }

    private void inicializarComponentes(){
        campoNome = findViewById(R.id.editProdutoNome);
        campoCategoria = findViewById(R.id.editProdutoCategoria);
        campoQuantidade = findViewById(R.id.editProdutoQuantidade);
        campoDescricao = findViewById(R.id.editProdutoDescricao);
        campoPreco = findViewById(R.id.editProdutoPreco);
        imagemProduto1 = findViewById(R.id.imageProduto1);
        imagemProduto2 = findViewById(R.id.imageProduto2);

        imagemProduto1.setOnClickListener(this);
        imagemProduto2.setOnClickListener(this);


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