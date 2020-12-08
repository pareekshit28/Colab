package com.colab.app;

import android.content.Context;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class AppServices extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public MutableLiveData<ArrayList<Map<String, Object>>> result = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Map<String, Object>>> communityList = new MutableLiveData<>();

    public void addUserDetails(String phn, String whphn) {
        Map<String, Object> details = new HashMap<>();
        assert user != null;
        details.put("uid", user.getUid());
        details.put("name",user.getDisplayName());
        details.put("email",user.getEmail());
        details.put("dp",user.getPhotoUrl().toString());
        details.put("phone", phn);
        details.put("whatsapp", whphn);
        db.collection("users").document(user.getUid()).collection("details").document("details").set(details);
    }

    public void getQueryCommunities(String query) {
        ArrayList<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
        db.collection("query").orderBy("query").startAt(query).endAt(query + "\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        temp.add(documentSnapshot.getData());
                    }
                } else {
                    Map<String, Object> error = new HashMap<>();
                    error.put("name", "Error, Try again Later");
                    error.put("place", "");
                    temp.add(error);
                }
                result.setValue(temp);
            }
        });

    }

    public void createCommunity(String name, String place, Context context) {
        String query = name.toLowerCase().replace(" ", "");
        String id = UUID.randomUUID().toString();
        Map<String, String> community = new HashMap<>();
        community.put("name", name);
        community.put("place", place);
        community.put("query", query);
        community.put("id", id);

        db.collection("communities").document(id).collection("details").document("details").set(community).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Map<String, String> addQuery = new HashMap<>();
                    addQuery.put("name", name);
                    addQuery.put("place", place);
                    addQuery.put("id", id);
                    addQuery.put("query",query);
                    db.collection("query").document(id).set(addQuery).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Community Created Successfully!", Toast.LENGTH_SHORT).show();
                                joinCommunity(id,name, context);
                            }
                        }
                    });
                }
            }
        });
    }

    public void joinCommunity(String id, String name, Context context) {
        Map<String, String> data = new HashMap<>();
        data.put("id", id);
        data.put("name", name);
        assert user != null;
        db.collection("users").document(user.getUid()).collection("communities").document(id).set(data).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Map<String, String> userDetails = new HashMap<>();
                    userDetails.put("uid", user.getUid());
                    db.collection("communities").document(id).collection("users").document(user.getUid()).set(userDetails).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Joined Community Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }


    public void getCommunityList(){
        db.collection("users").document(user.getUid()).collection("communities").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    ArrayList<Map<String,Object>> communities = new ArrayList<>();
                    for(DocumentSnapshot documentSnapshot : value.getDocuments()){
                        Map<String,Object> map = new HashMap<>();
                        map.put("id",documentSnapshot.get("id").toString());
                        map.put("name",documentSnapshot.get("name").toString());
                        communities.add(map);
                    }
                    communityList.setValue(communities);
                }

            }
        });
    }
    }


