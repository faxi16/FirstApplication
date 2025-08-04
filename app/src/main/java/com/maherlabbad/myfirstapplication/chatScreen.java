package com.maherlabbad.myfirstapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.maherlabbad.myfirstapplication.databinding.FragmentChatScreenBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class chatScreen extends Fragment {
    private FragmentChatScreenBinding binding;
    private NavController navController;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> messages;
    private String ReceiverId;
    private Bitmap imgProfile;
    private String img;

    private ArrayList<String> mes_Ids;
    private String SenderId;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private int mes_size = 0;

    public chatScreen() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.chatRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.chatRecyclerview.setAdapter(chatAdapter);
        navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView2);
        loadDetails();
        firestore.collection(Constants.KEY_COLLECTION_USERS).document(SenderId).update(Constants.KEY_ACTIVE_CHAT_WITH,ReceiverId);
        firestore.collection(Constants.KEY_COLLECTION_USERS).document(SenderId).update(Constants.KEY_STATUS,"online");
        getMessages();
        listeners();

    }

    @Override
    public void onStop() {
        super.onStop();
        firestore.collection(Constants.KEY_COLLECTION_USERS).document(SenderId).update(Constants.KEY_ACTIVE_CHAT_WITH,"");
        firestore.collection(Constants.KEY_COLLECTION_USERS).document(SenderId).update(Constants.KEY_STATUS,"offline");
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    /*
    private void getMessages() {
    messages.clear();

    firestore.collection(Constants.KEY_COLLECTION_USERS).document(SenderId).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if(error != null){
                Log.e("Firestore", "Listener failed", error);
                return;
            }else{
                if(value != null){
                    firestore.collection(Constants.KEY_COLLECTION_USERS).document(ReceiverId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            binding.status.setText(documentSnapshot.getString(Constants.KEY_STATUS));
                            if(documentSnapshot.getString(Constants.KEY_ACTIVE_CHAT_WITH).equals(SenderId)){
                                firestore.collection(Constants.KEY_COLLECTION_USERS).document(SenderId).collection("messages").orderBy(Constants.KEY_TIMESTAMP).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(error != null){
                                            Log.e("Firestore", "Listener failed", error);
                                            return;
                                        }
                                        if(value != null){
                                            value.getDocuments().forEach(documentSnapshot -> {
                                                String senderId = documentSnapshot.getString(Constants.KEY_SENDER_ID);
                                                String receiverId = documentSnapshot.getString(Constants.KEY_RECEIVER_ID);
                                                if(senderId.equals(SenderId) && receiverId.equals(ReceiverId)){
                                                    documentSnapshot.getReference().update("isseen",true);
                                                }
                                            });
                                        }

                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Listener failed", e);
                        }
                    });
                    for(DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        if(!mes_Ids.contains(documentSnapshot.getId())) {
                            String senderId = documentSnapshot.getString(Constants.KEY_SENDER_ID);
                            String receiverId = documentSnapshot.getString(Constants.KEY_RECEIVER_ID);
                            if ((senderId.equals(SenderId) && receiverId.equals(ReceiverId))) {
                                if(documentSnapshot.getDate(Constants.KEY_TIMESTAMP) != null) {
                                    ChatMessage chatMessage = new ChatMessage();
                                    chatMessage.senderId = documentSnapshot.getString(Constants.KEY_SENDER_ID);
                                    chatMessage.receiverId = documentSnapshot.getString(Constants.KEY_RECEIVER_ID);
                                    chatMessage.message = documentSnapshot.getString(Constants.KEY_MESSAGE);
                                    chatMessage.dateObject = documentSnapshot.getDate(Constants.KEY_TIMESTAMP);
                                    chatMessage.Datetime = getReadableDateTime(chatMessage.dateObject);
                                    chatMessage.isseen = documentSnapshot.getBoolean("isseen");
                                    messages.add(chatMessage);
                                    mes_Ids.add(documentSnapshot.getId());
                                }

                            }
                        }
                    }
                    if(mes_size != messages.size()) {
                        mes_size = messages.size();
                        Collections.sort(messages, new Comparator<ChatMessage>() {
                            @Override
                            public int compare(ChatMessage o1, ChatMessage o2) {
                                return o1.Datetime.compareTo(o2.Datetime);
                            }
                        });
                        if (messages.size() == 0) {
                            chatAdapter.notifyDataSetChanged();
                        } else {
                            chatAdapter.notifyItemInserted(messages.size() - 1);
                            binding.chatRecyclerview.scrollToPosition(messages.size() - 1);
                        }
                        binding.chatRecyclerview.setVisibility(View.VISIBLE);
                    }

                }
            }
        }
    });
    firestore.collection(Constants.KEY_COLLECTION_USERS).document(ReceiverId).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if (error != null) {
                Log.e("Firestore", "Listener failed", error);
                return;
            }
            if (value != null) {
                for(DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    if(!mes_Ids.contains(documentSnapshot.getId())) {
                        String senderId = documentSnapshot.getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentSnapshot.getString(Constants.KEY_RECEIVER_ID);
                        if ((senderId.equals(ReceiverId) && receiverId.equals(SenderId))){
                            if(documentSnapshot.getDate(Constants.KEY_TIMESTAMP) != null) {
                                ChatMessage chatMessage = new ChatMessage();
                                chatMessage.senderId = documentSnapshot.getString(Constants.KEY_SENDER_ID);
                                chatMessage.receiverId = documentSnapshot.getString(Constants.KEY_RECEIVER_ID);
                                chatMessage.message = documentSnapshot.getString(Constants.KEY_MESSAGE);
                                chatMessage.dateObject = documentSnapshot.getDate(Constants.KEY_TIMESTAMP);
                                chatMessage.Datetime = getReadableDateTime(chatMessage.dateObject);
                                chatMessage.isseen = documentSnapshot.getBoolean("isseen");
                                messages.add(chatMessage);
                                mes_Ids.add(documentSnapshot.getId());
                            }
                        }
                    }
                }
                if(mes_size != messages.size()){
                    mes_size = messages.size();
                    Collections.sort(messages, new Comparator<ChatMessage>() {
                        @Override
                        public int compare(ChatMessage o1, ChatMessage o2) {
                            return o1.Datetime.compareTo(o2.Datetime);
                        }
                    });
                    if(messages.size() == 0){
                        chatAdapter.notifyDataSetChanged();
                    }else{
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                        binding.chatRecyclerview.scrollToPosition(messages.size() - 1);
                    }
                    binding.chatRecyclerview.setVisibility(View.VISIBLE);
                }
            }
            binding.progressBar.setVisibility(View.GONE);
        }
    });

    }

     */

    private void getMessages() {
        messages.clear();
        mes_Ids.clear();
        listenToMessages(SenderId, ReceiverId);
        listenToMessages(ReceiverId, SenderId);
        listenToReceiverStatusAndSeenUpdates();
    }

    private void listenToMessages(String fromId, String toId) {
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(fromId)
                .collection("messages")
                .orderBy(Constants.KEY_TIMESTAMP)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Message listener error", error);
                        return;
                    }

                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            if (!mes_Ids.contains(doc.getId())) {
                                String senderId = doc.getString(Constants.KEY_SENDER_ID);
                                String receiverId = doc.getString(Constants.KEY_RECEIVER_ID);

                                if (senderId.equals(fromId) && receiverId.equals(toId)) {
                                    Date timestamp = doc.getDate(Constants.KEY_TIMESTAMP);
                                    if (timestamp != null) {
                                        ChatMessage chatMessage = new ChatMessage();
                                        chatMessage.senderId = senderId;
                                        chatMessage.receiverId = receiverId;
                                        chatMessage.message = doc.getString(Constants.KEY_MESSAGE);
                                        chatMessage.dateObject = timestamp;
                                        chatMessage.Datetime = getReadableDateTime(timestamp);
                                        chatMessage.isseen = doc.getBoolean("isseen");

                                        messages.add(chatMessage);
                                        mes_Ids.add(doc.getId());
                                    }
                                }
                            }else {
                                for (int i = 0; i < messages.size(); i++) {
                                    ChatMessage m = messages.get(i);
                                    if (m.senderId.equals(fromId)
                                            && m.receiverId.equals(toId)
                                            && m.dateObject.equals(doc.getDate(Constants.KEY_TIMESTAMP))) {
                                        Boolean updatedSeen = doc.getBoolean("isseen");
                                        if (updatedSeen != null && !updatedSeen.equals(m.isseen)) {
                                            m.isseen = updatedSeen;
                                            chatAdapter.notifyItemChanged(i);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        if(mes_size != messages.size()){
                            mes_size = messages.size();
                            updateMessageList();
                        }
                    }
                });
    }
    private void listenToReceiverStatusAndSeenUpdates() {
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(ReceiverId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;

                    String status = snapshot.getString(Constants.KEY_STATUS);
                    if (status != null) binding.status.setText(status);

                    String activeChatWith = snapshot.getString(Constants.KEY_ACTIVE_CHAT_WITH);
                    if (SenderId.equals(activeChatWith)) {
                        markMessagesAsSeen();
                    }
                });
    }

    private void markMessagesAsSeen() {
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(SenderId)
                .collection("messages")
                .orderBy(Constants.KEY_TIMESTAMP)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Mark seen listener error", error);
                        return;
                    }

                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            String senderId = doc.getString(Constants.KEY_SENDER_ID);
                            String receiverId = doc.getString(Constants.KEY_RECEIVER_ID);

                            if (senderId.equals(SenderId) && receiverId.equals(ReceiverId)) {
                                doc.getReference().update("isseen", true);
                            }
                        }
                    }
                });
    }

    private void updateMessageList() {
        Collections.sort(messages, Comparator.comparing(o -> o.dateObject));

        if (messages.size() == 0) {
            chatAdapter.notifyDataSetChanged();
        } else {
            chatAdapter.notifyItemInserted(mes_size - 1);
            binding.chatRecyclerview.scrollToPosition(mes_size - 1);
        }

        binding.chatRecyclerview.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }


    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        messages = new ArrayList<>();
        mes_Ids = new ArrayList<>();
        SenderId = auth.getCurrentUser().getUid();
        img = getArguments().getString("image");
        imgProfile = getUserImage(img);
        chatAdapter = new ChatAdapter(messages,imgProfile,SenderId);

    }

    private void listeners(){
        binding.imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_chatScreen_to_chatting);
            }
        });

        binding.inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                firestore.collection(Constants.KEY_COLLECTION_USERS).document(SenderId).update(Constants.KEY_STATUS,"online");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firestore.collection(Constants.KEY_COLLECTION_USERS).document(SenderId).update(Constants.KEY_STATUS,"typing...");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });
    }

    private void sendMessage(){
        firestore.collection(Constants.KEY_COLLECTION_USERS).document(SenderId).update(Constants.KEY_STATUS,"online");
        if(binding.inputMessage.getText().toString().isEmpty() || binding.inputMessage.getText().toString().trim().equals("")){
            Toast.makeText(requireContext(),"Please Enter a Message",Toast.LENGTH_LONG).show();
            return;
        }
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,SenderId);
        message.put(Constants.KEY_RECEIVER_ID,ReceiverId);
        message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        message.put("isseen",false);
        firestore.collection(Constants.KEY_COLLECTION_USERS).document(SenderId).collection("messages").add(message);
        //firestore.collection("messages").add(message);
        binding.inputMessage.setText(null);

    }

    private void loadDetails(){
        if(getArguments() != null){
            String name = (String) getArguments().getSerializable("name");
            binding.textName.setText(name);
            ReceiverId = (String) getArguments().getSerializable("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatScreenBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        // Inflate the layout for this fragment
        return view;
    }
}