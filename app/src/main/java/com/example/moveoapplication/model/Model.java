package com.example.moveoapplication.model;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moveoapplication.note.Note;


import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model {


    public static final Model instance = new Model();
    ModelFirebase modelFirebase = new ModelFirebase();
    User activeUser = null;
    MutableLiveData<List<User>> usersList = new MutableLiveData<List<User>>();
    MutableLiveData<List<Note>> notesListForUser = new MutableLiveData<List<Note>>();
    Long delete_postId;
    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());
    MutableLiveData<NotesListLoadingState> notesListLoadingState = new MutableLiveData<NotesListLoadingState>();



    /* ************************************ enum loading posts ************************************** */

    public enum NotesListLoadingState {
        loading,
        loaded
    }

    public LiveData<NotesListLoadingState> getNotesListLoadingState() {
        return notesListLoadingState;
    }

    private Model() {
    }


    /* ************************************ init user login+register ******************************** */

    public void checkUserName(String userName, CheckUserNameListener listener) {
        modelFirebase.checkUserName(userName, listener);
    }

    public void registerUser(String email, String password, String fullName, String username, RegisterListener listener) {
        modelFirebase.registerUser(email, password, fullName, username, listener);
    }

    public void loginUser(String email, String password, LoginListener listener) {
        modelFirebase.loginUser(email, password, listener);
        modelFirebase.getUserByUserEmail(email, user -> activeUser = user);
    }

    public void logout(LogoutListener listener) {
        modelFirebase.logout(listener);
    }


    /* ************************************ users *************************************************** */

    public void getUserByEmail(String eMail, GetUserByEmailListener listener) {
        modelFirebase.getUserByUserEmail(eMail, new GetUserByUserName() {
            @Override
            public void onComplete(User user) {
                listener.onComplete(user);
            }
        });
    }


    public void getCurrentUser(GetCurrentUserListener listener) {
        modelFirebase.getCurrentUser(listener);
    }

    public void deleteUserImage(String proImageName, DeleteUserImageListener listener) {
        modelFirebase.deleteUserImage(proImageName, listener);
    }


    public User getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public void saveUserImage(Bitmap imageBitmap, String imageName, SaveImageUserListener listener) {
        modelFirebase.saveUserImage(imageBitmap, imageName, listener);
    }

    public void saveUpdateUser(String user_name, SaveUserChangeListener listener) {
        modelFirebase.saveUpdateUser(user_name, listener);
    }

    public LiveData<List<User>> getAllUsers() {
        modelFirebase.getAllUsers(list -> usersList.postValue(list));
        return usersList;
    }


    // ********************************************************************
    public LiveData<List<Note>> getAllNotesForUser(String userEmail_) {
        if (notesListForUser.getValue() == null) {
            refreshUserNotesList(userEmail_);
        }
        return notesListForUser;
    }

    public interface GetAllNotesListener {
        void onComplete(List<Note> list);
    }

    public void getAllNotes(GetAllNotesListener listener) {
        modelFirebase.getAllNotes(new ModelFirebase.GetAllNotesListener() {
            @Override
            public void onComplete(List<Note> list) {
                listener.onComplete(list);
            }
        });
    }

    public void refreshUserNotesList(String userEmail_) {

        notesListLoadingState.setValue(NotesListLoadingState.loading);

        modelFirebase.getAllNotes(list ->
                executor.execute(() -> {

                    List<Note> userNotesList = new ArrayList<Note>();

                    for (Note note : list) {
                        if (note.getPostUser().equals(userEmail_)) {
                            Log.d("TAG", "email active user is " + note.getPostUser());
                            userNotesList.add(note);
                        }
                    }

                    // sort the posts lists in descending order
                    Collections.sort(userNotesList, Collections.reverseOrder());
                    notesListForUser.postValue(userNotesList);
                    notesListLoadingState.postValue(NotesListLoadingState.loaded);

                }));
    }


    public void getNoteId(GetNoteIdListener listener) {
        modelFirebase.changeNoteId(listener);
    }

    public void savePostImage(Bitmap imageBitmap, String imageName, SaveImageNoteListener listener) {
        modelFirebase.saveNoteImage(imageBitmap, imageName, listener);
    }

    public void addNote(Note note, AddNoteListener listener) {
        modelFirebase.addNote(note, listener);
    }

    public Note getNoteById(String pId, GetNoteByIdListener listener) {
        modelFirebase.getNoteById(pId, listener);
        return null;
    }
    public void deleteNoteImage(Note note, DeleteNoteImageListener listener) {
        modelFirebase.deleteNoteImage(note, listener);
    }

    public void deletePost(Note note, DeleteNoteListener listener) {
        modelFirebase.create_noteDelete(note);
        modelFirebase.get_postDelete(noteId -> {
            modelFirebase.deletePost(note, listener);
            delete_postId = noteId;
        });
    }



    /* ************************************ interface *********************************************** */

    public interface DeleteNoteListener {
        void onComplete();
    }
    public interface DeleteNoteImageListener {
        void onComplete(Boolean flag);
    }

    public interface GetNoteByIdListener {
        void onComplete(Note note);
    }

    public interface SaveImageNoteListener {
        void onComplete(String url);
    }

    public interface AddNoteListener {
        void onComplete();
    }


    public interface GetNoteIdListener {
        void onComplete(Long id);
    }

    public interface GetUserByEmailListener {
        void onComplete(User user);
    }


    public interface DeleteUserImageListener {
        void onComplete(Boolean flag);
    }

    public interface GetCurrentUserListener {
        void onComplete(User user);
    }


    public interface SaveUserChangeListener {
        void onComplete();
    }


    public interface SaveImageUserListener {
        void onComplete(String url);
    }

    public interface CheckUserNameListener {
        void onComplete(boolean flag);
    }

    public interface RegisterListener {
        void onComplete();

        void onAddUser();
    }

    public interface LoginListener {
        void onComplete(boolean flag);
    }

    public interface LogoutListener {
        void onComplete();
    }

    public interface GetUserByUserName {
        void onComplete(User user);
    }


}
