package com.example.mascotagoogle;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //Variables
    private EditText txtCodigo, txtNombre, txtDueño, txtDireccion;
    private ListView lista;
    private Spinner spMascota;

    //Variable conexion
    private FirebaseFirestore db;
    //Datos spinner
    String[] TiposMascotas ={"Perro","Gato","Pajaro"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializar Firestore
        db =FirebaseFirestore.getInstance();


        //Unir variables con vistas del XML
        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        txtDueño = findViewById(R.id.txtDueño);
        txtDireccion = findViewById(R.id.txtDireccion);
        spMascota = findViewById(R.id.spMascota);
        lista = findViewById(R.id.lista);

        //Poblar spinner de las mascotas
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TiposMascotas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMascota.setAdapter(adapter);
    }
    //Metodo Enviar Datos
    public void enviarDatosFirestore(View view) {
        //Obtener datos de vista
        String codigo = txtCodigo.getText().toString();
        String nombre = txtNombre.getText().toString();
        String dueño = txtDueño.getText().toString();
        String direccion = txtDireccion.getText().toString();
        String tipoMascota = spMascota.getSelectedItem().toString();

        //Creamos mapa
        Map<String, Object> mascota = new HashMap<>();
        mascota.put("codigo", codigo);
        mascota.put("nombre", nombre);
        mascota.put("dueño", dueño);
        mascota.put("direccion", direccion);
        mascota.put("tipoMascota", tipoMascota);

        //Enviamos los datos a Firebase
        db.collection("mascotasgoole").document(codigo).set(mascota).addOnSuccessListener(aVoid -> {
            Toast.makeText(MainActivity.this, "Datos enviados a la firestore Correctamente", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "Error al anviar datos a Firestore" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }
    //Boton cargar lista
    public void CargarLista(View view){
        CargarListaFirestore();
    }
    //Metodo Cargar Lista
    public void CargarListaFirestore() {
        //Obtener instancia de firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Hacemos una consulta
        db.collection("mascotasgoogle").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    //Si la consulta es exitosa, procesara los documentos obtenidos
                    //Creando una lista para almacenar las cadenas de informacion de mascotas
                    List<String> listaMascotas = new ArrayList<>();

                    //Recorrer todos los datos obtenidos ordenandolos en una lista
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String linea = "||" + document.getString("codigo")+"||" + document.getString("nombre")+"||" + document.getString("dueño")+"||" + document.getString("direccion");
                        listaMascotas.add(linea);
                    }
                    //Crear un Adapter con la lista de mascotas
                    //Y establece el adaptador en el ListView
                    ArrayAdapter<String> adaptador = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, listaMascotas);
                    lista.setAdapter(adaptador);
                }else {
                    //Se imprimira en consola si hay errores al traer los datos
                    Log.e("TAG","Error al obtener datos de Firestone",task.getException());
                }
            }
        });
    }
}