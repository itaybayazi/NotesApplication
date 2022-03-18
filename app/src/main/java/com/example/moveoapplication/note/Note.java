package com.example.moveoapplication.note;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class Note implements Comparable<Note> {

    final public static String COLLECTION_NAME = "Notes";
    final public static String COLLECTION_DELETE_NAME = "DeleteNote";


    Long id;
    String postUser;
    String imageUrl;
    String date;
    String title;
    String description;
    Long updateData = new Long(0);
    Boolean display = true;
    float latitude;
    float longitude;
    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }




    public Note() {
    }

    public Note(String description, Long id, String postUser,String date , float latitude , float longitude) {
        this.date = date;
        this.description = description;
        this.id = id;
        this.postUser = postUser;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("updateData", FieldValue.serverTimestamp());
        json.put("imageUrl", imageUrl);
        json.put("description", description);
        json.put("id", id);
        json.put("title", title);
        json.put("postUser", postUser);
        json.put("date", date);
        json.put("latitude", latitude);
        json.put("longitude", longitude);
        return json;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static Note create(Map<String, Object> json) {

        if(json.get("id")!=null){
            Log.d("tag","not nulllllllllllllllllll");
        }

        Long id = (Long) json.get("id");
        String imageUrl = (String) json.get("imageUrl");
        String postUser = (String) json.get("postUser");
        String date = (String) json.get("date");
        String title = (String) json.get("title");
        double latitude = (double) json.get("latitude");
        double longitude = (double) json.get("longitude");

        Timestamp ts = (Timestamp) json.get("updateData");
        String description = (String) json.get("description");

        Long update = ts.getSeconds();

        Note note = new Note(description, id, postUser,date,(float) latitude, (float) longitude);
        note.setImageUrl(imageUrl);
        note.setUpdateData(update);
        note.setTitle(title);
        return note;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean flag) {
        this.display = flag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPostUser() {
        return postUser;
    }

    public Long getUpdateData() {
        return updateData;
    }

    public String getDescription() {
        return description;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUpdateData(Long updateData) {
        this.updateData = updateData;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(Note o) {
        return this.getId().compareTo(o.getId());

    }
}
