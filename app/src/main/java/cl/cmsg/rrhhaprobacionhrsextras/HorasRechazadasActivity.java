package cl.cmsg.rrhhaprobacionhrsextras;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.renderscript.Sampler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.ValidacionConexion;
import cl.cmsg.rrhhaprobacionhrsextras.horasextras.HorasExtras;
import cl.cmsg.rrhhaprobacionhrsextras.horasextras.HorasExtrasAdapter;

public class HorasRechazadasActivity extends AppCompatActivity {
    DatePicker dp_mes;
    ListView listViewPendientes;
    HorasExtrasAdapter horasExtrasAdapter;
    HorasExtras horasExtras;
    ArrayList<HorasExtras> arrayListHorasExtra = new ArrayList<>();
    MiDbHelper miDbHelper;
    TextView lblRut;
    TextView lblNombre;
    TextView lblFecha;
    TextView lblTipoPacto;

    TextView lblPeriodo;
    int lvl=1;
    Button btnPeriodoSelect;
    String Rut_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horas_rechazadas);
        btnPeriodoSelect = (Button) findViewById(R.id.btnPeriodoSelect);
        listViewPendientes = (ListView) findViewById(R.id.lstHorasRechazadas);
        // Datepicker, intenta esconder dia y mostrar solo meses
        dp_mes = (DatePicker) findViewById(R.id.dp_mes);
        dp_mes.setMaxDate(new Date().getTime());
        Calendar cal =Calendar.getInstance();
        cal.add(Calendar.YEAR,-2);
        dp_mes.setMinDate(cal.getTimeInMillis());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initMonthPicker();
        miDbHelper = MiDbHelper.getInstance(this);
        // Boton de seleccionar fecha, envia al datepicker

        btnPeriodoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year          = dp_mes.getYear();
                int monthOfYear   = dp_mes.getMonth();
                int dayOfMonth    = dp_mes.getDayOfMonth();


                        int month_i = monthOfYear + 1;
                        Log.e("selected month:", Integer.toString(month_i));
                        //Add whatever you need to handle Date changes
                        // Guardar fecha
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);

                        //Cargar lista

                        arrayListHorasExtra.clear();
                        lblRut = (TextView) findViewById(R.id.lblRut);
                        lblNombre = (TextView) findViewById(R.id.lblNombre);
                        lblFecha = (TextView) findViewById(R.id.lblFecha);
                        lblTipoPacto = (TextView) findViewById(R.id.lblTipoPacto);
                        lblPeriodo = (TextView) findViewById(R.id.lblPeriodo);

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
                            Rut_user=miDbHelper.getRutUsuario();

                            if(E1.equals("R") && rut1.equals(Rut_user)){
                                lvl=1;
                            }else if(E2.equals("R") && rut2.equals(Rut_user) ){
                                lvl=2;
                            }else if(E3.equals("R") && rut3.equals(Rut_user) ){
                                lvl=3;
                            }
                            if(lvl!=0){

                                rut= cursor.getString(cursor.getColumnIndex("Rut"));

                                nombre=cursor.getString(cursor.getColumnIndex("nombre"));

                                fecha=cursor.getString(cursor.getColumnIndex("fecha"));

                                tipo_pacto = cursor.getString(cursor.getColumnIndex("tipo_pacto"));

                                cant_horas=cursor.getDouble(cursor.getColumnIndex("cant_horas"));

                                horasExtras = new HorasExtras(rut,nombre,fecha,tipo_pacto,cant_horas,lvl);
                                arrayListHorasExtra.add(horasExtras);

                            }

                        }
                        cursor.close();
                        horasExtrasAdapter = new HorasExtrasAdapter(arrayListHorasExtra,getApplicationContext());

                        listViewPendientes.setAdapter(horasExtrasAdapter);
                        listViewPendientes.setTextFilterEnabled(true);


            }
        });



        // Al apretar un item llama a la actividad detalle para mostrar todos los datos de la solicitud
        listViewPendientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(),DetalleActivity.class);
                HorasExtras horasExtras=arrayListHorasExtra.get(position);
                intent.putExtra("Rut",horasExtras.getRut());
                intent.putExtra("fecha",horasExtras.getFecha());
                intent.putExtra("tipo_pacto",horasExtras.getTipo_pacto());


                startActivity(intent);
            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buscar_solo, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        //SearchView mSearchView= new SearchView(getSupportActionBar().getThemedContext());
        // mSearchView = (SearchView) findViewById(R.id.action_search);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // this is your adapter that will be filtered
                if(horasExtrasAdapter !=null){
                    horasExtrasAdapter.getFilter().filter(query);
                }

                //System.out.println("on query submit: "+query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                // this is your adapter that will be filtered
                if(horasExtrasAdapter !=null) {
                    horasExtrasAdapter.getFilter().filter(newText);
                }
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


        if (id == R.id.action_search) {

            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    public void initMonthPicker(){
        dp_mes = (DatePicker) findViewById(R.id.dp_mes);

        /*int year    = dp_mes.getYear();
        int month   = dp_mes.getMonth();
        int day     = dp_mes.getDayOfMonth();

        dp_mes.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int month_i = monthOfYear + 1;
                Log.e("selected month:", Integer.toString(month_i));
                //Add whatever you need to handle Date changes
            }
        });*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
            if (daySpinnerId != 0)
            {
                View daySpinner = dp_mes.findViewById(daySpinnerId);
                if (daySpinner != null)
                {
                    daySpinner.setVisibility(View.GONE);
                }
            }

            int monthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");
            if (monthSpinnerId != 0)
            {
                View monthSpinner = dp_mes.findViewById(monthSpinnerId);
                if (monthSpinner != null)
                {
                    monthSpinner.setVisibility(View.VISIBLE);
                }
            }

            int yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
            if (yearSpinnerId != 0)
            {
                View yearSpinner = dp_mes.findViewById(yearSpinnerId);
                if (yearSpinner != null)
                {
                    yearSpinner.setVisibility(View.VISIBLE);
                }
            }
        } else { //Older SDK versions
            Field f[] = dp_mes.getClass().getDeclaredFields();
            for (Field field : f)
            {
                if(field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner"))
                {
                    field.setAccessible(true);
                    Object dayPicker = null;
                    try {
                        dayPicker = field.get(dp_mes);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) dayPicker).setVisibility(View.GONE);
                }

                if(field.getName().equals("mMonthPicker") || field.getName().equals("mMonthSpinner"))
                {
                    field.setAccessible(true);
                    Object monthPicker = null;
                    try {
                        monthPicker = field.get(dp_mes);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) monthPicker).setVisibility(View.VISIBLE);
                }

                if(field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner"))
                {
                    field.setAccessible(true);
                    Object yearPicker = null;
                    try {
                        yearPicker = field.get(dp_mes);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) yearPicker).setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
