package cl.cmsg.rrhhaprobacionhrsextras;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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

        lblRut = (TextView) findViewById(R.id.lblRut);
        lblNombre = (TextView) findViewById(R.id.lblNombre);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        Bundle bundle= getIntent().getExtras();
        listViewPendientes = (ListView) findViewById(R.id.lstHorasPendientes);
        miDbHelper = MiDbHelper.getInstance(this);
        /*miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","0000-00-00",6,100000,
                "Porque si!","Informatica","Informatica",0);
        miDbHelper.insertarSolicitud("22222222-1","Persona Mcperson Jr","0002-00-00",6,100000,
                "Porque si!","Informatica","Informatica",0);
        miDbHelper.insertarSolicitud("11114441-1","Persona Mcperson Senior","0001-00-00",6,100000,
                "Porque si!","Informatica","Informatica",0);
        miDbHelper.insertarSolicitud("11111121-3","Persona Mcpersona","0000-03-00",6,100000,
                "Porque si!","Informatica","Informatica",0);*/

        Cursor cursor =   miDbHelper.getDatoSolicitudAll();
        String rut;
        String nombre;
        String fecha;

        while(cursor.moveToNext()){
            rut= cursor.getString(cursor.getColumnIndex("rut"));
            //lblRut.setText(lblRut.getText().toString() + " " +rut);

            nombre= cursor.getString(cursor.getColumnIndex("nombre"));
            //lblNombre.setText(lblNombre.getText().toString() + " " +nombre);

            fecha= cursor.getString(cursor.getColumnIndex("fecha"));
           // lblFecha.setText(lblFecha.getText().toString() + " " +fecha);

            horasExtras = new HorasExtras(rut,nombre,fecha);
            arrayListHorasExtra.add(horasExtras);


        }
        /*horasExtras = new HorasExtras("11111111-1","Persona Mcperson","0000-00-00");
        arrayListHorasExtra.add(horasExtras);
        horasExtras = new HorasExtras("22222222-1","Persona Mcperson Jr","0002-00-00");
        arrayListHorasExtra.add(horasExtras);*/
        //arrayListHorasExtra.add(horasExtras);
        //arrayListHorasExtra.add(horasExtras);
        //arrayListHorasExtra.add(horasExtras);
        //arrayListHorasExtra.add(horasExtras);
       // arrayListHorasExtra.add(horasExtras);

        horasExtrasAdapter = new HorasExtrasAdapter(arrayListHorasExtra,this);
        listViewPendientes.setAdapter(horasExtrasAdapter);

        listViewPendientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // switch (arrayListHorasExtra.indexOf(position)){
                 //   case 1:
                        Intent intent = new Intent(getApplicationContext(),DetalleActivity.class);
                        HorasExtras horasExtras=arrayListHorasExtra.get(position);

                        intent.putExtra("rut",horasExtras.getRut());
                        intent.putExtra("fecha",horasExtras.getFecha());
                        startActivity(intent);
                   //     break;
                //}
            }
        });
    }
}
