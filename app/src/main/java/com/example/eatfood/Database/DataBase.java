package com.example.eatfood.Database;

import android.app.Notification;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.eatfood.Model.Orders;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;


public class DataBase extends SQLiteAssetHelper {

    private static final String Db_Name = "eatdb.db";
    private static final int DB_Ver = 1;

    public DataBase(Context context) {
        super(context, Db_Name, null, DB_Ver);
      //  context.openOrCreateDatabase(Db_Name, context.MODE_PRIVATE, null);

        }

    public List<Orders> getCart() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProductName", "ProductID", "Quantity", "Price", "Discount"};
        String sqlTable = "OrderDetail";
        qb.setTables(sqlTable);
        Cursor cursor = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<Orders> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                result.add(new Orders(cursor.getString(cursor.getColumnIndex("ProductId"))
                        , cursor.getString(cursor.getColumnIndex("ProductName")), cursor.getString(cursor.getColumnIndex("Quantity")),
                        cursor.getString(cursor.getColumnIndex("Price")), cursor.getString(cursor.getColumnIndex("Discount"))));
            } while (cursor.moveToNext());

        }
        return result;
    }
    public void addToCart(Orders orders)
    {
        SQLiteDatabase db = getReadableDatabase();

            String query = String.format("INSERT INTO OrderDetail(ProductId,ProductName,Quantity,Price,Discount) VALUES ('%s','%s','%s','%s','%s');"
                ,orders.getProductId()
                ,orders.getProductName(),
                orders.getQuantity(),
                orders.getPrice(),
                orders.getDiscount());
        db.execSQL(query);
    }
    public void cleanCart()
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }

}
