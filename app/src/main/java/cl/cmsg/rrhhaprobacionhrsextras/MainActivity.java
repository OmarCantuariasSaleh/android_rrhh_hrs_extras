package cl.cmsg.rrhhaprobacionhrsextras;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cl.cmsg.rrhhaprobacionhrsextras.clases.Alertas;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.ValidacionConexion;
import cl.cmsg.rrhhaprobacionhrsextras.clases.VolleyS;

public class MainActivity extends AppCompatActivity{

	MiDbHelper miDbHelper;
	ImageButton btnActualizar;
	ListView lista;
	ProgressDialog progressDialog;
	String mensaje;
	String titulo;
	String[] opciones;
	int errorSwitch=0;
    Cursor cursor;
	int error1=0;
	int error2=0;
	int error3=0;
	TextView lblBienvenido;
	int filas=0;
	@Override
	protected void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		miDbHelper = MiDbHelper.getInstance(this,MainActivity.this);
		btnActualizar = (ImageButton) findViewById(R.id.btnActualizar);
		miDbHelper.deleteErrorLogALL();
		final VolleyS volleyS = VolleyS.getInstance(this);
		lblBienvenido = (TextView) findViewById(R.id.lblBienvenido);
		//lblBienvenido.setText("Bienvenido "+ String.get);

		lista = (ListView) findViewById(R.id.Lista);
		opciones = new String[] {
				"Horas extras pendientes",
				"Horas extras aprobadas",
				"Enviar Errrores [ "+String.valueOf(miDbHelper.CuentaErrores())+" ]",
				"Version: "+getString(R.string.version)
		};

		lista.setAdapter(
				new ArrayAdapter<>(
						this,
						android.R.layout.simple_list_item_1,
						android.R.id.text1,
						opciones
				)
		);

		lista.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){

				switch (position){
					case 0:
						Intent intent = new Intent(MainActivity.this,HorasPendientesActivity.class);
						startActivity(intent);
						break;
					case 1:
						Intent intent2 = new Intent(getApplicationContext(),HorasAprobadasActivity.class);
						startActivity(intent2);
						break;
					case 2:

						progressDialog = new ProgressDialog(MainActivity.this);
						progressDialog.setTitle("Actualizando");
						progressDialog.setMessage("Espere un momento");
						progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						//TODO Agregar carga
						progressDialog.setCancelable(false);
						progressDialog.show();

						if(miDbHelper.CuentaErrores()== 0){
							progressDialog.dismiss();
							//TODO Borrar errores de prueba ---------------
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							miDbHelper.insertarLogError("hola");
							//TODO Borrar errores de prueba --------------
							updateList();
							break;
						}
						if(!ValidacionConexion.isExisteConexion(MainActivity.this)){
							progressDialog.dismiss();
							Alertas.alertaConexion(MainActivity.this);
							break;
						}
                        cursor = miDbHelper.getLogErrores();
						errorSwitch=miDbHelper.CuentaErrores();

						error1=0;
						error2=0;
						error3=0;

						while(cursor.moveToNext()){
							String fechaHora, descripcion, version_app, mac;
                            try {
                                fechaHora = URLEncoder.encode(cursor.getString(cursor.getColumnIndex("fecha_hora")),"UTF-8");
                                descripcion=URLEncoder.encode(cursor.getString(cursor.getColumnIndex("descripcion")),"UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                fechaHora="";
                                descripcion="";
                            }

							version_app = cursor.getString(cursor.getColumnIndex("version_app"));
							mac = cursor.getString(cursor.getColumnIndex("mac"));

							final int indexout=cursor.getInt(cursor.getColumnIndex("id_log_errores"));

                            String url=getString(R.string.URL_EnviarErrores)
                                    +"?fecha_hora="+fechaHora
                                    +"&version_app="+version_app
                                    +"&mac="+mac
                                    +"&descripcion="+descripcion
                                    +"&apk_key="+getString(R.string.APK_KEY)
                                    +"&run="+miDbHelper.getRutUsuario();
                            StringRequest jsonObjectRequest = new StringRequest(

									Request.Method.GET // FORMA QUE LLAMAREMOS, O SEA GET
									, url
									// URL QUE LLAMAREMOS
									, new Response.Listener<String>(){ // OBJETO QUE USAREMOS PARA LA ESCUCHA DE LA RESPUESTA
								@Override
								public void onResponse(String response){

									//Log.e("omar",response);
									if(response==null || response.isEmpty()){
										progressDialog.dismiss();
										mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
										volleyS.cancelAll();

										error1++;
										if(error1==1){
											Alertas.alertaSimple("ERROR",mensaje,MainActivity.this);
											miDbHelper.insertarLogError("Variable response es Nulo o Vacio");
											updateList();
											Log.e("Omar", "Variable response es Nulo o Vacio");
										}

										return;
									}

									if(!response.toLowerCase().equals("exito")){
										Log.e("Omar","entro a error2");
										progressDialog.dismiss();
										volleyS.cancelAll();
										error2++;
										if(error2==1){
											Alertas.alertaSimple("Error","Comuniquese con informatica, el servidor responde con formato incorrecto",MainActivity.this);

											miDbHelper.insertarLogError("Servidor respondio con error:"+response);
											updateList();
											Log.e("Omar","Servidor respondio con error:"+response);
										}

										return;
									}
									// Borrar solicitudes antiguas
									miDbHelper.deleteLogError(indexout);

									errorSwitch--;

									if(errorSwitch==0){
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), "Errores enviados exitosamente", Toast.LENGTH_SHORT).show();
									}

									updateList();
									//Alertas.alertaSimple(titulo,mensaje,MainActivity.this);

								}
							}
									, new Response.ErrorListener(){ // QUE HACER EN CASO DE ERROR
								@Override
								public void onErrorResponse(VolleyError error){
									progressDialog.dismiss();

									volleyS.cancelAll();

									mensaje = "Servidor no responde \n" +
											" Asegurese de estar conectado a internet o intentelo mas tarde";
									//cursor.moveToLast();

									error3++;
									if(error3==1){
										Alertas.alertaSimple("Error",mensaje,MainActivity.this);
										miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : "+error.getMessage());
										updateList();
										Log.e("Omar","Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : "+error.getMessage() );
									}
								}
							}
							);
							volleyS.addToQueue(jsonObjectRequest, MainActivity.this);
						}



						cursor.close();

						//enviarErrores();
						break;
					case 3:
						Toast.makeText(getApplicationContext(),getString(R.string.version), Toast.LENGTH_SHORT).show();
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
				if(!ValidacionConexion.isExisteConexion(MainActivity.this)){
					progressDialog.dismiss();
					Alertas.alertaConexion(MainActivity.this);
					return;
				}
				final StringRequest jsonObjectRequest = new StringRequest(

						Request.Method.GET // FORMA QUE LLAMAREMOS, O SEA GET
						, getString(R.string.URL_PendienteAprobacion) +"?run="+ miDbHelper.getRutUsuario() // URL QUE LLAMAREMOS
						, new Response.Listener<String>(){ // OBJETO QUE USAREMOS PARA LA ESCUCHA DE LA RESPUESTA
					@Override
					public void onResponse(String response){
						progressDialog.dismiss();
						JSONObject jsonObject;
						Boolean error;
						String fecha;
						int run;
						String nombre;
						int valor;
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

						//Log.e("omar",response);
						if(response==null || response.isEmpty()){
							progressDialog.dismiss();
							titulo = "ERROR";
							mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
							Alertas.alertaSimple(titulo,mensaje,MainActivity.this);

							miDbHelper.insertarLogError("Variable response es Nulo o Vacio");
							updateList();
							Log.e("Omar", "Variable response es Nulo o Vacio");
							return;
						}
						try {
							//Log.e("Omar","entro a try");
							jsonObject= new JSONObject(response);
							//Log.e("Respuesta",response);
						} catch (JSONException e) {

							titulo = "ERROR \n";
							mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto y el siguiente error:\n\n"+String.valueOf(e);
							Alertas.alertaSimple(titulo,mensaje,MainActivity.this);

							miDbHelper.insertarLogError("Error de formato en variable 'response', no parece ser tipo JSON. Mensaje de error : "+e.getMessage());
							updateList();
							Log.e("Omar","Error de formato en variable 'response', no parece ser tipo JSON. Mensaje de error : "+e.getMessage() );
							return;
						}
						try{
							error = jsonObject.getBoolean("error");

						} catch (JSONException e) {
							e.printStackTrace();
							titulo = "ERROR \n";
							mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
							Alertas.alertaSimple(titulo,mensaje,MainActivity.this);

							miDbHelper.insertarLogError("Error de formato en variable 'error', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
							updateList();
							Log.e("Omar","Error de formato en variable 'mensaje', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
							return;
						}
						if(error){
							//Log.e("Omar","entro a error");


							try {
								mensajesrv = jsonObject.getString("mensaje");
							} catch (JSONException e) {
								titulo = "ERROR \n";
								mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
								Alertas.alertaSimple(titulo,mensaje,MainActivity.this);

								miDbHelper.insertarLogError("Error de formato en variable 'mensaje', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
								updateList();
								Log.e("Omar","Error de formato en variable 'mensaje', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage() );
								return;
							}
							titulo = "Servidor responde con el siguiente error:";

							mensaje = mensajesrv;
							miDbHelper.insertarLogError(titulo+" "+mensaje);
							updateList();
							Alertas.alertaSimple(titulo,mensaje,MainActivity.this);
							return;
						}
						//Log.e("Respuesta",response);


						JSONArray jsonArray = null;
						try {
							jsonArray = jsonObject.getJSONArray("filas");
						} catch (JSONException e) {
							titulo = "ERROR \n";
							mensaje = "Comuniquese con informatica, el servidor responde con formato incorrecto";
							Alertas.alertaSimple(titulo,mensaje,MainActivity.this);

							miDbHelper.insertarLogError("Error de formato en variable 'filas', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
							updateList();
							Log.e("Omar", "Error de formato en variable 'filas', No existe o es un formato incorrecto. Mensaje de error : "+e.getMessage());
							return;
						}
							// Borrar solicitudes antiguas
							miDbHelper.deleteSolicitudALL();
							Log.e("Omar","entro a else");
							filas=0;
							for(int i=0;i<jsonArray.length();i++){
								//Log.e("Omar","entro a for");
								JSONObject jsonData= null;
								try {
									jsonData = jsonArray.getJSONObject(i);
								} catch (JSONException e) {
									titulo = "ERROR \n";
									mensaje = "Comuniquese con informatica, el servidor no retorna filas";
									Alertas.alertaSimple(titulo,mensaje,MainActivity.this);

									miDbHelper.insertarLogError("Error de formato en variable 'filas',datos del arreglo no son JSONObject o no tienen formato correcto. Mensaje de error : "+e.getMessage());
									updateList();
									Log.e("Omar", "Error de formato en variable 'filas',datos del arreglo no son JSONObject o no tienen formato correcto. Mensaje de error : "+e.getMessage());
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
									Log.e("Omar", "Rut "+String.valueOf(run)+" // Nombre: "+nombre+" // fecha: "+fecha+" // horas: "+cantidad
											+" // Valor: $"+valor+" // Motivo: "+motivo+" // Coment: "+comentario+" // Cent.Costo: "+centro_costo+" // Area: "+area
											+" // Tipo Pacto: "+tipo_pacto+" // E1: "+estado1+" // R1: "+rut_admin1+" // E2: "+estado2+" // RU2: "+rut_admin2
											+" // E3: "+estado3+" // R3: "+rut_admin3);
									filas++;
								} catch (JSONException e) {
									e.printStackTrace();
									titulo = "ERROR \n";
									mensaje = "Comuniquese con informatica, el servidor retorna filas incorrectas";
									Alertas.alertaSimple(titulo,mensaje,MainActivity.this);

									miDbHelper.insertarLogError("Filas del arreglo no tienen formato correcto o estan vacias. Mensaje de error : "+e.getMessage());
									updateList();
									Log.e("Omar", "Filas del arreglo no tienen formato correcto o estan vacias. Mensaje de error : "+e.getMessage());
									return;
								}

								try{
									miDbHelper.insertarSolicitud(
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
											,rut_admin3);
								}catch (Exception e){
									Log.e("Omar","No Exitoso");
									titulo = "ERROR \n";
									mensaje = "Error de base de datos \n" +
											" Comuniquese con informatica inmediatamente";
									Alertas.alertaSimple(titulo,mensaje,MainActivity.this);
									miDbHelper.insertarLogError("Una o mas filas del arreglo contienen datos que no coinciden con la tabla en la fila "+String.valueOf(i));
									updateList();
									Log.e("Omar","Una o mas filas del arreglo contienen datos que no coinciden con la tabla en la fila, datos : "+e.getMessage());
									return;
								}

						}
						titulo = "Exito";
						mensaje = "Actualizacion exitosa, "+String.valueOf(filas)+" filas nuevas ingresadas";
						Alertas.alertaSimple(titulo,mensaje,MainActivity.this);

					}
				}
						, new Response.ErrorListener(){ // QUE HACER EN CASO DE ERROR
					@Override
					public void onErrorResponse(VolleyError error){
						progressDialog.dismiss();
						volleyS.cancelAll();

						titulo = "Error";
						mensaje = "Servidor no responde \n" +
								"Asegurese de estar conectado a internet o intentelo mas tarde";
						Alertas.alertaSimple(titulo,mensaje,MainActivity.this);
						//cursor.moveToLast();
						miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : "+error);
						updateList();
						Log.e("Omar","Ocurrio un error al comunicarse con el servidor a travez de Volley. Mensaje : "+error );
					}
				}
				);
				volleyS.addToQueue(jsonObjectRequest, MainActivity.this);
			}
		});



	}


	public void updateList(){
		opciones = new String[] {
				"Horas extras pendientes",
				"Horas extras aprobadas",
				"Enviar Errrores [ "+String.valueOf(miDbHelper.CuentaErrores())+" ]",
				"Version: "+getString(R.string.version)
		};
		lista.setAdapter(
				new ArrayAdapter<>(
						MainActivity.this,
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
