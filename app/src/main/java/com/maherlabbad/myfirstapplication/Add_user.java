package com.maherlabbad.myfirstapplication;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.maherlabbad.myfirstapplication.databinding.FragmentAddUserBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Add_user extends Fragment implements UserListener {

    private FragmentAddUserBinding binding;
    private ArrayList<User> users;
    private userAdapter adapter;
    private NavController navController;
    private FirebaseFirestore firestore;
    private ArrayList<String> Emails;
    private FirebaseAuth auth;
    private UserDatabase db;
    private userDao userDao;
    private CompositeDisposable compositeDisposable;



    public Add_user() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        users = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();
        Emails = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        db = UserDatabase.getInstance(requireContext());
        userDao = db.userDao();

    }

    @Override
    public void onStop() {
        super.onStop();
        firestore.collection(Constants.KEY_COLLECTION_USERS).document(auth.getCurrentUser().getUid()).update(Constants.KEY_STATUS,"offline");
    }

    @Override
    public void onStart() {
        super.onStart();
        firestore.collection(Constants.KEY_COLLECTION_USERS).document(auth.getCurrentUser().getUid()).update(Constants.KEY_STATUS,"online");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.RecyclerViewAddusers.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new userAdapter(users,this);
        binding.RecyclerViewAddusers.setAdapter(adapter);
        navController = Navigation.findNavController(view);
        firestore.collection(Constants.KEY_COLLECTION_USERS).document(auth.getCurrentUser().getUid()).update(Constants.KEY_ACTIVE_CHAT_WITH,"");
        listeners();
    }

    private void getData(){
        String Email = binding.editTextTextEmailAddress.getText().toString();
        String current_Email = auth.getCurrentUser().getEmail();
        if(!Email.equals(current_Email)) {
            if (Email.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter an email", Toast.LENGTH_LONG).show();
            } else {
                firestore.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo(Constants.KEY_EMAIL, Email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot queryDocumentSnapshots = task.getResult();
                            if(!(queryDocumentSnapshots.isEmpty() || Emails.contains(Email))){
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                User user = new User(documentSnapshot.getString(Constants.KEY_NAME), documentSnapshot.getString(Constants.KEY_IMAGE),documentSnapshot.getString(Constants.KEY_UID));
                                Emails.add(Email);
                                users.add(user);
                                adapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(requireContext(), "Incorrect Email", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        }else{
            Toast.makeText(requireContext(),"This is your Email,Try again",Toast.LENGTH_LONG).show();
        }
    }

    private void listeners(){
        binding.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_add_user_to_chatting);
            }
        });
        binding.butSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddUserBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onUserClicked(User user) {
        compositeDisposable.add(
                userDao.contains(user.uid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isInDb -> {
                            if (!isInDb) {
                                compositeDisposable.add(
                                        userDao.insert(user)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe()
                                );
                            }
                            Add_userDirections.ActionAddUserToChatScreen action =
                                    Add_userDirections.actionAddUserToChatScreen(user.uid, user.name, user.image);
                            navController.navigate(action);
                        }, throwable -> {
                            Log.e("DB_ERROR", "contains sorgusu başarısız", throwable);
                        })
        );
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}