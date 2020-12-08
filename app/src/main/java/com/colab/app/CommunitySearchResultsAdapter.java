package com.colab.app;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommunitySearchResultsAdapter extends RecyclerView.Adapter<CommunitySearchResultsAdapter.ViewHolder> {

    AppServices services = new AppServices();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    final ArrayList<Map<String,Object>> result;

    public CommunitySearchResultsAdapter(ArrayList<Map<String, Object>> result) {
        this.result = result;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.communities_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(result.size() > 0){
            holder.name.setText(result.get(position).get("name").toString());
            holder.place.setText(result.get(position).get("place").toString());
            db.collection("users").document(user.getUid()).collection("communities").whereEqualTo("id",result.get(position).get("id")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        if(Objects.requireNonNull(task.getResult()).size() > 0){
                            holder.joinbtn.setText(R.string.exit);
                            holder.joinbtn.setTextColor(Color.parseColor("#EF5349"));
                            holder.joinbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    db.collection("users").document(user.getUid()).collection("communities").document(result.get(position).get("id").toString()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                db.collection("communities").document(result.get(position).get("id").toString()).collection("users").document(user.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            notifyDataSetChanged();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            holder.joinbtn.setText(R.string.join);
                            holder.joinbtn.setTextColor(Color.parseColor("#808080"));
                            holder.joinbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String id = result.get(position).get("id").toString();
                                    String name = result.get(position).get("name").toString();
                                    Map<String, String> data = new HashMap<>();
                                    data.put("id", id);
                                    data.put("name",name);
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
                                                            notifyDataSetChanged();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            });

        }
        else {
            holder.name.setText(R.string.no_communities_found);
            holder.place.setText(R.string.create_a_new_community);
            holder.joinbtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return result.size() > 0 ? result.size() : 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView place;
        private final Button joinbtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.community_name);
            place = itemView.findViewById(R.id.community_place);
            joinbtn = itemView.findViewById(R.id.joinbtn);
        }
    }
}
