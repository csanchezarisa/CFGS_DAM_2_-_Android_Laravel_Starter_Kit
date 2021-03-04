package com.example.laravelstarterkit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ApiResultActivity extends AppCompatActivity {

    private int tipoLlamda;
    private String id;
    private ProgressDialog progressDialog;
    private AsyncHttpResponseHandler handler;

    private TextView txtStautsCode;
    private TextView txtResponse;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_result);

        // Se vinculan los elementos del layout
        txtStautsCode = (TextView) findViewById(R.id.txtStatusCode);
        txtResponse = (TextView) findViewById(R.id.txtResponse);
        listView = (ListView) findViewById(R.id.list);

        // Se prepara el progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        // Se recupera el tipo de llamada y el id
        tipoLlamda = getIntent().getExtras().getInt("tipo_llamada");
        id = getIntent().getExtras().getString("id");

        hacerPeticionApi();
    }

    private void hacerPeticionApi() {
        AsyncHttpClient client = new AsyncHttpClient();

        crearHandlerParaPeticion();

        String url = Conexion.URL_BASE;
        switch (tipoLlamda) {
            case CrudActivity.INDEX: url += "api/apios/";
                client.get(url, this.handler);
                break;
            case CrudActivity.SEARCH: url += "api/apios/" + id;
                client.get(url, this.handler);
                break;
            case CrudActivity.UPDATE: url += "api/apios/" + id;

                // Se crean los parámetros "simulando" que se ha rellenado un formulario
                RequestParams params = new RequestParams();
                params.add("nom", "pruebas desde android");
                params.add("tipus", "tipos de pruebas desde android");
                params.add("caducitat", "2000-01-01");

                client.put(url, params, this.handler);
                break;
            case CrudActivity.DELETE: url += "api/apios/" + id;
                client.delete(url, this.handler);
                break;
        }
    }

    /** Inicia el handler para gestionar las peticiones HTTP */
    private void crearHandlerParaPeticion() {
        handler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                progressDialog.setTitle("Realizando petición");
                progressDialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();

                String response = new String(responseBody);

                txtStautsCode.setText(String.valueOf(statusCode));
                txtResponse.setText(response);

                ArrayList<ApioClass> apios = new ArrayList<>();

                try {

                    // Si la llamada es de tipo INDEX devolverá un array de JSON
                    // Sino, devolverá un único json
                    if (tipoLlamda == CrudActivity.INDEX) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            // Por cada objeto json en el array, se crea un apio y se añade a la lista
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = (JSONObject) jsonArray.get(i);

                                int id = -1;
                                String nombre = "vacío";
                                String tipo = "vacío";
                                String caducidad = "vacío";

                                try {

                                    // Se crea el apio y se añade a la lista ed apios
                                    id = json.getInt("id");
                                    nombre = json.getString("nom");
                                    tipo = json.getString("tipus");
                                    caducidad = json.getString("caducitat");

                                    ApioClass apio = new ApioClass(id, nombre, tipo, caducidad);
                                    apios.add(apio);
                                }
                                catch (Exception e) {

                                }
                            }

                        }
                        catch (Exception e) {

                        }
                    }
                    else {
                        JSONObject json = new JSONObject(response);
                        int id = -1;
                        String nombre = "vacío";
                        String tipo = "vacío";
                        String caducidad = "vacío";

                        try {

                            // Se crea el apio y se añade a la lista ed apios
                            id = json.getInt("id");
                            nombre = json.getString("nom");
                            tipo = json.getString("tipus");
                            caducidad = json.getString("caducitat");

                            ApioClass apio = new ApioClass(id, nombre, tipo, caducidad);
                            apios.add(apio);
                        }
                        catch (Exception e) {

                        }
                    }
                }
                catch (Exception e) {
                    return;
                }

                if (apios.size() > 0) {
                    String[] from = new String[2];
                    ArrayAdapter<ApioClass> adapter = new ArrayAdapter<ApioClass>(getApplicationContext(), android.R.layout.simple_list_item_1, apios);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();

                txtStautsCode.setText(String.valueOf(statusCode));
                txtResponse.setText(error.toString());

            }
        };
    }

    private void peticionCorrecta() {

    }

    private void peticionIncorrecta() {

    }
}