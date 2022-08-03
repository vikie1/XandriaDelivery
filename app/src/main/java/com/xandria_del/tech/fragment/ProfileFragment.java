package com.xandria_del.tech.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xandria_del.tech.R;
import com.xandria_del.tech.activity.user.LoginActivity;
import com.xandria_del.tech.constants.FirebaseRefs;
import com.xandria_del.tech.model.User;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private DatabaseReference firebaseDatabaseReference;
    private String userId;

    private View view;
    private Context context;
    private double points;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.DELIVERY_USERS);
        userId = Objects.requireNonNull(Objects.requireNonNull(
                        FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .replaceAll("[\\-+. ^:,]","_");

        Button logoutBtn = view.findViewById(R.id.logout_btn);
        Button resetPassword = view.findViewById(R.id.reset_password);

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(context, LoginActivity.class));
        });
        resetPassword.setOnClickListener(v -> setUpPasswordReset());

        getUser();
        return view;
    }

    private void setUpPasswordReset() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.password_update);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button passwordResetButton = dialog.findViewById(R.id.password_reset_btn);
        TextInputEditText passwordText = dialog.findViewById(R.id.password_input);
        TextInputEditText confirmPasswordText = dialog.findViewById(R.id.password_conf_input);
        passwordResetButton.setOnClickListener(v -> {
            String newPassword = passwordText.getText() == null ? "" : passwordText.getText().toString() ;
            String confPassword = confirmPasswordText.getText() == null ? "" : confirmPasswordText.getText().toString();

            if (newPassword.isEmpty() && confPassword.isEmpty()) {
                Toast.makeText(context,"Enter a valid password", Toast.LENGTH_LONG).show();
            }
            else if (newPassword.equals(confPassword)) {
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updatePassword(newPassword);
                Toast.makeText(context, "Password updated successfully", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
            else {
                Toast.makeText(context, "Passwords should match", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void getUser() {
        firebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "An error occurred while retrieving the details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUser(User value) {
        TextView usernameText = view.findViewById(R.id.display_username);
        TextView emailText = view.findViewById(R.id.display_email);
        TextView phoneText = view.findViewById(R.id.display_phone);
        TextView pointsView = view.findViewById(R.id.display_points);

        usernameText.setText(HtmlCompat.fromHtml(
                getString(R.string.name).concat(" ").concat(value.getName()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        emailText.setText(HtmlCompat.fromHtml(
                getString(R.string.email).concat(" ").concat(value.getEmail()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        phoneText.setText(HtmlCompat.fromHtml(
                getString(R.string.phone).concat(" ").concat(value.getPhoneNumber()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        pointsView.setText(HtmlCompat.fromHtml(
                getString(R.string.points).concat(" ").concat(String.valueOf(value.getPoints())),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
    }
}