package com.technologyg.taxiiidriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    //Edit Text
    private EditText mEd_Txt_User;
    private EditText mEd_Txt_Pass;
    //Text
    private TextView mTxt_Register;
    private TextView mTxt_resetPass;
    //Buttons
    private Button mBtn_login;
    //PROVIDERS
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //FIND BY ID
        mEd_Txt_User = findViewById(R.id.ed_txt_user_login);
        mEd_Txt_Pass = findViewById(R.id.ed_txt_pass_login);
        mTxt_Register = findViewById(R.id.txt_registrar_login);
        mTxt_resetPass = findViewById(R.id.txt_resetPass);
        mBtn_login = findViewById(R.id.btn_login_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Click listener
        mTxt_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
        mBtn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String email = mEd_Txt_User.getText().toString();
        String pass = mEd_Txt_Pass.getText().toString();

        if (!email.isEmpty() && !pass.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "BIENVENIDO", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, MapActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "LLena todos los campos por favor", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}