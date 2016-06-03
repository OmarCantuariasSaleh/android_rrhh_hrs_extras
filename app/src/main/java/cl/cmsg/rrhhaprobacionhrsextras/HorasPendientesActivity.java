package cl.cmsg.rrhhaprobacionhrsextras;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
            String rut1 = cursor.getString(cursor.getColumnIndex("rut_admin1"));
            String rut2 = cursor.getString(cursor.getColumnIndex("rut_admin2"));
            String rut3 = cursor.getString(cursor.getColumnIndex("rut_admin3"));


           if(E1.equals("P") && rut1.equals(miDbHelper.getRutUsuario())){
                lvl=1;
            }else if(E2.equals("P") && rut2.equals(miDbHelper.getRutUsuario()) && E3.equals("A") && E1.equals("A")){
                lvl=2;
            }else if(E3.equals("P") && rut3.equals(miDbHelper.getRutUsuario()) && E1.equals("A") && E2.equals("A")){
                lvl=3;
            }
            Log.e("Omar","Lvl:"+ lvl+ " Estado 1: "+E1+" Estado 2: "+E2+" Estado 3: "+E3);
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
