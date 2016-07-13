package cl.cmsg.rrhhaprobacionhrsextras.clases;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by ocantuarias on 03-06-2016.
 */
public class Alertas{

	public static void alertaSimple(String titulo, String mensaje, Context context){
		new AlertDialog.Builder(context)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle(titulo)
			.setMessage(mensaje)
			.setPositiveButton("OK", null)
			.setCancelable(true)
			.show()
		;
	}

	public static void alertaConexion(Context context){
		new AlertDialog.Builder(context)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle("Error de conexion")
			.setMessage("Se requiere una conexion a internet, asegurese de tener conexion e intentelo nuevamente")
			.setNeutralButton("OK", null)
			.setCancelable(true)
			.show()
		;
	}

	public static void alertaErrorInterno(Context context){
		new AlertDialog.Builder(context)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle("Error interno")
			.setMessage("Favor enviar log de errores al servidor para una pronta solución.")
			.setPositiveButton("OK", null)
			.setCancelable(true)
			.show()
		;
	}


}
