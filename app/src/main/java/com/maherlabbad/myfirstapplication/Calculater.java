package com.maherlabbad.myfirstapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.maherlabbad.myfirstapplication.databinding.FragmentCalculaterBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Calculater extends Fragment {

    private FragmentCalculaterBinding binding;
    private String code = "";
    private double number1 = 0;
    private boolean check = false;
    private double number2 = 0;
    private double result = 0;
    private String islem = "";
    private boolean check_virgul = true;
    private String txt = "";
    private int num = 0;
    private String text1_str = "";
    private  String text2_str = "";
    private TextView text1;
    private TextView text2;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public Calculater() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(code.isEmpty()){
            chooseCode();
        }

        text1 = binding.textView;
        text2 = binding.textView2;

        Button rakam_virgul = binding.buttonvirgul;
        rakam_virgul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virgul(v);
            }
        });
        Button rakam_birsil = binding.buttonsilme;
        rakam_birsil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birSil(v);
            }
        });
        Button rakam_bol100 = binding.buttonkalan;
        rakam_bol100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bol100(v);
            }
        });
        Button rakam_eksibircarp = binding.button;
        rakam_eksibircarp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eksiBirCarp(v);
            }
        });
        Button rakam_bolme = binding.buttonbolme;
        rakam_bolme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bolme(v);
            }
        });
        Button rakam_carpma = binding.buttoncarpma;
        rakam_carpma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carpma(v);
            }
        });
        Button rakam_cikartma = binding.buttoncKartma;
        rakam_cikartma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cikartma(v);
            }
        });
        Button rakam_esit = binding.buttonesit;
        rakam_esit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esit(v);
            }
        });
        Button rakam_toplama = binding.buttontoplama;
        rakam_toplama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toplama(v);
            }
        });
        Button rakam_1 = binding.button1;
        rakam_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rakam1(v);
            }
        });
        Button rakam_2 = binding.button2;
        rakam_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rakam2(v);
            }
        });
        Button rakam_3 = binding.button3;
        rakam_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rakam3(v);
            }
        });
        Button rakam_4 = binding.button4;
        rakam_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rakam4(v);
            }
        });
        Button rakam_5 = binding.button5;
        rakam_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rakam5(v);
            }
        });
        Button rakam_6 = binding.button6;
        rakam_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rakam6(v);
            }
        });
        Button rakam_7 = binding.button7;
        rakam_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rakam7(v);
            }
        });
        Button rakam_8 = binding.button8;
        rakam_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rakam8(v);
            }
        });
        Button rakam_9 = binding.button9;
        rakam_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rakam9(v);
            }
        });
        Button rakam_0 = binding.button0;
        rakam_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rakam0(v);
            }
        });
        Button rakam_sil = binding.buttonAC;
        rakam_sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sil(v);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("Myprefs",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        code = sharedPreferences.getString("code","");
        if(getArguments() != null && code.isEmpty()){
            String newcode = getArguments().getString("code");
            editor.putString("code",newcode).apply();
            if(newcode != null){
                code = newcode;
            }
        }
    }

    private void chooseCode(){
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView2);
        navController.navigate(R.id.action_calculater_to_chooseAnCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCalculaterBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }
    public void virgul(View view){

        if(!text2_str.equalsIgnoreCase("") && !text1_str.contains(",") && check_virgul) {
            text1_str += ",";
            text2_str += ".";
            num = 0;
            check_virgul = false;
            text1.setText(text1_str);
        }else if(!text1_str.contains(",")){
            rakam0(view);
            virgul(view);
        }
    }
    public void cikartma(View view){
        check = false;
        check_virgul = true;
        if(!text2_str.equalsIgnoreCase("")) {

            if (islem.equalsIgnoreCase("") || islem.equalsIgnoreCase("cikartma")) {
                islem = "cikartma";
                if (number1 == 0) {
                    number1 = Double.parseDouble(text2_str);
                    result = number1;
                } else {
                    number2 = Double.parseDouble(text2_str);
                    result -= number2;
                    number1 = result;
                    number2 = 0;
                }
                num = 0;
                txt += text1_str + " - ";
                text2.setText(txt);
                text2_str = "";
                text1_str = "";
                text1.setText(text1_str);
            } else {
                esit(view);
                cikartma(view);
            }
        }
    }
    public void toplama(View view){
        check = false;
        check_virgul = true;
        if(!text2_str.equalsIgnoreCase("")) {

            if (islem.equalsIgnoreCase("") || islem.equalsIgnoreCase("toplama")) {
                islem = "toplama";
                if (number1 == 0) {
                    number1 = Double.parseDouble(text2_str);
                    result = number1;
                } else {
                    number2 = Double.parseDouble(text2_str);
                    result += number2;
                    number1 = result;
                    number2 = 0;
                }

                num = 0;
                txt += text1_str + " + ";
                text2.setText(txt);
                text2_str = "";
                text1_str = "";
                text1.setText(text1_str);
            } else {
                esit(view);
                toplama(view);
            }
        }

    }
    public void carpma(View view){
        check = false;

        check_virgul = true;
        if(!text2_str.equalsIgnoreCase("")) {

            if (islem.equalsIgnoreCase("") || islem.equalsIgnoreCase("carpma")) {

                islem = "carpma";
                if (number1 == 0) {
                    number1 = Double.parseDouble(text2_str);
                    result = number1;
                } else {
                    number2 = Double.parseDouble(text2_str);
                    result *= number2;
                    number1 = result;
                    number2 = 0;
                }

                num = 0;
                txt += text1_str + " * ";
                text2.setText(txt);
                text2_str = "";
                text1_str = "";
                text1.setText(text1_str);
            } else {
                esit(view);
                carpma(view);
            }
        }
    }
    public void bolme(View view){
        check = false;
        check_virgul = true;
        if(!text2_str.equalsIgnoreCase("")) {

            if (islem.equalsIgnoreCase("") || islem.equalsIgnoreCase("bolme")) {
                islem = "bolme";
                if (number1 == 0) {
                    number1 = Double.parseDouble(text2_str);
                    result = number1;
                } else {
                    number2 = Double.parseDouble(text2_str);
                    result /= number2;
                    number1 = result;
                    number2 = 0;
                }

                num = 0;
                txt += text1_str + " / ";
                text2.setText(txt);
                text2_str = "";
                text1_str = "";
                text1.setText(text1_str);

            } else {
                esit(view);
                bolme(view);
            }
        }
    }
    public void esit(View view){
        check = true;
        check_virgul = true;
        if(!text2_str.equalsIgnoreCase("")) {
            islem = "";
            if (txt.endsWith(" + ")) {
                result += Double.parseDouble(text2_str);
            } else if (txt.endsWith(" - ")) {
                result -= Double.parseDouble(text2_str);
            } else if (txt.endsWith(" * ")) {
                result *= Double.parseDouble(text2_str);
            } else if (txt.endsWith(" / ")) {
                result /= Double.parseDouble(text2_str);
            }
            text1.setText(Double.toString(result));
            text2.setText("");
            text2_str = text1.getText().toString();
            text1_str = text2_str;
            num = 0;
            number1 = 0;
            number2 = 0;
            txt = "";
        }
    }
    public void birSil(View view){
        if(num > 0) {
            num--;
            text2_str = text2_str.substring(0, text2_str.length() - 1);
            text1_str = text1_str.substring(0, text1_str.length() - 1);
            text1.setText(text1_str);
        }
    }
    public void bol100(View view){
        if(!text2_str.equalsIgnoreCase("")) {
            text2_str = Double.toString(Double.parseDouble(text2_str) / 100);
            text1_str = text2_str;
            text1.setText(text1_str);
        }
    }
    public String formatNumber(String number) {
        try {
            long parsed = Long.parseLong(number.replace(".", ""));
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMANY);
            return formatter.format(parsed);
        } catch (NumberFormatException e) {
            return number;
        }
    }

    private void kodKontrol() {
        if (text2_str.equals(code)) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView2);
            navController.navigate(R.id.action_calculater_to_chatApp);
        }
    }
    public void eksiBirCarp(View view){
        if(!text2_str.equalsIgnoreCase("")) {
            double temp = 0;
            if(text2_str.contains("-")){
                temp = Double.parseDouble(text2_str.substring(1));
            }else{
                temp = Double.parseDouble(text2_str) * -1;
            }
            text2_str = String.valueOf(temp);
            text1_str = text2_str;
            text1.setText(text1_str);
        }
    }
    public void sil(View view){
        text1_str = "";
        text1.setText(text1_str);
        num = 0;
        number1 = 0;
        number2 = 0;
        result = 0;
        text2_str = "";
        text2.setText(text2_str);
    }
    public void rakam1(View view){
        if(check){
            sil(view);
            check = false;
        }

        text1_str += "1";
        text1_str = formatNumber(text1_str);

        text2_str += "1";
        num ++;
        text1.setText(text1_str);
        kodKontrol();

    }
    public void rakam2(View view){
        if(check){
            sil(view);
            check = false;
        }
        text1_str += "2";
        text1_str = formatNumber(text1_str);
        text2_str += "2";
        num ++;
        text1.setText(text1_str);
        kodKontrol();
    }
    public void rakam3(View view){
        if(check){
            sil(view);
            check = false;
        }
        text1_str += "3";
        text1_str = formatNumber(text1_str);
        text2_str += "3";
        num ++;
        text1.setText(text1_str);
        kodKontrol();
    }
    public void rakam4(View view){
        if(check){
            sil(view);
            check = false;
        }
        text1_str += "4";
        text1_str = formatNumber(text1_str);
        text2_str += "4";
        num ++;
        text1.setText(text1_str);
        kodKontrol();
    }
    public void rakam5(View view){
        if(check){
            sil(view);
            check = false;
        }
        text1_str += "5";
        text1_str = formatNumber(text1_str);
        text2_str += "5";
        num ++;
        text1.setText(text1_str);
        kodKontrol();
    }
    public void rakam6(View view){
        if(check){
            sil(view);
            check = false;
        }
        text1_str += "6";
        text1_str = formatNumber(text1_str);
        text2_str += "6";
        num ++;
        text1.setText(text1_str);
        kodKontrol();
    }
    public void rakam7(View view){
        if(check){
            sil(view);
            check = false;
        }
        text1_str += "7";
        text1_str = formatNumber(text1_str);
        text2_str += "7";
        num ++;
        text1.setText(text1_str);
        kodKontrol();
    }
    public void rakam8(View view){
        if(check){
            sil(view);
            check = false;
        }
        text1_str += "8";
        text1_str = formatNumber(text1_str);
        text2_str += "8";
        num ++;
        text1.setText(text1_str);
        kodKontrol();
    }
    public void rakam9(View view){
        if(check){
            sil(view);
            check = false;
        }
        text1_str += "9";
        text1_str = formatNumber(text1_str);
        text2_str += "9";
        num ++;
        text1.setText(text1_str);
        kodKontrol();
    }
    public void rakam0(View view){
        if(check){
            sil(view);
            check = false;
        }
        if(text2_str.isEmpty() || Integer.parseInt(text2_str)!=0){
            text1_str += "0";
        }
        text1_str = formatNumber(text1_str);
        text2_str += "0";
        num ++;
        text1.setText(text1_str);
        kodKontrol();
    }
}