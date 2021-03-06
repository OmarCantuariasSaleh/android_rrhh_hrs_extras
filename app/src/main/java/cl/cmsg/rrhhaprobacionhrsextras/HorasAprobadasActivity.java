package cl.cmsg.rrhhaprobacionhrsextras;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cl.cmsg.rrhhaprobacionhrsextras.clases.Formatos;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.ValidacionConexion;
import cl.cmsg.rrhhaprobacionhrsextras.horasextras.HorasExtras;
import cl.cmsg.rrhhaprobacionhrsextras.horasextras.HorasExtrasAdapter;

public class HorasAprobadasActivity extends AppCompatActivity{

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
	DatePicker dpPeriodo;
	int lvl = 1;
	Button btnPeriodoSelect;
	String mac;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_horas_aprovadas);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mac = ValidacionConexion.getDireccionMAC(HorasAprobadasActivity.this);
		miDbHelper = MiDbHelper.getInstance(this);

		btnPeriodoSelect = (Button) findViewById(R.id.btnPeriodoSelect);
		listViewPendientes = (ListView) findViewById(R.id.lstHorasPendientes);
		lblRut = (TextView) findViewById(R.id.lblRut);
		lblNombre = (TextView) findViewById(R.id.lblNombre);
		lblFecha = (TextView) findViewById(R.id.lblFecha);
		lblTipoPacto = (TextView) findViewById(R.id.lblTipoPacto);
		lblCostoTotal = (TextView) findViewById(R.id.lblCostoTotal);
		lblPeriodo = (TextView) findViewById(R.id.lblPeriodo);
		lblCantAprov = (TextView) findViewById(R.id.lblCantAprov);
		dpPeriodo = (DatePicker) findViewById(R.id.dp_mes);

		dpPeriodo.setMaxDate(new Date().getTime());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -2);
		dpPeriodo.setMinDate(cal.getTimeInMillis());
		initMonthPicker();

		btnPeriodoSelect.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				int year = dpPeriodo.getYear();
				int monthOfYear = dpPeriodo.getMonth();
				int dayOfMonth = dpPeriodo.getDayOfMonth();
				int costo = 0;
				// Guardar fecha
				Calendar newDate = Calendar.getInstance();
				newDate.set(year, monthOfYear, dayOfMonth);

				//Cargar lista

				arrayListHorasExtra.clear();

				String fecha1;
				String fecha2;

				if (monthOfYear == 0){
					fecha1 = String.valueOf(year - 1) + "-12-22";
					fecha2 = String.valueOf(year) + "-" + String.format("%02d", monthOfYear + 1) + "-22";
				} else{
					fecha1 = String.valueOf(year) + "-" + String.format("%02d", monthOfYear) + "-22";
					fecha2 = String.valueOf(year) + "-" + String.format("%02d", monthOfYear + 1) + "-22";
				}

				Cursor cursor = miDbHelper.getDatoSolicitudPorFecha(fecha1, fecha2);
				String rut, nombre, fecha;
				String tipo_pacto;
				double cant_horas;

				double cantidad = 0;
				String periodo = "";

				switch (monthOfYear) {
					case 0:
						periodo = "Enero " + String.valueOf(year);
						break;
					case 1:
						periodo = "Febrero " + String.valueOf(year);
						break;
					case 2:
						periodo = "Marzo " + String.valueOf(year);
						break;
					case 3:
						periodo = "Abril " + String.valueOf(year);
						break;
					case 4:
						periodo = "Mayo " + String.valueOf(year);
						break;
					case 5:
						periodo = "Junio " + String.valueOf(year);
						break;
					case 6:
						periodo = "Julio " + String.valueOf(year);
						break;
					case 7:
						periodo = "Agosto " + String.valueOf(year);
						break;
					case 8:
						periodo = "Septiembre " + String.valueOf(year);
						break;
					case 9:
						periodo = "Octubre " + String.valueOf(year);
						break;
					case 10:
						periodo = "Noviembre " + String.valueOf(year);
						break;
					case 11:
						periodo = "Diciembre " + String.valueOf(year);
						break;
				}

				lblPeriodo.setText(periodo);
				lblPeriodo.setVisibility(View.VISIBLE);

				while (cursor.moveToNext()){

					lvl = 0;
					String E1 = cursor.getString(cursor.getColumnIndex("estado1"));
					String E2 = cursor.getString(cursor.getColumnIndex("estado2"));
					String E3 = cursor.getString(cursor.getColumnIndex("estado3"));
					String rut1 = cursor.getString(cursor.getColumnIndex("rut_admin1"));
					String rut2 = cursor.getString(cursor.getColumnIndex("rut_admin2"));
					String rut3 = cursor.getString(cursor.getColumnIndex("rut_admin3"));

					if (E1.equals("A") && rut1.equals(miDbHelper.getRutUsuario())){
						lvl = 1;
					} else if (E2.equals("A") && rut2.equals(miDbHelper.getRutUsuario())){
						lvl = 2;
					} else if (E3.equals("A") && rut3.equals(miDbHelper.getRutUsuario())){
						lvl = 3;
					}
					if (lvl != 0){
						cantidad++;
						rut = cursor.getString(cursor.getColumnIndex("Rut"));

						nombre = cursor.getString(cursor.getColumnIndex("nombre"));

						fecha = cursor.getString(cursor.getColumnIndex("fecha"));

						tipo_pacto = cursor.getString(cursor.getColumnIndex("tipoPacto"));

						cant_horas = cursor.getDouble(cursor.getColumnIndex("cantidadHoras"));

						horasExtras = new HorasExtras(rut, nombre, fecha, tipo_pacto, cant_horas, lvl);
						arrayListHorasExtra.add(horasExtras);

						costo += cursor.getInt(cursor.getColumnIndex("monto_pagar"));
					}

				}
				cursor.close();

				if (costo > 0){
					lblCostoTotal.setText("Costo total del periodo : $" + Formatos.getNumberFormat().format(costo));
					lblCostoTotal.setVisibility(View.VISIBLE);
					lblCantAprov.setText("Total de Horas aprobadas : " + String.valueOf(cantidad));
					lblCantAprov.setVisibility(View.VISIBLE);
				} else{
					lblCostoTotal.setVisibility(View.GONE);
					lblCantAprov.setVisibility(View.GONE);
				}

				horasExtrasAdapter = new HorasExtrasAdapter(arrayListHorasExtra, getApplicationContext());

				listViewPendientes.setAdapter(horasExtrasAdapter);
				listViewPendientes.setTextFilterEnabled(true);
			}
		});

		listViewPendientes.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				Intent intent = new Intent(getApplicationContext(), DetalleActivity.class);
				HorasExtras horasExtras = arrayListHorasExtra.get(position);
				intent.putExtra("Rut", horasExtras.getRut());
				intent.putExtra("fecha", horasExtras.getFecha());
				intent.putExtra("tipoPacto", horasExtras.getTipo_pacto());

				startActivity(intent);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_buscar_solo, menu);
		MenuItem menuItem = menu.findItem(R.id.action_search);

		SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setSubmitButtonEnabled(false);
		mSearchView.setQueryHint("Texto a buscar");

		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
			@Override
			public boolean onQueryTextSubmit(String query){
				// this is your adapter that will be filtered
				if (horasExtrasAdapter != null){
					horasExtrasAdapter.getFilter().filter(query);
				}

				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText){
				// this is your adapter that will be filtered
				if (horasExtrasAdapter != null){
					horasExtrasAdapter.getFilter().filter(newText);
				}
				return true;
			}
		});
		//listViewPendientes.setTextFilterEnabled(true);

		return true;

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up buttonOk, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_search){
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void initMonthPicker(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
			if (daySpinnerId != 0){
				View daySpinner = dpPeriodo.findViewById(daySpinnerId);
				if (daySpinner != null){
					daySpinner.setVisibility(View.GONE);
				}
			}

			int monthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");
			if (monthSpinnerId != 0){
				View monthSpinner = dpPeriodo.findViewById(monthSpinnerId);
				if (monthSpinner != null){
					monthSpinner.setVisibility(View.VISIBLE);
				}
			}

			int yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
			if (yearSpinnerId != 0){
				View yearSpinner = dpPeriodo.findViewById(yearSpinnerId);
				if (yearSpinner != null){
					yearSpinner.setVisibility(View.VISIBLE);
				}
			}
		} else{ //Older SDK versions
			Field f[] = dpPeriodo.getClass().getDeclaredFields();
			for (Field field : f){
				if (field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner")){
					field.setAccessible(true);
					Object dayPicker = null;
					try {
						dayPicker = field.get(dpPeriodo);
					} catch (IllegalAccessException e){
						e.printStackTrace();
					}
					((View) dayPicker).setVisibility(View.GONE);
				}

				if (field.getName().equals("mMonthPicker") || field.getName().equals("mMonthSpinner")){
					field.setAccessible(true);
					Object monthPicker = null;
					try {
						monthPicker = field.get(dpPeriodo);
					} catch (IllegalAccessException e){
						e.printStackTrace();
					}
					((View) monthPicker).setVisibility(View.VISIBLE);
				}

				if (field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner")){
					field.setAccessible(true);
					Object yearPicker = null;
					try {
						yearPicker = field.get(dpPeriodo);
					} catch (IllegalAccessException e){
						e.printStackTrace();
					}
					((View) yearPicker).setVisibility(View.VISIBLE);
				}
			}
		}
	}
}
