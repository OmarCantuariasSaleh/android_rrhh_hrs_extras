package cl.cmsg.rrhhaprobacionhrsextras;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
    final String mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
    final String tituloERROR="ERROR";
    int cant_horas;
    int errorNuloVacio = 0;
    int errorRESPONSE = 0;
    int errorFormatoError = 0;
    int error4 = 0;
    int error5 = 0;
    int error6 = 0;
    int errorconn = 0;
    int exito1 = 0;
    long total=0;


    private static final int PEND = 179;
    RadioButton radioHorasExtra;
    RadioButton radioFestivos;
    Button btnAprobarTodo;
    Button btnRechazarTodo;
    TextView lblcontador;
    int contador=0;
    ProgressDialog progressDialog;
    LinearLayout layBotones;

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
        btnRechazarTodo = (Button) findViewById(R.id.btnRechazarTodo);
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

                startActivityForResult(intent, PEND);

                return true;
            }
        });



        listViewPendientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(miDbHelper.isAdmin()){
                    Intent intent = new Intent(getApplicationContext(), DetalleActivity.class);
                    HorasExtras horasExtras = arrayListHorasExtra.get(i);
                    intent.putExtra("Rut", horasExtras.getRut());
                    intent.putExtra("fecha", horasExtras.getFecha());
                    intent.putExtra("tipo_pacto", horasExtras.getTipo_pacto());

                    startActivityForResult(intent, PEND);
                    return;
                }

                SparseBooleanArray checkedItemPositions = listViewPendientes.getCheckedItemPositions();

                if(checkedItemPositions!=null && checkedItemPositions.size()>0){

                    contador=0;
                    for(int x=0;x<checkedItemPositions.size();x++){
                        if(checkedItemPositions.valueAt(x)){
                           contador++;
                        }
                    }
                    lblcontador.setText(String.valueOf(contador));
                }
            }
        });

        btnAprobarTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int x = Integer.parseInt(lblcontador.getText().toString());
                if(x>0){
                    new AlertDialog.Builder(HorasPendientesActivity.this)
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressDialog = new ProgressDialog(HorasPendientesActivity.this);
                                    progressDialog.setTitle("Actualizando datos");
                                    progressDialog.setMessage("Espere un momento");
                                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();

                                    SparseBooleanArray checkedItemPositions = listViewPendientes.getCheckedItemPositions();

                                    if(checkedItemPositions!=null && checkedItemPositions.size()>0){

                                        contador=0;
                                        String run_A;
                                        String fecha_A;
                                        String E_A="A";
                                        String tipoPacto_A;
                                        String lvl_A;
                                        errorNuloVacio = 0;
                                        errorRESPONSE = 0;
                                        errorFormatoError = 0;
                                        error4 = 0;
                                        error5 = 0;
                                        error6 = 0;
                                        errorconn = 0;
                                        exito1 = 0;
                                        total = checkedItemPositions.size();

                                        for(int y=0;y<total;y++){
                                            progressDialog.incrementProgressBy((int) (y * 100 / total));
                                            if(checkedItemPositions.valueAt(y)){
                                                HorasExtras horasExtras = arrayListHorasExtra.get(y);
                                                run_A =  horasExtras.getRut();
                                                fecha_A =  horasExtras.getFecha();
                                                tipoPacto_A =  horasExtras.getTipo_pacto();
                                                lvl_A = String.valueOf(horasExtras.getLvl());
                                                actualizaEstado(run_A, fecha_A, E_A, lvl_A, tipoPacto_A);
                                            }
                                        }
                                        lblcontador.setText(String.valueOf(contador));
                                    }

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


                }else{
                    Toast.makeText(getApplicationContext(), "No hay solicitudes seleccionadas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRechazarTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int x = Integer.parseInt(lblcontador.getText().toString());
                if(x>0){

                    new AlertDialog.Builder(HorasPendientesActivity.this)
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressDialog = new ProgressDialog(HorasPendientesActivity.this);
                                    progressDialog.setTitle("Actualizando datos");
                                    progressDialog.setMessage("Espere un momento");
                                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();

                                    SparseBooleanArray checkedItemPositions = listViewPendientes.getCheckedItemPositions();

                                    if(checkedItemPositions!=null && checkedItemPositions.size()>0){

                                        contador=0;
                                        String run_A;
                                        String fecha_A;
                                        String E_A="R";
                                        String tipoPacto_A;
                                        String lvl_A;
                                        errorNuloVacio = 0;
                                        errorRESPONSE = 0;
                                        errorFormatoError = 0;
                                        error4 = 0;
                                        error5 = 0;
                                        error6 = 0;
                                        errorconn = 0;
                                        exito1 = 0;
                                        total = checkedItemPositions.size();
                                        for(int x=0;x<total;x++){

                                            progressDialog.incrementProgressBy((int) (x * 100 / total));
                                            if(checkedItemPositions.valueAt(x)){
                                                HorasExtras horasExtras = arrayListHorasExtra.get(x);
                                                run_A =  horasExtras.getRut();
                                                fecha_A =  horasExtras.getFecha();
                                                tipoPacto_A =  horasExtras.getTipo_pacto();
                                                lvl_A = String.valueOf(horasExtras.getLvl());
                                                actualizaEstado(run_A, fecha_A, E_A, lvl_A, tipoPacto_A);
                                            }
                                        }
                                        lblcontador.setText(String.valueOf(contador));
                                    }

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
        if (requestCode == PEND) {
            if (resultCode == RESULT_OK) {
                llenarLista();
            }
        }
        if(miDbHelper.isAdmin()){
            llenarLista();
        }
    }

    void llenarLista() {

        lblRut = (TextView) findViewById(R.id.lblRut);
        lblNombre = (TextView) findViewById(R.id.lblNombre);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        lblTipoPacto = (TextView) findViewById(R.id.lblTipoPacto);
        listViewPendientes = (ListView) findViewById(R.id.lstHorasPendientes);
        miDbHelper = MiDbHelper.getInstance(this);
        arrayListHorasExtra.clear();
        String rut_user = miDbHelper.getRutUsuario();
        Cursor cursor;
        layBotones = (LinearLayout) findViewById(R.id.layBotones);

        if(miDbHelper.isAdmin()){
            cursor = miDbHelper.getDatoSolicitudADMIN();
            layBotones.setVisibility(View.GONE);
        }else {
            cursor = miDbHelper.getDatoSolicitudLVL(rut_user);
        }
        contador = 0;
        lblcontador.setText(String.valueOf(contador));
        while (cursor.moveToNext()) {

            int lvl = 0;

            String E1 = cursor.getString(cursor.getColumnIndex("estado1"));
            String E2 = cursor.getString(cursor.getColumnIndex("estado2"));
            String E3 = cursor.getString(cursor.getColumnIndex("estado3"));
            String rut1 = cursor.getString(cursor.getColumnIndex("rut_admin1"));
            String rut2 = cursor.getString(cursor.getColumnIndex("rut_admin2"));
            String rut3 = cursor.getString(cursor.getColumnIndex("rut_admin3"));
            String tipoPacto = cursor.getString(cursor.getColumnIndex("tipo_pacto"));

            if (!E1.equals("R") || !E2.equals("R") || !E3.equals("R")) {
                if(miDbHelper.isAdmin()){
                    if (E1.equals("P") ) {
                        lvl = 1;
                    } else if (E1.equals("A") && E2.equals("P")) {
                        lvl = 2;
                    } else if (E1.equals("A") && E2.equals("A") && E3.equals("P") ) {
                        lvl = 3;
                    }
                }else {

                    if (E1.equals("P") && rut1.equals(miDbHelper.getRutUsuario())) {
                        lvl = 1;
                    } else if (E1.equals("A") && E2.equals("P") && rut2.equals(miDbHelper.getRutUsuario())) {
                        lvl = 2;
                    } else if (E1.equals("A") && E2.equals("A") && E3.equals("P") && rut3.equals(miDbHelper.getRutUsuario())) {
                        lvl = 3;
                    }
                }
            }

            if (radioFestivos.isChecked() && !tipoPacto.equals("F")) {
                lvl = 0;
            } else if (radioHorasExtra.isChecked() && tipoPacto.equals("F")) {
                lvl = 0;
            }


            if (lvl != 0) {
                rut = cursor.getString(cursor.getColumnIndex("Rut"));

                nombre = cursor.getString(cursor.getColumnIndex("nombre"));

                fecha = cursor.getString(cursor.getColumnIndex("fecha"));

                tipo_pacto =cursor.getString(cursor.getColumnIndex("tipo_pacto"));

                cant_horas = cursor.getInt(cursor.getColumnIndex("cant_horas"));
                horasExtras = new HorasExtras(rut, nombre, fecha, tipo_pacto, cant_horas,lvl);

                arrayListHorasExtra.add(horasExtras);
            }

        }
        cursor.close();
        horasExtrasAdapter = new HorasExtrasAdapter(arrayListHorasExtra, this);
        listViewPendientes.setAdapter(horasExtrasAdapter);
    }

    public void actualizaEstado(final String rut_S,final String fecha_S, final String estado_Final,final String lvl_S, final String tipo_pacto_S){
        final VolleyS volleyS = VolleyS.getInstance(this);

        if(!ValidacionConexion.isExisteConexion(HorasPendientesActivity.this)){
            progressDialog.dismiss();
            errorconn++;
            if(errorconn==1) {

                llenarLista();
                Alertas.alertaConexion(HorasPendientesActivity.this);

            return;
        }

        final String mac = ValidacionConexion.getDireccionMAC(HorasPendientesActivity.this);

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
                JSONObject jsonObject;
                Boolean error;
                String mensajesrv;

                if(response==null || response.isEmpty()){
                    progressDialog.dismiss();
                    if(++errorNuloVacio==1) {

                        Alertas.alertaSimple(tituloERROR, mensaje, HorasPendientesActivity.this);
                        miDbHelper.insertarLogError("Variable response es Nulo o Vacio",mac);
                        llenarLista();

                    }
                    return;
                }
                try {
                    jsonObject= new JSONObject(response);
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    if(++errorRESPONSE ==1) {
                        Alertas.alertaSimple(tituloERROR, mensaje, HorasPendientesActivity.this);
                        llenarLista();
                        miDbHelper.insertarLogError("Error de formato en 'response', no parece ser tipo JSON. Mensaje de error : " + e.getMessage(),mac);
                    }
                    return;
                }
                try{
                    error = jsonObject.getBoolean("error");

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    if(++errorFormatoError ==1) {
                        Alertas.alertaSimple(tituloERROR, mensaje, HorasPendientesActivity.this);
                        miDbHelper.insertarLogError("Error de formato en variable 'error', No existe o es un formato incorrecto. Mensaje de error : " + e.getMessage(),mac);
                        llenarLista();
                    }
                    return;
                }
                if(error){

                    try {
                        mensajesrv = jsonObject.getString("mensaje");
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        if(++error4==1) {
                            Alertas.alertaSimple(tituloERROR, mensaje, HorasPendientesActivity.this);
                            miDbHelper.insertarLogError("Error de formato en variable 'mensaje', No existe o es un formato incorrecto. Mensaje de error : " + e.getMessage(),mac);
                            llenarLista();
                        }
                        return;
                    }
                    progressDialog.dismiss();
                    error5++;
                    if(error5==1) {
                        Alertas.alertaSimple("Servidor responde con el siguiente error:", mensajesrv, HorasPendientesActivity.this);
                        llenarLista();
                    }
                    return;
                }

                llenarLista();
                progressDialog.dismiss();
                miDbHelper.actualizarEstado(rut_S,fecha_S,estado_Final,lvl_S, tipo_pacto_S);
               if(exito1++==1) {
                   new AlertDialog.Builder(HorasPendientesActivity.this)
                           .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   setResult(RESULT_OK);
                                   llenarLista();
                               }
                           })
                           .setCancelable(false)
                           .setMessage("Actualizacion exitosa")
                           .setTitle("Exito")
                           .show()
                   ;
               }

            }
        }
                , new Response.ErrorListener(){ // QUE HACER EN CASO DE ERROR
            @Override
            public void onErrorResponse(VolleyError error){
                progressDialog.dismiss();
                volleyS.cancelAll();

                error6++;
                if(error6==1) {
                   Alertas.alertaSimple(tituloERROR, "Servidor no responde, asegurese de estar conectado a internet o intentelo mas tarde", HorasPendientesActivity.this);
                   miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : " + error,mac);
                   llenarLista();
               }
            }
        }
        );
        volleyS.addToQueue(jsonObjectRequest, HorasPendientesActivity.this);
        }

    }
}
