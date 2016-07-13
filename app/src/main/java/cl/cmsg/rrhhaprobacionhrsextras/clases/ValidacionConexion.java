package cl.cmsg.rrhhaprobacionhrsextras.clases;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by ocantuarias on 03-06-2016.
 */
public abstract class ValidacionConexion{

	public static boolean isExisteConexion(Activity activity){
		ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return !(networkInfo == null || !networkInfo.isConnected());
	}

	public static String getDireccionMAC(Context context){
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		String direccionMAC = info.getMacAddress();

		if (info == null || direccionMAC == null || direccionMAC.isEmpty()){
			return "";
		}

		return direccionMAC;
	}

}
