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

import cl.cmsg.rrhhaprobacionhrsextras.clases.Alertas;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.ValidacionConexion;
import cl.cmsg.rrhhaprobacionhrsextras.clases.VolleyS;

public class MainActivity extends AppCompatActivity{

	Alertas alertas;
	MiDbHelper miDbHelper;
	ImageButton btnActualizar;
	ListView lista;
	ProgressDialog progressDialog;
	String mensaje;
	String titulo;

	@Override
	protected void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		miDbHelper = MiDbHelper.getInstance(this,MainActivity.this);
		btnActualizar = (ImageButton) findViewById(R.id.btnActualizar);

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

				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setTitle("Actualizando");
				progressDialog.setMessage("Espere un momento");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(false);
				progressDialog.show();

				final StringRequest jsonObjectRequest = new StringRequest(

						Request.Method.GET // FORMA QUE LLAMAREMOS, O SEA GET
						, getString(R.string.URL_PendienteAprobacion) +"?run="+ miDbHelper.getRutUsuario() // URL QUE LLAMAREMOS
						, new Response.Listener<String>(){ // OBJETO QUE USAREMOS PARA LA ESCUCHA DE LA RESPUESTA
					@Override
					public void onResponse(String response){
						progressDialog.dismiss();
						JSONObject jsonObject=null;
						Boolean error=true;
						String fecha="";
						int run=0;
						String nombre="";
						int valor=0;
						double cantidad = 0;
						String motivo="";
						String comentario="";
						String centro_costo="";
						String area="";
						String tipo_pacto="";
						String estado1="";
						String rut_admin1="";
						String estado2 ="";
						String rut_admin2 ="";
						String estado3 = "";
						String rut_admin3 = "";
						String mensajesrv= null;


						if(response.equals(null) || response.isEmpty()){

							titulo = "ERROR \n";
							mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
							alertas.alertaSimple(titulo,mensaje,MainActivity.this);

							miDbHelper.insertarLogError("Variable response es Nulo o Vacio");
							return;
						}
						try {
							//Log.e("Omar","entro a try");
							jsonObject= new JSONObject(response);
							//Log.e("Respuesta",response);
						} catch (JSONException e) {

							titulo = "ERROR \n";
							mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto y el siguiente error:\n\n"+String.valueOf(e);
							alertas.alertaSimple(titulo,mensaje,MainActivity.this);

							miDbHelper.insertarLogError("Error de formato en variable 'response', no parece ser tipo JSON. Mensaje de error : "+e.getMessage());
							return;
						}
						try{
							error = jsonObject.getBoolean("error");

						} catch (JSONException e) {
							e.printStackTrace();
							titulo = "ERROR \n";
							mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
							alertas.alertaSimple(titulo,mensaje,MainActivity.this);

							miDbHelper.insertarLogError("Error de formato en variable 'error', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
							return;
						}
						if(error){
							//Log.e("Omar","entro a error");


							try {
								mensajesrv = jsonObject.getString("mensaje");
							} catch (JSONException e) {
								titulo = "ERROR \n";
								mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
								alertas.alertaSimple(titulo,mensaje,MainActivity.this);

								miDbHelper.insertarLogError("Error de formato en variable 'mensaje', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
								return;
							}

							new AlertDialog.Builder(MainActivity.this)
									.setIcon(android.R.drawable.ic_dialog_info)
									.setTitle("Servidor responde con el siguiente error: ")
									.setMessage(mensajesrv)
									.setNeutralButton("Ok",null)
									.show()
							;
							return;
						}
						//Log.e("Respuesta",response);


						JSONArray jsonArray = null;
						try {
							jsonArray = jsonObject.getJSONArray("filas");
						} catch (JSONException e) {
							new AlertDialog.Builder(MainActivity.this)
									.setIcon(android.R.drawable.ic_dialog_info)
									.setTitle("ERROR \n")
									.setMessage("Comuniquese con informatica, el servidor responde con formato incorrecto")
									.setNeutralButton("Ok",null)
									.show()
							;
							miDbHelper.insertarLogError("Error de formato en variable 'filas', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
							return;
						}
						int switchBD=0;
							// Borrar solicitudes antiguas
							miDbHelper.deleteSolicitudALL();
							Log.e("Omar","entro a else");
							for(int i=0;i<jsonArray.length();i++){

								JSONObject jsonData= null;
								try {
									jsonData = jsonArray.getJSONObject(i);
								} catch (JSONException e) {
									new AlertDialog.Builder(MainActivity.this)
											.setIcon(android.R.drawable.ic_dialog_info)
											.setTitle("ERROR \n")
											.setMessage("Comuniquese con informatica, el servidor no retorna filas")
											.setNeutralButton("Ok",null)
											.show()
									;
									miDbHelper.insertarLogError("Error de formato en variable 'filas',datos del arreglo no son JSONObject o no tienen formato correcto. Mensaje de error : "+e.getMessage());
									return;
								}

								try {

									run = jsonData.getInt("run");
									fecha = jsonData.getString("fecha");
									nombre = jsonData.getString("nombre");
									cantidad = jsonData.getDouble("cantidad");
									valor = jsonData.getInt("valor");
									motivo = jsonData.getString("motivo");
									comentario = jsonData.getString("comentario");
									centro_costo = jsonData.getString("centro_costo");
									area = jsonData.getString("area");
									tipo_pacto = jsonData.getString("tipo_pacto");
									estado1 = jsonData.getString("estado1");
									rut_admin1 = jsonData.getString("run1");
									estado2 = jsonData.getString("estado2");
									rut_admin2 = jsonData.getString("run2");
									estado3 = jsonData.getString("estado3");
									rut_admin3 = jsonData.getString("run3");

								} catch (JSONException e) {
									e.printStackTrace();
									new AlertDialog.Builder(MainActivity.this)
											.setIcon(android.R.drawable.ic_dialog_info)
											.setTitle("ERROR \n")
											.setMessage("Comuniquese con informatica, el servidor retorna filas incorrectas")
											.setNeutralButton("Ok",null)
											.show()
									;
									miDbHelper.insertarLogError("Filas del arreglo no tienen formato correcto o estan vacias. Mensaje de error : "+e.getMessage());
									return;
								}


								//Insertar solicitud nueva
								if(!miDbHelper.insertarSolicitud(
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
									new AlertDialog.Builder(MainActivity.this)
											.setIcon(android.R.drawable.ic_dialog_info)
											.setTitle("Error")
											.setMessage("Error de base de datos \n Comuniquese con informatica inmediatamente")
											.setNeutralButton("Ok",null)
											.show()
									;
									miDbHelper.insertarLogError("Una o mas filas del arreglo contienen datos que no coinciden con la tabla en la fila "+String.valueOf(i));
									return;
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
				}
						, new Response.ErrorListener(){ // QUE HACER EN CASO DE ERROR
					@Override
					public void onErrorResponse(VolleyError error){
						progressDialog.dismiss();
						volleyS.cancelAll();


						new AlertDialog.Builder(MainActivity.this)
								.setIcon(android.R.drawable.ic_dialog_info)
								.setTitle("Error")
								.setMessage("Servidor no responde \n Asegurese de estar conectado a internet o intentelo mas tarde")
								.setNeutralButton("Ok",null)
								.show()
						;
						miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : "+error);

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
