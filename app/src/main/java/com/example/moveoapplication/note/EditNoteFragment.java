package com.example.moveoapplication.note;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.moveoapplication.R;
import com.example.moveoapplication.model.Model;
import com.example.moveoapplication.note.Note;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;


public class EditNoteFragment extends Fragment {

    View view;
    Note note_;
    Button save_btn;
    Bitmap imageBitmap;
    EditText noteDesc_et;
    EditText noteTitle_et;
    ImageView noteImage_iv;
    ProgressBar progressBar;
    ImageButton camBtn, galleryBtn, delImage;
    Button delete_btn;

    static final int REQUESTS_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_note, container, false);
        delImage = view.findViewById(R.id.frag_editN_del_btn);
        camBtn = view.findViewById(R.id.frag_editN_cam_btn);
        noteImage_iv = view.findViewById(R.id.editNoteFrag_img);
        save_btn = view.findViewById(R.id.editNoteFrag_save_btn);
        noteDesc_et = view.findViewById(R.id.editNoteFrag_desc_et);
        galleryBtn = view.findViewById(R.id.frag_editN_gallery_btn);
        progressBar = view.findViewById(R.id.frag_editNote_progressBar);
        noteTitle_et = view.findViewById(R.id.editNoteFrag_title_et);
        delete_btn = view.findViewById(R.id.editNoteFrag_delete_btn);

        progressBar.setVisibility(View.GONE);
        String pId = EditNoteFragmentArgs.fromBundle(getArguments()).getNoteId();

        Model.instance.getNoteById(pId, note -> {
            note_ = note;
            noteDesc_et.setText(note.getDescription());
            noteTitle_et.setText(note.getTitle());
            if (note.getImageUrl() != null) {
                Picasso.get().load(note.getImageUrl()).into(noteImage_iv);
            }
        });


        camBtn.setOnClickListener(v -> openCam());
        save_btn.setOnClickListener(v -> savePost());
        delImage.setOnClickListener(v -> deleteImage());
        galleryBtn.setOnClickListener(v -> openGallery());
        delete_btn.setOnClickListener(v -> deleteNote());

        return view;

    }

    private void deleteNote() {
        if (Model.instance.getActiveUser() != null) {
            Model.instance.refreshUserNotesList(Model.instance.getActiveUser().getEmail());
        }
        Model.instance.deletePost(note_, () -> {
            delete_btn.setEnabled(false);
            noteDesc_et.setEnabled(false);
            noteTitle_et.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            Log.d("TAG", "delete successful");
            if (Model.instance.getActiveUser() != null) {
                Model.instance.refreshUserNotesList(Model.instance.getActiveUser().getEmail());
            }
            Navigation.findNavController(view).navigateUp();
        });
    }
    private void deleteImage() {
        Model.instance.deleteNoteImage(note_, flag -> {
            if (flag == true) {
                Navigation.findNavController(view).navigateUp();
            }
        });
    }


    private void savePost() {


        note_.setDescription(noteDesc_et.getText().toString());
        note_.setTitle(noteTitle_et.getText().toString());

        if (imageBitmap != null) {
            Model.instance.savePostImage(imageBitmap, note_.getId() + ".jpg", url -> {
                note_.setImageUrl(url);
                Model.instance.addNote(note_, () -> {
                    if (Model.instance.getActiveUser() != null) {
                        Model.instance.refreshUserNotesList(Model.instance.getActiveUser().getEmail());
                    }
                    camBtn.setEnabled(false);
                    save_btn.setEnabled(false);
                    galleryBtn.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    if (Model.instance.getActiveUser() != null) {
                        Model.instance.refreshUserNotesList(Model.instance.getActiveUser().getEmail());
                    }
                    Navigation.findNavController(view).navigateUp();
                    if (Model.instance.getActiveUser() != null) {
                        Model.instance.refreshUserNotesList(Model.instance.getActiveUser().getEmail());
                    }

                });
            });
        } else {
            Model.instance.addNote(note_, () -> {

                if (Model.instance.getActiveUser() != null) {
                    Model.instance.refreshUserNotesList(Model.instance.getActiveUser().getEmail());
                }

                camBtn.setEnabled(false);
                save_btn.setEnabled(false);
                galleryBtn.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                Navigation.findNavController(view).navigateUp();
            });
        }
    }

    private void openCam() {
        Intent openCamIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCamIntent, REQUESTS_IMAGE_CAPTURE);
    }

    private void openGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, REQUEST_IMAGE_GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTS_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extars = data.getExtras();
                imageBitmap = (Bitmap) extars.get("data");
                noteImage_iv.setImageBitmap(imageBitmap);
            }
        } else if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();
                try {
                    imageBitmap = BitmapFactory.decodeStream(getContext()
                            .getContentResolver().openInputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                noteImage_iv.setImageBitmap(imageBitmap);
            }
        }
    }
}