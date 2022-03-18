package com.example.moveoapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moveoapplication.model.Model;
import com.example.moveoapplication.note.AddNoteFragment;
import com.example.moveoapplication.note.Note;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    float longitude;
    float latitude;
    private GoogleMap mMap;
    String noteId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        //initialize map fragment
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        //TODO:: of the flag is true - you can put the pin else - you can't
        Boolean isFlag = MapsFragmentArgs.fromBundle(getArguments()).getFlag();

        //Async map
        supportMapFragment.getMapAsync((new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                mMap = googleMap;

                Model.instance.getCurrentUser(user -> {
                    if (isFlag == false) {
                        // User is signed in
                        if (user != null) {
                            Model.instance.mainThread.post(() -> {
                                Model.instance.setActiveUser(user);
                                Model.instance.getAllNotes(list -> {
                                    List<Note> userNotesList = new ArrayList<Note>();
                                    for (Note note : list) {
                                        if (note.getPostUser().equals(user.getEmail())) {
                                            Log.d("TAG", "email active user is " + note.getPostUser());
                                            userNotesList.add(note);
                                        }
                                    }

                                    if (userNotesList != null) {
                                        for (Note n : userNotesList) {
                                            mMap.addMarker(new MarkerOptions().title(n.getTitle())
                                                    .position(new LatLng(n.getLatitude(), n.getLongitude()))
                                                    .snippet(n.getDate()).visible(true));
                                        }
                                        Log.d("taga", "the siezzezezeez is " + userNotesList.size());
                                    }

                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(@NonNull Marker marker) {
                                            Log.d("pin", marker.getTitle() + " title");
                                            Log.d("pin", marker.getId() + " id title");

                                            for (Note no : userNotesList) {
                                                if (no.getTitle().equals(marker.getTitle())) {
                                                    noteId = "" + no.getId();
                                                }
                                            }
                                            Navigation.findNavController(view).navigate(
                                                    MapsFragmentDirections.actionMapsFragmentToEditNoteFragment("" + noteId));
                                        }
                                    });
                                });
                            });
                        }
                    } else {
                        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(@NonNull LatLng latLng) {


                                MarkerOptions clicked = new MarkerOptions().title(latLng.latitude + " : " + latLng.longitude).position(latLng).snippet("clicked").visible(true);

                                mMap.addMarker(clicked);
                                longitude = (float) clicked.getPosition().longitude;
                                latitude = (float) clicked.getPosition().latitude;
                                Navigation.findNavController(view).navigate(MapsFragmentDirections.actionMapsFragmentToAddNoteFragment(latitude, longitude));

                            }
                        });
                    }
                });


                //When map is loaded
               /* if (Model.isFromFeed) {
                    MarkerOptions m = new MarkerOptions();
                    longitude = MapsFragmentArgs.fromBundle(getArguments()).getLongitude();
                    latitude = MapsFragmentArgs.fromBundle(getArguments()).getLatitude();
                    m.position(new LatLng(latitude, longitude));
                    mMap.addMarker(m);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10));
                } else if (Model.isFromEditPost) {
                    MarkerOptions m = new MarkerOptions();
                    longitude = MapFragmentArgs.fromBundle(getArguments()).getLongitude();
                    latitude = MapFragmentArgs.fromBundle(getArguments()).getLatitude();
                    m.position(new LatLng(latitude, longitude));
                    mMap.addMarker(m);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10));
                }*/


            }
        }));
        return view;
    }


}

//onMapClick-> what to do after click
