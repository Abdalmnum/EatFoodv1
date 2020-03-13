package com.example.eatfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eatfood.Common.common;
import com.example.eatfood.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    private EditText ed_name, ed_email, ed_password, ed_phone;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private Button signup_btn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ed_name = findViewById(R.id.input_name);
        ed_email = findViewById(R.id.input_email);
        ed_phone = findViewById(R.id.input_phone);
        ed_password = findViewById(R.id.input_password);


        signup_btn = findViewById(R.id.btn_signup);
        loadingBar = new ProgressDialog(this);


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();


        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();

            }
        });

    }

    public static String EncodeEmail(String email) {
        return email.replace(".", ",");
    }

    private void createAccount() {

        final String name = ed_name.getText().toString();
        final String email = ed_email.getText().toString();
        final String password = ed_password.getText().toString();
        final String phone = ed_phone.getText().toString();


        if (TextUtils.isEmpty(name)) {
            ed_name.setError("Please Set Your Name ..!");
        } else if (TextUtils.isEmpty(email)) {
            ed_email.setError("Please Set Your Email ..!");
        } else if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            ed_password.setError("between 4 and 10 alphanumeric characters");

        } else if (TextUtils.isEmpty(phone)) {
            ed_phone.setError("Please Set Your Phone ..!");
        } else if (common.isConnectedToInternet(getBaseContext())) {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            EncodeEmail(email);

            addUser(name, email, phone, password);
        } else {
            Toast.makeText(SignUp.this, "Please check Internet Connection !", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void addUser(final String name, final String email, final String phone, final String password) {

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.child("Users").child(phone).exists())) {

                    Toast.makeText(SignUp.this, "This " + phone + " already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(SignUp.this, "Please try again using another phone.", Toast.LENGTH_SHORT).show();

                } else {
                    User user = new User(name, email, password, phone);

                    databaseReference.child("Users").child(phone).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(SignUp.this, SignIn.class);
                                        startActivity(intent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(SignUp.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
