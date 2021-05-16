package com.example.smartdustbin.wasteManagement;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdustbin.notification.MyBroadCastReceiver;
import com.example.smartdustbin.notification.NotificationManager;
import com.example.smartdustbin.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;

    NotificationManager notificationManager;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = view.findViewById(R.id.dashboard_recycler_view);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //query
        Query query = firebaseFirestore.collection("dustbins")
                .whereEqualTo("ownerId", mAuth.getCurrentUser().getUid());

        //Recycler options
        FirestoreRecyclerOptions<DustbinModel> options = new FirestoreRecyclerOptions.Builder<DustbinModel>()
                .setQuery(query, DustbinModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DustbinModel, DustbinViewHolder>(options) {

            @NonNull
            @Override
            public DustbinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.waste_meter, parent, false);

                return new DustbinViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DustbinViewHolder holder, int position, @NonNull DustbinModel model) {
                //getting bus details from fire store

                holder.wasteRoomName.setText(model.getDustbinRoom());
                holder.wastePercentage.setText((100 -(model.getWasteLevel() * 4)) + "%");

                holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent history = new Intent(getContext(), WasteHistoryActivity.class);
                        history.putExtra("dustbin", model);
                        startActivity(history);
                    }
                });

            }

            // https://stackoverflow.com/questions/57630866/how-to-see-if-firestorerecycleradapter-is-empty/57631222#57631222
            @Override
            public void onDataChanged() {
                // do your thing
                if (getItemCount() == 0) {
                    Toast.makeText(getContext(), "Please add devices to view them in dashboard.", Toast.LENGTH_SHORT).show();
                }

            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);



        return view;
    }

    private class DustbinViewHolder extends RecyclerView.ViewHolder {

        private TextView wasteRoomName, wastePercentage;
        private ConstraintLayout constraintLayout;

        public DustbinViewHolder(@NonNull View itemView) {
            super(itemView);
            wasteRoomName = itemView.findViewById(R.id.waste_room_name);
            wastePercentage = itemView.findViewById(R.id.waste_percentage);
            constraintLayout = itemView.findViewById(R.id.waste_meter);

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