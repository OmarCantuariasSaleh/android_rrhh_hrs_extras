package cl.cmsg.rrhhaprobacionhrsextras;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		//setSupportActionBar(toolbar);

		lista = (ListView) findViewById(R.id.Lista);
		final String[] opciones = new String[] {
				"Horas extras pendientes",
				"Horas extras aprobadas",
				"Version: 0.2"

		};

		lista.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				switch (opciones[position]){
					case "Horas extras pendientes":Toast.makeText(getApplicationContext(),"0.1", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(getApplicationContext(),HorasPendientesActivity.class);
						startActivity(intent);


						break;
					case "Horas extras aprobadas":
						Intent intent2 = new Intent(getApplicationContext(),HorasAprobadasActivity.class);
						startActivity(intent2);
						break;
					case "Version: 0.2":
						Toast.makeText(getApplicationContext(),"0.1", Toast.LENGTH_SHORT).show();
						break;
				}

				// if (opciones[position].equals("Crear Reporte")){
//     startActivity(new Intent(getApplicationContext(), CrearReporteActivity.class));
				//} else if (opciones[position].contains("Reportes Pendientes")){

				//}
			}
		});

		lista.setAdapter(
				new ArrayAdapter<>(
						this,
						android.R.layout.simple_list_item_1,
						android.R.id.text1,
						opciones
				)
		);


	}
	ListView lista;
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings){
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
