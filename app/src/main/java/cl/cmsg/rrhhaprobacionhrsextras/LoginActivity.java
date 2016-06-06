package cl.cmsg.rrhhaprobacionhrsextras;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.cmsg.rrhhaprobacionhrsextras.clases.Alertas;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.Rut;
import cl.cmsg.rrhhaprobacionhrsextras.clases.ValidacionConexion;
import cl.cmsg.rrhhaprobacionhrsextras.clases.VolleyS;

public class LoginActivity extends AppCompatActivity {

    Button button;
    MiDbHelper miDbHelper;
    EditText editText;
    ProgressDialog progressDialog;
    String mensaje;
    String titulo;
    Alertas alertas;
String rut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        button = (Button) findViewById(R.id.buttonOk);
        editText = (EditText) findViewById(R.id.editText);
        miDbHelper = MiDbHelper.getInstance(this,LoginActivity.this);
        final VolleyS volleyS = VolleyS.getInstance(this);





// TODO Borrar cuando entre a produccion
        // Borrar solicitudes guardadas para agregar/actualizar
        //miDbHelper.deleteSolicitudALL();
        //miDbHelper.deleteUser();
        //miDbHelper.insertarUsuario("6774875","Omar Cantuarias Saleh");
        /*// Solicitud lvl 3 Pendiente --------------------------------------------------
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-02-02",8,1222000,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","A","123412341-1","P","16841244-4");
        miDbHelper.insertarSolicitud("11111111-1"
                ,"Persona Mcperson"
                ,"2014-01-10"
                ,6
                ,100300
                ,"Retraso"
                ,"Atraso en avance de proyecto que era para ayer"
                ,"Informatica"
                ,"Informatica"
                ,"Festivo"
                ,"A"
                ,"12312312-1"
                ,"A"
                ,"123412341-1"
                ,"P"
                ,"16841244-4");
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-01-11",6,100300,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","A","123412341-1","P","16841244-4");
        miDbHelper.insertarSolicitud("14444111-1","Persona Mcperson","2014-01-10",6,100300,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","16841244-4",null,null,null,null);

        // Solicitud para otro lvl 3
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-01-05",6,105000,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","A","123412341-1","P","12222244-4");

        // Solicitud lvl 2 Pendiente --------------------------------------------------
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-01-07",6,106600,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","P","123412341-1",null,null);
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-01-03",6,100000,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","A","123412341-1",null,null);
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-01-30",6,100000,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","P","16841244-4",null,null);

        // Solicitud lvl 1 pendiente --------------------------------------------------
        miDbHelper.insertarSolicitud("11222111-1","Persona Mcperson","2014-01-10",6,100000,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1",null,null,null,null);*/
//TODO Borrar cuando entre a produccion

//TODO cambiar para que pregunte si el rut en el servidor es igual al de la BD del CeL --
        if(!miDbHelper.getRutUsuario().equals("")){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
//TODO end
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle("Actualizando");
                progressDialog.setMessage("Espere un momento");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                rut = editText.getText().toString();
                if (!Rut.isRutValido(rut)){

                    Toast.makeText(getApplicationContext(),"rut invalido", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                rut= rut.replace(".","");
                rut= rut.replace("-","");
                rut= rut.trim();
                rut= rut.substring(0,rut.length()-1);

                String mac= ValidacionConexion.getDireccionMAC(LoginActivity.this);
                    final StringRequest jsonObjectRequest = new StringRequest(

                            Request.Method.GET // FORMA QUE LLAMAREMOS, O SEA GET
                            , getString(R.string.URL_RegistrarUsuario) +"?run="+rut+"&mac="+mac+"&numero_proyecto=14&token="+getString(R.string.gcm_Token)// URL QUE LLAMAREMOS
                            , new Response.Listener<String>(){ // OBJETO QUE USAREMOS PARA LA ESCUCHA DE LA RESPUESTA
                        @Override
                        public void onResponse(String response){
                            progressDialog.dismiss();
                            JSONObject jsonObject=null;
                            Boolean error=true;
                            String mensajesrv= null;

                            String nombre;

                            if(response.equals(null) || response.isEmpty()){

                                titulo = "ERROR \n";
                                mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
                                alertas.alertaSimple(titulo,mensaje,LoginActivity.this);

                                miDbHelper.insertarLogError("Variable response es Nulo o Vacio");
                                return;
                            }

                            try {
                                //Log.e("Omar","entro a try");
                                jsonObject= new JSONObject(response);
                                //Log.e("Respuesta",response);
                            } catch (JSONException e) {

                                titulo = "ERROR \n";
                                mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto y el siguiente error:\n\n"+String.valueOf(e);
                                alertas.alertaSimple(titulo,mensaje,LoginActivity.this);

                                miDbHelper.insertarLogError("Error de formato en variable 'response', no parece ser tipo JSON. Mensaje de error : "+e.getMessage());
                                return;
                            }

                            try{
                                error = jsonObject.getBoolean("error");

                            } catch (JSONException e) {
                                e.printStackTrace();
                                titulo = "ERROR \n";
                                mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
                                alertas.alertaSimple(titulo,mensaje,LoginActivity.this);

                                miDbHelper.insertarLogError("Error de formato en variable 'error', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
                                return;
                            }
                            if(error){
                                //Log.e("Omar","entro a error");

                                try {
                                    mensajesrv = jsonObject.getString("mensaje");
                                } catch (JSONException e) {
                                    titulo = "ERROR \n";
                                    mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
                                    alertas.alertaSimple(titulo,mensaje,LoginActivity.this);

                                    miDbHelper.insertarLogError("Error de formato en variable 'mensaje', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
                                    return;
                                }
                                titulo = "Servidor responde con el siguiente error:";

                                alertas.alertaSimple(titulo,mensajesrv,LoginActivity.this);
                                return;
                            }
                                miDbHelper.deleteUser();
                                miDbHelper.insertarUsuario(rut,mensajesrv);
                                Toast.makeText(getApplicationContext(),"Registrado", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                finish();
                                startActivity(intent);

                        }
                    }
                            , new Response.ErrorListener(){ // QUE HACER EN CASO DE ERROR
                        @Override
                        public void onErrorResponse(VolleyError error){
                            progressDialog.dismiss();
                            volleyS.cancelAll();

                            titulo = "Error";
                            mensaje = "Servidor no responde \n" +
                                    " Asegurese de estar conectado a internet o intentelo mas tarde";
                            alertas.alertaSimple(titulo,mensaje,LoginActivity.this);

                            miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : "+error);
                        }



                    }
                    );
                    volleyS.addToQueue(jsonObjectRequest, LoginActivity.this);




            }
        });



    }
}
