package cl.cmsg.rrhhaprobacionhrsextras.clases;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ocantuarias on 15-06-2016.
 */
public class Reciever {
    MiDbHelper miDbHelper;
    ProgressDialog progressDialog;
    String mensaje;

    public void RecibirUna(String response, Context context) {

        JSONObject jsonObject;
        Boolean error;
        String fecha;
        String run;
        String nombre;
        int valor;
        double cantidad = 0;
        String motivo = "";
        String comentario = "";
        String centro_costo = "";
        String area = "";
        String tipo_pacto = "";
        String estado1 = "";
        String rut_admin1 = "";
        String estado2 = "";
        String rut_admin2 = "";
        String estado3 = "";
        String rut_admin3 = "";
        String mensajesrv = null;
        final String mac = ValidacionConexion.getDireccionMAC(context);
        miDbHelper = MiDbHelper.getInstance(context);

        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonData = null;
            try {
                jsonData = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
//                Alertas.alertaSimple("Error", "Comuniquese con informatica, el servidor no retorna filas", context);

                miDbHelper.insertarLogError("Error de formato en variable 'filas',datos del arreglo no son JSONObject o no tienen formato correcto. Mensaje de error : " + e.getMessage()+" en Reciever, RecibirUna", mac);

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
                mensaje = "Comuniquese con informatica, el servidor retorna filas incorrectas";
//                Alertas.alertaSimple("Error", mensaje, context);

                miDbHelper.insertarLogError("Filas del arreglo no tienen formato correcto o estan vacias en Reciever, RecibirUna. Mensaje de error : " + e.getMessage(), mac);

                return;
            }
            if(miDbHelper.yaExiste(run,fecha,tipo_pacto)){
                return;
            }
            try {
                miDbHelper.insertarSolicitud(
                        String.valueOf(run)
                        , nombre
                        , fecha
                        , cantidad
                        , valor
                        , motivo
                        , comentario
                        , centro_costo
                        , area
                        , tipo_pacto
                        , estado1
                        , rut_admin1
                        , estado2
                        , rut_admin2
                        , estado3
                        , rut_admin3);
            } catch (Exception e) {
                mensaje = "Error de base de datos \n" +
                        " Comuniquese con informatica inmediatamente";
                miDbHelper.insertarLogError("Una o mas filas del arreglo contienen datos que no coinciden con la tabla en la fila " + String.valueOf(i)+" en Reciever, RecibirUna", mac);

                return;
            }

        }

    }
}


