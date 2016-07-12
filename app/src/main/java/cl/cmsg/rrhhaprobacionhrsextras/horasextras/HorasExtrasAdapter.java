package cl.cmsg.rrhhaprobacionhrsextras.horasextras;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cl.cmsg.rrhhaprobacionhrsextras.R;
import cl.cmsg.rrhhaprobacionhrsextras.clases.Formatos;

/**
 * Created by ocantuarias on 24-05-2016.
 */
public class HorasExtrasAdapter extends BaseAdapter implements Filterable{

    Context context;
    ArrayList<HorasExtras> arrayListHorasExtras;
    ArrayList<HorasExtras> arrayList;
    private List<String> list = new ArrayList<String>();
    ArrayList<HorasExtras> mOriginalValues;

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
        HorasExtras horasExtras = arrayListHorasExtras.get(position);

        TextView rut = (TextView) convertView.findViewById(R.id.lblRut);

        //rut.setText(horasExtras.getRut());
        rut.setText(Formatos.getNumberFormat().format(Integer.valueOf(horasExtras.getRut())));

        TextView nombre = (TextView) convertView.findViewById(R.id.lblNombre);
        nombre.setText(horasExtras.getNombre());

        TextView fecha = (TextView) convertView.findViewById(R.id.lblFecha);
        fecha.setText(Formatos.fechaFormat(horasExtras.getFecha()));

        TextView tipo_pacto = (TextView) convertView.findViewById(R.id.lblTipoPacto);

        switch (horasExtras.getTipo_pacto().toLowerCase()){
            case "h":
                tipo_pacto.setText(R.string.HORAEXTRA);
                break;
            case "t":
                tipo_pacto.setText(R.string.TRATO);
                break;
            case "f":
                tipo_pacto.setText(R.string.FESTIVO);
                break;
        }



        TextView cant_horas = (TextView) convertView.findViewById(R.id.lblCantHoras);
        cant_horas.setText(horasExtras.getCant_horas());

        LinearLayout linearLayout= (LinearLayout) convertView.findViewById(R.id.layoutFondoLista);
        horasExtras.setLayout(linearLayout);


        return convertView;

    }

    @Override
    public Filter getFilter() {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,FilterResults results) {
                    arrayListHorasExtras = (ArrayList<HorasExtras>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<HorasExtras> FilteredArrList = new ArrayList<HorasExtras>();

                    if (mOriginalValues == null) {
                        mOriginalValues = arrayListHorasExtras; // saves the original data in mOriginalValues
                    }

                    /********
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        //Log.e("Omar", constraint.toString());
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            HorasExtras data = mOriginalValues.get(i);
                           // Log.e("Omar", data.getNombre());

                            if (data.getRut().toLowerCase().contains(constraint.toString())) {
                                FilteredArrList.add(data);
                                continue;
                            }
                            if (data.getFecha().toLowerCase().contains(constraint.toString())) {
                                FilteredArrList.add(data);
                                continue;
                            }
                            if (data.getCant_horas().toLowerCase().contains(constraint.toString())) {
                                FilteredArrList.add(data);
                                continue;
                            }
                            if (data.getNombre().toLowerCase().contains(constraint.toString())) {
                                FilteredArrList.add(data);

                            }
                            if (data.getTipo_pacto().toLowerCase().contains(constraint.toString())) {
                                FilteredArrList.add(data);

                            }
                        }
                        // set the Filtered result to return
                       results.values = FilteredArrList;
                        results.count = FilteredArrList.size();
                    }
                    return results;
                }
            };
            return filter;
        }

}
