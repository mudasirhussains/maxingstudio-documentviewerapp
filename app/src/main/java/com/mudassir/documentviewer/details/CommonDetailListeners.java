package com.mudassir.documentviewer.details;


import com.mudassir.documentviewer.room.entity.DocNoteModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface CommonDetailListeners {


    void onCommonDetailItemClicked(DocNoteModel docModel);
    void onDocFavorite(DocNoteModel docModel, int position);
    void onSendFile(DocNoteModel docModel);
}
