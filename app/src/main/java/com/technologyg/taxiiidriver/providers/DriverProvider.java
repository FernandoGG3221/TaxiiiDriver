package com.technologyg.taxiiidriver.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.technologyg.taxiiidriver.models.Driver;

import java.util.HashMap;
import java.util.Map;

public class DriverProvider {
    DatabaseReference mDatabase;

    public DriverProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver");
    }

    //CREATE NEW USER
    public Task<Void> create(Driver u){
        Map<String, Object> map = new HashMap<>();
        map.put("email", u.getEmail());
        map.put("name", u.getName());
        map.put("f_name", u.getF_name());
        map.put("s_name", u.getS_name());
        map.put("phone", u.getPhone());
        map.put("pass", u.getPass());
        return mDatabase.child(u.getId()).setValue(map);
    }

    //UPDATE IMAGE OF PROFILE
    public Task<Void>update(Driver u){
        Map<String, Object> map = new HashMap<>();
        map.put("name", u.getName());
        map.put("image", u.getImage());
        return mDatabase.child(u.getId()).updateChildren(map);
    }

    //UPDATE DATA OF PROFILE
    public Task<Void>updateData(Driver u){
        Map<String, Object> map = new HashMap<>();
        map.put("name", u.getName());
        return mDatabase.child(u.getId()).updateChildren(map);
    }

    //GET ID OF USER
    public DatabaseReference getUserID(String idUser){
        return mDatabase.child(idUser);
    }

}

