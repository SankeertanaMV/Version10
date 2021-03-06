package com.example.sankeerthana.version10;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity  implements View.OnClickListener {

    private final AppCompatActivity activity = RegisterActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutFlatno;
    private TextInputLayout textInputLayoutAddress;

    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextFlatno;
    private TextInputEditText textInputEditTextAddress;

    private AppCompatButton appCompatButtonRegister;
    private AppCompatTextView appCompatTextViewLoginLink;

    //  private InputValidation inputValidation;
    private DBHelper databaseHelper;
    private Registerdetails registerdetails;
    private FirebaseAuth firebaseAuth;


    String email;
    String name;
    String password;
    String flatno;
    String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }

    private void initViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutFlatno = (TextInputLayout) findViewById(R.id.textInputLayoutFlatno);
        textInputLayoutAddress = (TextInputLayout) findViewById(R.id.textInputLayoutAddress);

        textInputEditTextName = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        textInputEditTextFlatno = (TextInputEditText) findViewById(R.id.textInputEditTextFlatno);
        textInputEditTextAddress = (TextInputEditText) findViewById(R.id.textInputEditTextAddress);


        appCompatButtonRegister = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewLoginLink = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonRegister.setOnClickListener(this);
        appCompatTextViewLoginLink.setOnClickListener(this);

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        //  inputValidation = new InputValidation(activity);
        databaseHelper = new DBHelper(activity);
        registerdetails = new Registerdetails();
        firebaseAuth = FirebaseAuth.getInstance();

    }


    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.appCompatButtonRegister:
                postDataToSQLite();
                break;

            case R.id.appCompatTextViewLoginLink:
                finish();
                break;
        }
    }

    /**
     * This method is to validate the input text fields and post data to SQLite
     */
    private void postDataToSQLite() {
        if (validate()) {
            firebaseAuth.createUserWithEmailAndPassword(textInputEditTextEmail.getText().toString(), textInputEditTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        //sendEmailVerification();
                        sendUserData();
                        addUser();
                        firebaseAuth.signOut();
                        Toast.makeText(RegisterActivity.this, "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void addUser() {
        registerdetails.setName(textInputEditTextName.getText().toString().trim());
        registerdetails.setEmailid(textInputEditTextEmail.getText().toString().trim());
        registerdetails.setPassword(textInputEditTextPassword.getText().toString().trim());
        registerdetails.setFlatno(textInputEditTextFlatno.getText().toString());
        registerdetails.setAddress(textInputEditTextAddress.getText().toString());

        databaseHelper.addRegister(registerdetails);
    }

    private void sendUserData() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid());
        myRef.setValue(registerdetails);
    }


    private boolean validate() {
        boolean x = false;
        name = textInputEditTextName.getText().toString().trim();
        password = textInputEditTextPassword.getText().toString().trim();
        email = textInputEditTextEmail.getText().toString().trim();
        flatno = textInputEditTextFlatno.getText().toString();
        address = textInputEditTextAddress.getText().toString();

        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || flatno.isEmpty() || address.isEmpty()) {

            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_LONG).show();
        } else if (databaseHelper.checkFlatno(flatno)) {
            Toast.makeText(this, "Flat number is already registered", Toast.LENGTH_LONG).show();
        } else {
            x = true;
        }
        return x;
    }
}