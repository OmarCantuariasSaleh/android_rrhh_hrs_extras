package cl.cmsg.rrhhaprobacionhrsextras;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;

import cl.cmsg.rrhhaprobacionhrsextras.clases.Alertas;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.ValidacionConexion;
import cl.cmsg.rrhhaprobacionhrsextras.clases.VolleyS;

public class MainActivity extends AppCompatActivity {

    MiDbHelper miDbHelper;
    MenuItem btnActualizarPendientes;
    Button btnSolPendientes;
    Button btnSolAprobadas;
    Button btnSolRechazadas;
    Button btnError;
    Button btnVersion;
    ProgressDialog progressDialog;
    final String mensajeERROR = "Comuniquese con informatica, el servidor responde con formato incorrecto";
    final String tituloERROR ="ERROR";
    int counter=0;
    long total = 0;
    Cursor cursor;
    String mac;
    int errorSwitch = 0;    //Cuenta cuantos errores quedan por enviar

    //Contadores de errores repetidos, se aseguran de no repetir mensajes de error al servidor y usuario

    int errorNuloVacio = 0;     //Errores tipo nulo o vacio
    int errorNoExito = 0;       //Errores de mensajeERROR de exito
    int errorConnVolley = 0;    //Errores conexion con servidor o errores Volley varios
    int errorURLEncoder = 0;    //Errores de URLEncoder

    //-----------------------------

    TextView lblBienvenido;
    int filas = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        miDbHelper = MiDbHelper.getInstance(this);
        btnActualizarPendientes = (MenuItem) findViewById(R.id.btnActualizar);
        btnSolPendientes = (Button) findViewById(R.id.btnSolPendientes);
        btnSolAprobadas = (Button) findViewById(R.id.btnSolAprobadas);
        btnSolRechazadas = (Button) findViewById(R.id.btnSolRechazadas);
        btnError = (Button) findViewById(R.id.btnError);
        btnVersion = (Button) findViewById(R.id.btnVersion);
        mac= ValidacionConexion.getDireccionMAC(MainActivity.this);

        final VolleyS volleyS = VolleyS.getInstance(this);
        lblBienvenido = (TextView) findViewById(R.id.lblBienvenido);
        lblBienvenido.setText("Bienvenido " + miDbHelper.getNombreUsuario());
       // Find the toolbar view inside the activity layout
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Get access to the custom title view
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);


        updateList();

        //Click Listeners

        btnSolPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //Ingresar a modulo de solicitudes pendientes
                Intent intent = new Intent(MainActivity.this, HorasPendientesActivity.class);
                startActivity(intent);
            }
        });

        btnSolAprobadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //Ingresar a modulo de solicitudes Aprobadas
                Intent intent = new Intent(getApplicationContext(), HorasAprobadasActivity.class);
                startActivity(intent);
            }
        });

        btnSolRechazadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //Ingresar a modulo de solicitudes Rechazadas
                Intent intent3 = new Intent(getApplicationContext(), HorasRechazadasActivity.class);
                startActivity(intent3);
            }
        });

        btnError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            //Enviar errores al desarrollador

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Actualizando");
                progressDialog.setMessage("Espere un momento");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setCancelable(false);
                progressDialog.show();

                if (miDbHelper.CuentaErrores() == 0) {
                    progressDialog.dismiss();
                    //TODO Borrar errores de prueba ---------------
                    miDbHelper.insertarLogError("hola",mac);
                    miDbHelper.insertarLogError("hola",mac);
                    //miDbHelper.insertarLogError("hola",mac);
                    //miDbHelper.insertarLogError("hola",mac);
                   /* miDbHelper.insertarLogError("hola",mac);
                    miDbHelper.insertarLogError("hola",mac);
                    miDbHelper.insertarLogError("hola",mac);
                    miDbHelper.insertarLogError("hola",mac);
                    miDbHelper.insertarLogError("hola",mac);
                    miDbHelper.insertarLogError("hola",mac);
                    miDbHelper.insertarLogError("hola",mac);
*/
                    //TODO Borrar errores de prueba --------------
                    updateList();
                    return;
                }
                if (!ValidacionConexion.isExisteConexion(MainActivity.this)) {
                    progressDialog.dismiss();
                    Alertas.alertaConexion(MainActivity.this);
                    return;
                }
                cursor = miDbHelper.getLogErrores();
                errorSwitch = miDbHelper.CuentaErrores();
                errorNuloVacio = 0;
                errorNoExito = 0;
                errorConnVolley = 0;
                errorURLEncoder = 0;
                total=cursor.getCount();
                counter=0;
                while (cursor.moveToNext()) {

                    String fechaHora;
                    String descripcion;
                    final String version_app;
                    try {
                        fechaHora = URLEncoder.encode(cursor.getString(cursor.getColumnIndex("fecha_hora")), "UTF-8");
                        descripcion = URLEncoder.encode(cursor.getString(cursor.getColumnIndex("descripcion")), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        fechaHora = "";
                        descripcion = "";
                        if(++errorURLEncoder ==1) {
                            progressDialog.dismiss();
                            miDbHelper.insertarLogError("No se pudo codificar con el URLEncoder en MainActivity, btnError listener al enviar Errores " + e.getMessage(), mac);
                            Alertas.alertaSimple(tituloERROR,"Ocurrio un error interno, por favor comuniquese con informatica",MainActivity.this);
                            return;
                        }
                    }

                    version_app = cursor.getString(cursor.getColumnIndex("version_app"));

                    final int indexout = cursor.getInt(cursor.getColumnIndex("id_log_errores"));

                    String url = getString(R.string.URL_EnviarErrores)
                            + "?fecha_hora=" + fechaHora
                            + "&version_app=" + version_app
                            + "&mac=" + mac
                            + "&descripcion=" + descripcion
                            + "&apk_key=" + getString(R.string.APK_KEY)
                            + "&run=" + miDbHelper.getRutUsuario();
                    StringRequest jsonObjectRequest = new StringRequest(

                            Request.Method.GET // FORMA QUE LLAMAREMOS, O SEA GET
                            , url
                            // URL QUE LLAMAREMOS
                            , new Response.Listener<String>() { // OBJETO QUE USAREMOS PARA LA ESCUCHA DE LA RESPUESTA
                        @Override
                        public void onResponse(String response) {

                            if (response == null || response.isEmpty()) {
                                progressDialog.dismiss();
                                volleyS.cancelAll();

                                if (++errorNuloVacio == 1) {
                                    Alertas.alertaSimple(tituloERROR, mensajeERROR, MainActivity.this);
                                    miDbHelper.insertarLogError("Variable response es Nulo o Vacio en MainActivity, btnError listener",mac);
                                    updateList();
                                }

                                return;
                            }

                            if (!response.toLowerCase().equals("exito")) {
                                progressDialog.dismiss();
                                volleyS.cancelAll();
                                if (++errorNoExito == 1) {
                                    Alertas.alertaSimple(tituloERROR,"Servidor respondio con error:" + response, MainActivity.this);
                                }

                                return;
                            }

                            // Borrar solicitudes antiguas
                            miDbHelper.deleteLogError(indexout);
                            counter ++ ;
                            progressDialog.incrementProgressBy((int) (counter * 100 / total));
                            if (--errorSwitch == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Errores enviados exitosamente", Toast.LENGTH_SHORT).show();
                            }

                            updateList();

                        }
                    }
                            , new Response.ErrorListener() { // QUE HACER EN CASO DE ERROR
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();

                            volleyS.cancelAll();

                            if (++errorConnVolley == 1) {
                                Alertas.alertaSimple(tituloERROR, "Servidor no responde Asegurese de estar conectado a internet o intentelo mas tarde", MainActivity.this);
                                miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : " + error.getMessage()+"  , en MainActivity, btnError listener",mac);
                                updateList();
                            }
                        }
                    }
                    );
                    volleyS.addToQueue(jsonObjectRequest, MainActivity.this);
                }
                cursor.close();

            }
        });

        btnVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!ValidacionConexion.isExisteConexion(MainActivity.this)){
                    Alertas.alertaConexion(MainActivity.this);
                    return;
                }

                String url = getString(R.string.URL_VERSION) + "?apk_key=" + getString(R.string.APK_KEY);

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setTitle("Comprobando");
                progressDialog.setMessage("Aguarde unos segundos por favor");
                progressDialog.show();

                StringRequest request = new StringRequest(Request.Method.GET
                        , url
                        ,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response){
                                volleyS.cancelAll();
                                progressDialog.dismiss();
                                if (response == null || response.isEmpty()){

                                    Alertas.alertaSimple(tituloERROR, "El servidor no responde, intentelo mas tarde.",MainActivity.this);
                                    miDbHelper.insertarLogError("La variable response es vacia o nula en MainActivity, btnVersion",mac);
                                    return;
                                }

                                Integer versionCelular = Integer.valueOf(getString(R.string.version).replace(".", ""));
                                Integer versionServidor = Integer.valueOf(response.replace(".", ""));

                                if (versionCelular == null || versionServidor == null){
                                    miDbHelper.insertarLogError("La version del celular o del servidor responde vacio o nulo en MainActivity, btnVersion",mac);
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .setTitle("Error detectando versiones")
                                            .setMessage("Informe este error a Ing. Software")
                                            .setPositiveButton("De acuerdo", null)
                                            .show()
                                    ;

                                    return;
                                }

                                if (versionServidor > versionCelular){
                                    new DownloadTask(MainActivity.this).execute(getString(R.string.URL_DOWNLOAD_APP));
                                    return;
                                }

                                if (versionServidor < versionCelular){
                                    miDbHelper.insertarLogError("Celular posee version " + versionCelular + " y servidor figura con version " + versionServidor,mac);
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .setTitle("Error detectado")
                                            .setMessage("Se ha detectado un error, favor presione el boton de errores para comunicarlo. Contacte a Ing. Software")
                                            .setPositiveButton("Entendido", null)
                                            .show()
                                    ;
                                    return;
                                }
                                Toast.makeText(MainActivity.this, "Usted tiene la última versión :)", Toast.LENGTH_LONG).show();
                            }
                        }
                        ,
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                miDbHelper.insertarLogError("Error de Volley :"+error.getMessage()+" en MainActivity, btnVersion",mac);
                                Alertas.alertaSimple(tituloERROR, mensajeERROR,MainActivity.this);
                            }
                        }
                );

                volleyS.addToQueue(request,MainActivity.this);

            }
        });








    }

    public void actualizarPendientes(){
        if (!ValidacionConexion.isExisteConexion(MainActivity.this)) {
            Alertas.alertaConexion(MainActivity.this);
            return;
        }
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Actualizando datos");
        progressDialog.setMessage("Espere un momento");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();
        final VolleyS volleyS = VolleyS.getInstance(this);
        final StringRequest jsonObjectRequest = new StringRequest(

                Request.Method.GET // FORMA QUE LLAMAREMOS, O SEA GET
                , getString(R.string.URL_PendienteAprobacion) + "?run=" + miDbHelper.getRutUsuario() // URL QUE LLAMAREMOS
                , new Response.Listener<String>() { // OBJETO QUE USAREMOS PARA LA ESCUCHA DE LA RESPUESTA
            @Override
            public void onResponse(String response) {
                //progressDialog.dismiss();
                JSONObject jsonObject;
                Boolean error;
                String fecha;
                int run;
                String nombre;
                int valor;
                double cantidad;
                String motivo;
                String comentario;
                String centro_costo;
                String area;
                String tipo_pacto;
                String estado1;
                String rut_admin1;
                String estado2;
                String rut_admin2;
                String estado3;
                String rut_admin3;
                String mensajesrv;

                if (response == null || response.isEmpty()) {
                    progressDialog.dismiss();
                    Alertas.alertaSimple(tituloERROR, mensajeERROR, MainActivity.this);
                    miDbHelper.insertarLogError("Variable response es Nulo o Vacio en MainActivity, btnActualizarPendientes",mac);
                    updateList();
                    return;
                }
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Alertas.alertaSimple(tituloERROR, mensajeERROR, MainActivity.this);
                    miDbHelper.insertarLogError("Error de formato en 'response', no parece ser tipo JSON. Mensaje de error : " + e.getMessage()+" en MainActivity, btnActualizarPendientes",mac);
                    updateList();
                    return;
                }
                try {
                    error = jsonObject.getBoolean("error");
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Alertas.alertaSimple(tituloERROR, mensajeERROR, MainActivity.this);
                    miDbHelper.insertarLogError("Error de formato en variable 'error', No existe o es un formato incorrecto. Mensaje de error : " + e.getMessage()+" en MainActivity, btnActualizarPendientes",mac);
                    updateList();
                    return;
                }
                if (error) {
                    progressDialog.dismiss();
                    try {
                        mensajesrv = jsonObject.getString("mensaje");
                    } catch (JSONException e) {
                        Alertas.alertaSimple(tituloERROR, mensajeERROR, MainActivity.this);
                        miDbHelper.insertarLogError("Error de formato en variable 'mensaje', No existe o es un formato incorrecto en MainActivity, btnActualizarPendientes." +
                                " Mensaje de error : " + e.getMessage()+"",mac);
                        updateList();
                        return;
                    }
                    updateList();
                    miDbHelper.insertarLogError("Servidor retorna el siguiente error: " + mensajesrv+". En MainActivity, btnActualizarPendientes ",mac);
                    Alertas.alertaSimple("Servidor responde con el siguiente error:", mensajesrv, MainActivity.this);
                    return;
                }


                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("filas");
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Alertas.alertaSimple(tituloERROR, mensajeERROR, MainActivity.this);
                    miDbHelper.insertarLogError("Error de formato en variable 'filas', No existe o es un formato incorrecto en MainActivity, btnActualizarPendientes . Mensaje de error : " + e.getMessage(),mac);
                    updateList();
                    return;
                }

                // Borrar solicitudes antiguas
                miDbHelper.deleteSolicitudPendientes();
                filas = 0;
                total=jsonArray.length();
                for (int i = 0; i < total; i++) {
                    JSONObject jsonData = null;
                    try {
                        jsonData = jsonArray.getJSONObject(i);
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        Alertas.alertaSimple(tituloERROR, mensajeERROR, MainActivity.this);
                        miDbHelper.insertarLogError("Error de formato en variable 'filas',datos del arreglo no son JSONObject o no tienen formato correcto en MainActivity, btnActualizarPendientes. \nMensaje de error : " + e.getMessage(),mac);
                        updateList();
                        return;
                    }

                    try {

                        run = jsonData.getInt("run");
                        fecha = jsonData.getString("fecha");
                        nombre = jsonData.getString("nombre");
                        cantidad = jsonData.getDouble("cantidad");
                        valor = jsonData.getInt("valor");
                        motivo = jsonData.getString("motivo");
                        comentario = jsonData.getString("comentario");
                        centro_costo = jsonData.getString("centro_costo");
                        area = jsonData.getString("area");
                        tipo_pacto = jsonData.getString("tipo_pacto");
                        estado1 = jsonData.getString("estado1");
                        rut_admin1 = jsonData.getString("run1");
                        estado2 = jsonData.getString("estado2");
                        rut_admin2 = jsonData.getString("run2");
                        estado3 = jsonData.getString("estado3");
                        rut_admin3 = jsonData.getString("run3");

                        filas++;
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        Alertas.alertaSimple(tituloERROR, mensajeERROR, MainActivity.this);
                        updateList();
                        miDbHelper.insertarLogError("Filas del arreglo no tienen formato correcto o estan vacias, en MainActivity, btnActualizarPendientes. Mensaje de error : " + e.getMessage(),mac);
                        updateList();
                        return;
                    }
                    if(miDbHelper.yaExiste(String.valueOf(run),fecha,tipo_pacto)){
                        continue;
                    }
                    if(miDbHelper.insertarSolicitud(
                            String.valueOf(run)
                            , nombre
                            , fecha
                            , cantidad
                            , valor
                            , motivo
                            , comentario
                            , centro_costo
                            , area
                            , tipo_pacto
                            , estado1
                            , rut_admin1
                            , estado2
                            , rut_admin2
                            , estado3
                            , rut_admin3,mac)){
                        progressDialog.incrementProgressBy((int) (i * 100 / total));

                    } else {
                        progressDialog.dismiss();
                        Alertas.alertaSimple(tituloERROR,"Error de base de datos Comuniquese con informatica inmediatamente", MainActivity.this);
                        miDbHelper.insertarLogError("Una o mas filas del arreglo contienen datos que no coinciden con la tabla en la fila " + String.valueOf(i),mac);
                        updateList();
                        return;
                    }

                }
                progressDialog.dismiss();
                updateList();
                Alertas.alertaSimple("Exito","Actualizacion exitosa, Hay " + String.valueOf(miDbHelper.CuentaSolicitudes()) + " solicitudes pendientes", MainActivity.this);

            }
        }
                , new Response.ErrorListener() { // QUE HACER EN CASO DE ERROR
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                volleyS.cancelAll();
                Alertas.alertaSimple(tituloERROR, "Servidor no responde, asegurese de estar conectado a internet o intentelo mas tarde", MainActivity.this);
                miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley, en MainActivity, btnActualizarPendientes. Mensaje : " + error.getMessage(),mac);
                updateList();
            }
        }
        );
        volleyS.addToQueue(jsonObjectRequest, MainActivity.this);
    }
    public void updateList() {

       //Actualizar Pendientes
        btnSolPendientes.setText("Solicitudes Pendientes [ " + String.valueOf(miDbHelper.CuentaSolicitudes()) + " ]");

        //Actualizar Errores
        btnError.setText("Enviar Errores [ " + String.valueOf(miDbHelper.CuentaErrores()) + " ]");

        //Actualizar Version
        btnVersion.setText("Version: " + getString(R.string.version));

        if(miDbHelper.isAdmin()){
            btnSolRechazadas.setVisibility(View.GONE);
           // btnSolRechazadas.setVisibility(View.INVISIBLE);
            btnSolAprobadas.setVisibility(View.GONE);
           // btnSolAprobadas.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up buttonOk, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnActualizar) {
            actualizarPendientes();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadTask extends AsyncTask<String,Integer,String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;
       final String rutaAndNombreArchivoActualizacion = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/actualizacion_hrsextra.apk";

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power buttonOk during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Descargando Actualización, por favor espere.");
            progressDialog.setIndeterminate(true);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            progressDialog.dismiss();
            if (result != null){
//    Si es distinto de Null significa que trae un mensajeERROR de error.
                Alertas.alertaSimple("Error interno", result,MainActivity.this);
                miDbHelper.insertarLogError("Error Interno:"+result+" en MainActivity, DownloadTask",mac);
                return;
            }

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Archivo Exitosamente Descargado")
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_menu_save)
                    .setPositiveButton("Aplicar actualización",
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i){
                                    finish();
                                    Intent promptInstall =

                                            new Intent(Intent.ACTION_VIEW)
                                                    .setDataAndType(Uri.parse("file://"+rutaAndNombreArchivoActualizacion)
                                                            ,"application/vnd.android.package-archive"
                                                    )
                                            ;
                                    startActivity(promptInstall);
                                }
                            }
                    )
                    .show()
            ;
            this.cancel(true);

        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            URL url = null;
            try {
                url = new URL(sUrl[0]);
            } catch (MalformedURLException e){
                miDbHelper.insertarLogError("No se detecta la ruta para buscar actualizaciones en MainActivity, doInBackground. Mensaje: "+e.getMessage(), mac);
                return "No se detecta ruta para buscar actualizacion.";
            }
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e){
                miDbHelper.insertarLogError("No se pudo abrir una conexión en MainActivity, doInBackground. Mensaje: "+e.getMessage(), mac);
                return "No se pudo conectar al servidor en busca de actualizaciones.";
            }
            try {
                connection.connect();
            } catch (IOException e){
                miDbHelper.insertarLogError("No se pudo conectar con el objeto connection al actualizar la aplicacion en MainActivity, doInBackground. Mensaje: "+e.getMessage(), mac);
                return "Error interno, favor informar";
            }

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    miDbHelper.insertarLogError("El servidor no responde  en MainActivity, doInBackground. Mensajes: getResponseCode() " + connection.getResponseCode()+" getResponseMessage() " + connection.getResponseMessage(),mac);
                    return "El servidor no responde. Intente en unos momentos y si el problema pesriste, favor informar a Ing. Software";
                }
            } catch (IOException e){
                miDbHelper.insertarLogError("El servidor responde con un codigo extraño o desconocido al actualizar la aplicacion  en MainActivity, doInBackground.", mac);
                return "El servidor no responde o responde con un mensaje desconocido.";
            }

            try {
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                File file = new File(rutaAndNombreArchivoActualizacion);
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1){
                    // allow canceling with back buttonOk
                    if (isCancelled()){
                        input.close();
                        return null;
                    }
                    total += count;
                    // publicamos el progreso
                    if (fileLength > 0){  // solo si se sabe la longitud
                        publishProgress((int) (total * 100 / fileLength));
                    }
                    output.write(data, 0, count);
                }

            }catch (SocketException e){
                if (e.getMessage().contains("timed out")){
                    return "Se superó el tiempo de espera para la comunicación. Compruebe su conexión a Internet.";
                }
                miDbHelper.insertarLogError("SocketException  en MainActivity, doInBackground :" + e.getMessage(),mac);
                return e.getMessage();
            } catch (Exception e) {
                miDbHelper.insertarLogError("Exception al descargar actualizacion  en MainActivity, doInBackground. Mensaje: " + e.getMessage(), mac);
                return e.getMessage();
            } finally {
                try {
                    if (output != null){
                        output.close();
                    }
                    if (input != null){
                        input.close();
                    }
                } catch (IOException ignored) {
                }

                if (connection != null){
                    connection.disconnect();
                }
            }

            return null;
        }


    }

}
