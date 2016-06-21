package cl.cmsg.rrhhaprobacionhrsextras.horasextras;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cl.cmsg.rrhhaprobacionhrsextras.R;

/**
 * Created by ocantuarias on 24-05-2016.
 */
public class HorasExtras {

    String rut;
    String nombre;
    String fecha;
    String tipo_pacto;
    String cant_horas;
    LinearLayout layout;
    TextView textView;
    int lvl;

    public LinearLayout getLayout() {
        return layout;
    }

    public void setLayout(LinearLayout layout) {
        this.layout = layout;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void cambiaFondo(){
        //Drawable background = layout.getBackground();
        ColorDrawable backgroundColor = (ColorDrawable) layout.getBackground();

        if(this.layout.getResources().getColor(R.color.colorPrimaryDark)!= backgroundColor.getColor())  {
           this.layout.setBackgroundColor(this.layout.getResources().getColor(R.color.colorPrimaryDark));
            //this.textView.setText("");
        }else{
            this.layout.setBackgroundColor(this.layout.getResources().getColor(R.color.colorPrimary));
        }


    }

    public void cambiaFondo2(){
        this.layout.setBackgroundColor(Color.GREEN);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }
    public String getTipo_pacto() {
        return tipo_pacto;
    }
    public String getCant_horas() { return cant_horas; }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getLvl() {
        return lvl;
    }

    public HorasExtras(String rut, String nombre, String fecha, String tipo_pacto, double cant_horas, int lvl) {

        this.rut = rut;
        this.nombre = nombre;
        this.fecha = fecha;
        this.tipo_pacto= tipo_pacto;
        this.cant_horas= String.valueOf(cant_horas);
        this.lvl = lvl;

    }
}
