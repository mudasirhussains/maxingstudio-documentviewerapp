package com.mudassir.documentviewer.bottomnavigation.favorite;


import com.mudassir.documentviewer.room.entity.DocNoteModel;

public interface FavoriteListeners {
    void onFavoriteClickedForUnFav(int id,int position);
    void onFavDetailItemClicked(DocNoteModel docModel);
    void onSendFile(DocNoteModel docModel);
}
