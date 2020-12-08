package com.colab.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(user!=null) {

            db.collection("users").document(user.getUid()).collection("communities").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getDocuments().size() > 0) {
                            Intent intent = new Intent(SplashScreenActivity.this, HomeScreenActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            db.collection("users").document(user.getUid()).collection("skills").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().getDocuments().size() > 0) {
                                            Intent intent = new Intent(SplashScreenActivity.this, JoinCommunityActivity.class);
                                            intent.putExtra("source","SplashScreen");
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            db.collection("users").document(user.getUid()).collection("details").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult().getDocuments().size() > 0) {
                                                            Intent intent = new Intent(SplashScreenActivity.this, SkillsActivity.class);
                                                            intent.putExtra("source","SplashScreen");
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Intent intent = new Intent(SplashScreenActivity.this, DetailsActivity.class);
                                                            intent.putExtra("source","SplashScreen");
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }

                    }
                }
            });
        }
        else {
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}