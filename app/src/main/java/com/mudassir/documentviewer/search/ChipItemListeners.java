package com.mudassir.documentviewer.search;


import com.mudassir.documentviewer.room.entity.DocNoteModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface ChipItemListeners {
    void onChipItemClicked(int position);
    void onSearchItemClicked(DocNoteModel doc);
}
