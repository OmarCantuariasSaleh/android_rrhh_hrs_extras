package cl.cmsg.rrhhaprobacionhrsextras;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horas_pendientes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lblRut = (TextView) findViewById(R.id.lblRut);
        lblNombre = (TextView) findViewById(R.id.lblNombre);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        listViewPendientes = (ListView) findViewById(R.id.lstHorasPendientes);
        miDbHelper = MiDbHelper.getInstance(this);

        String rut_user=miDbHelper.getRutUsuario();
        Cursor cursor =   miDbHelper.getDatoSolicitudLVL(rut_user);
        String rut;
        String nombre;
        String fecha;

        while(cursor.moveToNext()){

            int lvl = 0;

            String E1 = cursor.getString(cursor.getColumnIndex("estado1"));
            String E2 = cursor.getString(cursor.getColumnIndex("estado2"));
            String E3 = cursor.getString(cursor.getColumnIndex("estado3"));

            if(E1.equals("P")){
                lvl=1;
            }else if(E2!=null && E2.equals("P")){
                lvl=2;
            }else if(E3!=null && E3.equals("P")){
                lvl=3;
            }
            if(lvl!=0){
                rut= cursor.getString(cursor.getColumnIndex("Rut"));

                nombre=cursor.getString(cursor.getColumnIndex("nombre"));

                fecha=cursor.getString(cursor.getColumnIndex("fecha"));

                horasExtras = new HorasExtras(rut,nombre,fecha);
                arrayListHorasExtra.add(horasExtras);
            }

        }

        horasExtrasAdapter = new HorasExtrasAdapter(arrayListHorasExtra,this);
        listViewPendientes.setAdapter(horasExtrasAdapter);

        listViewPendientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(getApplicationContext(),DetalleActivity.class);
                        HorasExtras horasExtras=arrayListHorasExtra.get(position);
                        intent.putExtra("Rut",horasExtras.getRut());
                        intent.putExtra("fecha",horasExtras.getFecha());

                        startActivity(intent);
            }
        });
    }
}
