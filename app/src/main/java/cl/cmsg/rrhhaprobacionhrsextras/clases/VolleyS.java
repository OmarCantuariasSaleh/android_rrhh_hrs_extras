package cl.cmsg.rrhhaprobacionhrsextras.clases;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ocantuarias on 01-06-2016.
 */
public class VolleyS {

    private static VolleyS mVolleyS = null;
    private static Context context = null;
    //Este objeto es la cola que usará la aplicación
    private RequestQueue mRequestQueue;

    private VolleyS(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static VolleyS getInstance(Context contexto) {
        if (mVolleyS == null) {
            mVolleyS = new VolleyS(contexto);
            context = contexto;
        }
        return mVolleyS;
    }

    public void cancelAll(){
        mRequestQueue.cancelAll(context);
    }

    public void addToQueue(Request request, Context context){
        if (request == null){
            return;
        }
        request.setTag(context);
        if (mRequestQueue == null){
            mRequestQueue = getRequestQueue();
        }
        request.setRetryPolicy(new DefaultRetryPolicy(
                6000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue.add(request);
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void cancelPendingRequests(){
        if (mRequestQueue != null){
            mRequestQueue.cancelAll(this);
        }
    }

}
