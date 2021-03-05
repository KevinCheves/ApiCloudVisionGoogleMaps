package com.appmovil.tareagooglemaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appmovil.tareagooglemaps.WebService.Asynchtask;
import com.appmovil.tareagooglemaps.WebService.WebService;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback, Asynchtask {
TextView nombre, capital, iso;
String iso2;
ImageView bandera;
    SupportMapFragment mapFragment;
    GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        Bundle b = this.getIntent().getExtras();
        iso2 = b.getString("iso");

        nombre = (TextView)findViewById(R.id.txtnombre);
        capital  = (TextView)findViewById(R.id.txtcapital);
        iso  = (TextView)findViewById(R.id.txtIso);
        bandera  = (ImageView) findViewById(R.id.bandera);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("http://www.geognos.com/api/en/countries/info/"+iso2+".json",
                datos, this, this);
        ws.execute("GET");
    }

    @Override
    public void processFinish(String result) throws JSONException {
            try {
                String atributo = "";
                JSONObject JSONpais = new JSONObject(result);
                JSONObject results = JSONpais.getJSONObject("Results");

                String nombrePais = results.getString("Name");
                JSONObject capit = results.getJSONObject("Capital");
                String capi = capit.getString("Name");
                JSONObject codigopais = results.getJSONObject("CountryCodes");
                String iso2 = codigopais.getString("iso2");
                JSONObject rec = results.getJSONObject("GeoRectangle");
                JSONArray localizacion = results.getJSONArray("GeoPt");

                nombre.setText(nombrePais);
                capital.setText(capi);
                iso.setText(iso2);
                Glide.with(this).load("http://www.geognos.com/api/en/countries/flag/"+iso2+".png").into(bandera);
                CameraUpdate ir= CameraUpdateFactory.newLatLngZoom(new LatLng(localizacion.getDouble(0),localizacion.getDouble(1)), 4);
                map.moveCamera(ir);

                PolylineOptions lineas = new PolylineOptions()
                        .add(new LatLng(rec.getDouble("North"), rec.getDouble("West")))
                        .add(new LatLng(rec.getDouble("North"), rec.getDouble("East")))
                        .add(new LatLng(rec.getDouble("South"), rec.getDouble("East")))
                        .add(new LatLng(rec.getDouble("South"), rec.getDouble("West")))
                        .add(new LatLng(rec.getDouble("North"), rec.getDouble("West")));
                lineas.width(8);
                lineas.color(Color.RED);
                map.addPolyline(lineas);



            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
    public void clickmapcarreteras(View view) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    public void clickmapsatelite(View view) {
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    public void clickmaphibrido(View view) {
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    public void clickmapatopografico(View view) {
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    public void clickzoommas(View view) {
        map.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void clickzoommenos(View view) {
        map.animateCamera(CameraUpdateFactory.zoomOut());
    }
}