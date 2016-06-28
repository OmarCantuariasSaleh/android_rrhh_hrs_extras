package cl.cmsg.rrhhaprobacionhrsextras.clases;

/**
 * Created by ocantuarias on 31-05-2016.
 */
public abstract class Rut {

    public static boolean isRutValido(String rut){
        try {
            rut = rut.toUpperCase();
            rut = rut.replace(".", "").replace("-", "");

            if (rut.length() < 6){
                return false;
            }

            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                return true;
            }

        } catch (NumberFormatException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
