package com.maherlabbad.myfirstapplication;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maherlabbad.myfirstapplication.databinding.ItemContainerReceivedMessageBinding;
import com.maherlabbad.myfirstapplication.databinding.ItemContainerSentMessageBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ChatMessage> chatMessageList;
    private Bitmap receiverProfileImage;
    private String senderId;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVER = 2;

    public ChatAdapter( ArrayList<ChatMessage> chatMessageList, Bitmap receiverProfileImage, String senderId) {
        this.chatMessageList = chatMessageList;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessageList.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }else{
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chatMessageList.get(position));
        }else{
            ((ReceivedMessageViewHolder) holder).setData(chatMessageList.get(position),receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }
        public void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.Datetime);

            if (chatMessage.isseen ) {
                binding.seenIc.setVisibility(View.VISIBLE);
                binding.notSeenIc.setVisibility(View.GONE);
            } else {
                binding.seenIc.setVisibility(View.GONE);
                binding.notSeenIc.setVisibility(View.VISIBLE);
            }

        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        public void setData(ChatMessage chatMessage, Bitmap image){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.Datetime);
            binding.imageProfile.setImageBitmap(image);
        }

    }

}
