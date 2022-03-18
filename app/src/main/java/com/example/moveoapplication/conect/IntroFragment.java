package com.example.moveoapplication.conect;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moveoapplication.R;
import com.example.moveoapplication.model.Model;


public class IntroFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_intro, container, false);

        Model.instance.executor.execute(() -> {
            Model.instance.getCurrentUser(user -> {
                // User is signed in
                if (user != null) {
                    Model.instance.mainThread.post(() -> {
                        Model.instance.setActiveUser(user);
                        Navigation.findNavController(view).navigate(IntroFragmentDirections.actionIntroFragmentToWelcomeFragment(user.getUserName()));
                    });
                } else {
                    Model.instance.mainThread.post(() -> Navigation.findNavController(view).navigate(IntroFragmentDirections.actionIntroFragmentToLoginFragment())
                    );
                }
            });
        });


        return view;
    }
}