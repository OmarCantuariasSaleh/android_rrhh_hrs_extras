package cl.cmsg.rrhhaprobacionhrsextras;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.VolleyS;

public class MainActivity extends AppCompatActivity{

	MiDbHelper miDbHelper;
	ImageButton btnActualizar;
	ListView lista;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		miDbHelper = MiDbHelper.getInstance(this);
		btnActualizar = (ImageButton) findViewById(R.id.btnActualizar);

		ProgressDialog progressDialog= new ProgressDialog(this);
		final VolleyS volleyS = VolleyS.getInstance(this);

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
					case "Horas extras pendientes":
						Intent intent = new Intent(MainActivity.this,HorasPendientesActivity.class);
						startActivity(intent);
						break;
					case "Horas extras aprobadas":
						Intent intent2 = new Intent(getApplicationContext(),HorasAprobadasActivity.class);
						startActivity(intent2);
						break;
					case "Version: 0.2":
						Toast.makeText(getApplicationContext(),"0.2", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});

		btnActualizar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				final StringRequest jsonObjectRequest = new StringRequest(

						Request.Method.GET // FORMA QUE LLAMAREMOS, O SEA GET
						, getString(R.string.URL_PendienteAprobacion) +"?run="+ miDbHelper.getRutUsuario() // URL QUE LLAMAREMOS
						, new Response.Listener<String>(){ // OBJETO QUE USAREMOS PARA LA ESCUCHA DE LA RESPUESTA
					@Override
					public void onResponse(String response){
						try {
							JSONObject jsonObject= new JSONObject(response);
							Boolean error = jsonObject.getBoolean("error");
							if(error){
								String mensaje= jsonObject.getString("mensaje");
								new AlertDialog.Builder(MainActivity.this)
										.setIcon(android.R.drawable.ic_dialog_info)
										.setTitle("Servidor responde con el siguiente error: ")
										.setMessage(mensaje)
										.setNeutralButton("Ok",null)
										.show()
								;
							}else{
								JSONArray jsonArray = jsonObject.getJSONArray("filas");

								// Borrar solicitudes antiguas
								miDbHelper.deleteSolicitudALL();

								for(int i=0;i<jsonArray.length();i++){

									JSONObject jsonData= jsonArray.getJSONObject(i);
									int run = jsonData.getInt("run");
									String fecha = jsonData.getString("fecha");
									String nombre = jsonData.getString("nombre");
									double cantidad = jsonData.getDouble("cantidad");
									int valor = jsonData.getInt("valor");
									String motivo = jsonData.getString("motivo");
									String comentario = jsonData.getString("comentario");
									String centro_costo = jsonData.getString("centro_costo");
									String area = jsonData.getString("area");
									String tipo_pacto = jsonData.getString("tipo_pacto");
									String estado1 = jsonData.getString("estado1");
									String rut_admin1 = jsonData.getString("run1");
									String estado2 = jsonData.getString("estado2");
									String rut_admin2 = jsonData.getString("run2");
									String estado3 = jsonData.getString("estado3");
									String rut_admin3 = jsonData.getString("run3");

									//Insertar solicitud nueva
									if(
									!miDbHelper.insertarSolicitud(
											String.valueOf(run)
											,nombre
											,fecha
											,cantidad
											,valor
											,motivo
											,comentario
											,centro_costo
											,area
											,tipo_pacto
											,estado1
											,rut_admin1
											,estado2
											,rut_admin2
											,estado3
											,rut_admin3)){
									Log.e("Omar","No Exitoso");
									}else{
										Log.e("Omar","Exitoso");

									}
								}
								new AlertDialog.Builder(MainActivity.this)
										.setIcon(android.R.drawable.ic_dialog_info)
										.setTitle("Exito")
										.setMessage("Actualizacion exitosa")
										.setNeutralButton("Ok",null)
										.show()
								;

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						Log.e("Respuesta",response);

					}
				}
						, new Response.ErrorListener(){ // QUE HACER EN CASO DE ERROR
					@Override
					public void onErrorResponse(VolleyError error){
						volleyS.cancelAll();
						new AlertDialog.Builder(MainActivity.this)
								.setIcon(android.R.drawable.ic_dialog_info)
								.setTitle("Error")
								.setMessage("Servidor no responde \n "+error)
								.setNeutralButton("Ok",null)
								.show()
						;

					}
				}
				);

				volleyS.addToQueue(jsonObjectRequest, MainActivity.this);
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
