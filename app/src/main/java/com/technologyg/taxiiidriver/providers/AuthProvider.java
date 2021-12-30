package com.technologyg.taxiiidriver.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthProvider {

    FirebaseAuth mAhut;

    //Initialize Firebase Auth
    public AuthProvider(){
        mAhut = FirebaseAuth.getInstance();
    }

    //REGISTER IN DATABASE FIREBASE
    public Task<AuthResult> register(String email, String pass){
        return mAhut.createUserWithEmailAndPassword(email,pass);
    }

    //ACCESS TO DATABASE OF FIREBASE
    public Task<AuthResult> login(String email, String pass){
        return mAhut.signInWithEmailAndPassword(email, pass);
    }

    //LOGOUT
    public void logout(){
        mAhut.signOut();
    }

    //GET ID OF CURRENT USER
    public String getId(){
        System.out.println("AuthProvider: getId");
        System.out.println("mAhut: "+ mAhut);
        String id = "";
        id = mAhut.getCurrentUser().getUid();
        System.out.println(id);
        return id;
    }

    //SESSIONS EXIST? TO
    public boolean existSession(){
        boolean exist = false;
        if (mAhut.getCurrentUser() != null){
            exist = true;
        }
        return exist;
    }
}
