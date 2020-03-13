package com.example.eatfood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatfood.Common.common;
import com.example.eatfood.Model.Request;
import com.example.eatfood.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {

//that activity for load list of data from firebase

    private FirebaseDatabase database;
    private DatabaseReference requests;

    private RecyclerView order_recycler;
    private RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        database = FirebaseDatabase.getInstance();
        //access table Request in firebase to get data from it
        requests = database.getReference("Requests");

        //acees recycler from activity_order_status
        order_recycler = findViewById(R.id.listOrders);
        // food_recycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        order_recycler.setLayoutManager(layoutManager);

        if(getIntent() == null) {
            loadOrders(common.currentOnlineUser.getPhone());
        }else
          loadOrders(getIntent().getStringExtra("userPhone"));

    }

    private void loadOrders(String phone) {
        final FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(requests.orderByChild("phone").equalTo(phone), Request.class)
                        .build();

        adapter =
                new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
                    @NonNull
                    @Override
                    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
                         OrderViewHolder orderViewHolder = new OrderViewHolder(view);
                        return orderViewHolder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int i, @NonNull Request request) {
                        orderViewHolder.txtOrderId.setText(adapter.getRef(i).getKey());
                        orderViewHolder.txtOrderStatus.setText(common.convertCodeToStatus(request.getStatus()));
                        orderViewHolder.txtOrderAddresse.setText(request.getAddress());
                        orderViewHolder.txtOrderPhone.setText(request.getPhone());

                    }
                };
        order_recycler.setAdapter(adapter);
        adapter.startListening();

    }



}
