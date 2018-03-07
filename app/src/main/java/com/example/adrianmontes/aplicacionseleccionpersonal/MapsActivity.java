package com.example.adrianmontes.aplicacionseleccionpersonal;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrianmontes.aplicacionseleccionpersonal.Modelo.Ciudad;
import com.example.adrianmontes.aplicacionseleccionpersonal.ViewModel.CityViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//Vamos a acceder a la informacion de las coordenadas de una ciudad que cojemos en un Json
//Con esas coordenadas accedemos a otra web que nos devuelve otro JSON con su tiempo
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private EditText edtCiudad;
    private Button btnBuscar;
    private GoogleMap mMap;
    TextView campodeTexto;

    //Este constructor pertenece al ViewModel y es un ejemplo para ver como cada vez que hacemos un cambio de pantalla
    //O dejamos la APP en segundo plano los datos los seguimos teniendo en ViewModel, de esta manera no tenemos que volver
    //a pedir los datos al servidor, en el metodo onChange cada vez que hay un cambio en los datos que guardamos
    //Nos lo devuelve en ese metodo
    public MapsActivity() {
        CityViewModel model= ViewModelProviders.of(this).get(CityViewModel.class);
        //Con esta clase Obserbo los cambios que hay en el contador que cree en la otra clase, que es el escuchador
        //Se va a ejecutar cada vez que aya un cambio
        Observer<Integer> counterObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                campodeTexto.setText(integer+"");
                campodeTexto.setText(integer+"");
            }
        };
        //De esta manera accedemos a los datos del ViewModel cada vez que queramos
        model.getmContador().observe(this,counterObserver);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        edtCiudad = findViewById(R.id.edtCiudad);
        campodeTexto=findViewById(R.id.TextoComprobar);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    public void MetodoBoton(View view) {

        if (edtCiudad.getText().toString().isEmpty()) {

            Toast.makeText(getApplication(), "Introduzca una ciudad para buscar", Toast.LENGTH_LONG);

        } else {
            //Ahora llamamos al Hilo y le pasamos la ciudad en cojemos del campo de texto para buscarla
            new ObtenerGeonames().execute(edtCiudad.getText().toString());
        }

    }





    //El primer valor el lo que le pasamos, el segundo es lo que nos devuelve mientras se va ejecutando, y el tercero
    //Es lo que va a devolvernos
    public class ObtenerGeonames extends AsyncTask<String, Void, Ciudad> {

        //Despues de la ejecucion tenemos los datos que recojimos del Json en la calse ciudad
        //y recojemos la latitud y longitud
        @Override
        protected void onPostExecute(Ciudad ciudad) {
            if (ciudad != null) {

                // Add a marker in Sydney and move the camera
                LatLng Markciudad = new LatLng(ciudad.getLatitude(), ciudad.getLongitude());
                mMap.addMarker(new MarkerOptions().position(Markciudad).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(Markciudad));


                new GetTemperature().execute(ciudad);

            } else {

                Toast.makeText(getApplication(), "Ciudad no encontrada", Toast.LENGTH_LONG);

            }

        }



        //Este metodo Devuelve un objeto ciudad con toda la informacion obtenida de la API
        @Override
        protected Ciudad doInBackground(String... strings) {
            Ciudad ciudad = new Ciudad();
            try {
                //hacemos la conexion con el JSON y recojemos los datos en un Json nuestro
                URL url = new URL("http://api.geonames.org/searchJSON?q="+strings+"&maxRows=20&startRow=0&lang%20=en&isNameRequired=true&style=FULL&username=ilgeonamessample");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    StringBuilder linea = new StringBuilder();
                    while ((line = reader.readLine()) != null) {

                        linea.append(line);
                    }

                    //Aqui obetenemos el Json que recojimos de la URL
                    //Para poder acceder por el Json cada etiqueta es un array y vamos entrando por sus arboles que son
                    //Arrays
                    JSONObject responseJSON = new JSONObject(linea.toString());
                    //Aqui accedemos a la primera posicion
                    JSONArray geonames = responseJSON.getJSONArray("geonames");
                    //Ahora accedemos a la primera posicion del objeto geonames
                    JSONObject cityJSON = geonames.getJSONObject(0);
                    ciudad = new Ciudad();
                    //Ahora metemos en la clase que creamos ciudad todos los valores que recojimos del JSON
                    ciudad.setName(cityJSON.getString("name"));
                    ciudad.setRegion(cityJSON.getString("continentCode"));
                    ciudad.setLatitude(cityJSON.getDouble("lat"));
                    ciudad.setLongitude(cityJSON.getDouble("lng"));
                    //ahora accedo a los parametros de bbox y recojo los valores que tiene en su interior
                    JSONArray ObjetoBbox=cityJSON.getJSONArray("bbox");
                    JSONObject ObjetoJSON=ObjetoBbox.getJSONObject(0);
                    ciudad.setEast(ObjetoJSON.getString("east"));
                    ciudad.setSouth(ObjetoJSON.getString("south"));
                    ciudad.setNorth(ObjetoJSON.getString("north"));
                    ciudad.setWest(ObjetoJSON.getString("west"));



                    return ciudad;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class GetTemperature extends AsyncTask<Ciudad, Void, String> {
        EditText edtTemperaturas;
        @Override
        protected void onPostExecute(String ciudad) {
            super.onPostExecute(ciudad);


            edtTemperaturas=findViewById(R.id.edtTiempoCiudad);
            edtTemperaturas.setText(ciudad);



        }

        @Override
        protected String doInBackground(Ciudad... ciudad) {

            //hacemos la conexion con el JSON y recojemos los datos en un Json nuestro

            try {





                URL url = new URL( "http://api.geonames.org/weatherJSON?north="+ciudad[0].getNorth()+"&south="+ciudad[0].getSouth()+"&east="+ciudad[0].getSouth()+"&west="+ciudad[0].getWest()+"&username=ilgeonamessample");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    StringBuilder linea = new StringBuilder();
                    while ((line = reader.readLine()) != null) {

                        linea.append(line);
                    }

                    //Aqui obetenemos el Json que recojimos de la URL
                    //Para poder acceder por el Json cada etiquea es un array y vamos entrando por sus arboles que son
                    //Arrays
                    JSONObject responseJSON = new JSONObject(linea.toString());
                    JSONArray weatherObservations = responseJSON.getJSONArray("weatherObservations");
                    JSONObject cityJSON = weatherObservations.getJSONObject(0);
                    String temperatura=cityJSON.getString("temperature");

                    return temperatura;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}


