package com.maherlabbad.myfirstapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.maherlabbad.myfirstapplication.databinding.FragmentChatAppBinding;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class chatApp extends Fragment {

    private FragmentChatAppBinding binding;
    private FirebaseAuth auth;
    private NavController navController;

    public chatApp() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void loading(Boolean isloading){
        if(isloading){
            binding.ButtonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.ButtonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listener();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user != null){
            if(user.isEmailVerified()) {
                navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView2);
                navController.navigate(R.id.action_chatApp_to_chatting);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatAppBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        // Inflate the layout for this fragment
        return view;
    }

    public void SignIn(){
        loading(true);
        String Email = binding.inputEmail.getText().toString();
        String Password = binding.inputPassword.getText().toString();

        if(!(Email.isEmpty() || Password.isEmpty())){

            auth.signInWithEmailAndPassword(Email,Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    loading(false);
                    FirebaseUser user = auth.getCurrentUser();
                    if(user != null){
                        if(user.isEmailVerified()){
                            navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView2);
                            navController.navigate(R.id.action_chatApp_to_chatting);
                        }else{
                            Toast.makeText(requireContext(),"Please verify your Email",Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loading(false);
                    Toast.makeText(requireContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else{
            loading(false);
            Toast.makeText(requireContext(),"Please Enter all Fields",Toast.LENGTH_LONG).show();
        }


    }

    private void listener(){
        binding.textCreateNewAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView2);
                navController.navigate(R.id.action_chatApp_to_signup);
            }
        });
        binding.ButtonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });
    }
}