package cl.cmsg.rrhhaprobacionhrsextras;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    TextView lblComentario;
    TextView lblCentroCosto;
    TextView lblArea;
    TextView lblTipoPacto;
    MiDbHelper miDbHelper;
    LinearLayout layoutBotones;
    Button btnAprobar;
    Button btnRechazar;
    String lvl="1";

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
        lblComentario = (TextView) findViewById(R.id.lblComentario);
        lblCentroCosto = (TextView) findViewById(R.id.lblCentroCosto);
        lblArea = (TextView) findViewById(R.id.lblArea);
        lblTipoPacto = (TextView) findViewById(R.id.lblTipoPacto);
        layoutBotones = (LinearLayout) findViewById(R.id.layoutBotones);
        btnAprobar = (Button) findViewById(R.id.btnAprobar);
        btnRechazar = (Button) findViewById(R.id.btnRechazar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle= getIntent().getExtras();
        miDbHelper = MiDbHelper.getInstance(this);
        final Cursor cursor =   miDbHelper.getDatoSolicitudDetalle(bundle.getString("Rut",""),bundle.getString("fecha",""));
        String rut;
        String nombre;
        String fecha;
        int cant_horas;
        int monto_pagar;
        String motivo;
        String comentario;
        String centro_costo;
        String area;
        String tipo_pacto;
        String estado1;
        String estado2;
        String estado3;

        while(cursor.moveToNext()){
            rut = cursor.getString(cursor.getColumnIndex("Rut"));
            lblRut.setText(lblRut.getText().toString() + " " +rut);

            nombre = cursor.getString(cursor.getColumnIndex("nombre"));
            lblNombre.setText(lblNombre.getText().toString() + " " +nombre);

            fecha = cursor.getString(cursor.getColumnIndex("fecha"));
            lblFecha.setText(lblFecha.getText().toString() + " " +fecha);

            cant_horas = (cursor.getInt(cursor.getColumnIndex("cant_horas")));
            lblCantHoras.setText(lblCantHoras.getText().toString() + " " +cant_horas);

            monto_pagar = cursor.getInt(cursor.getColumnIndex("monto_pagar"));
            lblMontoPagar.setText(lblMontoPagar.getText().toString()+monto_pagar);

            motivo = cursor.getString(cursor.getColumnIndex("motivo"));
            lblMotivo.setText(lblMotivo.getText().toString() + " " +motivo);

            comentario = cursor.getString(cursor.getColumnIndex("comentario"));
            lblComentario.setText(comentario);

            centro_costo = cursor.getString(cursor.getColumnIndex("centro_costo"));
            lblCentroCosto.setText(lblCentroCosto.getText().toString() + " " +centro_costo);

            area = cursor.getString(cursor.getColumnIndex("area"));
            lblArea.setText(lblArea.getText().toString() + " " +area);

            tipo_pacto = cursor.getString(cursor.getColumnIndex("tipo_pacto"));
            lblTipoPacto.setText(lblTipoPacto.getText().toString() + " " +tipo_pacto);

            estado1= cursor.getString(cursor.getColumnIndex("estado1"));
            estado2= cursor.getString(cursor.getColumnIndex("estado2"));
            estado3= cursor.getString(cursor.getColumnIndex("estado3"));

            // Verificar si detalle es de solicitud aprobada o pendiente, separadas por nivel
            lvl="1";
            if(estado2==null){      //Si estado2 esta vacio es lvl 1
                if(estado1.equals("A")){
                    layoutBotones.setVisibility(View.INVISIBLE);
                }else if (estado1.equals("P")){
                    layoutBotones.setVisibility(View.VISIBLE);
                }
            }else if(estado3==null){ //Si estado3 esta vacio es lvl 2
                lvl="2";
                if(estado2.equals("A")){
                    layoutBotones.setVisibility(View.INVISIBLE);
                }else if (estado2.equals("P")){
                    layoutBotones.setVisibility(View.VISIBLE);
                }
            }else{  //Si estado3 no es vacio es lvl 3
                lvl="3";
                if(estado3.equals("A")){
                    layoutBotones.setVisibility(View.INVISIBLE);
                }else if (estado3.equals("P")){
                    layoutBotones.setVisibility(View.VISIBLE);
                }
            }
            break;
        }

        btnAprobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rutA= cursor.getString(cursor.getColumnIndex("Rut"));
                String fechaA= cursor.getString(cursor.getColumnIndex("fecha"));
                String estadoA= "A";

                MiDbHelper.getInstance(getApplicationContext());
                miDbHelper.actualizarEstado(rutA,fechaA,estadoA,lvl);

                Intent intent = new Intent(getApplicationContext(),HorasPendientesActivity.class);
                cursor.close();
                startActivity(intent);
            }
        });

        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rutR= cursor.getString(cursor.getColumnIndex("Rut"));
                String fechaR= cursor.getString(cursor.getColumnIndex("fecha"));
                String estadoR= "R";

                MiDbHelper.getInstance(getApplicationContext());
                miDbHelper.actualizarEstado(rutR,fechaR,estadoR,lvl);

                Intent intent = new Intent(getApplicationContext(),HorasPendientesActivity.class);
                cursor.close();
                startActivity(intent);
            }
        });

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
