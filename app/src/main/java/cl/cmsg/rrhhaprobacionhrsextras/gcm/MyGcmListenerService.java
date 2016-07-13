/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cl.cmsg.rrhhaprobacionhrsextras.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import cl.cmsg.rrhhaprobacionhrsextras.MainActivity;
import cl.cmsg.rrhhaprobacionhrsextras.R;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.Reciever;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService{

	private static final String TAG = "MyGcmListenerService";

	/**
	 * Called when message is received.
	 *
	 * @param from SenderID of the sender.
	 * @param data Data bundle containing message data as key/value pairs.
	 *             For Set of keys use data.keySet().
	 */
	// [START receive_message]
	@Override
	public void onMessageReceived(String from, Bundle data){
		String titulo = data.getString("title");
		String datos = data.getString("datos");
//		if (from.startsWith("/topics/")){
//			// message received from some topic.
//		} else{
//			// normal downstream message.
//		}

		// [START_EXCLUDE]
		/**
		 * Production applications would usually process the message here.
		 * Eg: - Syncing with server.
		 *     - Store message in local database.
		 *     - Update UI.
		 */

		/**
		 * In some cases it may be useful to show a notification indicating to the user
		 * that a message was received.
		 */
		sendNotification(titulo, datos);
		// [END_EXCLUDE]
	}
	// [END receive_message]

	/**
	 * Create and show a simple notification containing the received gcm message.
	 */
	private void sendNotification(String titulo, String datos){
		Intent intent = new Intent(this, MainActivity.class);
		MiDbHelper miDbHelper = MiDbHelper.getInstance(this);

		Reciever reciever = new Reciever();
		if (titulo.toLowerCase().equals("actualizacion")){
			intent.putExtra("Update", true);
		} else{
			reciever.recibirUna(datos, getApplicationContext());
		}

		int numeroSolicitudes = miDbHelper.CuentaSolicitudes();

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
			PendingIntent.FLAG_ONE_SHOT);

		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

		notificationBuilder.setSmallIcon(R.mipmap.network_clock)
			.setAutoCancel(true)
			.setSound(defaultSoundUri)
			.setContentIntent(pendingIntent);

		if (titulo.toLowerCase().equals("actualizacion")){
			notificationBuilder.setContentTitle("Actualizacion disponible")
				.setContentText("Aprobacion de Horas Extras tiene una nueva actualizacion disponible, haga click para actualizar o");
		} else{
			notificationBuilder.setContentTitle("Solicitudes Pendientes")
				.setContentText("Usted tiene " + numeroSolicitudes + " solicitudes pendientes");
		}
		NotificationManager notificationManager =
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

	}
}
