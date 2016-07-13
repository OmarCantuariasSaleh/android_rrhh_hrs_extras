package cl.cmsg.rrhhaprobacionhrsextras;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
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

import android.app.SearchManager;
//import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cl.cmsg.rrhhaprobacionhrsextras.clases.Alertas;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.ValidacionConexion;
import cl.cmsg.rrhhaprobacionhrsextras.clases.VolleyS;
import cl.cmsg.rrhhaprobacionhrsextras.horasextras.HorasExtras;
import cl.cmsg.rrhhaprobacionhrsextras.horasextras.HorasExtrasAdapter;

public class HorasPendientesActivity extends AppCompatActivity  {

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
    final String mensajeERROR = "Comuniquese con informatica, el servidor responde con formato incorrecto";
    final String tituloERROR = "ERROR";
    double cant_horas;
    int errorNuloVacio = 0;
    int errorRESPONSE = 0;
    int errorFormatoError = 0;
    int errorFormatoMensaje = 0;
    int errorPositivo = 0;
    int errorConnVolley = 0;
    int errorconn = 0;
    int exito1 = 0;
    long total = 0;
    int mTab;
    int filas = 0;
    MenuItem btnActualizarPendientes;

    SearchView mSearchView;

    private static final int PEND = 179;
    Button btnAprobarTodo;
    Button btnRechazarTodo;
    TextView lblcontador;
    int contador = 0;
    ProgressDialog progressDialog;
    LinearLayout layBotones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horas_pendientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        btnActualizarPendientes = (MenuItem) findViewById(R.id.btnActualizar);

        final TabLayout tabs = (TabLayout) findViewById(R.id.tabs);


        btnAprobarTodo = (Button) findViewById(R.id.btnAprobarTodo);
        btnRechazarTodo = (Button) findViewById(R.id.btnRechazarTodo);
        lblcontador = (TextView) findViewById(R.id.lblContador);
        mTab = 0;
        tabs.addTab(tabs.newTab().setText("Trato "));
        tabs.addTab(tabs.newTab().setText("Hora Extra "));
        tabs.addTab(tabs.newTab().setText("Festivo "));
        llenarLista();


        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTab = tabs.getSelectedTabPosition();
                llenarLista();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
                if (miDbHelper.isAdmin()) {
                    Intent intent = new Intent(getApplicationContext(), DetalleActivity.class);
                    HorasExtras horasExtras = arrayListHorasExtra.get(i);
                    intent.putExtra("Rut", horasExtras.getRut());
                    intent.putExtra("fecha", horasExtras.getFecha());
                    intent.putExtra("tipo_pacto", horasExtras.getTipo_pacto());

                    startActivityForResult(intent, PEND);
                    return;
                }

                SparseBooleanArray checkedItemPositions = listViewPendientes.getCheckedItemPositions();

                if (checkedItemPositions != null && checkedItemPositions.size() > 0) {

                    contador = 0;
                    for (int x = 0; x < checkedItemPositions.size(); x++) {
                        if (checkedItemPositions.valueAt(x)) {
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
                if (x > 0) {
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

                                    if (checkedItemPositions != null && checkedItemPositions.size() > 0) {

                                        contador = 0;
                                        String run_A;
                                        String fecha_A;
                                        String E_A = "A";
                                        String tipoPacto_A;
                                        String lvl_A;
                                        errorNuloVacio = 0;
                                        errorRESPONSE = 0;
                                        errorFormatoError = 0;
                                        errorFormatoMensaje = 0;
                                        errorPositivo = 0;
                                        errorConnVolley = 0;
                                        errorconn = 0;
                                        exito1 = 0;
                                        total = checkedItemPositions.size();

                                        for (int y = 0; y < total; y++) {
                                            progressDialog.incrementProgressBy((int) (y * 100 / total));
                                            if (checkedItemPositions.valueAt(y)) {
                                                HorasExtras horasExtras = arrayListHorasExtra.get(y);
                                                run_A = horasExtras.getRut();
                                                fecha_A = horasExtras.getFecha();
                                                tipoPacto_A = horasExtras.getTipo_pacto();
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


                } else {
                    Toast.makeText(getApplicationContext(), "No hay solicitudes seleccionadas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRechazarTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int x = Integer.parseInt(lblcontador.getText().toString());
                if (x > 0) {

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

                                    if (checkedItemPositions != null && checkedItemPositions.size() > 0) {

                                        contador = 0;
                                        String run_A;
                                        String fecha_A;
                                        String E_A = "R";
                                        String tipoPacto_A;
                                        String lvl_A;
                                        errorNuloVacio = 0;
                                        errorRESPONSE = 0;
                                        errorFormatoError = 0;
                                        errorFormatoMensaje = 0;
                                        errorPositivo = 0;
                                        errorConnVolley = 0;
                                        errorconn = 0;
                                        exito1 = 0;
                                        total = checkedItemPositions.size();
                                        for (int x = 0; x < total; x++) {

                                            progressDialog.incrementProgressBy((int) (x * 100 / total));
                                            if (checkedItemPositions.valueAt(x)) {
                                                HorasExtras horasExtras = arrayListHorasExtra.get(x);
                                                run_A = horasExtras.getRut();
                                                fecha_A = horasExtras.getFecha();
                                                tipoPacto_A = horasExtras.getTipo_pacto();
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


                } else {
                    Toast.makeText(getApplicationContext(), "No hay solicitudes seleccionadas", Toast.LENGTH_SHORT).show();
                }
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
        if (miDbHelper.isAdmin()) {
            llenarLista();
        }
    }

    void llenarLista() {
        miDbHelper = MiDbHelper.getInstance(this);
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
     //   mSearchView = (SearchView) findViewById(R.id.action_search);
        tabs.getTabAt(0).setText("Trato \n " +
                String.valueOf(miDbHelper.CuentaSolicitudesTratos()));
        tabs.getTabAt(1).setText("Hora Extra \n" +
                String.valueOf(miDbHelper.CuentaSolicitudesHoraExtra()));
        tabs.getTabAt(2).setText("Festivo \n" +
                String.valueOf(miDbHelper.CuentaSolicitudesFestivo()));
        lblRut = (TextView) findViewById(R.id.lblRut);
        lblNombre = (TextView) findViewById(R.id.lblNombre);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        lblTipoPacto = (TextView) findViewById(R.id.lblTipoPacto);
        listViewPendientes = (ListView) findViewById(R.id.lstHorasPendientes);

        arrayListHorasExtra.clear();
        String rut_user = miDbHelper.getRutUsuario();
        Cursor cursor;
        layBotones = (LinearLayout) findViewById(R.id.layBotones);

        if (miDbHelper.isAdmin()) {
            cursor = miDbHelper.getDatoSolicitudADMIN();
            layBotones.setVisibility(View.GONE);
        } else {
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
                if (miDbHelper.isAdmin()) {
                    if (E1.equals("P")) {
                        lvl = 1;
                    } else if (E1.equals("A") && E2.equals("P")) {
                        lvl = 2;
                    } else if (E1.equals("A") && E2.equals("A") && E3.equals("P")) {
                        lvl = 3;
                    }
                } else {

                    if (E1.equals("P") && rut1.equals(miDbHelper.getRutUsuario())) {
                        lvl = 1;
                    } else if (E1.equals("A") && E2.equals("P") && rut2.equals(miDbHelper.getRutUsuario())) {
                        lvl = 2;
                    } else if (E1.equals("A") && E2.equals("A") && E3.equals("P") && rut3.equals(miDbHelper.getRutUsuario())) {
                        lvl = 3;
                    }
                }
            }

            if (mTab == 0 && !tipoPacto.equals("T")) {
                lvl = 0;
            }
            if (mTab == 1 && !tipoPacto.equals("H")) {
                lvl = 0;
            }
            if (mTab == 2 && !tipoPacto.equals("F")) {
                lvl = 0;
            }


            if (lvl != 0) {
                rut = cursor.getString(cursor.getColumnIndex("Rut"));

                nombre = cursor.getString(cursor.getColumnIndex("nombre"));

                fecha = cursor.getString(cursor.getColumnIndex("fecha"));

                tipo_pacto = cursor.getString(cursor.getColumnIndex("tipo_pacto"));

                cant_horas = cursor.getDouble(cursor.getColumnIndex("cant_horas"));
                horasExtras = new HorasExtras(rut, nombre, fecha, tipo_pacto, cant_horas, lvl);

                arrayListHorasExtra.add(horasExtras);
            }

        }
        cursor.close();
        horasExtrasAdapter = new HorasExtrasAdapter(arrayListHorasExtra, this);
        listViewPendientes.setAdapter(horasExtrasAdapter);
        listViewPendientes.setTextFilterEnabled(true);
        //setupSearchView();

    }


    public void actualizaEstado(final String rut_S, final String fecha_S, final String estado_Final, final String lvl_S, final String tipo_pacto_S) {
        final VolleyS volleyS = VolleyS.getInstance(this);

        if (!ValidacionConexion.isExisteConexion(HorasPendientesActivity.this)) {
            progressDialog.dismiss();
            errorconn++;
            if (errorconn == 1) {

                llenarLista();
                Alertas.alertaConexion(HorasPendientesActivity.this);

                return;
            }
        }
        final String mac = ValidacionConexion.getDireccionMAC(HorasPendientesActivity.this);

        final StringRequest jsonObjectRequest = new StringRequest(

                Request.Method.GET // FORMA QUE LLAMAREMOS, O SEA GET
                , getString(R.string.URL_ActualizarEstado)
                + "?run=" + rut_S
                + "&fecha=" + fecha_S
                + "&estado=" + estado_Final
                + "&lvl=" + lvl_S
                + "&apk_key=" + getString(R.string.APK_KEY)
                + "&mac=" + mac
                + "&run_admin=" + miDbHelper.getRutUsuario()
                + "&tipo_pacto=" + tipo_pacto_S


                , new Response.Listener<String>() { // OBJETO QUE USAREMOS PARA LA ESCUCHA DE LA RESPUESTA
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                Boolean error;
                String mensajesrv;

                if (response == null || response.isEmpty()) {
                    progressDialog.dismiss();
                    if (++errorNuloVacio == 1) {

                        Alertas.alertaSimple(tituloERROR, mensajeERROR, HorasPendientesActivity.this);
                        miDbHelper.insertarLogError("Variable response es Nulo o Vacio en HorasPendientesActivity, ActualizarEstado", mac);
                        llenarLista();

                    }
                    return;
                }
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    if (++errorRESPONSE == 1) {
                        Alertas.alertaSimple(tituloERROR, mensajeERROR, HorasPendientesActivity.this);
                        llenarLista();
                        miDbHelper.insertarLogError("Error de formato en 'response', no parece ser tipo JSON en HorasPendientesActivity, ActualizarEstado. Mensaje de error : " + e.getMessage(), mac);
                    }
                    return;
                }
                try {
                    error = jsonObject.getBoolean("error");

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    if (++errorFormatoError == 1) {
                        Alertas.alertaSimple(tituloERROR, mensajeERROR, HorasPendientesActivity.this);
                        miDbHelper.insertarLogError("Error de formato en variable 'error', No existe o es un formato incorrecto en HorasPendientesActivity, ActualizarEstado. Mensaje de error : " + e.getMessage(), mac);
                        llenarLista();
                    }
                    return;
                }
                if (error) {

                    try {
                        mensajesrv = jsonObject.getString("mensaje");
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        if (++errorFormatoMensaje == 1) {
                            Alertas.alertaSimple(tituloERROR, mensajeERROR, HorasPendientesActivity.this);
                            miDbHelper.insertarLogError("Error de formato en variable 'mensaje', No existe o es un formato incorrecto en HorasPendientesActivity, ActualizarEstado. Mensaje de error : " + e.getMessage(), mac);
                            llenarLista();
                        }
                        return;
                    }
                    progressDialog.dismiss();

                    if (++errorPositivo == 1) {
                        Alertas.alertaSimple("Servidor responde con el siguiente error:", mensajesrv, HorasPendientesActivity.this);
                        llenarLista();
                    }
                    return;
                }
                llenarLista();
                progressDialog.dismiss();
                miDbHelper.actualizarEstado(rut_S, fecha_S, estado_Final, lvl_S, tipo_pacto_S);
                if (++exito1 == total) {
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
                , new Response.ErrorListener() { // QUE HACER EN CASO DE ERROR
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                volleyS.cancelAll();

                errorConnVolley++;
                if (errorConnVolley == 1) {
                    Alertas.alertaSimple(tituloERROR, "Servidor no responde, asegurese de estar conectado a internet o intentelo mas tarde", HorasPendientesActivity.this);
                    miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley en HorasPendientesActivity ActualizarEstado. Mensaje : " + error, mac);
                    llenarLista();
                }
            }
        }
        );
        volleyS.addToQueue(jsonObjectRequest, HorasPendientesActivity.this);
    }

    public void actualizarPendientes() {
        if (!ValidacionConexion.isExisteConexion(HorasPendientesActivity.this)) {
            Alertas.alertaConexion(HorasPendientesActivity.this);
            return;
        }
        final String mac = ValidacionConexion.getDireccionMAC(HorasPendientesActivity.this);
        progressDialog = new ProgressDialog(HorasPendientesActivity.this);
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
                    Alertas.alertaSimple(tituloERROR, mensajeERROR, HorasPendientesActivity.this);
                    miDbHelper.insertarLogError("Variable response es Nulo o Vacio en MainActivity, btnActualizarPendientes", mac);
                    llenarLista();
                    return;
                }
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Alertas.alertaSimple(tituloERROR, mensajeERROR, HorasPendientesActivity.this);
                    miDbHelper.insertarLogError("Error de formato en 'response', no parece ser tipo JSON. Mensaje de error : " + e.getMessage() + " en MainActivity, btnActualizarPendientes", mac);
                    llenarLista();
                    return;
                }
                try {
                    error = jsonObject.getBoolean("error");
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Alertas.alertaSimple(tituloERROR, mensajeERROR, HorasPendientesActivity.this);
                    miDbHelper.insertarLogError("Error de formato en variable 'error', No existe o es un formato incorrecto. Mensaje de error : " + e.getMessage() + " en MainActivity, btnActualizarPendientes", mac);
                    llenarLista();
                    return;
                }
                if (error) {
                    progressDialog.dismiss();
                    try {
                        mensajesrv = jsonObject.getString("mensaje");
                    } catch (JSONException e) {
                        Alertas.alertaSimple(tituloERROR, mensajeERROR, HorasPendientesActivity.this);
                        miDbHelper.insertarLogError("Error de formato en variable 'mensaje', No existe o es un formato incorrecto en MainActivity, btnActualizarPendientes." +
                                " Mensaje de error : " + e.getMessage() + "", mac);
                        llenarLista();
                        return;
                    }
                    llenarLista();
                    miDbHelper.insertarLogError("Servidor retorna el siguiente error: " + mensajesrv + ". En MainActivity, btnActualizarPendientes ", mac);
                    Alertas.alertaSimple("Servidor responde con el siguiente error:", mensajesrv, HorasPendientesActivity.this);
                    return;
                }


                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("filas");
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Alertas.alertaSimple(tituloERROR, mensajeERROR, HorasPendientesActivity.this);
                    miDbHelper.insertarLogError("Error de formato en variable 'filas', No existe o es un formato incorrecto en MainActivity, btnActualizarPendientes . Mensaje de error : " + e.getMessage(), mac);
                    llenarLista();
                    return;
                }

                // Borrar solicitudes antiguas
                miDbHelper.deleteSolicitudPendientes();
                filas = 0;
                total = jsonArray.length();
                for (int i = 0; i < total; i++) {
                    JSONObject jsonData = null;
                    try {
                        jsonData = jsonArray.getJSONObject(i);
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        Alertas.alertaSimple(tituloERROR, mensajeERROR, HorasPendientesActivity.this);
                        miDbHelper.insertarLogError("Error de formato en variable 'filas',datos del arreglo no son JSONObject o no tienen formato correcto en MainActivity, btnActualizarPendientes. \nMensaje de error : " + e.getMessage(), mac);
                        llenarLista();
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
                        Alertas.alertaSimple(tituloERROR, mensajeERROR, HorasPendientesActivity.this);
                        llenarLista();
                        miDbHelper.insertarLogError("Filas del arreglo no tienen formato correcto o estan vacias, en MainActivity, btnActualizarPendientes. Mensaje de error : " + e.getMessage(), mac);
                        llenarLista();
                        return;
                    }
                    if (miDbHelper.yaExiste(String.valueOf(run), fecha, tipo_pacto)) {
                        continue;
                    }
                    if (miDbHelper.insertarSolicitud(
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
                            , rut_admin3, mac)) {
                        progressDialog.incrementProgressBy((int) (i * 100 / total));

                    } else {
                        progressDialog.dismiss();
                        Alertas.alertaSimple(tituloERROR, "Error de base de datos Comuniquese con informatica inmediatamente", HorasPendientesActivity.this);
                        miDbHelper.insertarLogError("Una o mas filas del arreglo contienen datos que no coinciden con la tabla en la fila " + String.valueOf(i), mac);
                        llenarLista();
                        return;
                    }

                }
                progressDialog.dismiss();
                llenarLista();
                Alertas.alertaSimple("Exito", "Actualizacion exitosa, Hay " + String.valueOf(miDbHelper.CuentaSolicitudes()) + " solicitudes pendientes", HorasPendientesActivity.this);

            }
        }
                , new Response.ErrorListener() { // QUE HACER EN CASO DE ERROR
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                volleyS.cancelAll();
                Alertas.alertaSimple(tituloERROR, "Servidor no responde, asegurese de estar conectado a internet o intentelo mas tarde", HorasPendientesActivity.this);
                miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley, en MainActivity, btnActualizarPendientes. Mensaje : " + error.getMessage(), mac);
                llenarLista();
            }
        }
        );
        volleyS.addToQueue(jsonObjectRequest, HorasPendientesActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buscar, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        //SearchView mSearchView= new SearchView(getSupportActionBar().getThemedContext());
       // mSearchView = (SearchView) findViewById(R.id.action_search);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // this is your adapter that will be filtered
                horasExtrasAdapter.getFilter().filter(query);
                System.out.println("on query submit: "+query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                // this is your adapter that will be filtered
                horasExtrasAdapter.getFilter().filter(newText);
                System.out.println("on text chnge text: "+newText);
                return true;
            }
        });
        //listViewPendientes.setTextFilterEnabled(true);

        setupSearchView(mSearchView);



        return true;
    }

    private void setupSearchView(SearchView mSearchView) {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(false);
       // mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint("Search Text");
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
        if (id == R.id.action_search) {

            return true;
        }



        return super.onOptionsItemSelected(item);
    }




}

