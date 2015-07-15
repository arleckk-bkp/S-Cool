package com.arleckk.s_cool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    private static final String LOGIN_URL = "http://scool.byethost24.com/loggin.php";
    EditText editTextUser,editTextPassword;
    FloatingActionButton fabEntrar;
    Button btnRegistrar;
    InputStream is;
    JSONObject jObj;
    String json;
    String verificar;
    SessionManager session;
    ProgressDialog pDialog;
    String departamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_my_toolbar);
        setSupportActionBar(toolbar);
        editTextUser = (EditText) findViewById(R.id.edit_text_user);
        editTextPassword = (EditText) findViewById(R.id.edit_text_pwd);
        fabEntrar = (FloatingActionButton) findViewById(R.id.fab_Entrar);
        btnRegistrar = (Button) findViewById(R.id.btn_registrar);
        btnRegistrar.setOnClickListener(this);
        fabEntrar.setOnClickListener(this);
        session = new SessionManager(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab_Entrar:
                new Login().execute();
                break;
            case R.id.btn_registrar:
                finish();
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                break;
        }//switch
    }

    public String obtenerDato(EditText editText){
        return editText.getText().toString();
    }

    class Login extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            JSONObject parametros = new JSONObject();
            try {
                parametros.accumulate("usuario",obtenerDato(editTextUser));
                parametros.accumulate("password", obtenerDato(editTextPassword));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject = makeHttpRequest(LOGIN_URL, "GET", parametros.toString());
                verificar = jsonObject.getString("estado");
                //departamento = obtenerDepartamento("http://scool.byethost24.com/departamento.php", obtenerDato(editTextUser), obtenerDato(editTextPassword));
                Log.i("departamento","departamento= "+departamento);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            ;
            return verificar;
        }//doInBackground

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Entrando, espere un momento." );
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            if(verificar.equals("1")){
                Toast.makeText(MainActivity.this, R.string.login_exito, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (MainActivity.this,HomeActivity.class);
                intent.putExtra("usuario",obtenerDato(editTextUser));
                //intent.putExtra("departamento","departamento= "+departamento);
                finish();
                startActivity(intent);
                session.createLoginSession("S-Cool",obtenerDato(editTextUser));
                Log.i("webservices", "verificar: " + verificar);
            }else {
                Toast.makeText(MainActivity.this, R.string.login_error , Toast.LENGTH_SHORT).show();
                Log.i("webservices", "verificar: " + verificar);
            }
        }
    }//Login

    private JSONObject makeHttpRequest(String url, String method, String params) {
        try {

            if (method == "POST") {

                HttpParams httpParameters = MainActivity.conectionParams();

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
                HttpParams httpParameters = MainActivity.conectionParams();

                DefaultHttpClient httpClient = new DefaultHttpClient(
                        httpParameters);
                HttpGet httpGet = new HttpGet(url + "?usuario="+obtenerDato(editTextUser)+"&password="+obtenerDato(editTextPassword));
                httpGet.setHeader("Accept", "application/json");
                httpGet.setHeader("Content-type",
                        "application/json; charset=UTF-8");
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
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

        } catch (Exception e) {
            Log.e("WebServices ","http json exception: "+e);
            return jObj = null;
        }
        try {
            jObj = new JSONObject(json);
            Log.i("WebServices ","http json object: "+jObj.toString());
        } catch (JSONException e) {
            Log.e("WebServices ","http json object exception: "+e);
            jObj = null;
        }
        return jObj;
    }

    public String obtenerDepartamento(String url,String usuario,String password){

        try {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("usuario",obtenerDato(editTextUser)));
        params.add(new BasicNameValuePair("password", obtenerDato(editTextPassword)));
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity ent = response.getEntity();
            String respuesta = EntityUtils.toString(ent);
            Log.i("departamento","departamento= "+respuesta);
            return respuesta;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

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



