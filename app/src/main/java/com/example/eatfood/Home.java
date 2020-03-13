package com.example.eatfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatfood.Common.common;
import com.example.eatfood.Interface.ItemClickListener;
import com.example.eatfood.Model.Catagory;
import com.example.eatfood.Service.ListenOrder;
import com.example.eatfood.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase database;
    private DatabaseReference catagory;
    FirebaseRecyclerAdapter<Catagory, MenuViewHolder> adapter;

    private TextView personName;
    private String valueName;

    private RecyclerView menu_recycler;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        catagory = database.getReference("Category");

        Paper.init(this);


        FloatingActionButton fab = findViewById(R.id.fab_home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(Home.this, Cart.class);
                startActivity(intent);

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Set Name For User
        View headerView = navigationView.getHeaderView(1);

//        valueName = user.getEmail();
        //  valueName = getIntent().getStringExtra("user");
        personName = findViewById(R.id.nav_person_name);
        //  personName.setText(valueName);

//Load Menu
        menu_recycler = findViewById(R.id.recycler_menu);
        menu_recycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        menu_recycler.setLayoutManager(layoutManager);

        if (common.isConnectedToInternet(getBaseContext())) {
            loadMenu();
        } else {
            Toast.makeText(Home.this, "Please check Internet Connection !", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent service = new Intent(Home.this, ListenOrder.class);
        startService(service);

    }

    private void loadMenu() {

        final FirebaseRecyclerOptions<Catagory> options =
                new FirebaseRecyclerOptions.Builder<Catagory>()
                        .setQuery(catagory, Catagory.class)
                        .build();
        adapter =
                new FirebaseRecyclerAdapter<Catagory, MenuViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, final int i, @NonNull Catagory catagory) {
                        menuViewHolder.menuTextItem.setText(catagory.getName());

                        Picasso.get().load(catagory.getImage()).into(menuViewHolder.menuImageItem);
                        final Catagory clickitem = catagory;
                        menuViewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(Home.this, Food.class);
                                intent.putExtra("CatagoryID", adapter.getRef(i).getKey());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
                        MenuViewHolder holder = new MenuViewHolder(view);
                        return holder;
                    }
                };
        menu_recycler.setAdapter(adapter);
        adapter.startListening();


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.refresh) {
            loadMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_sign_out) {

            Paper.book().destroy();

            Intent signIn = new Intent(Home.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(signIn);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
