package cl.cmsg.rrhhaprobacionhrsextras;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cl.cmsg.rrhhaprobacionhrsextras.clases.Alertas;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.ValidacionConexion;
import cl.cmsg.rrhhaprobacionhrsextras.clases.VolleyS;
import cl.cmsg.rrhhaprobacionhrsextras.horasextras.HorasExtras;
import cl.cmsg.rrhhaprobacionhrsextras.horasextras.HorasExtrasAdapter;

public class HorasPendientesActivity extends AppCompatActivity {

    ListView listViewPendientes;
    HorasExtrasAdapter horasExtrasAdapter;
    HorasExtras horasExtras;
    ArrayList<HorasExtras> arrayListHorasExtra = new ArrayList<>();
    MiDbHelper miDbHelper;
    TextView lblRut;
    TextView lblNombre;
    TextView lblFecha;
    TextView lblTipoPacto;
    String rut;
    String nombre;
    String fecha;
    String tipo_pacto;
    String mensaje;
    String titulo;
    int cant_horas;
    String lvl;
    private static final int pend = 179;
    RadioButton radioHorasExtra;
    RadioButton radioFestivos;
    Button btnAprobarTodo;
    TextView lblcontador;
    int contador=0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horas_pendientes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioHorasExtra = (RadioButton) findViewById(R.id.radioHorasExtras);
        radioFestivos = (RadioButton) findViewById(R.id.radioFestivos);
        btnAprobarTodo = (Button) findViewById(R.id.btnAprobarTodo);
        lblcontador = (TextView) findViewById(R.id.lblContador);

        llenarLista();

        listViewPendientes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {


                Intent intent = new Intent(getApplicationContext(), DetalleActivity.class);
                HorasExtras horasExtras = arrayListHorasExtra.get(position);
                intent.putExtra("Rut", horasExtras.getRut());
                intent.putExtra("fecha", horasExtras.getFecha());
                intent.putExtra("tipo_pacto", horasExtras.getTipo_pacto());

                //intent.putExtra("switch",0);
                startActivityForResult(intent, pend);

                //startActivity(intent);


                return true;
            }
        });



        listViewPendientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("Omar", "entro a onitem");
                SparseBooleanArray checkedItemPositions = listViewPendientes.getCheckedItemPositions();

                if(checkedItemPositions!=null && checkedItemPositions.size()>0){

                    Log.e("Omar", String.valueOf(checkedItemPositions.size()));
                    contador=0;
                    for(int x=0;x<checkedItemPositions.size();x++){
                        if(checkedItemPositions.valueAt(x)){
                           contador++;
                        }
                    }
                    lblcontador.setText(String.valueOf(contador));
                    //Toast.makeText(getApplicationContext(), "Registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAprobarTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int x = Integer.parseInt(lblcontador.getText().toString());
                if(x>0){
                    progressDialog = new ProgressDialog(HorasPendientesActivity.this);
                    progressDialog.setTitle("Actualizando datos");
                    progressDialog.setMessage("Espere un momento");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    SparseBooleanArray checkedItemPositions = listViewPendientes.getCheckedItemPositions();

                    if(checkedItemPositions!=null && checkedItemPositions.size()>0){

                        Log.e("Omar", String.valueOf(checkedItemPositions.size()));
                        contador=0;
                        String run_A;
                        String fecha_A;
                        String E_A="A";
                        String tipoPacto_A;
                        String lvl_A;

                        for(int i=0;i<checkedItemPositions.size();i++){
                            if(checkedItemPositions.valueAt(i)){
                                HorasExtras horasExtras = arrayListHorasExtra.get(i);
                                run_A =  horasExtras.getRut();
                                fecha_A =  horasExtras.getFecha();
                                tipoPacto_A =  horasExtras.getRut();
                                lvl_A =  horasExtras.getRut();
                                actualizaEstado(run_A,fecha_A,E_A,lvl_A,tipoPacto_A);
                            }
                        }
                        lblcontador.setText(String.valueOf(contador));
                        //Toast.makeText(getApplicationContext(), "Registrado", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(), "No hay solicitudes seleccionadas", Toast.LENGTH_SHORT).show();
                }
            }
        });


        radioHorasExtra.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                llenarLista();
            }
        });

        radioFestivos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                llenarLista();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//  super.onActivityResult(requestCode, resultCode, data);
        Log.e("Omar", "Entro a result");
        if (requestCode == pend) {
            if (resultCode == RESULT_OK) {
                // Aquí se pone lo que se hará si el resultado fue exitoso
                Log.e("Omar", "if result");
                llenarLista();
            }
        }
    }

    void llenarLista() {

        lblRut = (TextView) findViewById(R.id.lblRut);
        lblNombre = (TextView) findViewById(R.id.lblNombre);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        lblTipoPacto = (TextView) findViewById(R.id.lblTipoPacto);
        listViewPendientes = (ListView) findViewById(R.id.lstHorasPendientes);
        miDbHelper = MiDbHelper.getInstance(this, HorasPendientesActivity.this);
        arrayListHorasExtra.clear();
        String rut_user = miDbHelper.getRutUsuario();
        Cursor cursor = miDbHelper.getDatoSolicitudLVL(rut_user);
        contador = 0;
        lblcontador.setText(String.valueOf(contador));
        Log.e("Omar", "Rut usuario" + rut_user + " // " + String.valueOf(cursor.getCount()));
        while (cursor.moveToNext()) {

            int lvl = 0;

            String E1 = cursor.getString(cursor.getColumnIndex("estado1"));
            String E2 = cursor.getString(cursor.getColumnIndex("estado2"));
            String E3 = cursor.getString(cursor.getColumnIndex("estado3"));
            String rut1 = cursor.getString(cursor.getColumnIndex("rut_admin1"));
            String rut2 = cursor.getString(cursor.getColumnIndex("rut_admin2"));
            String rut3 = cursor.getString(cursor.getColumnIndex("rut_admin3"));
            String tipoPacto = cursor.getString(cursor.getColumnIndex("tipo_pacto"));

            // Log.e("Omar", "E1:"+E1+" E2:"+E2+" E3:"+E3);

            if (!E1.equals("R") || !E2.equals("R") || !E3.equals("R")) {
                if (E1.equals("P") && rut1.equals(miDbHelper.getRutUsuario())) {
                    lvl = 1;
                } else if (E2.equals("P") && rut2.equals(miDbHelper.getRutUsuario()) && E3.equals("A") && E1.equals("A")) {
                    lvl = 2;
                } else if (E3.equals("P") && rut3.equals(miDbHelper.getRutUsuario()) && E1.equals("A") && E2.equals("A")) {
                    lvl = 3;
                }
            }

            if (radioFestivos.isChecked() && !tipoPacto.equals("F")) {
                lvl = 0;
            } else if (radioHorasExtra.isChecked() && tipoPacto.equals("F")) {
                lvl = 0;
            }

            //Log.e("Omar", "E1:"+E1+" E2:"+E2+" E3:"+E3+" Lvl:"+lvl);
            //Log.e("Omar","Lvl:"+ lvl+ " Estado 1: "+E1+" Estado 2: "+E2+" Estado 3: "+E3);
            if (lvl != 0) {
                rut = cursor.getString(cursor.getColumnIndex("Rut"));

                nombre = cursor.getString(cursor.getColumnIndex("nombre"));

                fecha = cursor.getString(cursor.getColumnIndex("fecha"));

                tipo_pacto = cursor.getString(cursor.getColumnIndex("tipo_pacto"));

                cant_horas = cursor.getInt(cursor.getColumnIndex("cant_horas"));

                horasExtras = new HorasExtras(rut, nombre, fecha, tipo_pacto, cant_horas,lvl);

                arrayListHorasExtra.add(horasExtras);
            }

        }

        horasExtrasAdapter = new HorasExtrasAdapter(arrayListHorasExtra, this);
        //listViewPendientes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listViewPendientes.setAdapter(horasExtrasAdapter);
    }

    public void actualizaEstado(final String rut_S,final String fecha_S, final String estado_Final,final String lvl_S, final String tipo_pacto_S){
        final VolleyS volleyS = VolleyS.getInstance(this);

        if(!ValidacionConexion.isExisteConexion(HorasPendientesActivity.this)){
            progressDialog.dismiss();
            Alertas.alertaConexion(HorasPendientesActivity.this );
            return;
        }

        String mac = ValidacionConexion.getDireccionMAC(HorasPendientesActivity.this);
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
                    Alertas.alertaSimple(titulo,mensaje,HorasPendientesActivity.this);

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
                    Alertas.alertaSimple(titulo,mensaje,HorasPendientesActivity.this);

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
                    Alertas.alertaSimple(titulo,mensaje,HorasPendientesActivity.this);

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
                        Alertas.alertaSimple(titulo,mensaje,HorasPendientesActivity.this);

                        miDbHelper.insertarLogError("Error de formato en variable 'mensaje', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
                        Log.e("Omar","Error de formato en variable 'mensaje', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage() );
                        return;
                    }
                    titulo = "Servidor responde con el siguiente error:";
                    mensaje = mensajesrv;
                    Alertas.alertaSimple(titulo,mensaje,HorasPendientesActivity.this);
                    return;
                }
                //Log.e("Respuesta",response);

                miDbHelper.actualizarEstado(rut_S,fecha_S,estado_Final,lvl_S, tipo_pacto_S);
                titulo = "Exito";
                mensaje = "Actualizacion exitosa";
                //Alertas.alertaSimple(titulo,mensaje,DetalleActivity.this);
                new AlertDialog.Builder(HorasPendientesActivity.this)
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
                Alertas.alertaSimple(titulo,mensaje,HorasPendientesActivity.this);

                miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : "+error);
                Log.e("Omar","Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : "+error );

            }
        }
        );
        volleyS.addToQueue(jsonObjectRequest, HorasPendientesActivity.this);

    }

}
