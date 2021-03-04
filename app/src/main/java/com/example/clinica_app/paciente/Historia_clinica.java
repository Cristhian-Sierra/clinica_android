package com.example.clinica_app.paciente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinica_app.R;
import com.example.clinica_app.medico.MenuMedico;
import com.example.clinica_app.medico.historialMedico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

public class Historia_clinica extends AppCompatActivity implements View.OnClickListener {

    Button btnPdf, btnVolver;
    Spinner spHistoria;
    String email = null;
    ArrayList<String> historias = new ArrayList<>();
    ArrayList<String> data = new ArrayList<>();
    ArrayAdapter<String> hisAdapter;
    int idhistoria = 0;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("datosLogin",   Context.MODE_PRIVATE);
        email = prefs.getString("correo", "");
        setContentView(R.layout.activity_historia_clinica);
        requestQueue= Volley.newRequestQueue(this);
        btnVolver = (Button) findViewById(R.id.buttonVolverP);
        btnPdf = (Button) findViewById(R.id.btn_pdf);
        spHistoria = (Spinner) findViewById(R.id.spinner_historia);
        btnPdf.setOnClickListener(this);
        btnVolver.setOnClickListener(this);

        mostrarHistorias();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonVolverP){
            Intent intent = new Intent(getApplicationContext(), MenuPaciente.class);
            startActivity(intent);
        }
        if(v.getId() == R.id.btn_pdf){
            generarPDF();
        }
    }

    public void mostrarHistorias(){
        spHistoria.setAdapter(null);
        historias.clear();
        String url = "http://192.168.0.2/clinica_service/historiaClinica/read.php?idhistoria="+email;
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray=null;
                try {
                    jsonArray = response.getJSONArray("historia");
                    for(int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id= jsonObject.optInt("idhistoria");
                        String peso = jsonObject.optString("peso");
                        String altura = jsonObject.optString("altura");
                        String motivo_consulta = jsonObject.optString("motivo_consulta");
                        historias.add(motivo_consulta);
                        String enfermedades = jsonObject.optString("enfermedades");
                        historias.add(enfermedades);
                        String alergias = jsonObject.optString("alergias");
                        String medicamentos = jsonObject.optString("medicamentos");
                        String antecedentes_personales = jsonObject.optString("antecedentes_personales");
                        String antecedentes_familiares = jsonObject.optString("antecedentes_familiares");
                        hisAdapter = new ArrayAdapter<>(Historia_clinica.this,
                                android.R.layout.simple_spinner_item,historias);
                        hisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spHistoria.setAdapter(hisAdapter);
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

    }

    public void generarPDF(){
        System.out.println(email);
        String url = "http://192.168.0.2/clinica_service/historiaClinica/historiaClinicaPDF.php?idhistoria="+email;
        Uri link = Uri.parse(url);
        Intent i = new Intent(Intent.ACTION_VIEW, link);
        startActivity(i);
    }
}