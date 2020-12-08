package com.colab.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    AppServices services = new AppServices();
    DrawerLayout drawerLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    HomeAdapter homeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        NavigationView navView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        View navHeader = navView.getHeaderView(0);
        ImageView navDp = navHeader.findViewById(R.id.nav_userdp);
        TextView navUserName = navHeader.findViewById(R.id.nav_user_name);
        TextView navUserEmail = navHeader.findViewById(R.id.nav_email);
        RecyclerView cardRecyclerView = findViewById(R.id.home_card_recyclerview);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.bringToFront();
        navView.setNavigationItemSelectedListener(this);


        if (user != null) {
            Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(navDp);
            navUserName.setText(user.getDisplayName());
            navUserEmail.setText(user.getEmail());
        }


        Spinner spinner = findViewById(R.id.spinner);

        services.communityList.observe(this, new Observer<ArrayList<Map<String, Object>>>() {
            @Override
            public void onChanged(ArrayList<Map<String, Object>> maps) {
                ArrayList<String> communityNames = new ArrayList<>();
                for (Map map : maps) {
                    communityNames.add(map.get("name").toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(HomeScreenActivity.this, android.R.layout.simple_spinner_item, communityNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                db.collection("users").document(user.getUid()).collection("communities").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Query query = db.collection("communities").document(queryDocumentSnapshots.getDocuments().get(position).get("id").toString()).collection("users").orderBy("uid");
                        FirestoreRecyclerOptions<HomeModel> options = new FirestoreRecyclerOptions.Builder<HomeModel>().setQuery(query,HomeModel.class).setLifecycleOwner(HomeScreenActivity.this).build();
                        homeAdapter = new HomeAdapter(options,HomeScreenActivity.this);
                        cardRecyclerView.setLayoutManager(new LinearLayoutManager(HomeScreenActivity.this));
                        cardRecyclerView.hasFixedSize();
                        cardRecyclerView.setAdapter(homeAdapter);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        services.getCommunityList();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_join_community){
            Intent intent = new Intent(HomeScreenActivity.this,JoinCommunityActivity.class);
            intent.putExtra("source","Home");
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.nav_contact){
            Intent intent = new Intent(HomeScreenActivity.this,DetailsActivity.class);
            intent.putExtra("source","Home");
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.nav_skills){
            Intent intent = new Intent(HomeScreenActivity.this,SkillsActivity.class);
            intent.putExtra("source","Home");
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.nav_log_out){
            auth.signOut();
            Intent intent1 = new Intent(HomeScreenActivity.this, LoginActivity.class);
            startActivity(intent1);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}