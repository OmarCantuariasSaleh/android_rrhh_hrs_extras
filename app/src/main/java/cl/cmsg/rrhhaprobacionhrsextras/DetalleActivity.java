package cl.cmsg.rrhhaprobacionhrsextras;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    LinearLayout layoutBotones;
    Button btnAprobar;
    Button btnRechazar;

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
        layoutBotones = (LinearLayout) findViewById(R.id.layoutBotones);
        btnAprobar = (Button) findViewById(R.id.btnAprobar);
        btnRechazar = (Button) findViewById(R.id.btnRechazar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle= getIntent().getExtras();
        miDbHelper = MiDbHelper.getInstance(this);
        final Cursor cursor =   miDbHelper.getDatoSolicitudDetalle(bundle.getString("rut",""),bundle.getString("fecha",""));
        String rut;
        String nombre;
        String fecha;
        Integer cant_horas;
        Integer monto_pagar;
        String motivo;
        String centro_costo;
        String area;
        String estado;

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

            estado= cursor.getString(cursor.getColumnIndex("estado"));
            if(estado=="A"){
                layoutBotones.setVisibility(View.INVISIBLE);
            }else if (estado=="P"){
                layoutBotones.setVisibility(View.VISIBLE);
            }
            lblEstado.setText(estado);
            break;
        }

        btnAprobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rutA= cursor.getString(cursor.getColumnIndex("rut"));
                String fechaA= cursor.getString(cursor.getColumnIndex("fecha"));
                String estadoA= "A";

                MiDbHelper.getInstance(getApplicationContext());
                miDbHelper.actualizarEstado(rutA,fechaA,estadoA);

                Intent intent = new Intent(getApplicationContext(),HorasPendientesActivity.class);
                startActivity(intent);
            }
        });

        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rutR= cursor.getString(cursor.getColumnIndex("rut"));
                String fechaR= cursor.getString(cursor.getColumnIndex("fecha"));
                String estadoR= "R";

                MiDbHelper.getInstance(getApplicationContext());
                miDbHelper.actualizarEstado(rutR,fechaR,estadoR);

                Intent intent = new Intent(getApplicationContext(),HorasPendientesActivity.class);
                startActivity(intent);
            }
        });

        //lblRut.setText(lblRut.getText().toString() + "" + );
        //lblRut.setText(lblRut.getText().toString() + "" + bundle.getString("fecha",""));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
