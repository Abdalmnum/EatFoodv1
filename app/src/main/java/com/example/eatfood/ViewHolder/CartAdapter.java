package com.example.eatfood.ViewHolder;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.eatfood.Common.common;
import com.example.eatfood.Model.Orders;
import com.example.eatfood.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView txt_cart_name, txt_price;
    public ImageView img_cart_count;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        txt_price = itemView.findViewById(R.id.cart_item_price);
        img_cart_count = itemView.findViewById(R.id.cart_item_count);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("select Action :");
        contextMenu.add(0, 0, getAdapterPosition(), common.DELETE);
    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Orders> ordersList = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Orders> ordersList, Context context) {
        this.ordersList = ordersList;
        this.context = context;


    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_layout, parent, false);


        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        TextDrawable drawable = (TextDrawable) TextDrawable.builder().buildRound("" + ordersList.get(position).getQuantity(), android.R.color.holo_red_light);
        holder.img_cart_count.setImageDrawable(drawable);
        Locale locale = new Locale("en", "US");
        NumberFormat frmt = NumberFormat.getCurrencyInstance(locale);

        int price = (Integer.parseInt(ordersList.get(position).getPrice())) * (Integer.parseInt(ordersList.get(position).getQuantity()));
        holder.txt_price.setText(frmt.format(price));
        holder.txt_cart_name.setText(ordersList.get(position).getProductName());


    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }
}
