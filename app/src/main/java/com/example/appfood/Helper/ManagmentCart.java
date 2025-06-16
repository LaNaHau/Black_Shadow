package com.example.appfood.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.appfood.Domain.Foods;
import com.google.gson.Gson;

import java.util.ArrayList;


public class ManagmentCart {
    private Context context;
    private TinyDB tinyDB;
    private String userId;


    public ManagmentCart(Context context, String userId) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
        this.userId = userId;

    }

    public void insertFood(Foods item) {
        ArrayList<Foods> listpop = getListCart();
        boolean existAlready = false;
        int n = 0;
        for (int i = 0; i < listpop.size(); i++) {
            if (listpop.get(i).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = i;
                break;
            }
        }
        if (existAlready) {
            listpop.get(n).setNumberInCart(item.getNumberInCart());
        } else {
            listpop.add(item);
        }
        tinyDB.putListObject("CartList_" + userId, listpop);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Foods> getListCart() {
        return tinyDB.getListObject("CartList_" + userId, Foods.class);
    }

    public Double getTotalFee() {
        ArrayList<Foods> listItem = getListCart();
        double fee = 0;
        for (int i = 0; i < listItem.size(); i++) {
            fee = fee + (listItem.get(i).getPrice() * listItem.get(i).getNumberInCart());
        }
        return fee;
    }

    public void minusNumberItem(ArrayList<Foods> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (listItem.get(position).getNumberInCart() == 1) {
            listItem.remove(position);
        } else {
            listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CartList_" + userId, listItem);
        changeNumberItemsListener.change();
    }

    public void plusNumberItem(ArrayList<Foods> listItem, int position,
                               ChangeNumberItemsListener changeNumberItemsListener) {
        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList_" + userId, listItem);
        changeNumberItemsListener.change();
    }

    public void removeItem(ArrayList<Foods> listItem, int position,
                           ChangeNumberItemsListener changeNumberItemsListener) {
        listItem.remove(position);
        tinyDB.putListObject("CartList_" + userId, listItem);
        changeNumberItemsListener.change();
    }

    public void clearCart() {
        tinyDB.remove("CartList_" + userId);
    }


}
