package com.colab.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class JoinCommunityActivity extends AppCompatActivity {


    AppServices services = new AppServices();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_community);

        SearchView searchCommunities = findViewById(R.id.search_communities);
        RecyclerView searchResults = findViewById(R.id.search_results);
        Button createbtn = findViewById(R.id.createbtn);
        Button joinbtn = findViewById(R.id.join_community_button);
        Toolbar toolbar = findViewById(R.id.join_toolbar);

        setSupportActionBar(toolbar);

        if(Objects.equals(Objects.requireNonNull(getIntent().getExtras()).get("source"), "Home")){
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }


        createbtn.setVisibility(View.GONE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        searchResults.setLayoutManager(new LinearLayoutManager(this));

        services.result.observe(this, new Observer<ArrayList<Map<String, Object>>>() {
            @Override
            public void onChanged(ArrayList<Map<String, Object>> maps) {
                CommunitySearchResultsAdapter communitySearchResultsAdapter = new CommunitySearchResultsAdapter(maps);
                searchResults.setAdapter(communitySearchResultsAdapter);
                if(maps.size() == 0){
                    createbtn.setVisibility(View.VISIBLE);
                }
                else {
                    createbtn.setVisibility(View.GONE);
                }
            }
        });

        searchCommunities.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    services.getQueryCommunities(" ");
                }else {
                    services.getQueryCommunities(newText);
                }
                return false;
            }
        });

        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CreateCommunity.class);
                startActivity(intent);
            }
        });

        joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),HomeScreenActivity.class);
                intent.putExtra("source","Join");
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}