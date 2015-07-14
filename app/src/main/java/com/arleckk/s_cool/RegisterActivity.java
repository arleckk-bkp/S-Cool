package com.arleckk.s_cool;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    String json,verificar;
    InputStream is;
    JSONObject jObj;
    private static final String LOGIN_URL = "http://scool.byethost24.com/registrar.php";
    EditText editTextNombre,editTextAP,editTextAM,editTextUsuario,editTextPassword,editTextCoreo;
    Spinner spinnerDepartamento,spinnerTipo;
    FloatingActionButton fabRegistrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextNombre = (EditText) findViewById(R.id.edit_text_nombre_register);
        editTextAP = (EditText) findViewById(R.id.edit_text_ap_register);
        editTextAM = (EditText) findViewById(R.id.edit_text_am_register);
        editTextUsuario = (EditText) findViewById(R.id.edit_text_user_register);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password_register);
        editTextCoreo = (EditText) findViewById(R.id.edit_text_correo_register);
        spinnerDepartamento = (Spinner) findViewById(R.id.spinner_departamento);
        spinnerTipo = (Spinner) findViewById(R.id.spinner_tipo_usuario);
        fabRegistrar = (FloatingActionButton)  findViewById(R.id.fab_registrar);
        fabRegistrar.setOnClickListener(this);
    }

    public String obtenerDato(EditText editText){
        return editText.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab_registrar:
                    if (!isEmpty()) {
                        new Login().execute();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Uno o mas campos estan vacios", Toast.LENGTH_SHORT).show();
                    }
                break;
        }
    }

    public String parametrosURL(){
        return "nombre="+obtenerDato(editTextNombre)+"&ap="+obtenerDato(editTextAP)+"&am="+obtenerDato(editTextAM)
                +"&usuario="+obtenerDato(editTextUsuario)+"&password="+obtenerDato(editTextPassword)
                +"&correo="+obtenerDato(editTextCoreo)+"&departamento="+spinnerDepartamento.getSelectedItem().toString()+
                "&tipo="+String.valueOf(setTipoUsuario());
    }

    public Boolean isEmpty(){
        if (obtenerDato(editTextNombre).equals("")&&obtenerDato(editTextAP).equals("")&&obtenerDato(editTextAM).equals("")
                &&obtenerDato(editTextUsuario).equals("")&&obtenerDato(editTextPassword).equals("")
                &&obtenerDato(editTextCoreo).equals("")) {
            return true;
        }
        return false;
    }

    public int setTipoUsuario(){
        if (spinnerTipo.getSelectedItem().equals("estudiante")){
            return 1;
        } else {
            return 0;
        }
    }

    class Login extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... params) {
            JSONObject parametros = new JSONObject();
            try {
                JSONObject jsonObject = makeHttpRequest(LOGIN_URL, "GET", parametros.toString());
                verificar = jsonObject.getString("estado");
            } catch (IllegalArgumentException iae){
                Toast.makeText(RegisterActivity.this, "ERROR: no puede usar espacios", Toast.LENGTH_SHORT).show();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            ;
            return verificar;
        }//doInBackground

        @Override
        protected void onPostExecute(String result) {
            if(verificar.equals("1")){
                Toast.makeText(RegisterActivity.this, R.string.registro_exitoso , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (RegisterActivity.this,MainActivity.class);
                finish();
                startActivity(intent);
                Log.i("registro","verificar: "+verificar);
            }else {
                Toast.makeText(RegisterActivity.this, R.string.registro_error , Toast.LENGTH_SHORT).show();
                Log.i("webservices", "verificar: " + verificar);
            }
        }
    }//Login

    private JSONObject makeHttpRequest(String url, String method, String params) {
        try {

            if (method == "POST") {

                HttpParams httpParameters = RegisterActivity.conectionParams();

                DefaultHttpClient httpClient = new DefaultHttpClient(
                        httpParameters);

                HttpPost httpPost = new HttpPost(url);

                if( params != null )
                {
                    StringEntity entity = new StringEntity(params, "UTF-8");
                    entity.setContentEncoding("UTF-8");
                    httpPost.setEntity(entity);
                }

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type",
                        "application/json; charset=UTF-8");
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }else if(method == "GET"){
                HttpParams httpParameters = RegisterActivity.conectionParams();
                DefaultHttpClient httpClient = new DefaultHttpClient(
                        httpParameters);
                HttpGet httpGet = new HttpGet(url + "?"+parametrosURL());
                httpGet.setHeader("Accept", "application/json");
                httpGet.setHeader("Content-type",
                        "application/json; charset=UTF-8");
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (IllegalArgumentException iae){
            Toast.makeText(RegisterActivity.this, "ERROR: no puede usar espacios", Toast.LENGTH_SHORT).show();
        }
        catch (UnsupportedEncodingException e) {
            Log.i("WebServices ","UnsupportedEncodingException: "+e);
        } catch (ClientProtocolException e) {
            Log.i("WebServices ","ClientProtolocoException: "+e);
        } catch (IOException e) {
            Log.i("WebServices ","IOException: "+e);
        }

        try {

            if(is!=null){
                Log.i("WebServices ","is abierto");
            }else{
                Log.i("WebServices ","is cerrado");
            }
            Log.i("WebServices ","instanciando BufferedReader");


            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"), 8);


            StringBuilder sb = new StringBuilder();
            String line = null;


            Log.i("WebServices ","recorriendo reader");
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            if(is!=null)
                is.close();

            json = sb.toString();
            Log.i("WebServices ","http json: "+sb.toString());

        } catch (IllegalArgumentException iae){
            Toast.makeText(RegisterActivity.this, "ERROR: no puede usar espacios", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("WebServices ","http json exception: "+e);
            return jObj = null;
        }
        try {
            jObj = new JSONObject(json);
            Log.i("WebServices ","http json object: "+jObj.toString());
        } catch (IllegalArgumentException iae){
            Toast.makeText(RegisterActivity.this, "ERROR: no puede usar espacios", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.e("WebServices ","http json object exception: "+e);
            jObj = null;
        }
        return jObj;
    }

    private static HttpParams conectionParams() {

        HttpParams httpParameters = new BasicHttpParams();

        httpParameters.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                240000);
        httpParameters.setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
                240000);
        httpParameters.setLongParameter(ConnManagerPNames.TIMEOUT,
                240000);

        return httpParameters;
    }


}
