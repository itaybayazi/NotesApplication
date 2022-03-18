package com.example.moveoapplication.conect;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.moveoapplication.R;
import com.example.moveoapplication.model.Model;


public class RegisterFragment extends Fragment {

    Button register_btn, account_btn;
    EditText fullNameEt, usernameEt, emailEt, passwordEt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        fullNameEt = view.findViewById(R.id.frag_reg_fullname);
        usernameEt = view.findViewById(R.id.frag_reg_username);
        emailEt = view.findViewById(R.id.frag_reg_email);
        passwordEt = view.findViewById(R.id.frag_reg_password);

        register_btn = view.findViewById(R.id.frag_reg_register_btn);
        account_btn = view.findViewById(R.id.frag_reg_login_btn);


        account_btn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigateUp();
        });

        register_btn.setOnClickListener(v -> {
            registerUser(view);
        });


        return view;
    }

    private void registerUser(View view) {

        String fullName = fullNameEt.getText().toString();
        String username = usernameEt.getText().toString();
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();

        if (fullName.isEmpty()) {
            fullNameEt.setError("Full name is required");
            fullNameEt.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailEt.setError("Email is required");
            emailEt.requestFocus();
            return;
        }
        if (username.isEmpty()) {
            usernameEt.setError("Username is required");
            usernameEt.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Please provide valid email");
            emailEt.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordEt.setError("Password is required");
            passwordEt.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEt.setError("Minimum password length should be 6 characters!");
            passwordEt.requestFocus();
            return;
        }


       Model.instance.checkUserName(usernameEt.getText().toString(), flag -> {
            // it`s ok , userName is available
            if (flag == true) {
                Model.instance.registerUser(email, password, fullName, username, new Model.RegisterListener() {
                    @Override
                    public void onComplete() {
                        Navigation.findNavController(view)
                                .navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment());
                    }

                    @Override
                    public void onAddUser() {
                        Log.d("TAG", "onComplete -  register");
                    }
                });

            } else {
                usernameEt.setError("Username is not available");
                usernameEt.requestFocus();
            }
        });


    }


}