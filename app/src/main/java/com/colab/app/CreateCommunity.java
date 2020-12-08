package com.colab.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

public class CreateCommunity extends AppCompatActivity {

    AppServices services = new AppServices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);

        EditText name = findViewById(R.id.editTextCommunityName);
        EditText place = findViewById(R.id.editTextCommunityPlace);
        Button createbtn = findViewById(R.id.community_create_btn);
        Toolbar toolbar = findViewById(R.id.create_toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);




        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                services.createCommunity(name.getText().toString(),place.getText().toString(),getApplicationContext());
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}