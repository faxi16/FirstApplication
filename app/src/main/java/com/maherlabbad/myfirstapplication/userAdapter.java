package com.maherlabbad.myfirstapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.maherlabbad.myfirstapplication.databinding.RecyclerRowUserLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class userAdapter extends RecyclerView.Adapter<userAdapter.userHolder> {
    private ArrayList<User> users;
    private UserListener userListener;
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public userAdapter(ArrayList<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }


    @NonNull
    @Override
    public userHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowUserLayoutBinding recyclerRowUserLayoutBinding = RecyclerRowUserLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new userHolder(recyclerRowUserLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull userHolder holder, int position) {
        Bitmap image = getUserImage(users.get(position).image);
        holder.recyclerRowUserLayoutBinding.textName.setText(users.get(position).name);
        holder.recyclerRowUserLayoutBinding.userImageProfile.setImageBitmap(image);
        holder.recyclerRowUserLayoutBinding.getRoot().setOnClickListener(v-> userListener.onUserClicked(users.get(position)));
    }

    @Override
    public int getItemCount() {
        if(users != null){
            return users.size();
        }else{
            return 0;
        }
    }

    public class userHolder extends RecyclerView.ViewHolder{
        RecyclerRowUserLayoutBinding recyclerRowUserLayoutBinding;
        public userHolder(RecyclerRowUserLayoutBinding recyclerRowUserLayoutBinding) {
            super(recyclerRowUserLayoutBinding.getRoot());
            this.recyclerRowUserLayoutBinding = recyclerRowUserLayoutBinding;
        }
    }
}
