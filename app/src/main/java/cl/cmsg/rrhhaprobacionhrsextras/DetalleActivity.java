package cl.cmsg.rrhhaprobacionhrsextras;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import cl.cmsg.rrhhaprobacionhrsextras.clases.Formatos;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.Alertas;
import cl.cmsg.rrhhaprobacionhrsextras.clases.ValidacionConexion;
import cl.cmsg.rrhhaprobacionhrsextras.clases.VolleyS;

public class DetalleActivity extends AppCompatActivity{

	TextView lblRut;
	TextView lblNombre;
	TextView lblFecha;
	TextView lblCantHoras;
	TextView lblMontoPagar;
	TextView lblMotivo;
	TextView lblComentario;
	TextView lblCentroCosto;
	TextView lblArea;
	TextView lblTipoPacto;
	MiDbHelper miDbHelper;
	LinearLayout layoutBotones;
	Button btnAprobar;
	Button btnRechazar;
	String lvl;
	ProgressDialog progressDialog;
	String mensajeERROR = "Comuniquese con informatica, el servidor responde con formato incorrecto";
	final String tituloERROR = "ERROR";
	String rut;
	String nombre;
	String fecha;
	double cantidadHoras;
	int monto_pagar;
	String motivo;
	String comentario;
	String centroCosto;
	String area;
	String tipoPacto;
	String E1;
	String E2;
	String E3;
	String estadoFinal;

	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalle);

		final String mac = ValidacionConexion.getDireccionMAC(DetalleActivity.this);
		lblRut = (TextView) findViewById(R.id.lblRut);
		lblNombre = (TextView) findViewById(R.id.lblNombre);
		lblFecha = (TextView) findViewById(R.id.lblFecha);
		lblCantHoras = (TextView) findViewById(R.id.lblCantHoras);
		lblMontoPagar = (TextView) findViewById(R.id.lblMonto);
		lblMotivo = (TextView) findViewById(R.id.lblMotivo);
		lblComentario = (TextView) findViewById(R.id.lblComentario);
		lblCentroCosto = (TextView) findViewById(R.id.lblCentroCosto);
		lblArea = (TextView) findViewById(R.id.lblArea);
		lblTipoPacto = (TextView) findViewById(R.id.lblTipoPacto);
		layoutBotones = (LinearLayout) findViewById(R.id.layoutBotones);
		btnAprobar = (Button) findViewById(R.id.btnAprobar);
		btnRechazar = (Button) findViewById(R.id.btnRechazar);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//		Esta pantalla no se puede abrir si no es con un bundle
		Bundle bundle = getIntent().getExtras();
		if (bundle == null || bundle.isEmpty()){
			miDbHelper.insertarLogError("Error al mostrar datos en DetalleActivity,Bundle llego vacio o nulo", mac);
			Alertas.alertaSimple("ERROR", "Error mostrando datos, comuniquese con informatica", this);
			finish();
			return;
		}

		rut = bundle.getString("Rut", "");
		fecha = bundle.getString("fecha", "");
		tipoPacto = bundle.getString("tipoPacto", "");

		miDbHelper = MiDbHelper.getInstance(this);
		cursor = miDbHelper.getDatoSolicitudDetalle(rut, fecha, tipoPacto);
		//Imprimimos los datos en la pantalla
		if (!cursor.moveToNext()){
			miDbHelper.insertarLogError("Error al mostrar datos en DetalleActivity, la consulta no trae filas con rut " + rut + " fecha " + fecha + " tipoPacto " + tipoPacto, mac);
			Alertas.alertaErrorInterno(this);
			return;
		}

		rut = cursor.getString(cursor.getColumnIndex("Rut"));
		lblRut.setText(Formatos.getNumberFormat().format(Integer.valueOf(rut)));

		nombre = cursor.getString(cursor.getColumnIndex("nombre"));
		lblNombre.setText(nombre);

		fecha = cursor.getString(cursor.getColumnIndex("fecha"));
		lblFecha.setText(Formatos.fechaFormat(fecha));

		cantidadHoras = (cursor.getDouble(cursor.getColumnIndex("cantidadHoras")));
		lblCantHoras.setText(String.valueOf(cantidadHoras));

		monto_pagar = cursor.getInt(cursor.getColumnIndex("monto_pagar"));
		lblMontoPagar.setText(Formatos.getNumberFormat().format(monto_pagar));

		motivo = cursor.getString(cursor.getColumnIndex("motivo"));
		lblMotivo.setText(motivo);

		comentario = cursor.getString(cursor.getColumnIndex("comentario"));
		lblComentario.setText(comentario);

		centroCosto = cursor.getString(cursor.getColumnIndex("centroCosto"));
		lblCentroCosto.setText(centroCosto);

		area = cursor.getString(cursor.getColumnIndex("area"));
		lblArea.setText(area);

		tipoPacto = cursor.getString(cursor.getColumnIndex("tipoPacto"));

		switch (tipoPacto) {
			case "H":
				lblTipoPacto.setText(R.string.HORAEXTRA);
				break;
			case "T":
				lblTipoPacto.setText(R.string.TRATO);
				break;
			case "F":
				lblTipoPacto.setText(R.string.FESTIVO);
				break;
		}

		E1 = cursor.getString(cursor.getColumnIndex("estado1"));
		E2 = cursor.getString(cursor.getColumnIndex("estado2"));
		E3 = cursor.getString(cursor.getColumnIndex("estado3"));
		// Verificar si detalle es de solicitud aprobada o pendiente, separadas por nivel
		String rut1 = cursor.getString(cursor.getColumnIndex("rut_admin1"));
		String rut2 = cursor.getString(cursor.getColumnIndex("rut_admin2"));
		String rut3 = cursor.getString(cursor.getColumnIndex("rut_admin3"));

		lvl = "0";

		if (E1.equals("R") || E2.equals("R") || E3.equals("R") || miDbHelper.isAdmin()){
			layoutBotones.setVisibility(View.GONE);
		} else{
			if (
				(E1.equals("A") && rut1.equals(miDbHelper.getRutUsuario()))
					|| (E2.equals("A") && rut2.equals(miDbHelper.getRutUsuario()))
					|| (E3.equals("A") && rut3.equals(miDbHelper.getRutUsuario()))
				){
				layoutBotones.setVisibility(View.GONE);
			}
		}

		if (E1.equals("P") && rut1.equals(miDbHelper.getRutUsuario())){
			lvl = "1";
		} else if (E2.equals("P") && rut2.equals(miDbHelper.getRutUsuario()) && E3.equals("A") && E1.equals("A")){
			lvl = "2";
		} else if (E3.equals("P") && rut3.equals(miDbHelper.getRutUsuario()) && E1.equals("A") && E2.equals("A")){
			lvl = "3";
		}

		cursor.close();

		//Aprobar solicitud
		btnAprobar.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if (!ValidacionConexion.isExisteConexion(DetalleActivity.this)){
					Alertas.alertaConexion(DetalleActivity.this);
					return;
				}
				new AlertDialog.Builder(DetalleActivity.this)
					.setPositiveButton("Si", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialogInterface, int i){

							progressDialog = new ProgressDialog(DetalleActivity.this);
							progressDialog.setTitle("Actualizando datos");
							progressDialog.setMessage("Espere un momento");
							progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							progressDialog.setCancelable(false);
							progressDialog.show();
							estadoFinal = "A";
							actualizaEstado(rut, fecha, estadoFinal, lvl, tipoPacto);
						}
					})
					.setNegativeButton("No", null)
					.setCancelable(false)
					.setTitle("¿Esta seguro?")
					.setMessage("Esta accion no se puede deshacer.")
					.show()
				;

			}
		});

		//Rechazar solicitud
		btnRechazar.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if (!ValidacionConexion.isExisteConexion(DetalleActivity.this)){
					Alertas.alertaConexion(DetalleActivity.this);
					return;
				}

				new AlertDialog.Builder(DetalleActivity.this)
					.setPositiveButton("Si", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialogInterface, int i){

							progressDialog = new ProgressDialog(DetalleActivity.this);
							progressDialog.setTitle("Actualizando datos");
							progressDialog.setMessage("Espere un momento");
							progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							progressDialog.setCancelable(false);
							progressDialog.show();
							estadoFinal = "R";
							actualizaEstado(rut, fecha, estadoFinal, lvl, tipoPacto);
						}
					})
					.setNegativeButton("No", null)
					.setCancelable(false)
					.setTitle("¿Esta seguro?")
					.setMessage("Esta accion no se puede deshacer.")
					.show()
				;

			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home buttonOk
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//Actualizar estado, toma los datos clave de la fila, el estado a cambiar y el nivel, y solicita actualizar al servidor
	public void actualizaEstado(final String rut_S, final String fecha_S, final String estado_Final, final String lvl_S, final String tipo_pacto_S){
		final VolleyS volleyS = VolleyS.getInstance(this);

		if (!ValidacionConexion.isExisteConexion(DetalleActivity.this)){
			progressDialog.dismiss();
			Alertas.alertaConexion(DetalleActivity.this);
			return;
		}

		final String mac = ValidacionConexion.getDireccionMAC(DetalleActivity.this);

		final StringRequest jsonObjectRequest = new StringRequest(

			Request.Method.GET // FORMA QUE LLAMAREMOS, O SEA GET
			, getString(R.string.URL_ActualizarEstado)
			+ "?run=" + rut_S
			+ "&fecha=" + fecha_S
			+ "&estado=" + estado_Final
			+ "&lvl=" + lvl_S
			+ "&apk_key=" + getString(R.string.APK_KEY)
			+ "&mac=" + mac
			+ "&run_admin=" + miDbHelper.getRutUsuario()
			+ "&tipoPacto=" + tipo_pacto_S

			, new Response.Listener<String>(){ // OBJETO QUE USAREMOS PARA LA ESCUCHA DE LA RESPUESTA
			@Override
			public void onResponse(String response){

				progressDialog.dismiss();

				JSONObject jsonObject;
				Boolean error;

				String mensajesrv;

				if (response == null || response.isEmpty()){
					Alertas.alertaSimple(tituloERROR, mensajeERROR, DetalleActivity.this);
					miDbHelper.insertarLogError("Variable response es Nulo o Vacio en DetalleActivity, ActualizarEstado", mac);
					return;
				}

				try {
					jsonObject = new JSONObject(response);
				} catch (JSONException e){
					Alertas.alertaSimple(tituloERROR, mensajeERROR, DetalleActivity.this);
					miDbHelper.insertarLogError("Error de formato en 'response' en DetalleActivity, ActualizarEstado. No parece ser tipo JSON. Mensaje de error : " + e.getMessage(), mac);
					return;
				}

				try {
					error = jsonObject.getBoolean("error");
				} catch (JSONException e){
					Alertas.alertaSimple(tituloERROR, mensajeERROR, DetalleActivity.this);
					miDbHelper.insertarLogError("Error de formato en variable 'error' en DetalleActivity, ActualizarEstado. No existe o es un formato incorrecto. Mensaje de error : " + e.getMessage(), mac);
					return;
				}

				if (error){
					try {
						mensajesrv = jsonObject.getString("mensaje");
					} catch (JSONException e){
						Alertas.alertaSimple(tituloERROR, mensajeERROR, DetalleActivity.this);
						miDbHelper.insertarLogError("Error de formato en variable 'mensaje' en DetalleActivity, ActualizarEstado. No existe o es un formato incorrecto. Mensaje de error : " + e.getMessage(), mac);
						return;
					}
					Alertas.alertaSimple("Servidor responde con el siguiente error:", mensajesrv, DetalleActivity.this);
					return;
				}

				miDbHelper.actualizarEstado(rut_S, fecha_S, estado_Final, lvl_S, tipo_pacto_S);

				new AlertDialog.Builder(DetalleActivity.this)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialogInterface, int i){
							setResult(RESULT_OK);
							finish();
						}
					})
					.setCancelable(false)
					.setMessage("Actualizacion exitosa")
					.setTitle("Exito")
					.show()
				;


			}
		}
			, new Response.ErrorListener(){ // QUE HACER EN CASO DE ERROR
			@Override
			public void onErrorResponse(VolleyError error){
				progressDialog.dismiss();
				volleyS.cancelAll();
				Alertas.alertaSimple(tituloERROR, "Servidor no responde asegurese de estar conectado a internet o intentelo mas tarde", DetalleActivity.this);
				miDbHelper.insertarLogError("Ocurrio un error al comunicarse con el servidor a travez de Volley en DetalleActivity, ActualizarEstado. Mensaje : " + error.getMessage(), mac);

			}
		}
		);
		volleyS.addToQueue(jsonObjectRequest, DetalleActivity.this);

	}

}
