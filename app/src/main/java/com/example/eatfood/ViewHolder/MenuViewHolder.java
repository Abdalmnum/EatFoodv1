package com.example.eatfood.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatfood.Interface.ItemClickListener;
import com.example.eatfood.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

     public TextView menuTextItem;
     public ImageView menuImageItem;
     ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        menuTextItem = itemView.findViewById(R.id.cart_product_name);
        menuImageItem = itemView.findViewById(R.id.card_img);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
