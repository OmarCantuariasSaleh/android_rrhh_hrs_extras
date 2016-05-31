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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        lblRut = (TextView) findViewById(R.id.lblRut);
        lblNombre = (TextView) findViewById(R.id.lblNombre);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        Bundle bundle= getIntent().getExtras();
        listViewPendientes = (ListView) findViewById(R.id.lstHorasPendientes);
        miDbHelper = MiDbHelper.getInstance(this);



        // Solicitud lvl 3 Pendiente --------------------------------------------------
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-01-02",8,1222000,
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
                ,"A","12312312-1","A","123412341-1","P","16841244-4");

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
                ,"A","12312312-1","A","16841244-4",null,null);

        // Solicitud lvl 1 pendiente --------------------------------------------------
        miDbHelper.insertarSolicitud("11222111-1","Persona Mcperson","2014-01-10",6,100000,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1",null,null,null,null);

        String rut_user=miDbHelper.getRutUsuario();
        Cursor cursor =   miDbHelper.getDatoSolicitudLVL(rut_user);
        Toast.makeText(getApplicationContext(), rut_user, Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(getApplicationContext(), String.valueOf(lvl), Toast.LENGTH_SHORT).show();
            if(lvl!=0){
                rut= "Rut : "+cursor.getString(cursor.getColumnIndex("Rut"));
                //lblRut.setText(lblRut.getText().toString() + " " +Rut);

                nombre="Nombre : "+ cursor.getString(cursor.getColumnIndex("nombre"));
                //lblNombre.setText(lblNombre.getText().toString() + " " +nombre);

                fecha="Fecha : "+ cursor.getString(cursor.getColumnIndex("fecha"));
                // lblFecha.setText(lblFecha.getText().toString() + " " +fecha);

                horasExtras = new HorasExtras(rut,nombre,fecha);
                arrayListHorasExtra.add(horasExtras);
            }



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

                        Intent intent = new Intent(getApplicationContext(),DetalleActivity.class);
                        HorasExtras horasExtras=arrayListHorasExtra.get(position);

                        intent.putExtra("Rut",horasExtras.getRut());
                        intent.putExtra("fecha",horasExtras.getFecha());

                //Toast.makeText(getApplicationContext(), horasExtras.getRut() +" "+ horasExtras.getFecha(), Toast.LENGTH_SHORT).show();
                        startActivity(intent);

            }
        });
    }
}
