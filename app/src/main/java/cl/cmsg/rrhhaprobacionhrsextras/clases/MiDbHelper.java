package cl.cmsg.rrhhaprobacionhrsextras.clases;

/**
 * Created by ocantuarias on 25-05-2016.
 */


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cl.cmsg.rrhhaprobacionhrsextras.R;

/**
 * Created by jarriaran on 20-05-2015.
 */
@SuppressWarnings("SpellCheckingInspection")
public class MiDbHelper extends SQLiteOpenHelper{

	private static MiDbHelper mInstance = null;

	private static int DATABASE_VERSION = 7;
	private static String DATABASE_NAME = "rrhh_aprobacion";

	private Context context;
	private Activity activity;
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
			+ " (Rut varchar(11) not null"
			+ ",fecha date not null"
			+ ",nombre varchar(50) not null"
			+ ",cant_horas double not null"
			+ ",monto_pagar integer not null"
			+ ",motivo varchar(50) not null"
			+ ",comentario varchar(250) not null"
			+ ",centro_costo varchar(30) not null"
			+ ",area varchar(30) not null"
			+ ",tipo_pacto varchar(30) not null"
			+ ",estado1 char(1) not null"
			+ ",rut_admin1 char(11) not null"
			+ ",estado2 char(1) "
			+ ",rut_admin2 char(11)"
			+ ",estado3 char(1)"
			+ ",rut_admin3 char(11)"
			+ ", primary key (Rut, fecha,tipo_pacto))";

	String crearTablaUsuario =
		"create table " + tablaUsuario
			+ " (rut_u varchar(11) primary key"
			+ ",nombre_u varchar(50)  not null" +
			",isadmin integer(1) not null)";


	public static MiDbHelper getInstance(Context ctx){
		/**
		 * use the application context as suggested by CommonsWare.
		 * this will ensure that you dont accidentally leak an Activitys
		 * context (see this article for more information:
		 * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
		 */
		if (mInstance == null){
			mInstance = new MiDbHelper(ctx.getApplicationContext());
		}
		return mInstance;
	}

	/**
	 * constructor should be private to prevent direct instantiation.
	 * make call to static factory method "getInstance()" instead.
	 */
	private MiDbHelper(Context ctx){
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
		if (newVersion == 7){
			db.execSQL("drop table " + tablaSolicitud);
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
		return db.query(tablaLogErrores, new String[]{"*"}, null, null, null, null, null);
	}

	public String getRutUsuario(){

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(tablaUsuario, new String[]{"*"}, null, null, null, null, null);
		String rutU = "";
		if (cursor.moveToNext()){
			rutU = cursor.getString(cursor.getColumnIndex("rut_u"));
		}
		cursor.close();
		return rutU;
	}

	//Obtiene nombre de usuario
	public String getNombreUsuario(){
		String nombre = "Sin nombre";
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(tablaUsuario, new String[]{"*"}, null, null, null, null, null);

		if (cursor.moveToNext()){
			String[] partes;
			nombre = cursor.getString(cursor.getColumnIndex("nombre_u"));
			partes = nombre.split(" ");
			nombre = partes[0] + " " + partes[1];
		}
		cursor.close();
		return nombre;

	}

	//Cuenta cantidad de errores y retorna un int
	public int CuentaErrores(){
		int error;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(tablaLogErrores, new String[]{"*"}, null, null, null, null, null);
		error = cursor.getCount();
		cursor.close();
		return error;
	}

	//Cuenta cantidad de solicitudes y retorna un int
	public int CuentaSolicitudes(){
		SQLiteDatabase db = getReadableDatabase();
		String rutUsuario = getRutUsuario();
		Cursor cursor;
		if (isAdmin()){
			cursor = db.query(tablaSolicitud, new String[]{"Rut,fecha,tipo_pacto"}, null, null, null, null, null);
		} else{
			cursor = db.query(tablaSolicitud, new String[]{"Rut,fecha,tipo_pacto"}, "estado1='P' and rut_admin1=?"
					+ " or estado2='P' and rut_admin2=? and estado3='A' and estado1='A'" +
					" or estado3='P' and rut_admin3=? and estado1='A' and estado2='A'"
				, new String[]{rutUsuario, rutUsuario, rutUsuario}, null, null, null);
		}
		int pendientes = cursor.getCount();
		cursor.close();
		return pendientes;
	}//Cuenta cantidad de solicitudes y retorna un int

	public int CuentaSolicitudesTratos(){
		SQLiteDatabase db = getReadableDatabase();
		String rutUsuario = getRutUsuario();
		Cursor cursor;
		if (isAdmin()){
			cursor = db.query(tablaSolicitud, new String[]{"Rut,fecha,tipo_pacto"}, null, null, null, null, null);
		} else{
			cursor = db.query(tablaSolicitud, new String[]{"Rut,fecha,tipo_pacto"}, "tipo_pacto='T' and (estado1='P' and rut_admin1=?"
					+ " or estado2='P' and rut_admin2=? and estado3='A' and estado1='A'" +
					" or estado3='P' and rut_admin3=? and estado1='A' and estado2='A')"
				, new String[]{rutUsuario, rutUsuario, rutUsuario}, null, null, null);
		}
		int pendientes = cursor.getCount();
		cursor.close();
		return pendientes;
	}

	public int CuentaSolicitudesHoraExtra(){
		SQLiteDatabase db = getReadableDatabase();
		String rutUsuario = getRutUsuario();
		Cursor cursor;
		if (isAdmin()){
			cursor = db.query(tablaSolicitud, new String[]{"Rut,fecha,tipo_pacto"}, null, null, null, null, null);
		} else{
			cursor = db.query(tablaSolicitud, new String[]{"Rut,fecha,tipo_pacto"}, "tipo_pacto='H' and (estado1='P' and rut_admin1=?"
					+ " or estado2='P' and rut_admin2=? and estado3='A' and estado1='A'" +
					" or estado3='P' and rut_admin3=? and estado1='A' and estado2='A')"
				, new String[]{rutUsuario, rutUsuario, rutUsuario}, null, null, null);
		}
		int pendientes = cursor.getCount();
		cursor.close();
		return pendientes;
	}

	public int CuentaSolicitudesFestivo(){
		SQLiteDatabase db = getReadableDatabase();
		String rutUsuario = getRutUsuario();
		Cursor cursor;
		if (isAdmin()){
			cursor = db.query(tablaSolicitud, new String[]{"Rut,fecha,tipo_pacto"}, null, null, null, null, null);
		} else{
			cursor = db.query(tablaSolicitud, new String[]{"Rut,fecha,tipo_pacto"}, "tipo_pacto='F' and (estado1='P' and rut_admin1=?"
					+ " or estado2='P' and rut_admin2=? and estado3='A' and estado1='A'" +
					" or estado3='P' and rut_admin3=? and estado1='A' and estado2='A')"
				, new String[]{rutUsuario, rutUsuario, rutUsuario}, null, null, null);
		}
		int pendientes = cursor.getCount();
		cursor.close();
		return pendientes;
	}

	// Obtiene las solicitudes que el usuario puede ver
	public Cursor getDatoSolicitudLVL(String rut_user){

		SQLiteDatabase db = getReadableDatabase();

		return db.query(tablaSolicitud, new String[]{"*"}, "rut_admin1=? or rut_admin2=? or rut_admin3=?", new String[]{rut_user, rut_user, rut_user}, null, null, "fecha");
		//Cursor cursor = db.query(tablaSolicitud, new String[]{"*"},null,null , null, null, null);

	}

	// Obtiene las solicitudes que el usuario puede ver
	public Cursor getDatoSolicitudADMIN(){

		SQLiteDatabase db = getReadableDatabase();

		return db.query(tablaSolicitud, new String[]{"*"}, null, null, null, null, "fecha");

	}

	// Muestra las solicitudes aprobadas del mes seleccionado
	public Cursor getDatoSolicitudPorFecha(String fecha1, String fecha2){

		SQLiteDatabase db = getReadableDatabase();

		return db.query(tablaSolicitud, new String[]{"*"}, "fecha between ? and ?", new String[]{fecha1, fecha2}, null, null, "fecha");

	}

	public Cursor getDatoSolicitudDetalle(String rut, String fecha, String tipo_pacto){ //Muestra detalle
		SQLiteDatabase db = getReadableDatabase();
		return db.query(tablaSolicitud, new String[]{"*"}, "Rut=? AND fecha=? AND tipo_pacto=?", new String[]{rut, fecha, tipo_pacto}, null, null, "fecha");

	}

	// Borra todos los registros con sus respectivos where
	public boolean deleteLogError(int idLogError){
		SQLiteDatabase db = getReadableDatabase();
		long resultado = db.delete(tablaLogErrores, "id_log_errores=?", new String[]{String.valueOf(idLogError)});
		return (resultado >= 1);
	}

	// Borra TODAS las solicitudesa
//TODO BORRAR CUANDO SE APRUEBE!!! -----------------------------------------------------------------
	 /* public boolean deleteSolicitudPendientes(){
        SQLiteDatabase db = getReadableDatabase();
        long resultado = -1;
        resultado = db.delete(tablaSolicitud,null,null);
        return (resultado >= 1);
    }*/
//TODO FIN BORRAR ----------------------------------------------------------------------------------

	//TODO QUITAR COMENTARIO CUANDO SE APRUEBE------------------------------------------------------
	// Borra todas las solicitudes pendientes y deja las aprobadas y Rechazadas
	public boolean deleteSolicitudPendientes(){
		SQLiteDatabase db = getReadableDatabase();
		String rutUsuario = getRutUsuario();
		long resultado = db.delete(tablaSolicitud, "estado3='P' and rut_admin3=? and estado1='A' and estado2='A'"
				+ " or estado2='P' and rut_admin2=? and estado3='A' and estado1='A'" +
				" or estado1='P' and rut_admin1=?"
			, new String[]{rutUsuario, rutUsuario, rutUsuario});
		return (resultado >= 1);
	}
	//TODO FIN QUITAR COMENTARIO -------------------------------------------------------------------


	// Borra todos los Logs de error
	public boolean deleteErrorLogALL(){
		SQLiteDatabase db = getReadableDatabase();
		long resultado = db.delete(tablaLogErrores, null, null);
		return (resultado >= 1);
	}

	// Borra Usuario
	public boolean deleteUser(){
		SQLiteDatabase db = getReadableDatabase();
		long resultado = db.delete(tablaUsuario, null, null);
		return (resultado >= 1);
	}

	// Inserta log de error
	public boolean insertarLogError(String descripcion, String mac){
		db = getWritableDatabase();
		//   String mac= ValidacionConexion.getDireccionMAC();
		ContentValues campoValor = new ContentValues();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date date = new Date();

		campoValor.put("fecha_hora", dateFormat.format(date));
		campoValor.put("descripcion", descripcion);
		campoValor.put("version_app", context.getString(R.string.version));
		campoValor.put("mac", mac);

		long resultado = db.insertOrThrow(tablaLogErrores, null, campoValor);
		return resultado >= 1;
	}

	// Inserta Usuario
	public boolean insertarUsuario(String rut, String nombre, String mac, int isAdmin){
		db = getWritableDatabase();

		ContentValues campoValor = new ContentValues();

		campoValor.put("rut_u", rut);
		campoValor.put("nombre_u", nombre);
		campoValor.put("isadmin", isAdmin);

		try {
			long resultado = db.insertOrThrow(tablaUsuario, null, campoValor);
			return resultado >= 1;
		} catch (Exception e){
			insertarLogError("Insertar usuario arroja error " + e.getMessage() + " en MidbHelper, InsertarUsuario", mac);
		}
		return false;
	}

	//Insertar Solicitud
	public boolean insertarSolicitud(String rut, String nombre, String fecha
		, Double cant_horas, Integer monto_pagar, String motivo, String comentario
		, String centro_costo, String area, String tipo_pacto, String estado1, String rut_admin1
		, String estado2, String rut_admin2, String estado3, String rut_admin3, String mac
	){
		db = getWritableDatabase();
		ContentValues campoValor = new ContentValues();

		campoValor.put("Rut", rut);
		campoValor.put("fecha", fecha);
		campoValor.put("nombre", nombre);
		campoValor.put("cant_horas", cant_horas);
		campoValor.put("monto_pagar", monto_pagar);
		campoValor.put("motivo", motivo);
		campoValor.put("comentario", comentario);
		campoValor.put("centro_costo", centro_costo);
		campoValor.put("area", area);
		campoValor.put("tipo_pacto", tipo_pacto);
		campoValor.put("estado1", estado1);
		campoValor.put("rut_admin1", rut_admin1);
		campoValor.put("estado2", estado2);
		campoValor.put("rut_admin2", rut_admin2);
		campoValor.put("estado3", estado3);
		campoValor.put("rut_admin3", rut_admin3);
		long resultado;
		try {
			resultado = db.insertOrThrow(tablaSolicitud, null, campoValor);
			return resultado >= 1;
		} catch (Exception e){
			insertarLogError("Error de datos a insertar en MidbHelper, InsertarSolicitud. Error :" + e.getMessage(), mac);
			return false;
		}

	}

	//Actualizar estado de solicitud
	public boolean actualizarEstado(String rut, String fecha, String estado, String lvl, String tipo_pacto){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues campoValor = new ContentValues();
		campoValor.put("estado" + lvl, estado);
		long resultado = db.update(tablaSolicitud, campoValor, "Rut=? and fecha=? and tipo_pacto=?", new String[]{rut, fecha, tipo_pacto});
		return resultado >= 1;
	}

	//Boleano que confirma si la solicitud existe o no
	public boolean yaExiste(String rut, String fecha, String tipo_pacto){

		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.query(tablaSolicitud, new String[]{"*"}, "Rut=? and fecha=? and tipo_pacto=?", new String[]{rut, fecha, tipo_pacto}, null, null, null);

		if (cursor == null){
			return false;
		}
		if (cursor.getCount() > 0){
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	//Boleano que verifica si el usuario del celular es un Administrador de RRHH
	public boolean isAdmin(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(tablaUsuario, new String[]{"isadmin"}, null, null, null, null, null);
		if (cursor.moveToNext()){
			int isAdmin = cursor.getInt(cursor.getColumnIndex("isadmin"));
			if (isAdmin > 0){
				cursor.close();
				return true;
			}
		}
		cursor.close();
		return false;
	}
}
