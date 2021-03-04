package com.example.clinica_app.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinica_app.R;
import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Registrar_medico extends AppCompatActivity implements View.OnClickListener{

    EditText txtNombre, txtApellido, txtFechaNac, txtCorreo, txtClave, txtTarjetaP, txtPregunta, txtRespuesta;
    Spinner spnEspecialidad;
    Button btnRegistrar, btnFechaNac;
    int dd,mm,aa;
    int idEspecialidad;
    ArrayList<String> especialidad = new ArrayList<>();
    ArrayAdapter<String> espAdapter;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar__medico);
        requestQueue=Volley.newRequestQueue(this);
        txtNombre = (EditText) findViewById(R.id.editTextNombreMe);
        txtApellido = (EditText) findViewById(R.id.editTextApellidoMe);
        txtFechaNac = (EditText) findViewById(R.id.editTextDateMe);
        txtCorreo = (EditText) findViewById(R.id.editTextCorreoMe);
        txtClave = (EditText) findViewById(R.id.editTextClaveMe);
        txtTarjetaP = (EditText) findViewById(R.id.editTextTarjetaMe);
        txtPregunta = (EditText) findViewById(R.id.edtPreguntaM);
        txtRespuesta = (EditText) findViewById(R.id.edtRespuestaM);
        spnEspecialidad = (Spinner) findViewById(R.id.spinnerEspecialidadMe);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrarMedico);
        btnFechaNac = (Button) findViewById(R.id.btnFechaNMe);
        txtFechaNac.setOnClickListener(this);
        btnRegistrar.setOnClickListener(this);
        btnFechaNac.setOnClickListener(this);

        mostrarEspecialidad();
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.btnRegistrarMedico){
            String con = "http://192.168.0.2/clinica_service/medico/consultarTotalRegistros.php";
            StringRequest stringRequest= new StringRequest(Request.Method.GET, con, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String op= response.toString();
                            String opcion=op.trim();
                            int ops=Integer.parseInt(opcion);
                            if (ops < 10){
                                RegistrarMedico("http://192.168.0.2/clinica_service/medico/create.php");
                            }else{
                                Toast.makeText(getApplicationContext(), "SE HA SUPERADO LA CANTIDAD DE MEDICOS REGISTRADOS", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String,String>getParams() throws AuthFailureError{
                            Map<String,String> parametros= new HashMap<String, String>();
                            return parametros;
                        }
                    };

            requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        } else if(id == R.id.btnFechaN){

            final Calendar calendar =Calendar.getInstance();
            dd=calendar.get(Calendar.DAY_OF_MONTH);
            mm=calendar.get(Calendar.MONTH);
            aa=calendar.get(Calendar.YEAR);


            DatePickerDialog datePickerDialog= new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    txtFechaNac.setText(year+"-" + (month)+1+ "-"+dayOfMonth);
                }
            },aa,mm,dd);
            datePickerDialog.show();

        }


    }

    public void mostrarEspecialidad(){
        spnEspecialidad.setAdapter(null);
        especialidad.clear();
        String url = "http://192.168.0.2/clinica_service/especialidad/readAll.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = response.getJSONArray("especialidad");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.optInt("idEespecialidad");
                        String nombre = jsonObject.optString("nombre");
                        especialidad.add(nombre);
                        espAdapter = new ArrayAdapter<>(Registrar_medico.this, android.R.layout.simple_spinner_dropdown_item);
                        spnEspecialidad.setAdapter(espAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
        spnEspecialidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idEspecialidad = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                idEspecialidad = 0;
            }
        });
    }

    public void RegistrarMedico(String url){
        if (txtNombre.getText().toString().isEmpty()){
            txtNombre.setError("CAMPO VACIO");
        }else if (txtApellido.getText().toString().isEmpty()){
            txtApellido.setError("CAMPO VACIO");
        }else if (txtFechaNac.getText().toString().isEmpty()){
            txtFechaNac.setError("CAMPO VACIO");
        }else if (txtCorreo.getText().toString().isEmpty()){
            txtCorreo.setError("CAMPO VACIO");
        }else if (txtClave.getText().toString().isEmpty()){
            txtClave.setError("CAMPO VACIO");
        }else if(txtTarjetaP.getText().toString().isEmpty()){
            txtTarjetaP.setError("CAMPO VACIO");
        }else if (txtPregunta.getText().toString().isEmpty()){
            txtPregunta.setError("CAMPO VACIO");
        }else if (txtRespuesta.getText().toString().isEmpty()){
            txtRespuesta.setError("CAMPO VACIO");
        }else if (idEspecialidad == 0){
            Toast.makeText(getApplicationContext(), "NO SE HA ESCOGIDO ESPECIALIDAD", Toast.LENGTH_SHORT).show();
        }
        else{
            StringRequest stringRequest=
                    new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(), "SE HA REGISTRADO SATISFACTORIAMENTE", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String,String> getParams() throws AuthFailureError {
                            Map<String,String> parametros= new HashMap<String, String>();
                            parametros.put("nombre",txtNombre.getText().toString());
                            parametros.put("apellido",txtApellido.getText().toString());
                            parametros.put("fecha_nacimiento",txtFechaNac.getText().toString());
                            parametros.put("correo",txtCorreo.getText().toString());
                            parametros.put("clave",txtClave.getText().toString());
                            parametros.put("tarjetaprofesional",txtTarjetaP.getText().toString());
                            parametros.put("especialidad_idespecialidad", Integer.toString(idEspecialidad));
                            parametros.put("pregunta",txtPregunta.getText().toString());
                            parametros.put("respuesta",txtRespuesta.getText().toString());
                            return parametros;
                        }
                    };
            requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }
}