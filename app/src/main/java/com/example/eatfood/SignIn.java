package com.example.eatfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eatfood.Common.common;
import com.example.eatfood.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    private Button SignIn_btn;

    private EditText InputPassword, InputPhone;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";

    private CheckBox switchRememberMe;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        InputPhone = findViewById(R.id.input_SignIn_Phone);
        InputPassword = findViewById(R.id.input_SignIn_password);
        SignIn_btn = findViewById(R.id.btn_login);

        loadingBar = new ProgressDialog(this);

        switchRememberMe = findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        firebaseAuth = FirebaseAuth.getInstance();
        SignIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
    }

    private void LoginUser() {

        String phone = InputPhone.getText().toString();

        String password = InputPassword.getText().toString();

        if (phone.isEmpty()) {
            InputPhone.setError("enter a valid email");

        } else if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            InputPassword.setError("between 4 and 10 alphanumeric characters");

        } else if (common.isConnectedToInternet(getBaseContext())) {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);
        } else {
            Toast.makeText(SignIn.this, "Please check Internet Connection !", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if (switchRememberMe.isChecked()) {
            Paper.book().write(common.User_Key, phone);
            Paper.book().write(common.Pwd_Key, password);

        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()) {

                    loadingBar.dismiss();

                    User user = dataSnapshot.child(parentDbName).child(phone).getValue(User.class);
                    //user.setPhone(phone);

                    if (user.getPassword().equals(password)) {


                        Intent intent = new Intent(getApplicationContext(), Home.class);

                        common.currentOnlineUser = user;

                        startActivity(intent);
                        finish();
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(SignIn.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignIn.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

    }

}

