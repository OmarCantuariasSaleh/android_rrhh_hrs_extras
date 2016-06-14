package cl.cmsg.rrhhaprobacionhrsextras.horasextras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cl.cmsg.rrhhaprobacionhrsextras.R;

/**
 * Created by ocantuarias on 24-05-2016.
 */
public class HorasExtrasAdapter extends BaseAdapter{

    Context context;
    ArrayList<HorasExtras> arrayListHorasExtras;


    public HorasExtrasAdapter(ArrayList<HorasExtras> arrayListHorasExtras, Context context) {
        super();
        this.arrayListHorasExtras = arrayListHorasExtras;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayListHorasExtras.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListHorasExtras.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_horas_pendientes_bkp, null);
        }
        //Proveedor proveedorInformacion = proveedor.get(position);
        HorasExtras horasExtras = arrayListHorasExtras.get(position);

        TextView rut = (TextView) convertView.findViewById(R.id.lblRut);
        rut.setText(horasExtras.getRut());

        TextView nombre = (TextView) convertView.findViewById(R.id.lblNombre);
        nombre.setText(horasExtras.getNombre());

        TextView fecha = (TextView) convertView.findViewById(R.id.lblFecha);
        fecha.setText(horasExtras.getFecha());

        TextView tipo_pacto = (TextView) convertView.findViewById(R.id.lblTipoPacto);
        tipo_pacto.setText(horasExtras.getTipo_pacto());

        TextView cant_horas = (TextView) convertView.findViewById(R.id.lblCantHoras);
        cant_horas.setText(horasExtras.getCant_horas());

        LinearLayout linearLayout= (LinearLayout) convertView.findViewById(R.id.layoutFondoLista);
        horasExtras.setLayout(linearLayout);

        /*TextView textView = (TextView) convertView.findViewById(R.id.lblRut);
        horasExtras.setTextView(textView);*/

        //Log.e("Omar","Tipo de Pacto: "+horasExtras.getTipo_pacto() );
        return convertView;

       // return null;
    }

}
