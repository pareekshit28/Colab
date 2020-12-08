package com.colab.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class HomeAdapter extends FirestoreRecyclerAdapter<HomeModel,HomeAdapter.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final Context context;

    public HomeAdapter(@NonNull FirestoreRecyclerOptions<HomeModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull HomeModel model) {
        String uid = model.getUid();
        holder.constraintLayout.setVisibility(View.GONE);
        holder.name.setVisibility(View.GONE);
        holder.dp.setVisibility(View.GONE);
        holder.contact.setVisibility(View.GONE);
        holder.chipGroup.setVisibility(View.GONE);

            if(!uid.equals(user.getUid())){
                holder.constraintLayout.setPadding(0,24,0,24);
                db.collection("users").document(uid).collection("details").document("details").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        holder.name.setText(Objects.requireNonNull(documentSnapshot.get("name")).toString());
                        Glide.with(context).load(Objects.requireNonNull(documentSnapshot.get("dp")).toString()).circleCrop().into(holder.dp);
                        holder.constraintLayout.setVisibility(View.VISIBLE);
                        holder.name.setVisibility(View.VISIBLE);
                        holder.dp.setVisibility(View.VISIBLE);
                        holder.contact.setVisibility(View.VISIBLE);
                        holder.chipGroup.setVisibility(View.VISIBLE);
                        holder.contact.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                contactPageFragment contactPage = new contactPageFragment(Objects.requireNonNull(documentSnapshot.get("name")).toString(), documentSnapshot.get("phone").toString(), documentSnapshot.get("whatsapp").toString(), documentSnapshot.get("email").toString());
                                contactPage.show(((AppCompatActivity) context).getSupportFragmentManager(),"ModalBottomSheet");
                            }
                        });
                    }
                });
                db.collection("users").document(uid).collection("skills").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                            Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.skills_chip_item,holder.chipGroup,false);
                            chip.setText(Objects.requireNonNull(doc.get("skill")).toString());
                            holder.chipGroup.addView(chip);
                        }
                    }
                });
            }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView dp;
        private final ChipGroup chipGroup;
        private final Button contact;
        private final ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.home_item_name);
            dp = itemView.findViewById(R.id.home_item_dp);
            chipGroup = itemView.findViewById(R.id.home_card_chip_group);
            contact = itemView.findViewById(R.id.home_item_contact_btn);
            constraintLayout = itemView.findViewById(R.id.home_item_constraint);
        }
    }
}
