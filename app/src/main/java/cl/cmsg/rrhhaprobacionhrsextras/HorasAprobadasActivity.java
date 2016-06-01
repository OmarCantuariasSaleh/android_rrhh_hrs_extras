package cl.cmsg.rrhhaprobacionhrsextras;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.horasextras.HorasExtras;
import cl.cmsg.rrhhaprobacionhrsextras.horasextras.HorasExtrasAdapter;

public class HorasAprobadasActivity extends AppCompatActivity {

    ListView listViewPendientes;
    HorasExtrasAdapter horasExtrasAdapter;
    HorasExtras horasExtras;
    ArrayList<HorasExtras> arrayListHorasExtra = new ArrayList<>();
    MiDbHelper miDbHelper;
    TextView lblRut;
    TextView lblNombre;
    TextView lblFecha;
    int lvl=1;

    String SetFecha;
    Button btnPeriodoSelect;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horas_aprovadas);
        btnPeriodoSelect = (Button) findViewById(R.id.btnPeriodoSelect);
        listViewPendientes = (ListView) findViewById(R.id.lstHorasPendientes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        miDbHelper = MiDbHelper.getInstance(this);

        dateFormatter = new SimpleDateFormat("yyyy-MM-", Locale.US);

        btnPeriodoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog createDialog = createDialog();

                createDialog().show();
            }
        });




        listViewPendientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // switch (arrayListHorasExtra.indexOf(position)){
                //   case 1:
                Intent intent = new Intent(getApplicationContext(),DetalleActivity.class);
                HorasExtras horasExtras=arrayListHorasExtra.get(position);

                intent.putExtra("Rut",horasExtras.getRut());
                intent.putExtra("fecha",horasExtras.getFecha());
                startActivity(intent);
                //     break;
                //}
            }
        });

    }

    private DatePickerDialog createDialog() {
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Guardar fecha
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        SetFecha=(dateFormatter.format(newDate.getTime()));

                        //Toast.makeText(getApplicationContext(),SetFecha, Toast.LENGTH_SHORT).show();
                        //Cargar lista
                        arrayListHorasExtra.clear();
                        lblRut = (TextView) findViewById(R.id.lblRut);
                        lblNombre = (TextView) findViewById(R.id.lblNombre);
                        lblFecha = (TextView) findViewById(R.id.lblFecha);
                        Cursor cursor =   miDbHelper.getDatoSolicitudPorFecha(SetFecha);
                        String rut;
                        String nombre;
                        String fecha;
                        //Toast.makeText(getApplicationContext(),String.valueOf(cursor.getColumnCount()), Toast.LENGTH_SHORT).show();

                        while(cursor.moveToNext()){

                            lvl=0;
                            String E1=cursor.getString(cursor.getColumnIndex("estado1"));
                            String E2=cursor.getString(cursor.getColumnIndex("estado2"));
                            String E3=cursor.getString(cursor.getColumnIndex("estado3"));

                            if(E1.equals("A") && E2==null && E3==null){
                                lvl=1;
                            }else if(E2!=null && E2.equals("A") && E3==null){
                                lvl=2;
                            }else if(E3!=null && E3.equals("A")){
                                lvl=3;
                            }
                            //Toast.makeText(getApplicationContext()
                            //        ,"Lvl "+String.valueOf(lvl)+" Estados "+E1+", "+E2+", "+E3, Toast.LENGTH_SHORT).show();
                            if(lvl!=0){
                                rut= cursor.getString(cursor.getColumnIndex("Rut"));
                                //lblRut.setText(lblRut.getText().toString() + " " +Rut);

                                nombre=cursor.getString(cursor.getColumnIndex("nombre"));
                                //lblNombre.setText(lblNombre.getText().toString() + " " +nombre);

                                fecha=cursor.getString(cursor.getColumnIndex("fecha"));
                                // lblFecha.setText(lblFecha.getText().toString() + " " +fecha);

                                horasExtras = new HorasExtras(rut,nombre,fecha);
                                arrayListHorasExtra.add(horasExtras);
                            }

                        }
                        /*if(1==1){
                            return;
                        }*/
                        horasExtrasAdapter = new HorasExtrasAdapter(arrayListHorasExtra,getApplicationContext());

                            listViewPendientes.setAdapter(horasExtrasAdapter);



                    }
                }
                , 2014, 1, 24);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {

                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();

                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());

                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }

                    }

                }

            }

        }
        catch (Exception ex) {
            Log.e("Exception",String.valueOf(ex));
        }
        return dpd;
    }
}
