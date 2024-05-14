package com.example.research;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignClient;

    HashMap<String,Object> map = new HashMap<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference mDatabase;


    int sign = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginSave loginSaveInstance = new loginSave(this);
        int savedNumber = loginSaveInstance.getSavedNumber();

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();


        mGoogleSignClient = GoogleSignIn.getClient(this,gso);
        check();




    }

    public void check(){
        loginSave loginSaveInstance = new loginSave(this);
        int savedNumber = loginSaveInstance.getSavedNumber();
        if (savedNumber == 10){
            loginSaveInstance.saveNumber(10);
            Intent intent = new Intent(MainActivity.this,HomePage.class);
            startActivity(intent);
            finish();
        }


    }

    public void googleLogin(View view) {

        googleSignIn();
    }

    private void googleSignIn() {
        Intent intent = mGoogleSignClient.getSignInIntent();
        startActivityForResult(intent,sign);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==sign){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());


            }
            catch (Exception a){
                Toast.makeText(this,a.getMessage(),Toast.LENGTH_LONG).show();

            }

        }
    }

    private void firebaseAuth(String idToken) {
        loginSave loginSaveInstance = new loginSave(this);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;


                            Map<String, Object> data = new HashMap<>();
                            data.put("userName", user.getDisplayName());
                            data.put("userEmail",user.getEmail());

                            String documentId = user.getUid(); // Using documentId instead of userId

                            User userData = new User(user.getDisplayName(), user.getEmail());

                            mDatabase.child("users").child(documentId).setValue(userData);

                            loginSaveInstance.saveNumber(10);
                            Intent intent = new Intent(MainActivity.this,HomePage.class);
                            startActivity(intent);
                            finish();

                        }

                    }
                });
    }

    @IgnoreExtraProperties
    public class User {

        public String username;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

    }

}