package com.mudassir.documentviewer.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mudassir.documentviewer.room.dao.DocumentDao
import com.mudassir.documentviewer.room.entity.DocNoteModel
import com.mudassir.documentviewer.room.entity.DocNoteModelForRecent

@Database(
    entities = [DocNoteModel::class, DocNoteModelForRecent::class],
    version = 1,
    exportSchema = false
)
abstract class DatabaseBuilder : RoomDatabase() {

    abstract fun dao(): DocumentDao

    companion object {
        private var instance: DatabaseBuilder? = null

        @Synchronized
        fun getInstance(ctx: Context): DatabaseBuilder {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, DatabaseBuilder::class.java,
                    "doc_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()

            return instance!!

        }

    }
}