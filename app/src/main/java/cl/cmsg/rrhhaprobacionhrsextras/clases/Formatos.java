package cl.cmsg.rrhhaprobacionhrsextras.clases;

import android.util.Log;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ocantuarias on 04-07-2016.
 */
public class Formatos {

    public static String fechaFormat (String fecha){

        //Primero se transforma el String a Date
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        try {
            Date date = dateFormat1.parse(fecha);
            DateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");

            fecha = String.valueOf(dateFormat2.format(date));

        } catch (ParseException e) {
            Log.e("Omar", "Error de format: "+e.getMessage());
        }
        //Segundo se aplica formato nuevo

        return fecha;
    }

    public static NumberFormat getNumberFormat(){
        return NumberFormat.getNumberInstance(new Locale("es", "ES"));
    }

}
