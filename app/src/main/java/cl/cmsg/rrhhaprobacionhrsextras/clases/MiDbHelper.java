package cl.cmsg.rrhhaprobacionhrsextras.clases;

/**
 * Created by ocantuarias on 25-05-2016.
 */


        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import java.text.DateFormat;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.Locale;
        import java.util.StringTokenizer;

        import cl.cmsg.rrhhaprobacionhrsextras.R;

/**
 * Created by jarriaran on 20-05-2015.
 */
@SuppressWarnings("SpellCheckingInspection")
public class MiDbHelper extends SQLiteOpenHelper{

    private static MiDbHelper mInstance = null;

    private static int DATABASE_VERSION = 2;
    private static String DATABASE_NAME = "nombre_base_de_datos" ;

    private Context context;

    // Identificacion de tablas


    private String mensajeDeError = "";

    private SQLiteDatabase db;

    public static final String tablaLogErrores = "log_errores";
    public static final String tablaUsuario = "usuario";
    public static final String tablaSolicitud = "solicitud";

    String crearTablaLogErrores =
            "create table " + tablaLogErrores
            + " (id_log_errores integer primary key autoincrement"
            + ",fecha_hora datetime not null"
            + ",version_app varchar(10) not null"
            + ",mac varchar(20) not null"
            + ",descripcion text not null)";

    String crearTablaSolicitud =
            "create table " + tablaSolicitud
                    + " (rut varchar(11) not null"
                    + ",fecha date not null"
                    + ",nombre varchar(50) not null"
                    + ",cant_horas integer not null"
                    + ",monto_pagar integer not null"
                    + ",motivo varchar(50) not null"
                    + ",comentario varchar(250) not null"
                    + ",centro_costo varchar(30) not null"
                    + ",area varchar(30) not null"
                    + ",tipo_pacto varchar(30) not null"
                    + ",estado1 char(1) not null"
                    + ",rut_admin1 char(1) not null"
                    + ",estado2 char(1) "
                    + ",rut_admin2 char(1)"
                    + ",estado3 char(1)"
                    + ",rut_admin3 char(1)"
                    +", primary key (rut, fecha))";

    String crearTablaUsuario =
            "create table " + tablaUsuario
                    + " (rut_u varchar(11) primary key"
                    +",nombre_u varchar(50)  not null)";


    public static MiDbHelper getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new MiDbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private MiDbHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = ctx;
    }

    @Override
// Cuando se crea la base de datos (primera vez que se instala)
    public void onCreate(SQLiteDatabase db){
        db.execSQL(crearTablaLogErrores);
        db.execSQL(crearTablaSolicitud);
        db.execSQL(crearTablaUsuario);


    }

    @Override
// Cuando se actualiza
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if(newVersion==2){
            db.execSQL("drop table "+tablaSolicitud);
            db.execSQL(crearTablaSolicitud);
        }
    }

    public String getMensajeDeError(){
        return mensajeDeError;
    }

/*
####################################
######## Seccion de consultas ######
####################################
  */

    // Devuelve todos los registros en una variable tipo Cursor
    public Cursor getLogErrores(){
        SQLiteDatabase db = getReadableDatabase();
        return db.query(tablaLogErrores, new String[]{"*"},null, null, null, null, null);
    }

    public String getRutUsuario(){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(tablaUsuario, new String[]{"*"},null, null, null, null, null);
        while(cursor.moveToNext()){
            return cursor.getString(cursor.getColumnIndex("rut_u"));
        }

        return "";

    }

    public String getNombreUsuario(){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(tablaUsuario, new String[]{"*"},null, null, null, null, null);
        while(cursor.moveToNext()){
            return cursor.getString(cursor.getColumnIndex("nombre_u"));
        }

        return "";

    }
    // Muestra solicitudes por aprobar
    public Cursor getDatoSolicitudAll(){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(tablaSolicitud, new String[]{"rut,nombre,fecha"},"estado1=P or estado2=P or estado3=P",null , null, null, null);

        return cursor;
    }

    // Muestra las solicitudes aprobadas del mes seleccionado
    public Cursor getDatoSolicitudPorFecha(String fecha){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(tablaSolicitud, new String[]{"rut,nombre,fecha"},"fecha like ? and estado1=A or estado2=A or estado3=A",new String[]{fecha+"%"}, null, null, null);
        //Cursor cursor = db.query(tablaSolicitud, new String[]{"rut,nombre,fecha"},null,null , null, null, null);

        return cursor;
    }

   /* public Cursor getDatoSolicitud(String rut, String fecha){ //Muestra solicitudes por aprovar

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(tablaSolicitud, new String[]{"rut,nombre,fecha"},"rut=? and fecha=? and estado=P",new String[]{rut,fecha} , null, null, null);

        return cursor;


    }*/
    public Cursor getDatoSolicitudDetalle(String rut, String fecha){ //Muestra detalle

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(tablaSolicitud, new String[]{"*"},"rut=? and fecha=?",new String[]{rut,fecha} , null, null, null);

        return cursor;


    }

    // Borra todos los registros con sus respectivos where
    public boolean deleteLogError(int idLogError){
        SQLiteDatabase db = getReadableDatabase();
        long resultado = -1;
        resultado = db.delete(tablaLogErrores,"id_log_errores=?",new String[]{String.valueOf(idLogError)});
        return (resultado >= 1);
    }

    // Borra solicitud por rut+fecha
    public boolean deleteSolicitud(String rut, String fecha){
        SQLiteDatabase db = getReadableDatabase();
        long resultado = -1;
        resultado = db.delete(tablaSolicitud,"rut=? and fecha=?",new String[]{rut,fecha});
        return (resultado >= 1);
    }

    // Borra todas las solicitudes
    public boolean deleteSolicitudALL(){
        SQLiteDatabase db = getReadableDatabase();
        long resultado = -1;
        resultado = db.delete(tablaSolicitud,null,null);
        return (resultado >= 1);
    }

    // Inserta un registro en la tabla
    public boolean insertarLogError(String descripcion){
        db = getWritableDatabase();

        ContentValues campoValor = new ContentValues();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        Date date = new Date();

        campoValor.put("fecha_hora", dateFormat.format(date));
        campoValor.put("descripcion", descripcion);
        campoValor.put("version_app", context.getString(R.string.version));

        long resultado = db.insertOrThrow(tablaLogErrores, null, campoValor);
        return resultado >= 1;
    }

    public boolean insertarUsuario(String rut, String nombre){
        db = getWritableDatabase();

        ContentValues campoValor = new ContentValues();

        campoValor.put("rut_u", rut);
        campoValor.put("nombre_u", nombre);

        long resultado = db.insertOrThrow(tablaUsuario, null, campoValor);
        return resultado >= 1;
    }

    public boolean insertarSolicitud(String rut, String nombre, String fecha, Integer cant_horas, Integer monto_pagar, String motivo,
                                     String centro_costo, String area, String estado){
        db = getWritableDatabase();

        ContentValues campoValor = new ContentValues();

        campoValor.put("rut", rut);
        campoValor.put("nombre", nombre);
        campoValor.put("fecha", fecha);
        campoValor.put("cant_horas", cant_horas);
        campoValor.put("monto_pagar", monto_pagar);
        campoValor.put("motivo", motivo);
        campoValor.put("centro_costo", centro_costo);
        campoValor.put("area", area);
        campoValor.put("estado", estado);

        long resultado = db.insertOrThrow(tablaSolicitud, null, campoValor);
        return resultado >= 1;
    }

    public boolean actualizarEstado (String rut, String fecha,String estado){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues campoValor = new ContentValues();
        campoValor.put("estado", estado);
        long resultado = db.update(tablaSolicitud, campoValor, "rut=? and fecha=?", new String[]{rut,fecha});
        return resultado >= 1;
    }


}
