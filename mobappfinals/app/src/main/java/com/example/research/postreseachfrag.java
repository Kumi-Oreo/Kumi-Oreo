package com.example.research;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class postreseachfrag extends Fragment {

    EditText editText,title,abs,author,email,contactNumber,year,location,paperType,schoolEmail;
    Button btn,selectFile;

    Spinner spinner;
    String selectedValue;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postreseachfrag, container, false);
        CardView cardView = view.findViewById(R.id.cardView);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int screenHeight = view.getHeight();

                int heightDiff = screenHeight - (r.bottom - r.top);

                if (heightDiff > 200) {
                    int availableHeight = screenHeight - heightDiff-160;

                    ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                    layoutParams.height = availableHeight;

                    cardView.setLayoutParams(layoutParams);
                } else {
                    ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();

                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                    cardView.setLayoutParams(layoutParams);
                }
            }
        });


        spinner = view.findViewById(R.id.state);

        String[] options = {"Public", "Private"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, options);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        editText = view.findViewById(R.id.edittext);
        title = view.findViewById(R.id.titleName);
        abs = view.findViewById(R.id.abs);
        author = view.findViewById(R.id.author);
        email = view.findViewById(R.id.email);
        contactNumber = view.findViewById(R.id.contactNumber);
        year = view.findViewById(R.id.year);
        location = view.findViewById(R.id.location);
        paperType = view.findViewById(R.id.paperType);
        schoolEmail = view.findViewById(R.id.schoolEmail);
        btn = view.findViewById(R.id.bt_select);
        selectFile = view.findViewById(R.id.selectFile);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the adapter
                selectedValue = (String) parent.getItemAtPosition(position);

                // Do something with the selected value
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (optional)
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();



        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select();
            }
        });

        return view;
    }
    private  void select(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"PDF FILE SELECT"),12);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedFileUri = data.getData();
            String fileName = getFileName(selectedFileUri);
            editText.setText(fileName);


            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upload(data.getData());
                }
            });
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    public void upload(Uri data){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        progressDialog.show();

        StorageReference reference = storageReference.child(editText.getText().toString());
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> UriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!UriTask.isComplete());
                        Uri uri = UriTask.getResult();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = auth.getCurrentUser();

//                        dataPut dataPut = new dataPut(uri.toString(),title.getText().toString(),abs.getText().toString(),author.getText().toString(),email.getText().toString(),contactNumber.getText().toString(),year.getText().toString(),location.getText().toString(),paperType.getText().toString(),schoolEmail.getText().toString(),selectedValue);
                        dataPut dataPut = new dataPut(title.getText().toString(),abs.getText().toString(),author.getText().toString(),uri.toString(),selectedValue,email.getText().toString(),
                                contactNumber.getText().toString(),year.getText().toString(),location.getText().toString(),paperType.getText().toString(),schoolEmail.getText().toString(),editText.getText().toString());
                        databaseReference.child("PDF").child(currentUser.getUid()).push().setValue(dataPut);

                        Toast.makeText(getContext(),"File Upload",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        clearEditTextFields();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100.0* taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("File Uploaded" + (int) progress+"%");
                    }
                });
    }
    private void clearEditTextFields() {
        editText.setText("");
        title.setText("");
        abs.setText("");
        author.setText("");
        email.setText("");
        contactNumber.setText("");
        year.setText("");
        location.setText("");
        paperType.setText("");
        schoolEmail.setText("");
    }


}

