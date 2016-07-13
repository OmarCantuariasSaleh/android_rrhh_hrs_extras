package cl.cmsg.rrhhaprobacionhrsextras.clases;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.cmsg.rrhhaprobacionhrsextras.R;

/**
 * Created by ocantuarias on 15-06-2016.
 */
public class Reciever{
	MiDbHelper miDbHelper;

	public void recibirUna(String response, Context context){

		final String mac = ValidacionConexion.getDireccionMAC(context);

		if (response == null || response.equals(null)){
			miDbHelper.insertarLogError("Response llega nulo en recibirUna, clase Reciever", mac);
			return;
		}
		if (response.trim().isEmpty()){
			miDbHelper.insertarLogError("Response llega vacio en recibirUna, clase Reciever", mac);
			return;
		}

		String fecha;
		String run;
		String nombre;
		int valor;
		double cantidad;
		String motivo, comentario, centroCosto, area, tipoPacto, estado1, rutAdmin1, estado2, rutAdmin2, estado3, rutAdmin3;

		miDbHelper = MiDbHelper.getInstance(context);

		JSONArray jsonArray;

		try {
			jsonArray = new JSONArray(response);
		} catch (JSONException e){
			miDbHelper.insertarLogError("Error de formato en 'response', no parece ser tipo JSON en Reciever, recibirUna. Mensaje de error : " + e.getMessage() + " en Reciever", mac);
			return;
		}

		for (int i = 0; i < jsonArray.length(); i++){
			JSONObject jsonData;
			try {
				jsonData = jsonArray.getJSONObject(i);
			} catch (JSONException e){
				miDbHelper.insertarLogError("Error de formato en variable 'filas',datos del arreglo no son JSONObject o no tienen formato correcto en Reciever, recibirUna. Mensaje de error : " + e.getMessage(), mac);
				return;
			}

			try {
				run = jsonData.getString("run");
				fecha = jsonData.getString("fecha");
				nombre = jsonData.getString("nombre");
				cantidad = jsonData.getDouble("cantidad");
				valor = jsonData.getInt("valor");
				motivo = jsonData.getString("motivo");
				comentario = jsonData.getString("comentario");
				centroCosto = jsonData.getString("centroCosto");
				area = jsonData.getString("area");
				tipoPacto = jsonData.getString("tipoPacto");
				estado1 = jsonData.getString("estado1");
				rutAdmin1 = jsonData.getString("run1");
				estado2 = jsonData.getString("estado2");
				rutAdmin2 = jsonData.getString("run2");
				estado3 = jsonData.getString("estado3");
				rutAdmin3 = jsonData.getString("run3");
			} catch (JSONException e){
				miDbHelper.insertarLogError("Datos de la fila no tienen formato correcto o estan vacias en Reciever, recibirUna. Mensaje de error : " + e.getMessage(), mac);
				return;
			}

			if (miDbHelper.yaExiste(run, fecha, tipoPacto)){
				continue;
			}

			if (!miDbHelper.insertarSolicitud(String.valueOf(run), nombre, fecha, cantidad, valor, motivo, comentario
				, centroCosto, area, tipoPacto
				, estado1, rutAdmin1
				, estado2, rutAdmin2
				, estado3, rutAdmin3
				, mac)){
				miDbHelper.insertarLogError("No se pudo insertar la solicitud a la tabla solicitudes sobre Reciever.recibirUna en la fila " + String.valueOf(i), mac);
				return;
			}

		}

	}


}


