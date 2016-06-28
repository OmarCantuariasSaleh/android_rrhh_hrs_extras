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
import java.util.ArrayList;
import java.util.Calendar;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.ValidacionConexion;
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
    TextView lblTipoPacto;
    TextView lblCostoTotal;
    TextView lblCantAprov;
    TextView lblPeriodo;
    int lvl=1;
    Button btnPeriodoSelect;
    String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horas_aprovadas);
        btnPeriodoSelect = (Button) findViewById(R.id.btnPeriodoSelect);
        listViewPendientes = (ListView) findViewById(R.id.lstHorasPendientes);
        mac = ValidacionConexion.getDireccionMAC(HorasAprobadasActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        miDbHelper = MiDbHelper.getInstance(this);

        btnPeriodoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog createDialog = createDialog();

                createDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());

                createDialog().show();
            }
        });

        listViewPendientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Omar", "entro1");
                Intent intent = new Intent(getApplicationContext(),DetalleActivity.class);
                HorasExtras horasExtras=arrayListHorasExtra.get(position);
                intent.putExtra("Rut",horasExtras.getRut());
                intent.putExtra("fecha",horasExtras.getFecha());
                String tp=horasExtras.getTipo_pacto();
                switch (tp){
                    case "Hora Extra":
                        intent.putExtra("tipo_pacto","H");
                        break;
                    case "Festivo":
                        intent.putExtra("tipo_pacto","F");
                        break;
                    case "Trato":
                        intent.putExtra("tipo_pacto","T");
                        break;
                }
                startActivity(intent);
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

                        //Cargar lista

                        arrayListHorasExtra.clear();
                        lblRut = (TextView) findViewById(R.id.lblRut);
                        lblNombre = (TextView) findViewById(R.id.lblNombre);
                        lblFecha = (TextView) findViewById(R.id.lblFecha);
                        lblTipoPacto = (TextView) findViewById(R.id.lblTipoPacto);
                        lblCostoTotal = (TextView) findViewById(R.id.lblCostoTotal);
                        lblPeriodo = (TextView) findViewById(R.id.lblPeriodo);
                        lblCantAprov = (TextView) findViewById(R.id.lblCantAprov);

                        String fecha1;
                        String fecha2;

                        if(monthOfYear==0){
                            fecha1 = String.valueOf(year-1)+"-12-22";
                            fecha2 = String.valueOf(year)+"-"+String.format("%02d",monthOfYear+1)+"-22";
                        }else {
                            fecha1 = String.valueOf(year)+"-"+String.format("%02d",monthOfYear)+"-22";
                            fecha2 = String.valueOf(year)+"-"+String.format("%02d",monthOfYear+1)+"-22";
                        }

                        Cursor cursor =   miDbHelper.getDatoSolicitudPorFecha(fecha1,fecha2);
                        String rut;
                        String nombre;
                        String fecha;
                        String tipo_pacto;
                        double cant_horas;
                        int costo=0;
                        double cantidad=0;
                        String periodo="";

                        switch (monthOfYear){
                            case 0 :
                                periodo="Enero "+String.valueOf(year);
                                break;
                            case 1 :
                                periodo="Febrero "+String.valueOf(year);
                                break;
                            case 2 :
                                periodo="Marzo "+String.valueOf(year);
                                break;
                            case 3 :
                                periodo="Abril "+String.valueOf(year);
                                break;
                            case 4 :
                                periodo="Mayo "+String.valueOf(year);
                                break;
                            case 5 :
                                periodo="Junio "+String.valueOf(year);
                                break;
                            case 6 :
                                periodo="Julio "+String.valueOf(year);
                                break;
                            case 7 :
                                periodo="Agosto "+String.valueOf(year);
                                break;
                            case 8 :
                                periodo="Septiembre "+String.valueOf(year);
                                break;
                            case 9 :
                                periodo="Octubre "+String.valueOf(year);
                                break;
                            case 10 :
                                periodo="Noviembre "+String.valueOf(year);
                                break;
                            case 11 :
                                periodo="Diciembre "+String.valueOf(year);
                                break;
                        }

                        lblPeriodo.setText(periodo);
                        lblPeriodo.setVisibility(View.VISIBLE);

                        while(cursor.moveToNext()){

                            lvl=0;
                            String E1=cursor.getString(cursor.getColumnIndex("estado1"));
                            String E2=cursor.getString(cursor.getColumnIndex("estado2"));
                            String E3=cursor.getString(cursor.getColumnIndex("estado3"));
                            String rut1 = cursor.getString(cursor.getColumnIndex("rut_admin1"));
                            String rut2 = cursor.getString(cursor.getColumnIndex("rut_admin2"));
                            String rut3 = cursor.getString(cursor.getColumnIndex("rut_admin3"));

                            if(E1.equals("A") && rut1.equals(miDbHelper.getRutUsuario())){
                                lvl=1;
                            }else if(E2.equals("A") && rut2.equals(miDbHelper.getRutUsuario()) ){
                                lvl=2;
                            }else if(E3.equals("A") && rut3.equals(miDbHelper.getRutUsuario()) ){
                                lvl=3;
                            }
                            if(lvl!=0){
                                cantidad++;
                                rut= cursor.getString(cursor.getColumnIndex("Rut"));

                                nombre=cursor.getString(cursor.getColumnIndex("nombre"));

                                fecha=cursor.getString(cursor.getColumnIndex("fecha"));

                                tipo_pacto="";
                                if(cursor.getString(cursor.getColumnIndex("tipo_pacto")).equals("H")){
                                    tipo_pacto = getString(R.string.HORAEXTRA);
                                }
                                if(cursor.getString(cursor.getColumnIndex("tipo_pacto")).equals("T")){
                                    tipo_pacto = getString(R.string.TRATO);
                                }
                                if(cursor.getString(cursor.getColumnIndex("tipo_pacto")).equals("F")){
                                    tipo_pacto = getString(R.string.FESTIVO);
                                }

                                cant_horas=cursor.getDouble(cursor.getColumnIndex("cant_horas"));

                                horasExtras = new HorasExtras(rut,nombre,fecha,tipo_pacto,cant_horas,lvl);
                                arrayListHorasExtra.add(horasExtras);

                                costo+= cursor.getInt(cursor.getColumnIndex("monto_pagar"));
                            }

                        }
                            cursor.close();
                        if(costo>0){
                            lblCostoTotal.setText("Costo total del periodo : $" +costo);
                            lblCostoTotal.setVisibility(View.VISIBLE);
                            lblCantAprov.setText("Cantidad : "+String.valueOf(cantidad));
                            lblCantAprov.setVisibility(View.VISIBLE);
                        }else{
                            lblCostoTotal.setVisibility(View.INVISIBLE);
                        }
                        horasExtrasAdapter = new HorasExtrasAdapter(arrayListHorasExtra,getApplicationContext());

                            listViewPendientes.setAdapter(horasExtrasAdapter);

                    }
                }
                , Calendar.getInstance().get(Calendar.YEAR)
                , Calendar.getInstance().get(Calendar.MONTH)
                ,Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {

                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();

                    for (java.lang.reflect.Field datePickerField : datePickerFields) {

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
            miDbHelper.insertarLogError("Error : "+ex.getMessage()+". En HorasAprobadasActivity, datepickerdialog",mac);
        }
        return dpd;
    }
}
