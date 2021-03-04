package com.example.laravelstarterkit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private TextView txt;
    private TextView txtTitle;
    private Button btnTokenLogin;
    private Button btnInvitadoLogin;
    private EditText edtToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Se vinculan los elementos del layout
        txt = (TextView) findViewById(R.id.txtEstadoApi);
        txtTitle = (TextView) findViewById(R.id.txtEstadoApiTitle);
        btnTokenLogin = (Button) findViewById(R.id.btnTokenLogin);
        btnInvitadoLogin = (Button) findViewById(R.id.btnInvitadoLogin);
        edtToken = (EditText) findViewById(R.id.edtToken);

        // Petición básica la servidor para ver si hay conexión
        peticionHttp();

        // Listener para login con token
        btnTokenLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Recupera el token. Si está vacío muestra un error
                String token;

                try {
                    token = edtToken.getText().toString();
                }
                catch (Exception e) {
                    token = "";
                }

                if (token.length() > 0) {

                    // Bundle para pasar el token
                    Bundle data = new Bundle();
                    data.putString("token", token);

                    // Intent para abrir la nueva activity
                    Intent intent = new Intent(getApplicationContext(), CrudActivity.class);
                    intent.putExtras(data);

                    // Inicio de la activity
                    startActivity(intent);
                }
                else {
                    edtToken.findFocus();
                    mostrarToastError();
                }

            }
        });

        // Listener para login con invitado
        btnInvitadoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Bundle para pasar el token vacío
                Bundle data = new Bundle();
                data.putString("token", "token");

                // Intent para abrir la nueva activity
                Intent intent = new Intent(getApplicationContext(), CrudActivity.class);
                intent.putExtras(data);

                // Inicio de la activity
                startActivity(intent);
            }
        });
    }

    /** Hace una petición basica a la api de Laravel para ver si hay conexión */
    private void peticionHttp() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1, 4000);
        String url = Conexion.URL_BASE + "api/prueba";

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                txtTitle.setText("¿Hay conexión con el servidor Laravel?");
                txt.setText("Prueba de conexión con la API");

                // Se bloquean los botones
                btnInvitadoLogin.setClickable(false);
                btnTokenLogin.setClickable(false);

                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(responseBody);
                String response = new String(responseBody);
                txt.setText(response);

                // Se desbloquean los botones
                btnInvitadoLogin.setClickable(true);
                btnTokenLogin.setClickable(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                txtTitle.setText("Ha habido un error");
                txt.setText("No. " + error.toString());
            }
        });

    }

    /** Muestra un toast de error explicando que el token está vacío */
    private void mostrarToastError() {
        Toast toast = new Toast(this);
        toast.setText("Error, token vacío");
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}