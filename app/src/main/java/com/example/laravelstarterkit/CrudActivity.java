package com.example.laravelstarterkit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class CrudActivity extends AppCompatActivity {

    private String token;
    private Button btnIndex;
    private Button btnSearch;
    private Button btnUpdate;
    private Button btnDelete;
    private EditText edtIdSearch;
    private EditText edtIdUpdateDelete;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);

        // Se recupera el token de la MainActivity
        token = getIntent().getExtras().getString("token");

        // Se vinculan los elementos del layout
        btnIndex = (Button) findViewById(R.id.btnIndex);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        edtIdSearch = (EditText) findViewById(R.id.edtSearchId);
        edtIdUpdateDelete = (EditText) findViewById(R.id.edtUpdateDeleteId);

        // Listeners
        btnIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Hay un token?
        if (token.length() > 0) startToken();
        else startInvitado();
    }

    /** Inicia la activity como invitado */
    private void startInvitado() {
        mostrarToastInvitado("Has accedido como invitado. Solo tienes permisos de lectura");
    }

    private void mostrarToastInvitado(String mensaje) {
        Toast toast = new Toast(this);
        toast.setText(mensaje);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /** Inivia la activity como usuario con token */
    private void startToken() {

        // Se crea un progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        pruebaDeConexionConToken();
    }

    /** Hace una HTTP request al servidor configurdo como base en
     * el fichero Conexion para ver si el token tiene el permiso
     * 'base' configurado */
    private void pruebaDeConexionConToken() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1, 5000);

        // Se añade un Header a la petición, con el token como autenticación
        client.addHeader("Authorization", "Bearer " + this.token);

        // Se recupera la url base del fichero de conexiones
        String url = Conexion.URL_BASE + "api/test";

        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                progressDialog.setTitle("Probando la conexión y los permisos basicos del token");
                progressDialog.show();

                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.hide();

                String response = new String(responseBody);

                // Si la respuesta, los primeros 15 carácteres coinciden con <!DOCTYPE html>
                // quiere decir que nos está devolviendo un html de login
                // el status code se pone a 400 de error
                if (response.substring(0, 15).equalsIgnoreCase("<!DOCTYPE html>")) statusCode = 400;

                if (statusCode == 200) {
                    mostrarToastInvitado("Token correctamente configurado");
                }
                else {
                    mostrarAlertErrorPermisosToken(response);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.hide();

                mostrarAlertErrorPermisosToken(error.toString());

            }
        });
    }

    private void mostrarAlertErrorPermisosToken(String mensaje) {

        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.setTitle("Ha habido un error con los permisos del token");
        alert.setMessage(mensaje);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }
}