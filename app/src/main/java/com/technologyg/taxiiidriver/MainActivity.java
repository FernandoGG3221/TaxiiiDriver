package com.technologyg.taxiiidriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.technologyg.taxiiidriver.providers.AuthProvider;

public class MainActivity extends AppCompatActivity {

    //PROVIDERS
    private AuthProvider mAuthProvider;
    //BUTTONS
    private Button mBtnLogin;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TaxiiiDriver);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INSTANCES
        mAuthProvider = new AuthProvider();
        //Find By Id
        mBtnLogin = findViewById(R.id.btn_login_main);
        mBtnRegister = findViewById(R.id.btn_register_main);

        //Click Listener
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.existSession() == true){
            Intent i = new Intent(MainActivity.this, MapActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}