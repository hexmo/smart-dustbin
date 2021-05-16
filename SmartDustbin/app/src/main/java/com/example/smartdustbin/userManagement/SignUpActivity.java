package com.example.smartdustbin.userManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.smartdustbin.utils.LoadingClass;
import com.example.smartdustbin.MainActivity;
import com.example.smartdustbin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    //Variables and Objects declaration
    private TextInputLayout signUpFullName, signUpPhoneNumber, signUpEmail, signUpPassword, signUpReenterPassword;

    //For firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //Loading class
    private LoadingClass loadingClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Loading
        loadingClass = new LoadingClass(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Hooks
        signUpFullName = findViewById(R.id.signup_full_name);
        signUpPhoneNumber = findViewById(R.id.signup_phone_number);
        signUpEmail = findViewById(R.id.signup_email);
        signUpPassword = findViewById(R.id.signup_password);
        signUpReenterPassword = findViewById(R.id.signup_password_re_enter);

    }

    public void gotToLoginActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void signUp(View view) {
        if (validateFullName() && validatePhoneNumber() && validateEmail() && validatePassword() && validateReenterPassword()) {
            String userEmail = signUpEmail.getEditText().getText().toString().trim();
            String userPassword = signUpPassword.getEditText().getText().toString();

            //loading animation start
            loadingClass.startLoading();

            //Creating user account in firebase database as adding more user data
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Adding other details of user in fire store db

                        //Getting values from sign up form
                        String _id = mAuth.getCurrentUser().getUid();
                        String _userFullName = signUpFullName.getEditText().getText().toString().trim();
                        String _userPhoneNumber = signUpPhoneNumber.getEditText().getText().toString().trim();

                        //Creating userModel object to insert into the database
                        UserModel userModelObj = new UserModel(_id, _userFullName, _userPhoneNumber);
//

                        db.collection("user").document(mAuth.getCurrentUser().getUid()).set(userModelObj).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("User data:", "DocumentSnapshot successfully written!");
                                Toast.makeText(SignUpActivity.this, "Successfully created account.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("User data:", "Error writing document", e);
                            }
                        });

                        //Stopping loading icon
                        loadingClass.dismissLoading();

                        //Toast.makeText(SignUpActivity.this, "Successfully created account.", Toast.LENGTH_SHORT).show();

                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(SignUpActivity.this, "Successfully created account.\nPlease Sign In To Continue.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();

                    } else {
                        Toast.makeText(SignUpActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //Stopping loading icon
                    loadingClass.dismissLoading();
                }
            });
        }
    }

    private boolean validateFullName() {
        String value = signUpFullName.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            signUpFullName.setError("Full name cannot be left empty.");
        } else {
            signUpFullName.setError(null);
            signUpFullName.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validatePhoneNumber() {
        String value = signUpPhoneNumber.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            signUpPhoneNumber.setError("Phone number cannot be empty.");
        } else if (value.length() < 10) {
            signUpPhoneNumber.setError("Phone number should be 10 characters long.");
        } else if (!value.substring(0, 2).equals("98")) {
            signUpPhoneNumber.setError("Phone number should start with 98.");
        } else {
            signUpPhoneNumber.setError(null);
            signUpPhoneNumber.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validateEmail() {
        String value = signUpEmail.getEditText().getText().toString().trim();
        String pattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]+";
        if (value.isEmpty()) {
            signUpEmail.setError("Email field cannot be left empty.");
        } else if (!value.matches(pattern)) {
            signUpEmail.setError("Invalid email address.");
        } else {
            signUpEmail.setError(null);
            signUpEmail.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validatePassword() {
        String value = signUpPassword.getEditText().getText().toString();
        String pattern = "(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}";
        if (value.isEmpty()) {
            signUpPassword.setError("Password cannot be empty.");
        } else if (value.length() < 8) {
            signUpPassword.setError("Password should be at least 8 characters long.");
        } else if (!value.matches(pattern)) {
            signUpPassword.setError("Password should contain at least one special character and one number.");
        } else {
            signUpPassword.setError(null);
            signUpPassword.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validateReenterPassword() {
        String value = signUpReenterPassword.getEditText().getText().toString();
        if (value.isEmpty()) {
            signUpReenterPassword.setError("This field cannot be left empty.");
        } else if (!value.equals(getSignUpPassword())) {
            signUpReenterPassword.setError("Both passwords do not match.");
        } else {
            signUpReenterPassword.setError(null);
            signUpReenterPassword.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private String getSignUpPassword() {
        return signUpPassword.getEditText().getText().toString();
    }
}