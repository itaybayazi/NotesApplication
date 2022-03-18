package com.example.moveoapplication.note;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.moveoapplication.model.Model;
import com.example.moveoapplication.model.User;

import java.util.List;

public class NoteViewModel extends ViewModel {
    LiveData<List<Note>> data;
    LiveData<List<User>> user_data;

    public NoteViewModel() {
        //:TODO GET ALL NOTES
        String email = "";
        if(Model.instance.getActiveUser().getEmail()!=null){
            email = Model.instance.getActiveUser().getEmail();
        }
        data = Model.instance.getAllNotesForUser(email);
        user_data = Model.instance.getAllUsers();
    }

    public LiveData<List<Note>> getNotesData() {
        return data;
    }

    public LiveData<List<User>> getAllUsersData() {
        user_data = Model.instance.getAllUsers();
        return user_data;
    }



}
