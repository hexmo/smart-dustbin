package com.example.smartdustbin.wasteManagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.smartdustbin.MainActivity;
import com.example.smartdustbin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddDeviceFragment extends Fragment {

    TextInputLayout deviceName, deviceId, deviceSecret;
    Button button;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    DustbinModel dustbinModel;


    public AddDeviceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_device, container, false);

        //hooks
        deviceId = view.findViewById(R.id.add_device_id);
        deviceName = view.findViewById(R.id.add_device_name);
        deviceSecret = view.findViewById(R.id.add_device_secret);
        button = view.findViewById(R.id.button_add_device);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateName() && validateCredentials()) {
                    //populate data
                    DocumentReference docRef = db.collection("dustbins")
                            .document(deviceId.getEditText().getText().toString());

                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            dustbinModel = documentSnapshot.toObject(DustbinModel.class);
                            if (dustbinModel != null) {
                                if (dustbinModel.isHasOwner()) {
                                    Toast.makeText(getContext(), "Device is linked with another account.", Toast.LENGTH_SHORT).show();
                                } else if (!dustbinModel.getSecret().equals(deviceSecret.getEditText().getText().toString())) {
                                    Toast.makeText(getContext(), "Secret does not match.", Toast.LENGTH_SHORT).show();
                                } else {
                                    dustbinModel.setOwnerId(mAuth.getCurrentUser().getUid());
                                    dustbinModel.setHasOwner(true);
                                    dustbinModel.setDustbinRoom(deviceName.getEditText().getText().toString());
                                    //saving updated result

                                    db.collection("dustbins").document(dustbinModel.getDustbinId()).set(dustbinModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Successfully added device.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getContext(), MainActivity.class));
                                            getActivity().finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Failed to update add device.", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            }else {
                                Toast.makeText(getContext(), "No such devices.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    docRef.get().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to add device. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });


        return view;
    }

    private boolean validateCredentials() {
        String id = deviceId.getEditText().getText().toString();
        String secret = deviceSecret.getEditText().getText().toString();

        if (id.isEmpty()) {
            deviceId.setError("Device ID cannot be empty.");
        } else if (secret.isEmpty()) {
            deviceSecret.setError("Device secret cannot be empty..");
        } else {

            deviceId.setError(null);
            deviceId.setErrorEnabled(false);
            deviceSecret.setError(null);
            deviceSecret.setErrorEnabled(false);
            return true;

        }

        return false;
    }

    private boolean validateName() {
        String name = deviceName.getEditText().getText().toString();
        if (name.isEmpty()) {
            deviceName.setError("Device name cannot be empty.");
        } else {
            deviceName.setError(null);
            deviceName.setErrorEnabled(false);
            return true;
        }

        return false;

    }
}