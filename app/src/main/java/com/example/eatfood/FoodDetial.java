package com.example.eatfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.eatfood.Common.common;
import com.example.eatfood.Database.DataBase;
import com.example.eatfood.Model.Food_model;
import com.example.eatfood.Model.Orders;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetial extends AppCompatActivity {

    private TextView food_name, food_price, food_description;
    private ImageView food_img;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton btnCart;
    private ElegantNumberButton numberButton;

    String foodID = "";
    FirebaseDatabase database;
    DatabaseReference foods;
    Food_model foodModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detial);

        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");

        //InitView
        numberButton = findViewById(R.id.number_button);

        btnCart = findViewById(R.id.fab_cart);

        //Adding to Cart
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DataBase(getBaseContext()).addToCart(new Orders(
                        foodID,
                        foodModel.getName(),
                        numberButton.getNumber(),
                        foodModel.getPrice(),
                        foodModel.getDiscount()

                ));
                Toast.makeText(FoodDetial.this,"Added to Cart Suceessfuly",Toast.LENGTH_SHORT).show();
               // Intent in = new Intent(FoodDetial.this,Cart.class);
               // startActivity(in);
            }
        });

        food_img = findViewById(R.id.img_food);

        food_name = findViewById(R.id.food_name);
        food_price = findViewById(R.id.food_price);
        food_description = findViewById(R.id.food_description);

        collapsingToolbarLayout = findViewById(R.id.collasping_food);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if (getIntent() != null)
            foodID = getIntent().getStringExtra("FoodId");
        if (!foodID.isEmpty()) {

            if (common.isConnectedToInternet(getBaseContext())) {
                getDetialFood(foodID);
            } else {
                Toast.makeText(FoodDetial.this, "Please check Internet Connection !", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void getDetialFood(String foodID) {

        foods.child(foodID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                foodModel = dataSnapshot.getValue(Food_model.class);
                Picasso.get().load(foodModel.getImage()).into(food_img);

                collapsingToolbarLayout.setTitle(foodModel.getName());
                food_price.setText(foodModel.getPrice());

                food_name.setText(foodModel.getName());
                food_description.setText(foodModel.getDescription());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
