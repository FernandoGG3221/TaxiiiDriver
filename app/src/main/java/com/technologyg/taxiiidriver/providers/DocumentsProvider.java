package com.technologyg.taxiiidriver.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.technologyg.taxiiidriver.models.DocumentsDriver;

import java.util.HashMap;
import java.util.Map;

public class DocumentsProvider {

    DatabaseReference mDatabase;
    DriverProvider mDriverProvider;

    public DocumentsProvider(String id){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(id);
        System.out.println("Documents Providers mDatabase: " + mDatabase);
    }

        public Task<Void>create(DocumentsDriver d){
            Map<String, Object>  map = new HashMap<>();
            map.put("RFC", d.getRFC());
            map.put("INE", d.getINE());
            map.put("Circulation_Vehicle_Card", d.getCirculationVehicleTarget());
            map.put("Car_Insurance", d.getVehicleSafe());
            map.put("Licence", d.getPermissionDriver());
            map.put("Plate", d.getPlate());
            return mDatabase.child("Documents").setValue(map);
        }

}
