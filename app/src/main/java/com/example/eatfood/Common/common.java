package com.example.eatfood.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.eatfood.Model.User;

public class common {
    public static User currentOnlineUser;
    public static final String DELETE = "Delete";
    public static final String User_Key = "User";
    public static final  String Pwd_Key = "Password";

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "on That way";
        else
            return "Shipped";
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

}
