package com.maherlabbad.myfirstapplication;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.Token;
import com.maherlabbad.myfirstapplication.databinding.FragmentSignupBinding;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Signup extends Fragment {


    private FragmentSignupBinding binding;
    private Bitmap selected_image;
    private FirebaseFirestore database;
    private ActivityResultLauncher<Intent> activity_launcher;
    private ActivityResultLauncher<String> permission_launcher;
    private String encodedImage;

    private FirebaseAuth auth;

    public Signup() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        register_launcher();
        auth = FirebaseAuth.getInstance();

    }

    private void listener(){

        binding.textSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView2);
                navController.navigate(R.id.action_signup_to_chatApp);
            }
        });
        binding.layoutimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selectimage(v);
            }
        });
        binding.ButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    public void Selectimage(View view){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permissoin", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //request
                            permission_launcher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }else{
                    permission_launcher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }
            }else{
                Intent intenttogallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity_launcher.launch(intenttogallery);
            }
        }else{
            if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permission_launcher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }else{
                    permission_launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }else{
                Intent intenttogallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity_launcher.launch(intenttogallery);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listener();
    }

    public void signUp(){
        if(isValidSignUpDetails()){
            loading(true);
            auth.createUserWithEmailAndPassword(binding.inputEmail.getText().toString(),binding.inputPasswordSignup.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(requireContext(),"Email sended for verification",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(requireContext(),"error email",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    database = FirebaseFirestore.getInstance();
                    HashMap<String,Object> user = new HashMap<>();
                    user.put(Constants.KEY_NAME,binding.inputName.getText().toString());
                    user.put(Constants.KEY_EMAIL,binding.inputEmail.getText().toString());
                    user.put(Constants.KEY_PASSWORD,binding.inputPasswordSignup.getText().toString());
                    user.put(Constants.KEY_IMAGE,encodedImage);
                    user.put(Constants.KEY_UID,auth.getCurrentUser().getUid());
                    user.put(Constants.KEY_STATUS,"offline");
                    user.put(Constants.KEY_ACTIVE_CHAT_WITH,"");
                    database.collection(Constants.KEY_COLLECTION_USERS).document(auth.getCurrentUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            loading(false);
                            NavController navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView2);
                            navController.navigate(R.id.action_signup_to_chatApp);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loading(false);
                            Toast.makeText(requireContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });/*.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            loading(false);
                            NavController navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView2);
                            navController.navigate(R.id.action_signup_to_chatApp);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loading(false);
                            Toast.makeText(requireContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });*/
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loading(false);
                    Toast.makeText(requireContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewbitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewbitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    private void register_launcher(){
        activity_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == RESULT_OK){
                    Intent intent_from_result = o.getData();
                    if(intent_from_result != null){
                        Uri dataimage = intent_from_result.getData();
                        try {
                            if(Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(),dataimage);
                                selected_image = ImageDecoder.decodeBitmap(source);
                                binding.imageProfile.setImageBitmap(selected_image);

                            }else{
                                selected_image = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),dataimage);
                                binding.imageProfile.setImageBitmap(selected_image);
                            }
                            binding.textImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(selected_image);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permission_launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if(o){
                    Intent intenttogallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activity_launcher.launch(intenttogallery);
                }else{
                    Toast.makeText(requireActivity(),"Permission needed for gallery please...",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void loading(Boolean isloading){
        if(isloading){
            binding.ButtonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.ButtonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isValidSignUpDetails(){
        if(encodedImage == null){
            Toast.makeText(requireContext(),"Please choose an image",Toast.LENGTH_LONG).show();
            return false;
        }else if (binding.inputName.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(requireContext(),"Please Enter your name",Toast.LENGTH_LONG).show();
            return false;
        } else if (binding.inputEmail.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(requireContext(),"Please Enter your email",Toast.LENGTH_LONG).show();
            return false;
        } else if (! binding.inputPasswordSignup.getText().toString().equals(binding.inputPasswordConfrim.getText().toString())) {
            Toast.makeText(requireContext(),"Your password should be same",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater,container,false);
        View view = binding.getRoot();


        // Inflate the layout for this fragment
        return view;
    }


}
