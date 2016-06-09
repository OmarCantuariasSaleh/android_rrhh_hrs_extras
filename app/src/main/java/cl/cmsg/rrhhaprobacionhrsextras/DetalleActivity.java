package cl.cmsg.rrhhaprobacionhrsextras;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.Alertas;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.Rut;
import cl.cmsg.rrhhaprobacionhrsextras.clases.ValidacionConexion;
import cl.cmsg.rrhhaprobacionhrsextras.clases.VolleyS;
import cl.cmsg.rrhhaprobacionhrsextras.gcm.ConstantesGlobales;
import cl.cmsg.rrhhaprobacionhrsextras.gcm.RegistrationIntentService;

public class DetalleActivity extends AppCompatActivity {

    TextView lblRut;
    TextView lblNombre;
    TextView lblFecha;
    TextView lblCantHoras;
    TextView lblMontoPagar;
    TextView lblMotivo;
    TextView lblComentario;
    TextView lblCentroCosto;
    TextView lblArea;
    TextView lblTipoPacto;
    MiDbHelper miDbHelper;
    LinearLayout layoutBotones;
    Button btnAprobar;
    Button btnRechazar;
    String lvl;
    ProgressDialog progressDialog;
    String mensaje;
    String titulo;
    String rut;
    String nombre;
    String fecha;
    int cant_horas;
    int monto_pagar;
    String motivo;
    String comentario;
    String centro_costo;
    String area;
    String tipo_pacto;
    String E1;
    String E2;
    String E3;
    String estado_Final;
    VolleyS volleyS;

    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        lblRut = (TextView) findViewById(R.id.lblRut);
        lblNombre = (TextView) findViewById(R.id.lblNombre);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        lblCantHoras = (TextView) findViewById(R.id.lblCantHoras);
        lblMontoPagar = (TextView) findViewById(R.id.lblMonto);
        lblMotivo = (TextView) findViewById(R.id.lblMotivo);
        lblComentario = (TextView) findViewById(R.id.lblComentario);
        lblCentroCosto = (TextView) findViewById(R.id.lblCentroCosto);
        lblArea = (TextView) findViewById(R.id.lblArea);
        lblTipoPacto = (TextView) findViewById(R.id.lblTipoPacto);
        layoutBotones = (LinearLayout) findViewById(R.id.layoutBotones);
        btnAprobar = (Button) findViewById(R.id.btnAprobar);
        btnRechazar = (Button) findViewById(R.id.btnRechazar);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle= getIntent().getExtras();
        miDbHelper = MiDbHelper.getInstance(this,DetalleActivity.this);
        cursor =   miDbHelper.getDatoSolicitudDetalle(bundle.getString("Rut",""),bundle.getString("fecha",""));


        while(cursor.moveToNext()){
            rut = cursor.getString(cursor.getColumnIndex("Rut"));
            lblRut.setText(lblRut.getText().toString() + " " +rut);

            nombre = cursor.getString(cursor.getColumnIndex("nombre"));
            lblNombre.setText(lblNombre.getText().toString() + " " +nombre);

            fecha = cursor.getString(cursor.getColumnIndex("fecha"));
            lblFecha.setText(lblFecha.getText().toString() + " " +fecha);

            cant_horas = (cursor.getInt(cursor.getColumnIndex("cant_horas")));
            lblCantHoras.setText(lblCantHoras.getText().toString() + " " +cant_horas);

            monto_pagar = cursor.getInt(cursor.getColumnIndex("monto_pagar"));
            lblMontoPagar.setText(lblMontoPagar.getText().toString()+monto_pagar);

            motivo = cursor.getString(cursor.getColumnIndex("motivo"));
            lblMotivo.setText(lblMotivo.getText().toString() + " " +motivo);

            comentario = cursor.getString(cursor.getColumnIndex("comentario"));
            lblComentario.setText(comentario);

            centro_costo = cursor.getString(cursor.getColumnIndex("centro_costo"));
            lblCentroCosto.setText(lblCentroCosto.getText().toString() + " " +centro_costo);

            area = cursor.getString(cursor.getColumnIndex("area"));
            lblArea.setText(lblArea.getText().toString() + " " +area);

            tipo_pacto = cursor.getString(cursor.getColumnIndex("tipo_pacto"));
            lblTipoPacto.setText(lblTipoPacto.getText().toString() + " " +tipo_pacto);

            E1= cursor.getString(cursor.getColumnIndex("estado1"));
            E2= cursor.getString(cursor.getColumnIndex("estado2"));
            E3= cursor.getString(cursor.getColumnIndex("estado3"));

            // Verificar si detalle es de solicitud aprobada o pendiente, separadas por nivel
            String rut1 = cursor.getString(cursor.getColumnIndex("rut_admin1"));
            String rut2 = cursor.getString(cursor.getColumnIndex("rut_admin2"));
            String rut3 = cursor.getString(cursor.getColumnIndex("rut_admin3"));

            lvl="0";

            if(E1.equals("R") || E3.equals("R") || E3.equals("R")) {
                layoutBotones.setVisibility(View.INVISIBLE);
            }else{
                if (E1.equals("A") && rut1.equals(miDbHelper.getRutUsuario())) {
                    layoutBotones.setVisibility(View.INVISIBLE);
                } else if (E2.equals("A") && rut2.equals(miDbHelper.getRutUsuario())) {
                    layoutBotones.setVisibility(View.INVISIBLE);
                } else if (E3.equals("A") && rut3.equals(miDbHelper.getRutUsuario())) {
                    layoutBotones.setVisibility(View.INVISIBLE);
                }
            }

            if(E1.equals("P") && rut1.equals(miDbHelper.getRutUsuario())){
                lvl="1";
            }else if(E2.equals("P") && rut2.equals(miDbHelper.getRutUsuario()) && E3.equals("A") && E1.equals("A")){
                lvl="2";
            }else if(E3.equals("P") && rut3.equals(miDbHelper.getRutUsuario()) && E1.equals("A") && E2.equals("A")){
                lvl="3";
            }


        }
        cursor.close();

        btnAprobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(DetalleActivity.this)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                progressDialog = new ProgressDialog(DetalleActivity.this);
                                progressDialog.setTitle("Actualizando datos");
                                progressDialog.setMessage("Espere un momento");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                if(!ValidacionConexion.isExisteConexion(DetalleActivity.this)){
                                    progressDialog.dismiss();
                                    Alertas.alertaConexion(DetalleActivity.this);
                                    return;
                                }

                                estado_Final= "A";
                                actualizaEstado(rut,fecha,estado_Final,lvl,tipo_pacto);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setCancelable(false)
                        .setTitle("¿Esta seguro?")
                        .setMessage("Esta accion no se puede deshacer.")
                        .show()
                ;



            }
        });

        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(DetalleActivity.this)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                progressDialog = new ProgressDialog(DetalleActivity.this);
                                progressDialog.setTitle("Actualizando datos");
                                progressDialog.setMessage("Espere un momento");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                if(!ValidacionConexion.isExisteConexion(DetalleActivity.this)){
                                    progressDialog.dismiss();
                                    Alertas.alertaConexion(DetalleActivity.this);
                                    return;
                                }

                                estado_Final= "R";
                                actualizaEstado(rut,fecha,estado_Final,lvl,tipo_pacto);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setCancelable(false)
                        .setTitle("¿Esta seguro?")
                        .setMessage("Esta accion no se puede deshacer.")
                        .show()
                ;

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void actualizaEstado(final String rut_S,final String fecha_S, final String estado_Final,final String lvl_S, final String tipo_pacto_S){
        final VolleyS volleyS = VolleyS.getInstance(this);

        if(!ValidacionConexion.isExisteConexion(DetalleActivity.this)){
            progressDialog.dismiss();
            Alertas.alertaConexion(DetalleActivity.this );
            return;
        }

        String mac = ValidacionConexion.getDireccionMAC(DetalleActivity.this);
        Log.e("Omar", mac);
        final StringRequest jsonObjectRequest = new StringRequest(

                Request.Method.GET // FORMA QUE LLAMAREMOS, O SEA GET
                , getString(R.string.URL_ActualizarEstado)
                +"?run="+ rut_S
                +"&fecha="+fecha_S
                +"&estado="+estado_Final
                +"&lvl="+lvl_S
                +"&apk_key="+getString(R.string.APK_KEY)
                +"&mac="+mac
                +"&run_admin="+miDbHelper.getRutUsuario()
                +"&tipo_pacto="+tipo_pacto_S

                // URL QUE LLAMAREMOS, TODO reemplazar por URL nueva
                , new Response.Listener<String>(){ // OBJETO QUE USAREMOS PARA LA ESCUCHA DE LA RESPUESTA
            @Override
            public void onResponse(String response){
                progressDialog.dismiss();
                JSONObject jsonObject;
                Boolean error;
                int run;
                String mensajesrv;

                Log.e("omar",response);
                if(response==null || response.isEmpty()){
                    progressDialog.dismiss();
                    titulo = "ERROR";
                    mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
                    Alertas.alertaSimple(titulo,mensaje,DetalleActivity.this);

                    miDbHelper.insertarLogError("Variable response es Nulo o Vacio");
                    Log.e("Omar", "Variable response es Nulo o Vacio");
                    return;
                }
                try {
                    //Log.e("Omar","entro a try");
                    jsonObject= new JSONObject(response);
                    //Log.e("Respuesta",response);
                } catch (JSONException e) {

                    titulo = "ERROR \n";
                    mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto y el siguiente error:\n\n"+String.valueOf(e);
                    Alertas.alertaSimple(titulo,mensaje,DetalleActivity.this);

                    miDbHelper.insertarLogError("Error de formato en variable 'response', no parece ser tipo JSON. Mensaje de error : "+e.getMessage());
                    Log.e("Omar","Error de formato en variable 'response', no parece ser tipo JSON. Mensaje de error : "+e.getMessage() );
                    return;
                }
                try{
                    error = jsonObject.getBoolean("error");

                } catch (JSONException e) {
                    e.printStackTrace();
                    titulo = "ERROR \n";
                    mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
                    Alertas.alertaSimple(titulo,mensaje,DetalleActivity.this);

                    miDbHelper.insertarLogError("Error de formato en variable 'error', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
                    Log.e("Omar","Error de formato en variable 'mensaje', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
                    return;
                }
                if(error){
                    //Log.e("Omar","entro a error");

                    try {
                        mensajesrv = jsonObject.getString("mensaje");
                    } catch (JSONException e) {
                        titulo = "ERROR \n";
                        mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
                        Alertas.alertaSimple(titulo,mensaje,DetalleActivity.this);

                        miDbHelper.insertarLogError("Error de formato en variable 'mensaje', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
                        Log.e("Omar","Error de formato en variable 'mensaje', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage() );
                        return;
                    }
                    titulo = "Servidor responde con el siguiente error:";
                    mensaje = mensajesrv;
                    Alertas.alertaSimple(titulo,mensaje,DetalleActivity.this);
                    return;
                }
                //Log.e("Respuesta",response);

                miDbHelper.actualizarEstado(rut,fecha,estado_Final,lvl);
                titulo = "Exito";
                mensaje = "Actualizacion exitosa";
                //Alertas.alertaSimple(titulo,mensaje,DetalleActivity.this);
                new AlertDialog.Builder(DetalleActivity.this)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .setMessage(mensaje)
                        .setTitle(titulo)
                        .show()
                ;


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
                Alertas.alertaSimple(titulo,mensaje,DetalleActivity.this);

                miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : "+error);
                Log.e("Omar","Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : "+error );

            }
        }
        );
        volleyS.addToQueue(jsonObjectRequest, DetalleActivity.this);

    }

}
