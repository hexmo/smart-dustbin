package com.example.smartdustbin.wasteManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdustbin.MainActivity;
import com.example.smartdustbin.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.OrderBy;

import java.util.Calendar;
import java.util.Date;
import com.google.firebase.firestore.Query.Direction;

public class WasteHistoryActivity extends AppCompatActivity {
    private DustbinModel dustbinModel;
    RecyclerView recyclerView;
    TextView dustbinName;

    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waste_history);
        //Get intent values
        Intent intent = getIntent();
        dustbinModel = (DustbinModel) intent.getSerializableExtra("dustbin");

        mAuth = FirebaseAuth.getInstance();
        //hooks
        recyclerView = findViewById(R.id.history_recycler_view);
        dustbinName = findViewById(R.id.history_title);

        //set history title name
        dustbinName.setText(dustbinModel.getDustbinRoom());


        firebaseFirestore = FirebaseFirestore.getInstance();

//        Query query = firebaseFirestore.collection("history")
//                .whereEqualTo("dustbinId", dustbinModel.getDustbinId()).orderBy("timeStamp",Direction.DESCENDING);


        Query query = firebaseFirestore.collection("history")
                .whereEqualTo("dustbinId", dustbinModel.getDustbinId());

        //Recycler options
        FirestoreRecyclerOptions<WasteHistoryModel> options = new FirestoreRecyclerOptions.Builder<WasteHistoryModel>()
                .setQuery(query, WasteHistoryModel.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<WasteHistoryModel, WasteHistoryHolder>(options) {
            @NonNull
            @Override
            public WasteHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.waste_history, parent, false);
                return new WasteHistoryHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull WasteHistoryHolder holder, int position, @NonNull WasteHistoryModel model) {
                holder.timestamp.setText(model.getTimeStamp());
                holder.wasteLevel.setText((model.getWasteLevel()*4) + " %");
            }

            // https://stackoverflow.com/questions/57630866/how-to-see-if-firestorerecycleradapter-is-empty/57631222#57631222
            @Override
            public void onDataChanged() {
                // do your thing
                if (getItemCount() == 0) {
                    Toast.makeText(WasteHistoryActivity.this, "No history records for this device.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void goBack(View view) {
        finish();
    }

    public void removeThisDevice(View view) {
        DocumentReference docRef = firebaseFirestore.collection("dustbins")
                .document(dustbinModel.getDustbinId());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                dustbinModel = documentSnapshot.toObject(DustbinModel.class);
                if (dustbinModel != null) {

                    dustbinModel.setOwnerId("");
                    dustbinModel.setHasOwner(false);
                    dustbinModel.setDustbinRoom("");
                    //saving updated result

                    firebaseFirestore.collection("dustbins").document(dustbinModel.getDustbinId()).set(dustbinModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(WasteHistoryActivity.this, MainActivity.class));


                            removeDustbinHistory();

                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WasteHistoryActivity.this, "Failed to update remove device.", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    Toast.makeText(WasteHistoryActivity.this, "Failed to remove device.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        docRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WasteHistoryActivity.this, "Failed to remove device. Try again.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void removeDustbinHistory() {
        firebaseFirestore.collection("history").document(dustbinModel.getDustbinId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(WasteHistoryActivity.this, "Successfully removed device.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WasteHistoryActivity.this, "Failed to remove device.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private class WasteHistoryHolder extends RecyclerView.ViewHolder {

        private TextView timestamp, wasteLevel;

        public WasteHistoryHolder(@NonNull View itemView) {
            super(itemView);
            timestamp = itemView.findViewById(R.id.waste_history_date);
            wasteLevel = itemView.findViewById(R.id.waste_history_level);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}