package com.example.eatfood;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatfood.Common.common;
import com.example.eatfood.Interface.ItemClickListener;
import com.example.eatfood.Model.Food_model;
import com.example.eatfood.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Food extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference food;

    private RecyclerView food_recycler;
    private RecyclerView.LayoutManager layoutManager;
    private String catagoryId;
    FirebaseRecyclerAdapter<Food_model, FoodViewHolder> adapter;

    //search Functionality

    FirebaseRecyclerAdapter<Food_model, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<String>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        database = FirebaseDatabase.getInstance();
        food = database.getReference("Foods");

        food_recycler = findViewById(R.id.recycler_food);
        // food_recycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        food_recycler.setLayoutManager(layoutManager);

        //GEt Intent Key
        if (getIntent() != null)
            catagoryId = getIntent().getStringExtra("CatagoryID");
        if (!catagoryId.isEmpty() && catagoryId != null) {

            if (common.isConnectedToInternet(getBaseContext())) {
                loadListFood(catagoryId);
            } else {
                Toast.makeText(Food.this, "Please check Internet Connection !", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //Search

        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter Your Food");

        loadSuggest(); //write function to load suggest from firebase

        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //when user typing suggest list changed
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<String>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when Search bar is close
                //Restore original adapter

                if (!enabled)
                    food_recycler.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void loadSuggest() {
        food.orderByChild("menuId").equalTo(catagoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Food_model model = postSnapshot.getValue(Food_model.class);
                    suggestList.add(model.getName()); //to add name of food to suggest list

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        final FirebaseRecyclerOptions<Food_model> options2 =
                new FirebaseRecyclerOptions.Builder<Food_model>()
                        .setQuery(food.orderByChild("name").equalTo(text.toString()), Food_model.class)
                        .build();

        searchAdapter =
                new FirebaseRecyclerAdapter<Food_model, FoodViewHolder>(options2) {
                    @Override
                    protected void onBindViewHolder(@NonNull FoodViewHolder foodViewHolder, final int i, @NonNull Food_model food_model) {
                        foodViewHolder.foodName.setText(food_model.getName());

                        Picasso.get().load(food_model.getImage()).into(foodViewHolder.foodImage);
                        final Food_model clickitem = food_model;
                        foodViewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(Food.this, FoodDetial.class);
                                intent.putExtra("FoodId", searchAdapter.getRef(i).getKey());
                                startActivity(intent);
                            }
                        });
                    }


                    @NonNull
                    @Override
                    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                        FoodViewHolder holder = new FoodViewHolder(view);
                        return holder;
                    }
                };
        food_recycler.setAdapter(searchAdapter);
        searchAdapter.startListening();


    }


    private void loadListFood(String catagoryId) {

        final FirebaseRecyclerOptions<Food_model> options =
                new FirebaseRecyclerOptions.Builder<Food_model>()
                        .setQuery(food.orderByChild("menuId").equalTo(catagoryId), Food_model.class)
                        .build();

        adapter =
                new FirebaseRecyclerAdapter<Food_model, FoodViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FoodViewHolder foodViewHolder, final int i, @NonNull Food_model food_model) {
                        foodViewHolder.foodName.setText(food_model.getName());

                        Picasso.get().load(food_model.getImage()).into(foodViewHolder.foodImage);
                        final Food_model clickitem = food_model;
                        foodViewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(Food.this, FoodDetial.class);
                                intent.putExtra("FoodId", adapter.getRef(i).getKey());
                                startActivity(intent);
                            }
                        });
                    }


                    @NonNull
                    @Override
                    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                        FoodViewHolder holder = new FoodViewHolder(view);
                        return holder;
                    }
                };
        food_recycler.setAdapter(adapter);
        adapter.startListening();


    }

}
