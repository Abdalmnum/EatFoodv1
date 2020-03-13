package com.example.eatfood;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatfood.Common.common;
import com.example.eatfood.Database.DataBase;
import com.example.eatfood.Model.Orders;
import com.example.eatfood.Model.Request;
import com.example.eatfood.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    RecyclerView list_cart;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;


    TextView total_price;
    AppCompatButton place_order_btn;

    List<Orders> cart = new ArrayList<>();
    CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");


        //init list

        list_cart = findViewById(R.id.list_Cart);
        //  list_cart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        list_cart.setLayoutManager(layoutManager);

        total_price = findViewById(R.id.total);
        place_order_btn = findViewById(R.id.btn_placeOrder);

        //place orders in request
        place_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cart.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your Cart is Empty !!", Toast.LENGTH_SHORT).show();

            }
        });

        loadListRequest();


    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more Step..!");
        alertDialog.setMessage("Enter your adresse: ");

        final EditText edtAddresse = new EditText(Cart.this);
        LinearLayout.LayoutParams oneP = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        edtAddresse.setLayoutParams(oneP);
        alertDialog.setView(edtAddresse);

        alertDialog.setIcon(R.drawable.ic_local_floating_btn);
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Request request = new Request(
                        common.currentOnlineUser.getPhone(),
                        common.currentOnlineUser.getName(),
                        edtAddresse.getText().toString(),
                        total_price.getText().toString(),
                        cart
                );
                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);

                new DataBase(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Your Order Placed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void loadListRequest() {
        cart = new DataBase(this).getCart();
        cartAdapter = new CartAdapter(cart, this);
        cartAdapter.notifyDataSetChanged();

        list_cart.setAdapter(cartAdapter);

        //calculate total price
        int total = 0;
        for (Orders orders : cart)
            total += (Integer.parseInt(orders.getPrice())) * (Integer.parseInt(orders.getQuantity()));

        Locale locale = new Locale("en", "US");
        NumberFormat frmt = NumberFormat.getCurrencyInstance(locale);

        total_price.setText(frmt.format(total));
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(common.DELETE))

            deleteCartItem(item.getOrder());

        return true;
    }

    private void deleteCartItem(int order) {
        cart.remove(order);

//remove it from sqllite database
        new DataBase(this).cleanCart();
        //update data from List to sqllite

        for (Orders item : cart)

            new DataBase(this).addToCart(item);

        loadListRequest();
    }
}

