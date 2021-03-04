package com.example.laravelstarterkit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private TextView txt;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = (TextView) findViewById(R.id.txtEstadoApi);
        txtTitle = (TextView) findViewById(R.id.txtEstadoApiTitle);
        peticionHttp();
    }

    private void peticionHttp() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1, 4000);
        String url = Conexion.URL_BASE + "api/test";
        String token = Conexion.TOKEN;

        client.addHeader("Authorization", "Bearer " + token);

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                txtTitle.setText("¿El token funciona?");
                txt.setText("Conectando con la API");
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(responseBody);
                String response = new String(responseBody);
                txt.setText(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                txtTitle.setText("Ha habido un error");
                String response = new String(error.toString());
                txt.setText(response);
            }
        });

    }
}