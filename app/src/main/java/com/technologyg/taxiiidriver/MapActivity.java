package com.technologyg.taxiiidriver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.technologyg.taxiiidriver.includes.MyToolbar;
import com.technologyg.taxiiidriver.providers.AuthProvider;
import com.technologyg.taxiiidriver.providers.GeoFireProvider;

import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    //PROVIDERS
    private AuthProvider mAuthProvider;
    private GeoFireProvider mGeoFireProvider;
    //MAPS
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    //LOCATIONS
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;
    private LatLng mCurrentLatLng;                      //Global Save Location
    //VARIABLES
    private final int LOCATION_REQUEST_CODE = 1;        //bandera para permitirle saber si se necesitan pedir los permisos de ubicacion
    private final int SETTINGS_REQUEST_CODE = 2;
    private boolean isConnect = false;
    //BUTTONS
    private Button mButtonConnect;
    //ICON IN THE MAP
    private Marker mMarker;

    //Escucha cada vez que el usuario se mueve
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    //Almacena la lat y lng del usuario cada vez que se mueve
                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    //Comprobar si existe algun marcador en el mapa
                    if(mMarker != null ){
                        mMarker.remove();
                    }

                    //Actualizar el icono en el mapa a traves de la longitud y la latitud
                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .title("Tú")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxiii_azul100)));
                    //Obtener la localización del usuario en tiempo real
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(17f).build()));

                    //Guardar la localizacion del usuario en firebase atraves de geofireProvider cada vez que se esta moviendo en el mapa
                    updateLocation();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //INSTANCES
        mAuthProvider = new AuthProvider();
        mGeoFireProvider = new GeoFireProvider();
        //  Iniciar o detener la ubicación del cliente
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        //Find By Id
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_mapActivity);
        mButtonConnect = findViewById(R.id.btn_connect_mapActivity);
        //Sync
        mMapFragment.getMapAsync(MapActivity.this);
        //Toolbar
        MyToolbar.show(MapActivity.this, "¡Taxiii!");

        //Click Listener
        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnect){
                    disconnect();
                }else{
                    starLocation();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(1);
        //Location Exactly  ****
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        //---------------   ****


        //INSTANCE
        mLocationRequest = new LocationRequest();
        //Tiempo que se va a actualizando la ubicacion del usuario en el mapa
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        //Hace uso del gps con la mayor precision posible   //LocationRequest.PRIORITY_HIGH_ACCURACY == 100
        mLocationRequest.setPriority(100);
        //desplazamiento minimo entre ubicacion actualizada en metros
        mLocationRequest.setSmallestDisplacement(5);

    }

    //Know if gps is enabled
    private boolean gpsActived(){
        boolean isActived = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //isProvider enable == si tiene el gps activado
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActived = true;
        }
        return isActived;
    }

    //Show Alert Dialog for go to settings and turn on gps
    private void showAlertDialogNoGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setMessage("Activa tu GPS para disfrutar de ¡Taxiii!").setPositiveButton("Configuración", (dialog, which) -> {
            //Es una actividad que cuando se inicia, espera que el usuario encienda su gps
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
        }).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SETTINGS_REQUEST_CODE && gpsActived()){
            if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }else{
            showAlertDialogNoGPS();
        }
    }

    //GET PERMISSION
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE){
            //pregunta si el usuario concedió los permisos
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if(gpsActived()){
                        //Cuando este evento se ejecuta, entra al LocationCallBack y obtiene la ubicacion del usuario en tiepo real
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }else{
                        showAlertDialogNoGPS();
                    }
                }else{
                    checkLocationPermission();
                }
            }else{
                checkLocationPermission();
            }
        }
    }

    //Desconectar la Escucha de la Posicion del Taxiii
    private void disconnect(){

        if(mFusedLocation != null){
            //Cambio del Texto del Btn
            mButtonConnect.setText("Conectarse");
            //Cambio del valor de la variable a true
            isConnect = false;
            //Remover las actualizaciones de la posición
            mFusedLocation.removeLocationUpdates(mLocationCallback);
            if(mAuthProvider.existSession()){
                //Elimina los datos almacenados de la BD
                mGeoFireProvider.removeLocation(mAuthProvider.getId());
            }
        }else{
            Toast.makeText(MapActivity.this, "No se ha podido desconectar", Toast.LENGTH_SHORT).show();
        }
    }

    //Iniciar el escuchador de la ubicacion
    private void starLocation(){
        //Comprobar la version del sdk de android sea mayor a Marshmellow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if(gpsActived()){
                    //Cambio del Texto del Btn
                    mButtonConnect.setText("Desconectarse");
                    //Cambio del valor de la variable a true
                    isConnect = true;
                    //Cuando este evento se ejecuta, entra al LocationCallBack y obtiene la ubicacion del usuario en tiepo real
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }else{
                    showAlertDialogNoGPS();
                }
            }else{
                checkLocationPermission();
            }
        }
        else{
            if(gpsActived()){
                //Cuando este evento se ejecuta, entra al LocationCallBack y obtiene la ubicacion del usuario en tiepo real
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }else{
                showAlertDialogNoGPS();
            }
        }
    }

    //Almacen la ubicacion del usuario cada vez que se mueve
    private void updateLocation(){
        if(mAuthProvider.existSession() && mCurrentLatLng != null){
            //guarda la localizacion en la BD
            mGeoFireProvider.saveLocation(mAuthProvider.getId(), mCurrentLatLng);
        }
    }

    //Verificar si el usuario acepta o no los permisos del gps
    private void checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(MapActivity.this)
                        .setTitle("Se requiere el permiso para continuar")
                        .setMessage("¡Taxiii! requiere del uso de GPS para brindarte el servicio")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Conceder permisos para acceder a la localización del celular
                                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        }).create().show();
            }else{
                //Conceder permisos para acceder a la localización del celular
                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    //Create option to menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Declarar el menú a utilizar
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Select item of menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(){
        //cuando el usuario cierra sesión, se desconecta la escucha de su ubicacion
        disconnect();
        mAuthProvider.logout();
        Intent i = new Intent(MapActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}