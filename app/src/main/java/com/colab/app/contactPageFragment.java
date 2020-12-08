package com.colab.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class contactPageFragment extends BottomSheetDialogFragment {

    final String name;
    final String phn;
    final String whn;
    final String email;

    public contactPageFragment(String name, String phn, String whn, String email){
        this.name = name;
        this.phn = phn;
        this.whn = whn;
        this.email = email;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_page,container,false);

        TextView title = v.findViewById(R.id.contact_title);
        TextView phoneNo = v.findViewById(R.id.contact_phone);
        TextView whatsappNo = v.findViewById(R.id.contact_whatsapp_no);
        TextView emailId = v.findViewById(R.id.contact_email);


        title.setText(name);
        phoneNo.setText(phn);
        emailId.setText(email);
        String text = "Message " + whn;
        whatsappNo.setText(text);
        whatsappNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://wa.me/91" + whn;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                if (intent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        return v;
    }
}
