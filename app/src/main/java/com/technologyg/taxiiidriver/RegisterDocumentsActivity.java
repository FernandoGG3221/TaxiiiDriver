package com.technologyg.taxiiidriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.technologyg.taxiiidriver.models.DocumentsDriver;
import com.technologyg.taxiiidriver.models.Driver;
import com.technologyg.taxiiidriver.providers.AuthProvider;
import com.technologyg.taxiiidriver.providers.DocumentsProvider;
import com.technologyg.taxiiidriver.providers.DriverProvider;

public class RegisterDocumentsActivity extends AppCompatActivity {
    //Edit Text
    private EditText mINE;
    private EditText mRFC;
    private EditText mLicence;
    private EditText mCardCirculation;
    private EditText mCarInsurance;
    private EditText mPlate;
    //Button
    private Button mBtnRegister;
    //FIREBASE INSTANCES
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    //PROVIDERS
    private AuthProvider mAuthProvider;
    private DocumentsProvider mDocProvider;
    private DriverProvider mDriverProvider;
    //Extras
    private String mExtraName;
    private String mExtraF_name;
    private String mExtraS_name;
    private String mExtraEmail;
    private String mExtraPhone;
    private String mExtraPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_documents);

        //Instances
        mAuthProvider  = new AuthProvider();
        mDriverProvider = new DriverProvider();

        //Find by ID
        mINE = findViewById(R.id.ed_txt_Ine_RD);
        mRFC = findViewById(R.id.ed_txt_RFC_RD);
        mLicence = findViewById(R.id.ed_txt_licence_RD);
        mCardCirculation = findViewById(R.id.ed_txt_card_RD);
        mCarInsurance = findViewById(R.id.ed_txt_carInsurance_RD);
        mPlate = findViewById(R.id.ed_txt_plate_RD);
        mBtnRegister = findViewById(R.id.btn_register_RegDocs);

        //Get Extras
        //mExtraId = getIntent().getStringExtra("id");
        mExtraName = getIntent().getStringExtra("name");
        mExtraF_name = getIntent().getStringExtra("fname");
        mExtraS_name = getIntent().getStringExtra("sname");
        mExtraEmail = getIntent().getStringExtra("email");
        mExtraPhone = getIntent().getStringExtra("phone");
        mExtraPass = getIntent().getStringExtra("pass");

        //Click listener
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerData();
            }
        });
    }

    private void registerData(){
        final String INE = mINE.getText().toString();
        final String RFC = mRFC.getText().toString();
        final String Licence = mLicence.getText().toString();
        final String CardCirculation = mCardCirculation.getText().toString();
        final String CarInsurance = mCarInsurance.getText().toString();
        final String Plate = mPlate.getText().toString();

        if(!INE.isEmpty() && !RFC.isEmpty() && !Licence.isEmpty() && !CardCirculation.isEmpty() && !CarInsurance.isEmpty() && !Plate.isEmpty()){
            completeRegister(mExtraEmail, mExtraName, mExtraF_name, mExtraS_name,
                             mExtraPhone, mExtraPass, INE, RFC, Licence,
                             CardCirculation, CarInsurance, Plate);
        }else{
            Toast.makeText(RegisterDocumentsActivity.this, "¡Porfavor de llenar todos los campos!", Toast.LENGTH_SHORT).show();
        }
    }

    private void completeRegister(final String email, final String name, final String f_name, final String s_name,
                                  final String phone, final String pass, final String ine, final String rfc, final String licence,
                                  final String card, final String carInsurance, final String plate){

        mAuthProvider.register(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    System.out.println("Id: " + id);
                    mDocProvider = new DocumentsProvider(id);
                    //Toast.makeText(RegisterDocumentsActivity.this, id, Toast.LENGTH_SHORT).show();
                    Driver d = new Driver(id, email, name, f_name, s_name, phone, pass);
                    DocumentsDriver dD = new DocumentsDriver(ine, rfc, licence, card, carInsurance, plate);
                    create(d, dD);
                }else{
                    Toast.makeText(RegisterDocumentsActivity.this, "Error al ejecutar el método CompleteRegister", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void create(Driver d, DocumentsDriver dD){
        mDriverProvider.create(d).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mDocProvider.create(dD).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterDocumentsActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(RegisterDocumentsActivity.this, MapActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(RegisterDocumentsActivity.this, "Error al crear el nodo de Documentos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(RegisterDocumentsActivity.this, "Error al registrar al usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(RegisterDocumentsActivity.this, RegisterActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

}