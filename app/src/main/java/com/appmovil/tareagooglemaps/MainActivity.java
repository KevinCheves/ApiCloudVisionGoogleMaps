package com.appmovil.tareagooglemaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelStore;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appmovil.tareagooglemaps.WebService.Asynchtask;
import com.appmovil.tareagooglemaps.WebService.WebService;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {
    FloatingActionMenu menuVistas, menuZoom;
    SupportMapFragment mapFragment;
    GoogleMap map;
    EditText lati, longi;
    RadioButton rblineas, rbpoly, rbinfo;
    int ban = 1, ban2 = 1;
    PolylineOptions lineas;
    PolygonOptions poly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuVistas = (FloatingActionMenu) findViewById(R.id.menuVistas);
        menuZoom = (FloatingActionMenu) findViewById(R.id.menuZOOM);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Snackbar.make(findViewById(R.id.frmlayout), "Mantener precionado para agregar marcador", Snackbar.LENGTH_SHORT).show();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerDragListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMapClickListener(this);

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        marker.hideInfoWindow();
        marker.showInfoWindow();
        marker.setTitle("Lat: " + marker.getPosition().latitude + " Lng: " + marker.getPosition().longitude);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        marker.hideInfoWindow();
        marker.showInfoWindow();
        marker.setTitle("Lat: " + marker.getPosition().latitude + " Lng: " + marker.getPosition().longitude);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        LatLng punto = new LatLng(point.latitude, point.longitude);
        map.addMarker(new MarkerOptions().position(punto).title("Lat: " + point.latitude + " " + "Lng: " + point.longitude).draggable(true));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        menuVistas.close(true);
        menuZoom.close(true);
        if (rblineas.isChecked()) {
            ban2 = 1;
            rbinfo.setEnabled(false);
            rbpoly.setEnabled(false);
            switch (ban) {
                case 1:
                    lineas = new PolylineOptions();
                    lineas.add(new LatLng(latLng.latitude, latLng.longitude));
                    lineas.width(8);
                    lineas.color(Color.RED);
                    map.addPolyline(lineas);
                    ban = 2;
                    break;
                case 2:
                    lineas.add(new LatLng(latLng.latitude, latLng.longitude));
                    map.addPolyline(lineas);
                    ban = 1;
                    rbinfo.setEnabled(true);
                    rbpoly.setEnabled(true);
                    break;
                default:
                    ban = 1;
                    break;
            }
        }
        if (rbpoly.isChecked()) {
            rbinfo.setEnabled(false);
            rblineas.setEnabled(false);
            switch (ban2) {
                case 1:
                    poly = new PolygonOptions();
                    poly.add(new LatLng(latLng.latitude, latLng.longitude));
                    poly.fillColor(Color.BLUE);
                    poly.strokeColor(Color.RED);
                    map.addPolygon(poly);
                    ban2 = 2;
                    break;
                default:
                    poly.add(new LatLng(latLng.latitude, latLng.longitude));
                    map.addPolygon(poly);
                    rbinfo.setEnabled(true);
                    rblineas.setEnabled(true);
                    ban2++;
                    break;
            }
        }
        if (rbinfo.isChecked()) {
            ban2 = 1;
            Projection proj = map.getProjection();
            Point coord = proj.toScreenLocation(latLng);
            Snackbar.make(findViewById(R.id.frmlayout), "Lat: " + latLng.latitude + " Lng: " + latLng.longitude + "\n X: " + coord.x + " Y: " + coord.y, Snackbar.LENGTH_SHORT).show();
        }
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

    public void clickir(View view) {
        Float latit = Float.parseFloat(lati.getText().toString());
        Float longit = Float.parseFloat(longi.getText().toString());
        float zo = (float) 13.1;
        CameraUpdate camup1 = CameraUpdateFactory.newLatLngZoom(new LatLng(latit, longit), zo);
        map.moveCamera(camup1);
        map.addMarker(new MarkerOptions().position(new LatLng(latit, longit)).title("Lat: " + latit + " " + "Lng: " + longit).draggable(true));
    }
    public void clickAnim(View view) {
        Float latit = Float.parseFloat(lati.getText().toString());
        Float longit = Float.parseFloat(longi.getText().toString());
        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(latit, longit))
                .zoom(13)
                .bearing(45)
                .tilt(70)
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        map.animateCamera(camUpd3);

    }

}
