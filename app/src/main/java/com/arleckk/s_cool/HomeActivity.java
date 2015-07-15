package com.arleckk.s_cool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    /**
     * Instancia del drawer
     */
    private DrawerLayout drawerLayout;

    /**
     * Titulo inicial del drawer
     */
    private String drawerTitle;
    private Button btnSubirArchivo;
    protected static final int REQUEST_CODE_PICK_FILE_TO_OPEN = 1;
    private static final String LOGIN_URL = "http://scool.byethost24.com/uploadfile.php";
    private static final String REGISTRO_ARCHIVO_URL = "http://scool.byethost24.com/registro_archivo.php";
    private static final String ARCHIVO_URL = "http://scool.byethost24.com/archivo.php";
    private static final String LISTA_URL = "http://scool.byethost24.com/archivo_listview.php";
    String rutaArchivo;
    File file;
    String verificar;
    static String json;
    static InputStream is;
    static JSONObject jObj;
    ProgressDialog pDialog;
    String respuesta;
    Bundle bundle;
    ArchivoAdapter archivoAdapter;
    ListView listaArchivo;
    ArrayList<Archivo> lista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        listaArchivo = (ListView) findViewById(R.id.lista_archivo);
        setToolbar(); // Setear Toolbar como action bar
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        btnSubirArchivo = (Button) findViewById(R.id.btn_subir_archivo);
        btnSubirArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, REQUEST_CODE_PICK_FILE_TO_OPEN);
            }
        });

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        drawerTitle = getResources().getString(R.string.home_item);
        if (savedInstanceState == null) {
            selectItem(drawerTitle);
        }
        listaArchivo.setOnItemClickListener(this);
        CrearListView crearListView = new CrearListView();
        crearListView.execute();



    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado
                        menuItem.setChecked(true);
                        // Crear nuevo fragmento
                        String title = menuItem.getTitle().toString();
                        selectItem(title);
                        return true;
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getMenuInflater().inflate(R.menu.menu_home, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(String title) {
        // Enviar título como arguemento del fragmento

        if(title.equals("Cerrar Sesión")){
            finish();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return ;
        }
        Uri uri = data.getData();
        switch (requestCode) {
            case REQUEST_CODE_PICK_FILE_TO_OPEN:
                // obtain the filename
                if (uri != null) {
                    file = new File(data.getData().getPath());
                    Toast.makeText(this, "path " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    new UploadFile().execute();
                    Log.i("file_path", "path: " + file.getPath());
                    Log.i("file_name", "nombre: " + file.getName());
                    Log.i("data_path", "data: " + data.getData().getPath());
                }
                break;
        }//switch
    }//onActivityResult

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        lista.get(position).getURL();
        String url = "http://scool.byethost24.com/"+lista.get(position).getURL();
        Log.i("URL",url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    class HttpFileUploader {

        URL connectURL;
        String responseString;
        String fileName;
        byte[] dataToServer;

        HttpFileUploader(String urlString, String fileName ){
            try{
                connectURL = new URL(urlString);
            }catch(Exception ex){
                Log.i("URL FORMATION","MALFORMATED URL");
            }

            this.fileName = fileName;
        }

        void doStart(FileInputStream stream){
            fileInputStream = stream;
            thirdTry();
        }

        FileInputStream fileInputStream = null;
        void thirdTry() {
            String existingFileName = fileName;

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String Tag="3rd";
            try
            {
                //------------------ CLIENT REQUEST

                Log.e(Tag,"Starting to bad things");

                // PHP Service connection
                HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"archivo\";filename=\"" + existingFileName +"\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag,"Headers are written");

                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Close input stream
                Log.e(Tag,"File is written");
                fileInputStream.close();
                dos.flush();

                InputStream is = conn.getInputStream();
                // retrieve the response from server
                int ch;

                StringBuffer b =new StringBuffer();
                while( ( ch = is.read() ) != -1 ){
                    b.append( (char)ch );
                }
                String s=b.toString();
                Log.i("Response",s);
                dos.close();

            }
            catch (MalformedURLException ex)
            {
                Log.e(Tag, "error: " + ex.getMessage(), ex);
            }

            catch (IOException ioe)
            {
                Log.e(Tag, "error: " + ioe.getMessage(), ioe);
            }
        }
    }

    class UploadFile extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeActivity.this);
            pDialog.setMessage("Subiendo el archivo espere." );
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpFileUploader uploader = new HttpFileUploader(LOGIN_URL, file.getName());
            try {
                uploader.doStart(new FileInputStream(file.getPath()));
                respuesta = insertarRegistro(REGISTRO_ARCHIVO_URL, file.getName());
                Log.i("registro_archivo","Respuesta: "+respuesta);
                Log.i("buscar_path", file.getParent());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            pDialog.dismiss();
            Toast.makeText(HomeActivity.this, "El archivo se agrego con exito", Toast.LENGTH_SHORT).show();
            new CrearListView().execute();
            Log.i("ArchivoExito", "El archivo se agrego con exito");
            if(respuesta.equals("1")){
                Log.i("registro_archivo", "se actualizo con exito");
            } else {
                Log.i("registro_archivo","revisar base de datos");
            }
        }//PostExecute
    }//Clase UploadFile

    class CrearListView extends AsyncTask<ArrayList, ArrayList, ArrayList<Archivo>> {

        @Override
        protected ArrayList<Archivo> doInBackground(ArrayList... params) {

            JSONObject jsonObject = makeHttpRequest(LISTA_URL,"POST",null);
            String nombreArchivo="",usuario="",fecha="",url="";
            lista = new ArrayList<Archivo>();
            try {
                Log.i("estodevuelveelJSON",jsonObject.getJSONObject(String.valueOf("0")).getString("nombre_a"));
                for(int i=0;i<jsonObject.length();i++){
                    nombreArchivo = jsonObject.getJSONObject(String.valueOf(i)).getString("nombre_a");
                    usuario =  jsonObject.getJSONObject(String.valueOf(i)).getString("autor");
                    fecha = jsonObject.getJSONObject(String.valueOf(i)).getString("fecha");
                    url =  jsonObject.getJSONObject(String.valueOf(i)).getString("URL");
                    Log.i("estodevuelveelJSON",nombreArchivo+usuario+fecha+url);
                    lista.add(new Archivo(nombreArchivo,fecha,usuario,url));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("estodevuelveelJSON", "ERROR");
            }
            return lista;
        }
        @Override
        protected void onPostExecute(ArrayList<Archivo> result){
            archivoAdapter = new ArchivoAdapter(HomeActivity.this,lista);
            listaArchivo.setAdapter(archivoAdapter);
        }
    }

    public String insertarRegistro(String url, String fileName){
        HttpClient httpClient;
        HttpPost httpPost;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(url);
        Bundle extras = getIntent().getExtras();
        params.add(new BasicNameValuePair("usuario",extras.getString("usuario")));
        params.add(new BasicNameValuePair("accion","agrego"));
        params.add(new BasicNameValuePair("nom_archivo",fileName));
        params.add(new BasicNameValuePair("estado","activo"));
        params.add(new BasicNameValuePair("lista_u",extras.getString("usuario")));
        params.add(new BasicNameValuePair("lista_d",extras.getString("departamento")));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            httpClient.execute(httpPost);
            return "1";
        }  catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    } //insetarRegistro

    private static JSONObject makeHttpRequest(String url, String method,
                                              String params) {
        try {

            if (method == "POST") {

                HttpParams httpParameters = HomeActivity.conectionParams();

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
//					HttpParams httpParameters = WebServices.conectionParams();
//
//					DefaultHttpClient httpClient = new DefaultHttpClient(
//						httpParameters);
//		            HttpGet httpGet = new HttpGet(url + "?");
//		            httpGet.setHeader("Accept", "application/json");
//		            httpGet.setHeader("Content-type",
//							"application/json; charset=UTF-8");
//		            HttpResponse httpResponse = httpClient.execute(httpGet);
//					HttpEntity httpEntity = httpResponse.getEntity();
//					is = httpEntity.getContent();
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


}//home activity
