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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moveoapplication.MapsFragmentArgs;
import com.example.moveoapplication.R;
import com.example.moveoapplication.model.Model;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNoteFragment extends Fragment {

    ImageButton camBtn, galleryBtn;
    ProgressBar progressBar;
    ImageView imgNote;
    TextView descTv;
    TextView titleTv;
    TextView dateTv;
    Button saveBtn;
    View view;
    float longitude_;
    float latitude_;


    static final int REQUESTS_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;
    Bitmap imageBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_note, container, false);

        longitude_=  AddNoteFragmentArgs.fromBundle(getArguments()).getLongitude();
        latitude_=  AddNoteFragmentArgs.fromBundle(getArguments()).getLatitude();

        progressBar = view.findViewById(R.id.frag_addN_progressBar);
        galleryBtn = view.findViewById(R.id.frag_addN_gallery_btn);
        saveBtn = view.findViewById(R.id.frag_addN_SAVE_btn);
        camBtn = view.findViewById(R.id.frag_addN_cam_btn);
        descTv = view.findViewById(R.id.frag_addN_desc_tv);
        imgNote = view.findViewById(R.id.frag_addN_iv_n);
        titleTv = view.findViewById(R.id.frag_addN_title_tv);
        dateTv = view.findViewById(R.id.frag_addN_date_tv);

        progressBar.setVisibility(View.GONE);

        setDate();

        camBtn.setOnClickListener(v -> {
            openCam();
        });
        galleryBtn.setOnClickListener(v -> {
            openGallery();
        });
        saveBtn.setOnClickListener(v -> {
            saveNote();
        });

        return view;
    }

    private void setDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());
        dateTv.setText(date);
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
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imgNote.setImageBitmap(imageBitmap);
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
                imgNote.setImageBitmap(imageBitmap);
            }
        }
    }

    private void saveNote() {

        descTv.setEnabled(false);
        titleTv.setEnabled(false);
        camBtn.setEnabled(false);
        saveBtn.setEnabled(false);
        galleryBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        String desc = descTv.getText().toString();
        String data_ = dateTv.getText().toString();



        Model.instance.getNoteId(id -> {
            Note note = new Note(desc, id, Model.instance.getActiveUser().getEmail(), data_, latitude_, longitude_);
            note.setTitle(titleTv.getText().toString());

            if (imageBitmap != null) {
                Model.instance.savePostImage(imageBitmap, note.getId() + ".jpg", url -> {
                    note.setImageUrl(url);
                    Model.instance.addNote(note, () -> {
                        if (Model.instance.getActiveUser() != null) {
                            Model.instance.refreshUserNotesList(Model.instance.getActiveUser().getEmail());
                        }
                          Navigation.findNavController(view).navigateUp();
                    });
                });
            } else {
                Model.instance.addNote(note, () -> {
                    if (Model.instance.getActiveUser() != null) {
                        Model.instance.refreshUserNotesList(Model.instance.getActiveUser().getEmail());
                    }
                    Navigation.findNavController(view).navigateUp();
                });
            }
        });

    }

}