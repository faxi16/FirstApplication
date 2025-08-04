package com.maherlabbad.myfirstapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.maherlabbad.myfirstapplication.databinding.FragmentChooseAnCodeBinding;

public class ChooseAnCode extends Fragment {

    private FragmentChooseAnCodeBinding binding;
    private NavController navController;

    public ChooseAnCode() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView2);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listeners();
    }
    private void listeners(){
        binding.button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.editTextNumber.getText().toString().isEmpty()){
                    Toast.makeText(requireContext(),"Please Enter Your Code",Toast.LENGTH_SHORT).show();
                }else{
                    String code = binding.editTextNumber.getText().toString();
                    Toast.makeText(requireContext(),code+" Saved",Toast.LENGTH_SHORT).show();
                    ChooseAnCodeDirections.ActionChooseAnCodeToCalculater action = ChooseAnCodeDirections.actionChooseAnCodeToCalculater(code);
                    navController.navigate(action);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChooseAnCodeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        // Inflate the layout for this fragment
        return view;
    }
}