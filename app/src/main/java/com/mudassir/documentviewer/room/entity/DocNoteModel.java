package com.mudassir.documentviewer.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "document_view_entity")
public class DocNoteModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    @ColumnInfo(name = "title")
    private String title = "";

    @ColumnInfo(name = "date_time")
    private String dateTime = "";

    @ColumnInfo(name = "is_trash")
    private Boolean isTrash = false;

    @ColumnInfo(name = "absolutePath")
    private String absolutePath = "";

    @ColumnInfo(name = "isRecent")
    private Boolean isRecent = false;

//    @ColumnInfo(name = "files")
//    private ArrayList<File> files;

    @ColumnInfo(name = "is_favorite")
    private Boolean isFavorite = false;

    private boolean isSelected = false;

    public Boolean getRecent() {
        return isRecent;
    }

    public void setRecent(Boolean recent) {
        isRecent = recent;
    }

    public Boolean getFavorite() {return isFavorite;}

    public void setFavorite(Boolean favorite) {isFavorite = favorite;}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getTrash() {return isTrash;}

    public void setTrash(Boolean trash) {isTrash = trash;}


    @NonNull
    @Override
    public String toString() {
        return title + " : " + dateTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }
}
