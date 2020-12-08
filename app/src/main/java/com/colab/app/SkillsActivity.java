package com.colab.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SkillsActivity extends AppCompatActivity {

    SkillsListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills);

        ImageButton addbtn = findViewById(R.id.addButton);
        ImageButton delbtn = findViewById(R.id.delbtn);
        EditText typeSkills = findViewById(R.id.editTextEnterSkill);
        Button nextbtn = findViewById(R.id.add_skills_button);
        RecyclerView recyclerView = findViewById(R.id.skills_recycle_view);
        Toolbar toolbar = findViewById(R.id.skills_toolbar);

        setSupportActionBar(toolbar);

        if(Objects.equals(Objects.requireNonNull(getIntent().getExtras()).get("source"), "Home")){
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            nextbtn.setVisibility(View.GONE);
        }

        //SkillsList RecyclerView Implementation
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = db.collection("users").document(user.getUid()).collection("skills").orderBy("skill");
        FirestoreRecyclerOptions<SkillsList> options = new FirestoreRecyclerOptions.Builder<SkillsList>().setQuery(query,SkillsList.class).build();
        adapter = new SkillsListItemAdapter(options);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        typeSkills.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0){
                    addbtn.setVisibility(View.VISIBLE);
                }else {
                    addbtn.setVisibility(View.GONE);
                }
            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = typeSkills.getText().toString();
                Map<String,String> data = new HashMap<String, String>();
                data.put("skill",text);
                db.collection("users").document(user.getUid()).collection("skills").add(data);
                typeSkills.setText("");
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),JoinCommunityActivity.class);
                intent.putExtra("source","Skills");
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}