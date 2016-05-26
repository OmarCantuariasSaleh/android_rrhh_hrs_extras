package cl.cmsg.rrhhaprobacionhrsextras;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;

public class DetalleActivity extends AppCompatActivity {

    TextView lblRut;
    TextView lblNombre;
    TextView lblFecha;
    TextView lblCantHoras;
    TextView lblMontoPagar;
    TextView lblMotivo;
    TextView lblCentroCosto;
    TextView lblArea;
    TextView lblEstado;
    MiDbHelper miDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        lblRut = (TextView) findViewById(R.id.lblRut);
        lblNombre = (TextView) findViewById(R.id.lblNombre);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        lblCantHoras = (TextView) findViewById(R.id.lblCantHoras);
        lblMontoPagar = (TextView) findViewById(R.id.lblMonto);
        lblMotivo = (TextView) findViewById(R.id.lblMotivo);
        lblCentroCosto = (TextView) findViewById(R.id.lblCentroCosto);
        lblArea = (TextView) findViewById(R.id.lblArea);
        lblEstado = (TextView) findViewById(R.id.lblEstado);

        Bundle bundle= getIntent().getExtras();
        miDbHelper = MiDbHelper.getInstance(this);
        Cursor cursor =   miDbHelper.getDatoSolicitud(bundle.getString("rut",""),bundle.getString("fecha",""));
        String rut;
        String nombre;
        String fecha;
        Integer cant_horas;
        Integer monto_pagar;
        String motivo;
        String centro_costo;
        String area;
        Integer estado;

        while(cursor.moveToNext()){
            rut= cursor.getString(cursor.getColumnIndex("rut"));
            lblRut.setText(lblRut.getText().toString() + " " +rut);

            nombre= cursor.getString(cursor.getColumnIndex("nombre"));
            lblNombre.setText(lblNombre.getText().toString() + " " +nombre);

            fecha= cursor.getString(cursor.getColumnIndex("fecha"));
            lblFecha.setText(lblFecha.getText().toString() + " " +fecha);

            cant_horas= (cursor.getInt(cursor.getColumnIndex("cant_horas")));
            lblCantHoras.setText(lblCantHoras.getText().toString() + " " +cant_horas);

            monto_pagar= cursor.getInt(cursor.getColumnIndex("monto_pagar"));
            lblMontoPagar.setText(lblMontoPagar.getText().toString() + " " +monto_pagar);

            motivo= cursor.getString(cursor.getColumnIndex("motivo"));
            lblMotivo.setText(lblMotivo.getText().toString() + " " +motivo);

            centro_costo= cursor.getString(cursor.getColumnIndex("centro_costo"));
            lblCentroCosto.setText(lblCentroCosto.getText().toString() + " " +centro_costo);

            area= cursor.getString(cursor.getColumnIndex("area"));
            lblArea.setText(lblArea.getText().toString() + " " +area);

            estado= cursor.getInt(cursor.getColumnIndex("estado"));
            lblEstado.setText(estado.toString());
            break;
        }

        //lblRut.setText(lblRut.getText().toString() + "" + );
        //lblRut.setText(lblRut.getText().toString() + "" + bundle.getString("fecha",""));

    }
}
