package com.example.eatfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eatfood.Common.common;
import com.example.eatfood.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button btn_login, btn_signUp;
    private TextView logo_text;
    private String user;
    private String pwd;

    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_login = findViewById(R.id.btn_login);
        btn_signUp = findViewById(R.id.btn_signUp);

        logo_text = findViewById(R.id.txt_logo);
        Paper.init(this);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(MainActivity.this, SignIn.class);
                startActivity(in);
            }
        });
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(MainActivity.this, SignUp.class);
                startActivity(in);
            }
        });
        user = Paper.book().read(common.User_Key);
        pwd = Paper.book().read(common.Pwd_Key);

        if (user != null && pwd != null)
        {
            if(!user.isEmpty() && !pwd.isEmpty())
                login(user,pwd);
                
        }


    }

    private void login(final String phone, final String password) {
        loadingBar = new ProgressDialog(MainActivity.this);

        loadingBar.setTitle("Login Account");
        loadingBar.setMessage("Please wait, while we are checking the credentials.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

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
                        Toast.makeText(MainActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

    }
}
