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
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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


public class HomeActivity extends AppCompatActivity {

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
    String rutaArchivo;
    File file;
    String verificar;
    String json;
    InputStream is;
    JSONObject jObj;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setToolbar(); // Setear Toolbar como action bar
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        btnSubirArchivo = (Button) findViewById(R.id.btn_subir_archivo);
        btnSubirArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.estrongs.action.PICK_FILE");
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
        protected String doInBackground(String... params) {
            HttpFileUploader uploader = new HttpFileUploader(LOGIN_URL, file.getName());
            try {
                uploader.doStart(new FileInputStream(file.getPath()));
                Log.i("buscar_path",file.getParent());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(HomeActivity.this, "El archivo se agrego con exito", Toast.LENGTH_SHORT).show();
            Log.i("ArchivoExito","El archivo se agrego con exito");
        }

    }



}//home activity
