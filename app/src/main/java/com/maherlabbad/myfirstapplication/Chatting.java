package com.maherlabbad.myfirstapplication;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.maherlabbad.myfirstapplication.databinding.FragmentChattingBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Chatting extends Fragment implements UserListener {

    private FragmentChattingBinding binding;
    private FirebaseAuth auth;
    private userAdapter adapter;
    private String uid;
    private FirebaseFirestore firestore;
    private NavController navController;
    private userDao userDao;
    private UserDatabase db;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ArrayList<User> users;

    public Chatting() {
        // Required empty public constructor
    }
    private void handleResponse(List<User> userss){
        users = new ArrayList<>(userss);
        adapter.setUsers(users);
        adapter.notifyDataSetChanged();
    }

    private void updateChat_with(String user) {
        FirebaseFirestore.getInstance()
                .collection(Constants.KEY_COLLECTION_USERS)
                .document(uid)
                .update(Constants.KEY_ACTIVE_CHAT_WITH, user);
    }

    private void updateStatus(String status) {
        FirebaseFirestore.getInstance()
                .collection(Constants.KEY_COLLECTION_USERS)
                .document(uid)
                .update(Constants.KEY_STATUS, status);
    }

    @Override
    public void onStop() {
        super.onStop();
        updateStatus("offline");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = UserDatabase.getInstance(requireContext());
        userDao = db.userDao();
        compositeDisposable.add(userDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResponse));
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView2);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerViewUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new userAdapter(users,this);
        binding.recyclerViewUsers.setAdapter(adapter);
        getData();
        listeners();
        updateStatus("online");
        updateChat_with("");
    }


    private void getData(){
        uid = auth.getCurrentUser().getUid();
        firestore.collection(Constants.KEY_COLLECTION_USERS).document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                firestore.collection(Constants.KEY_COLLECTION_USERS).document(uid).update(Constants.KEY_STATUS,"online");
                firestore.collection(Constants.KEY_COLLECTION_USERS).document(uid).update(Constants.KEY_ACTIVE_CHAT_WITH,"");
                String Name = documentSnapshot.getString(Constants.KEY_NAME);
                String image = documentSnapshot.getString(Constants.KEY_IMAGE);
                byte[] bytes = Base64.decode(image,Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                binding.imageProfile.setImageBitmap(bitmap);
                binding.textName.setText(Name);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(),"Can not access to database",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void listeners(){
        binding.imageSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus("offline");
                auth.signOut();
                navController.navigate(R.id.action_chatting_to_calculater);
            }
        });
        binding.AddNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_chatting_to_add_user);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChattingBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onUserClicked(User user) {
        firestore.collection(Constants.KEY_COLLECTION_USERS).document(uid).update(Constants.KEY_ACTIVE_CHAT_WITH,user.uid);
        ChattingDirections.ActionChattingToChatScreen action = ChattingDirections.actionChattingToChatScreen(user.uid, user.name,user.image);
        navController.navigate(action);
    }
}