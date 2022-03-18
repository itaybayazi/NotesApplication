package com.example.moveoapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.moveoapplication.model.Model;
import com.example.moveoapplication.note.NoteListRvFragmentDirections;


public class WelcomeFragment extends Fragment {

    View view;
    Button listBtn;
    Button mapBtn;
    TextView userTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_welcome, container, false);

        listBtn = view.findViewById(R.id.welcome_list_btn);
        mapBtn = view.findViewById(R.id.welcome_map_btn);
        userTv = view.findViewById(R.id.welcome_username_tv);

        String uName = WelcomeFragmentArgs.fromBundle(getArguments()).getUserEmail();
        userTv.setText("Welcome "+uName+" !");

        listBtn.setOnClickListener(v -> {
            if (Model.instance.getActiveUser() != null) {
                Navigation.findNavController(view).navigate(WelcomeFragmentDirections.actionWelcomeFragmentToNoteListRvFragment(Model.instance.getActiveUser().getEmail()));
            }
        });

        mapBtn.setOnClickListener(v -> Navigation.findNavController(view).navigate(WelcomeFragmentDirections.actionWelcomeFragmentToMapsFragment(false)));

        return view;
    }

    /* **************************************** Menu ************************************************ */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.welcome_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.menu_addNote:
                    Navigation.findNavController(this.view).navigate(WelcomeFragmentDirections.actionWelcomeFragmentToMapsFragment(true));
                    break;
                case R.id.menu_logout:
                    Model.instance.logout(() -> {
                        Navigation.findNavController(this.view)
                                .navigate(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment());
                    });
                    break;
            }
        } else {
            return true;
        }
        return false;
    }
}