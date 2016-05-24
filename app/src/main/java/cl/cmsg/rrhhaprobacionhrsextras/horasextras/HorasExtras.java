package cl.cmsg.rrhhaprobacionhrsextras.horasextras;

/**
 * Created by ocantuarias on 24-05-2016.
 */
public class HorasExtras {

    String rut;
    String nombre;
    String fecha;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public HorasExtras(String rut, String nombre, String fecha) {

        this.rut = rut;
        this.nombre = nombre;
        this.fecha = fecha;
    }
}
