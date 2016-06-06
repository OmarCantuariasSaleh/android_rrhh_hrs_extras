package cl.cmsg.rrhhaprobacionhrsextras.clases;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by ocantuarias on 03-06-2016.
 */
public abstract class Alertas {


    static public void alertaSimple (String titulo, String mensaje, Context context){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setNeutralButton("Ok",null)
                .setCancelable(true)
                .show()
        ;
    }

    static public void alertaConexion (Context context){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Error de conexion")
                .setMessage("Se requiere una conexion a internet, asegurese de tener conexion e intentelo nuevamente")
                .setNeutralButton("Ok",null)
                .show()
        ;
    }



    /*public void alertaPregunta (String titulo, String mensaje, Context context){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setNeutralButton("Ok",null)
                .show()
        ;
    }*/

}
