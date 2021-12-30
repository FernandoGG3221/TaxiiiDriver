package com.technologyg.taxiiidriver.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeoFireProvider {
    private DatabaseReference mDatabase;
    private GeoFire mGeofire;

    public GeoFireProvider(){
        //crear el nodo de almacenamiento de "active_drivers"
        mDatabase = FirebaseDatabase.getInstance().getReference().child("active_drivers");
        mGeofire = new GeoFire(mDatabase);
    }

    //almacena la localizacion del usuario en firebase
    public void saveLocation(String idDriver, LatLng latLng){
        mGeofire.setLocation(idDriver, new GeoLocation(latLng.latitude, latLng.longitude));
    }

    //eliminar la localizacion del usuario de la firebase
    public void removeLocation(String idDriver){
        mGeofire.removeLocation(idDriver);
    }
}
