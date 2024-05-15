package com.example.research;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import java.util.Locale;

public class homefrag extends Fragment {

    private TextView textView;
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(); // Initialize DatabaseReference

    LinearLayout linearLayout;
    EditText searchField;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homefrag, container, false);


        searchField = view.findViewById(R.id.searchField);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called to notify you that, within s, the count characters beginning at start have just replaced old text that had length before.
                // You can put your logic here to check if the EditText is empty and then call your function.
                String searchText = s.toString().trim().toLowerCase();
                if (searchText.isEmpty()) {
                    // Call your function here
                    handleEmptySearch();
                }
                serch(searchText);

            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called to notify you that, somewhere within s, the text has been changed.
            }
        });



        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // Perform search here
                    String searchText = searchField.getText().toString().trim();
                    performSearch(searchText);
                    return true; // Consume the action
                }
                return false; // Let the system handle the action
            }
        });



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getView() == null) {
            return;
        }
        linearLayout = view.findViewById(R.id.linear);
        loadall();






    }
    private ProgressDialog progressDialog;

    private void performSearch(String searchText) {

        linearLayout.removeAllViews();
    }

    public void serch(String key){
        linearLayout.removeAllViews();

        databaseReference = FirebaseDatabase.getInstance().getReference("PDF");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    linearLayout.removeAllViews();

                    for (DataSnapshot grandchildSnapshot : childSnapshot.getChildren()) {
                        String name = grandchildSnapshot.child("location").getValue(String.class);
                        String year = grandchildSnapshot.child("year").getValue(String.class);
                        String title = grandchildSnapshot.child("title").getValue(String.class);
                        linearLayout.removeAllViews();




                        try {
                            assert name != null;
                            linearLayout.removeAllViews();

                            if (name.toLowerCase(Locale.ROOT).contains(key) || year.toLowerCase(Locale.ROOT).contains(key) || Integer.parseInt(year) > Integer.parseInt(key)) {

                                reference.child("PDF").child(childSnapshot.getKey()).child(grandchildSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        dataPut data = dataSnapshot.getValue(dataPut.class);


                                        String title = data.title;
                                        String abs = data.location;
                                        String name = data.name;
                                        String url = data.url;
                                        String year = data.year;
                                        String pdfName = data.pdfName;

                                        ScrollView scrollView = new ScrollView(getContext());
                                        LinearLayout.LayoutParams scroll = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT
                                        );
                                        scroll.setMargins(0,0,0,30);
                                        scrollView.setLayoutParams(scroll);





                                        CardView cardView = new CardView(getContext());
                                        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT
                                        );
                                        cardLayoutParams.setMargins(30, 10, 30, 10); // Add some bottom margin between card views
                                        cardView.setLayoutParams(cardLayoutParams);
                                        cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                                        cardView.setRadius(50);


                                        GradientDrawable drawable = new GradientDrawable();
                                        drawable.setShape(GradientDrawable.RECTANGLE);
                                        drawable.setColor(ContextCompat.getColor(getContext(), R.color.purple_200)); // Background color
                                        drawable.setCornerRadius(50);


                                        // Create a LinearLayout to hold TextView and Button
                                        LinearLayout layout = new LinearLayout(getContext());
                                        layout.setOrientation(LinearLayout.VERTICAL);
                                        layout.setLayoutParams(new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT));
                                        layout.setBackgroundColor(Color.parseColor("#0097B2"));  // Use Color.parseColor to convert the hex color code to an integer

                                        // Create a new TextView for the data entry
                                        TextView textView = new TextView(getContext());
                                        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT
                                        );
                                        textView.setLayoutParams(textParams);
                                        textView.setPadding(16, 30, 16, 16);
                                        textView.setText("Title: " + title + "\n \nLocation: " + abs + "\n \nAuthor/s: " + name + "\n \nYear: " + year + "\n");
                                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
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
                                        buttonParams.setMargins(30, 15, 30, 15);
                                        button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_200));
                                        button.setTextColor(Color.WHITE);
                                        button.setBackground(drawable);

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
                                        buttonParams2.setMargins(30, 15, 30, 15);
                                        button2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_200));
                                        button2.setTextColor(Color.WHITE);
                                        button2.setBackground(drawable);

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

                                        Button button3 = new Button(getContext());
                                        LinearLayout.LayoutParams buttonParams3 = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT
                                        );
                                        buttonParams3.gravity = Gravity.CENTER; // Align button to the end of the layout
                                        button3.setLayoutParams(buttonParams2);
                                        button3.setText("Save");
                                        buttonParams3.setMargins(30, 15, 30, 15);
                                        button3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_200));
                                        button3.setTextColor(Color.WHITE);
                                        button3.setBackground(drawable);
                                        button3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                FirebaseUser currentUser = auth.getCurrentUser();

                                                writeNewUser(currentUser.getUid(), childSnapshot.getKey(), grandchildSnapshot.getKey());

                                            }
                                        });


                                        layout.addView(textView);
                                        layout.addView(button);
                                        layout.addView(button2);
                                        layout.addView(button3);

                                        cardView.addView(layout);
                                        scrollView.addView(cardView);
                                        linearLayout.addView(scrollView);


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }

                        }catch (Exception e){
                            linearLayout.removeAllViews();

                            assert name != null;
                            if (name.toLowerCase(Locale.ROOT).contains(key) || title.toLowerCase(Locale.ROOT).contains(key)) {

                                reference.child("PDF").child(childSnapshot.getKey()).child(grandchildSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        dataPut data = dataSnapshot.getValue(dataPut.class);


                                        String title = data.title;
                                        String abs = data.location;
                                        String name = data.name;
                                        String url = data.url;
                                        String year = data.year;
                                        String pdfName = data.pdfName;

                                        ScrollView scrollView = new ScrollView(getContext());
                                        LinearLayout.LayoutParams scroll = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT
                                        );
                                        scroll.setMargins(0,0,0,30);
                                        scrollView.setLayoutParams(scroll);





                                        CardView cardView = new CardView(getContext());
                                        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT
                                        );
                                        cardLayoutParams.setMargins(30, 10, 30, 10); // Add some bottom margin between card views
                                        cardView.setLayoutParams(cardLayoutParams);
                                        cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                                        cardView.setRadius(50);


                                        GradientDrawable drawable = new GradientDrawable();
                                        drawable.setShape(GradientDrawable.RECTANGLE);
                                        drawable.setColor(ContextCompat.getColor(getContext(), R.color.purple_200)); // Background color
                                        drawable.setCornerRadius(50);


                                        // Create a LinearLayout to hold TextView and Button
                                        LinearLayout layout = new LinearLayout(getContext());
                                        layout.setOrientation(LinearLayout.VERTICAL);
                                        layout.setLayoutParams(new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT));
                                        layout.setBackgroundColor(Color.parseColor("#0097B2"));  // Use Color.parseColor to convert the hex color code to an integer

                                        // Create a new TextView for the data entry
                                        TextView textView = new TextView(getContext());
                                        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT
                                        );
                                        textView.setLayoutParams(textParams);
                                        textView.setPadding(16, 30, 16, 16);
                                        textView.setText("Title: " + title + "\n \nLocation: " + abs + "\n \nAuthor/s: " + name + "\n \nYear: " + year + "\n");
                                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
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
                                        buttonParams.setMargins(30, 15, 30, 15);
                                        button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_200));
                                        button.setTextColor(Color.WHITE);
                                        button.setBackground(drawable);

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
                                        buttonParams2.setMargins(30, 15, 30, 15);
                                        button2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_200));
                                        button2.setTextColor(Color.WHITE);
                                        button2.setBackground(drawable);

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

                                        Button button3 = new Button(getContext());
                                        LinearLayout.LayoutParams buttonParams3 = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT
                                        );
                                        buttonParams3.gravity = Gravity.CENTER; // Align button to the end of the layout
                                        button3.setLayoutParams(buttonParams2);
                                        button3.setText("Save");
                                        buttonParams3.setMargins(30, 15, 30, 15);
                                        button3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_200));
                                        button3.setTextColor(Color.WHITE);
                                        button3.setBackground(drawable);
                                        button3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                FirebaseUser currentUser = auth.getCurrentUser();

                                                writeNewUser(currentUser.getUid(),childSnapshot.getKey(), grandchildSnapshot.getKey());

                                            }
                                        });


                                        layout.addView(textView);
                                        layout.addView(button);
                                        layout.addView(button2);
                                        layout.addView(button3);

                                        cardView.addView(layout);
                                        scrollView.addView(cardView);
                                        linearLayout.addView(scrollView);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }


                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                linearLayout.removeAllViews();
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadall(){
        linearLayout.removeAllViews();

        StringBuilder dataBuilder = new StringBuilder();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Get current user

        reference.child("PDF").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot pdfSnapshot : userSnapshot.getChildren()) {
                        dataPut data = pdfSnapshot.getValue(dataPut.class);

                        String mainid = userSnapshot.getKey();
                        String id = pdfSnapshot.getKey();
                        String title = data.title;
                        String abs = data.location;
                        String name = data.name;
                        String url = data.url;
                        String year = data.year;
                        String pdfName = data.pdfName;

                        ScrollView scrollView = new ScrollView(getContext());
                        LinearLayout.LayoutParams scroll = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );
                        scroll.setMargins(0,0,0,30);
                        scrollView.setLayoutParams(scroll);





                        CardView cardView = new CardView(getContext());
                        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );
                        cardLayoutParams.setMargins(30, 10, 30, 10); // Add some bottom margin between card views
                        cardView.setLayoutParams(cardLayoutParams);
                        cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        cardView.setRadius(50);


                        GradientDrawable drawable = new GradientDrawable();
                        drawable.setShape(GradientDrawable.RECTANGLE);
                        drawable.setColor(ContextCompat.getColor(getContext(), R.color.purple_200)); // Background color
                        drawable.setCornerRadius(50);


                        // Create a LinearLayout to hold TextView and Button
                        LinearLayout layout = new LinearLayout(getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));
                        layout.setBackgroundColor(Color.parseColor("#0097B2"));  // Use Color.parseColor to convert the hex color code to an integer

                        // Create a new TextView for the data entry
                        TextView textView = new TextView(getContext());
                        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );
                        textView.setLayoutParams(textParams);
                        textView.setPadding(16, 30, 16, 16);
                        textView.setText("Title: " + title + "\n \nLocation: " + abs + "\n \nAuthor/s: " + name + "\n \nYear: " + year + "\n");
                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
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
                        buttonParams.setMargins(30, 15, 30, 15);
                        button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_200));
                        button.setTextColor(Color.WHITE);
                        button.setBackground(drawable);

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
                        buttonParams2.setMargins(30, 15, 30, 15);
                        button2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_200));
                        button2.setTextColor(Color.WHITE);
                        button2.setBackground(drawable);

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

                        Button button3 = new Button(getContext());
                        LinearLayout.LayoutParams buttonParams3 = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );
                        buttonParams3.gravity = Gravity.CENTER; // Align button to the end of the layout
                        button3.setLayoutParams(buttonParams2);
                        button3.setText("Save");
                        buttonParams3.setMargins(30, 15, 30, 15);
                        button3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_200));
                        button3.setTextColor(Color.WHITE);
                        button3.setBackground(drawable);
                        button3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseUser currentUser = auth.getCurrentUser();

                                writeNewUser(currentUser.getUid(),id,mainid);

                            }
                        });


                        layout.addView(textView);
                        layout.addView(button);
                        layout.addView(button2);
                        layout.addView(button3);

                        cardView.addView(layout);
                        scrollView.addView(cardView);
                        linearLayout.addView(scrollView);

                        // Create views and add to layout...
                    }
                }

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    dataPut data = childSnapshot.getValue(dataPut.class);



                    String title = data.title;
                    String abs = data.location;
                    String name = data.name;
                    String url = data.url;
                    String year = data.year;
                    String pdfName = data.pdfName;

                    // Create a new CardView to wrap the TextView and Button


                }

                // Set the concatenated data as the text of the TextView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

    }

    void handleEmptySearch() {
        loadall();
    }



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

    public void writeNewUser(String userId, String name,String main) {
        DatabaseReference userRef = reference.child("saveStorage").child(userId).child(main);

        // Check if the child with the given name exists
        userRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Child with the given name does not exist, so add the user
                    User user = new User(name);
                    userRef.child(name).push().setValue(name)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(),"Saved Successfully",Toast.LENGTH_LONG).show();
                                    } else {
                                        // Handle the error
                                    }
                                }
                            });
                } else {
                    // Child with the given name already exists
                    Toast.makeText(getContext(),"Already saved this",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

    }




}
