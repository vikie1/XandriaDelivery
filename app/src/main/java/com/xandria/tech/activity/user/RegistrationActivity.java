package com.xandria.tech.activity.user;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.xandria.tech.R;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.model.User;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputEditText username, email, password, confirmPwd;
    private EditText phone;
    private ProgressBar PB;
    private CountryCodePicker ccp;
    private FirebaseAuth mAuth;
    private String contact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        username = findViewById(R.id.regUserName);
        email = findViewById(R.id.regEmail);
        phone = findViewById(R.id.phone_number_input);
        password = findViewById(R.id.regPassword);
        confirmPwd = findViewById(R.id.regConfirmPassword);
        ccp = findViewById(R.id.ccp);
        Button registerBtn = findViewById(R.id.RegisterBtn);
        TextView loginTxtBtn = findViewById(R.id.LoginTextBtn);
        PB = findViewById(R.id.loadingPB);
        mAuth = FirebaseAuth.getInstance();

        loginTxtBtn.setOnClickListener(view -> startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)));

        registerBtn.setOnClickListener(view -> {
            PB.setVisibility(View.VISIBLE);
            phoneVerificationDialog();
        });
    }

    private void verifyPhoneNumber(Dialog dialog) {
        String unVerifiedContact = phone.getText().toString().trim().isEmpty() ?
                null :
                ccp.getSelectedCountryCodeWithPlus() + phone.getText().toString();

        EditText editText = dialog.findViewById(R.id.verification_code_input);
        Button verifyBtn = dialog.findViewById(R.id.verify_btn);
        AtomicReference<String> verificationCode = new AtomicReference<>();
        AtomicReference<String> verificationId = new AtomicReference<>(); // could be used for creating a credential but i won't use it as no phone signing is being performed
        if (unVerifiedContact != null){
            PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(unVerifiedContact)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(RegistrationActivity.this)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            verificationCode.set(phoneAuthCredential.getSmsCode());
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(
                                    RegistrationActivity.this,
                                    "Phone not verified so not saved",
                                    Toast.LENGTH_LONG).show();
                            registerUser();
                        }

                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            verificationId.set(s);
                        }
                    })
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
        } else {
            Toast.makeText(
                    RegistrationActivity.this,
                    "You will not be able to place orders unless phone number is verified",
                    Toast.LENGTH_LONG).show();
            registerUser();
        }

        verifyBtn.setOnClickListener(v -> {
            if (editText.getText().toString().equals(verificationCode.get()) || verificationId.get() != null) //this means the number is verified
                RegistrationActivity.this.contact = unVerifiedContact; // we can now save the contact as it is verified
            else Toast.makeText(RegistrationActivity.this, "There's an error in your verification", Toast.LENGTH_LONG).show();
            registerUser();
            dialog.dismiss();
        });
    }

    /*
    registration remains only by email and password
    phone number is only verified and used for orders not signing in
     */
    private void registerUser() {
        String userNameStr = Objects.requireNonNull(username.getText()).toString();
        String emailStr = Objects.requireNonNull(email.getText()).toString();
        String passwordStr = Objects.requireNonNull(password.getText()).toString();
        String confirmPwdStr = Objects.requireNonNull(confirmPwd.getText()).toString();
        if(!passwordStr.equals(confirmPwdStr)){
            Toast.makeText(RegistrationActivity.this, "Please Enter Matching Passwords", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(userNameStr )
                && TextUtils.isEmpty(emailStr) &&
                TextUtils.isEmpty(passwordStr) && TextUtils.isEmpty(confirmPwdStr)){
            Toast.makeText(RegistrationActivity.this, "Enter All Fields", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(RegistrationActivity.this, task -> {
                if(task.isSuccessful()){
                    PB.setVisibility(View.GONE);
                    setUpUser();
                    Toast.makeText(RegistrationActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                }else{
                    PB.setVisibility(View.GONE);
                    Toast.makeText(RegistrationActivity.this, "Failed to Register User", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                }
            });
        }
    }

    private void setUpUser() {
        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        String userId = email != null ? email.replaceAll("[\\-+. ^:,]", "_") : null;

        User user = new User(
                Objects.requireNonNull(username.getText()).toString(),
                userId,
                email,
                contact
        );

        DatabaseReference userDBRef = FirebaseDatabase.getInstance().getReference(FirebaseRefs.DELIVERY_USERS);
        userDBRef.child(user.getUserId()).setValue(user);
    }

    private void phoneVerificationDialog(){
        Dialog dialog = new Dialog(RegistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.phone_verification);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        verifyPhoneNumber(dialog);
        dialog.show();
    }
}