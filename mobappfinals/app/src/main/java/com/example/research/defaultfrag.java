package com.example.research;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.Locale;


public class defaultfrag extends Fragment {



    private DatabaseReference databaseReference;

    EditText srcfield;
    Button searchbtn;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String name = "camarines 2024";
        String compare = "camarines";

        if (name.contains(compare.toLowerCase())){
            Toast.makeText(getContext(), "oo", Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_defaultfrag, container, false);


        searchbtn = view.findViewById(R.id.searchbtn);
        srcfield = view.findViewById(R.id.srcfield);

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serch(srcfield.getText().toString().toLowerCase());
            }
        });
        return view;


    }

    public void serch(String key){
        databaseReference = FirebaseDatabase.getInstance().getReference("PDF");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot grandchildSnapshot : childSnapshot.getChildren()) {
                        String name = grandchildSnapshot.child("location").getValue(String.class);
                        assert name != null;
                        Toast.makeText(getContext(), "" + name, Toast.LENGTH_SHORT).show();
                        if (name.toLowerCase(Locale.ROOT).contains(key)) {
                            Toast.makeText(getContext(), ""+ grandchildSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }




}