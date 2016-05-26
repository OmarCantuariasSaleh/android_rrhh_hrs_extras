package cl.cmsg.rrhhaprobacionhrsextras;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;

public class DetalleActivity extends AppCompatActivity {

    TextView lblRut;
    MiDbHelper miDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        lblRut = (TextView) findViewById(R.id.lblRut);
        Bundle bundle= getIntent().getExtras();
        miDbHelper = MiDbHelper.getInstance(this);
        Cursor cursor =   miDbHelper.getDatoSolicitud(bundle.getString("rut",""),bundle.getString("fecha",""));
        String rut;

        while(cursor.moveToNext()){



        }

        //lblRut.setText(lblRut.getText().toString() + "" + );
        //lblRut.setText(lblRut.getText().toString() + "" + bundle.getString("fecha",""));

    }
}
