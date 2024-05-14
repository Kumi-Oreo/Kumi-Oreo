package com.example.research;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePage extends AppCompatActivity {

    GoogleSignInClient mGoogleSignClient;
    ConstraintLayout constraintLayout;
    LinearLayout linearLayout;
    Button showHide,home,post,task,saved;



    int back = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        home = findViewById(R.id.home);
        post = findViewById(R.id.postResearch);
        task = findViewById(R.id.task);
        saved = findViewById(R.id.saved);


        constraintLayout = findViewById(R.id.buttonsLayout);
        linearLayout = findViewById(R.id.main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new defaultfrag())
                .addToBackStack(null)
                .commit();



        showHide = findViewById(R.id.show);
        constraintLayout.setMaxWidth(0);

        loginSave loginSaveInstance = new loginSave(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Initialize GoogleSignInClient
        mGoogleSignClient = GoogleSignIn.getClient(this, gso);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        TextView textView2 = findViewById(R.id.textView2);
        TextView userImg = findViewById(R.id.userImg);

        textView2.setText(currentUser.getDisplayName());

        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            userImg.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle placeholder if needed
                        }
                    });
        } else {
            // Handle case when photo URL is null
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main);
                if (currentFragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new homefrag())
                        .addToBackStack(null)
                        .commit();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main);
                if (currentFragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new postreseachfrag())
                        .addToBackStack(null)
                        .commit();

            }
        });

        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomePage.this,Todo.class);
                startActivity(intent);

            }
        });

        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main);
                if (currentFragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new saveresfrag())
                        .addToBackStack(null)
                        .commit();
            }
        });



    }

    public void logout(View view) {
        loginSave loginSaveInstance = new loginSave(this);

        if (mGoogleSignClient != null) {
            // Sign out from Google
            mGoogleSignClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // Start MainActivity after signing out
                    loginSaveInstance.saveNumber(0);

                    Intent intent = new Intent(HomePage.this, MainActivity.class);

                    startActivity(intent);
                }
            });
        } else {
            // Handle the case when mGoogleSignClient is null
        }

    }

    public void show(View view) {
        int val = constraintLayout.getMaxWidth();
        if (val == 600) {
            // Closing animation
            ValueAnimator anim = ValueAnimator.ofInt(600, 0);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    constraintLayout.setMaxWidth(val);
                }
            });
            anim.setDuration(0); // Adjust the duration as needed
            anim.start();
            showHide.setText("=");

        } else {
            // Opening animation
            ValueAnimator anim = ValueAnimator.ofInt(0, 600);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    constraintLayout.setMaxWidth(val);

                }
            });
            anim.setDuration(15); // Adjust the duration as needed
            anim.start();
            showHide.setText("<");

        }

    }

    public void Homefrag(View view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new homefrag())
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onBackPressed() {
        nofrag();
        back += 1;
        if (back == 2){
            finish();
            finishAffinity(); // Finish all activities in the task associated with this activity
            super.onBackPressed();
        }

    }
    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();


    }


    public void accident(View view) {
        showHide.setText("=");
        constraintLayout.setMaxWidth(0);
    }

    public void nofrag(){
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If there are fragments in the back stack, pop the back stack

        } else {

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main);
            if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, new defaultfrag())
                    .addToBackStack(null)
                    .commit();
        }
    }


}