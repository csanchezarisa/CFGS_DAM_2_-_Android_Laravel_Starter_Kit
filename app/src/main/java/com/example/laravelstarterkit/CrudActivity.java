package com.example.laravelstarterkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class CrudActivity extends AppCompatActivity {

    // Constantes para hacer la llamada a la activity
    // que hace la petición
    public static final int INDEX = 1;
    public static final int SEARCH = 2;
    public static final int UPDATE = 3;
    public static final int DELETE = 4;

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
                startApiResult(INDEX, null);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id;
                try {
                    id = Integer.parseInt(edtIdSearch.getText().toString());
                }
                catch (Exception e) {
                    id = -1;
                }

                if (id < 0) {
                    mostrarToastInvitado("Introduce un ID válido!");
                    edtIdSearch.findFocus();
                }
                else {
                    String idString = String.valueOf(id);
                    startApiResult(SEARCH, idString);
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id;
                try {
                    id = Integer.parseInt(edtIdUpdateDelete.getText().toString());
                }
                catch (Exception e) {
                    id = -1;
                }

                if (id < 0) {
                    mostrarToastInvitado("Introduce un ID válido!");
                    edtIdUpdateDelete.findFocus();
                }
                else {
                    String idString = String.valueOf(id);
                    startApiResult(UPDATE, idString);
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id;
                try {
                    id = Integer.parseInt(edtIdUpdateDelete.getText().toString());
                }
                catch (Exception e) {
                    id = -1;
                }

                if (id < 0) {
                    mostrarToastInvitado("Introduce un ID válido!");
                    edtIdUpdateDelete.findFocus();
                }
                else {
                    String idString = String.valueOf(id);
                    startApiResult(DELETE, idString);
                }
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

    /** Muestra un toast largo
     * @param mensaje String con el contenido del toast */
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

    /** Muestra un alert que no se puede cancelar ni cerrar con el mensaje de error de token
     * no válido y cierra la activity
     * @param mensaje String con el contenido del mensaje a mostrar*/
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

    /** Ejecuta la activity ApiResult según el tipo de llamada que se quiera
     * @param tipoLlamada int con la constante del tipo de llamada que se quiera
     * hacer
     * @param id String que puede ser null con los extras (ID) de búsqueda
     * edición y eliminación*/
    private void startApiResult(int tipoLlamada, @Nullable String id) {
        Intent intent = new Intent(getApplicationContext(), ApiResultActivity.class);
        Bundle data = new Bundle();
        data.putInt("tipo_llamada", tipoLlamada);
        if (id == null) id = "";
        data.putString("id", id);
        intent.putExtras(data);
        startActivity(intent);
    }
}