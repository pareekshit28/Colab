package com.colab.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        TextView userName = findViewById(R.id.contact_username);
        TextView userMail = findViewById(R.id.contact_email_id);
        ImageView dp = findViewById(R.id.dp);
        EditText phnin = (EditText) findViewById(R.id.editTextPhoneNumber);
        EditText whphnin = (EditText) findViewById(R.id.editTextWhatsappNumber);
        Button nextbtn = findViewById(R.id.details_button);
        Toolbar toolbar = findViewById(R.id.details_toolbar);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);

        if(Objects.equals(Objects.requireNonNull(getIntent().getExtras()).get("source"), "Home")){
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            nextbtn.setText(R.string.update);
        }

        if(googleSignInAccount!=null){
            userName.setText(googleSignInAccount.getDisplayName());
            userMail.setText(googleSignInAccount.getEmail());
            Glide.with(this).load(googleSignInAccount.getPhotoUrl()).circleCrop().into(dp);
        }

        assert user != null;
        db.collection("users").document(user.getUid()).collection("details").document("details").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(Objects.requireNonNull(task.getResult()).exists()){
                        phnin.setText(Objects.requireNonNull(task.getResult().get("phone")).toString());
                        whphnin.setText(Objects.requireNonNull(task.getResult().get("whatsapp")).toString());
                    }
                }
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppServices().addUserDetails(phnin.getText().toString(),whphnin.getText().toString());
                if(Objects.equals(Objects.requireNonNull(getIntent().getExtras()).get("source"), "Home")){
                    Intent intent = new Intent(getApplicationContext(),HomeScreenActivity.class);
                    intent.putExtra("source","Details");
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(getApplicationContext(),SkillsActivity.class);
                    intent.putExtra("source","Details");
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onNavigateUp();
    }
}