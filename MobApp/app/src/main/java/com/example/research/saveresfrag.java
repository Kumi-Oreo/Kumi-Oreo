package com.example.research;

import static android.content.ContentValues.TAG;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;


public class saveresfrag extends Fragment {

    private TextView textView;
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(); // Initialize DatabaseReference






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saveresfrag, container, false);
        return view;


    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getView() == null) {
            return;
        }

        StringBuilder dataBuilder = new StringBuilder();



        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Get current user
        String userId = currentUser.getUid(); // Sample user ID
        DatabaseReference userRef = reference.child("saveStorage").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinearLayout linearLayout = view.findViewById(R.id.linear);

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot pdfSnapshot : userSnapshot.getChildren()) {
                        dataPut data = pdfSnapshot.getValue(dataPut.class);

                        String mainid = userSnapshot.getKey();
                        String id = pdfSnapshot.getKey();

                        reference.child("PDF").child(mainid).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dataPut data = dataSnapshot.getValue(dataPut.class);


                                String title = data.title;
                                String abs = data.location;
                                String name = data.name;
                                String url = data.url;
                                String year = data.year;
                                String pdfName = data.pdfName;

                                CardView cardView = new CardView(getContext());
                                LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT
                                );
                                cardLayoutParams.setMargins(30, 10, 30, 10); // Add some bottom margin between card views
                                cardView.setLayoutParams(cardLayoutParams);
                                cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                                cardView.setRadius(50);


                                // Create a LinearLayout to hold TextView and Button
                                LinearLayout layout = new LinearLayout(getContext());
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT));

                                // Create a new TextView for the data entry
                                TextView textView = new TextView(getContext());
                                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT
                                );
                                textView.setLayoutParams(textParams);
                                textView.setPadding(16, 30, 16, 16);
                                textView.setText("Title: " + title + "\nLocation: " + abs + "\nName: " + name + "\nYear: " + year + "");
                                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // Set text size

                                // Create a new Button for downloading PDF
                                Button button = new Button(getContext());
                                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT
                                );
                                buttonParams.gravity = Gravity.CENTER; // Align button to the end of the layout
                                button.setLayoutParams(buttonParams);
                                button.setText("Download PDF");
                                button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                                button.setTextColor(Color.BLACK);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        downloadpdf(url, pdfName);
                                    }
                                });
                                Button button2 = new Button(getContext());
                                LinearLayout.LayoutParams buttonParams2 = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT
                                );
                                buttonParams2.gravity = Gravity.CENTER; // Align button to the end of the layout
                                button2.setLayoutParams(buttonParams2);
                                button2.setText("View");
                                button2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                                button2.setTextColor(Color.BLACK);
                                button2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String pdfUrl = url;
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant read permissions
                                        try {
                                            startActivity(intent);
                                        } catch (ActivityNotFoundException e) {
                                            // Handle the exception if no PDF viewer app installed
                                            Toast.makeText(getContext(), "No PDF viewer installed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });




                                layout.addView(textView);
                                layout.addView(button);
                                layout.addView(button2);

                                cardView.addView(layout);
                                linearLayout.addView(cardView);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });





                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("Firebase", "Error retrieving data", databaseError.toException());
            }
        });


    }


    private ProgressDialog progressDialog;

    private void downloadpdf(String urlpdf,String pdfName){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlpdf));
        request.setTitle("PDF Download");
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, pdfName);
        DownloadManager manager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Downloading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the download id from the intent
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId != -1) {
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(statusIndex);
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        Toast.makeText(getContext(),"Download Complete",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    }
                }
                cursor.close();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // Register the BroadcastReceiver
        getContext().registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the BroadcastReceiver
        getContext().unregisterReceiver(downloadReceiver);
    }

    @IgnoreExtraProperties
    public class User {

        public String research;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String research) {
            this.research = research;
        }

    }

}


