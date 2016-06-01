package cl.cmsg.rrhhaprobacionhrsextras;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cl.cmsg.rrhhaprobacionhrsextras.clases.MiDbHelper;
import cl.cmsg.rrhhaprobacionhrsextras.clases.Rut;

public class LoginActivity extends AppCompatActivity {

    Button button;
    MiDbHelper miDbHelper;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        button = (Button) findViewById(R.id.buttonOk);
        editText = (EditText) findViewById(R.id.editText);
        miDbHelper = MiDbHelper.getInstance(this);

// TODO Borrar cuando entre a produccion
        // Borrar solicitudes guardadas para agregar/actualizar
        miDbHelper.deleteSolicitudALL();

        // Solicitud lvl 3 Pendiente --------------------------------------------------
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-02-02",8,1222000,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","A","123412341-1","P","16841244-4");
        miDbHelper.insertarSolicitud("11111111-1"
                ,"Persona Mcperson"
                ,"2014-01-10"
                ,6
                ,100300
                ,"Retraso"
                ,"Atraso en avance de proyecto que era para ayer"
                ,"Informatica"
                ,"Informatica"
                ,"Festivo"
                ,"A"
                ,"12312312-1"
                ,"A"
                ,"123412341-1"
                ,"P"
                ,"16841244-4");
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-01-11",6,100300,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","A","123412341-1","P","16841244-4");
        miDbHelper.insertarSolicitud("14444111-1","Persona Mcperson","2014-01-10",6,100300,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","16841244-4",null,null,null,null);

        // Solicitud para otro lvl 3
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-01-05",6,105000,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","A","123412341-1","P","12222244-4");

        // Solicitud lvl 2 Pendiente --------------------------------------------------
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-01-07",6,106600,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","P","123412341-1",null,null);
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-01-03",6,100000,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","A","123412341-1",null,null);
        miDbHelper.insertarSolicitud("11111111-1","Persona Mcperson","2014-01-30",6,100000,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1","P","16841244-4",null,null);

        // Solicitud lvl 1 pendiente --------------------------------------------------
        miDbHelper.insertarSolicitud("11222111-1","Persona Mcperson","2014-01-10",6,100000,
                "Retraso","Atraso en avance de proyecto que era para ayer"
                ,"Informatica","Informatica","Horas Extra"
                ,"A","12312312-1",null,null,null,null);
//TODO Borrar cuando entre a produccion


        if(!miDbHelper.getRutUsuario().equals("")){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Button ok", Toast.LENGTH_SHORT).show();
                String rut = editText.getText().toString();

                if (Rut.isRutValido(rut)){
                    Toast.makeText(getApplicationContext(),"Rut valido", Toast.LENGTH_SHORT).show();
                    if(miDbHelper.getRutUsuario().equals(rut)){
                        Toast.makeText(getApplicationContext(),"Registrado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);

                    }

                }else{
                    Toast.makeText(getApplicationContext(),"rut invalido", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
