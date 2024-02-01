package com.ashcollege.responses.models;

import com.ashcollege.entities.Note;
import com.github.javafaker.Faker;

import java.text.SimpleDateFormat;

public class NoteModel {
    private int id;
    private String content;
    private String date;

    public NoteModel(Note note) {
        Faker faker=new Faker();

        this.id = note.getId();
        this.content = note.getContent();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.date = simpleDateFormat.format(note.getDate());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
