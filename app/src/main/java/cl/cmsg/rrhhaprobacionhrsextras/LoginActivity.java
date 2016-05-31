package cl.cmsg.rrhhaprobacionhrsextras;

import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
        //miDbHelper.insertarUsuario("16841244-4","Omar Cantuarias");

        // Borrar solicitudes guardadas para agregar/actualizar
        miDbHelper.deleteSolicitudALL();

        if(!miDbHelper.getRutUsuario().equals("")){
            Toast.makeText(getApplicationContext(),"Registrado", Toast.LENGTH_SHORT).show();
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
