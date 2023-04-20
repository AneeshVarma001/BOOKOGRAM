package com.example.bookshare.db

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import com.example.bookshare.BuildConfig

class SQLiteDb : SQLiteOpenHelper {
    constructor(context: Context?) : super(context, DATABASE_NAME, null, DATABASE_VERSION) {}
    constructor(context: Context?, name: String?, factory: CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    ) {
    }

    constructor(
        context: Context?,
        name: String?,
        factory: CursorFactory?,
        version: Int,
        errorHandler: DatabaseErrorHandler?
    ) : super(context, name, factory, version, errorHandler) {
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(UploadedBookTable.createQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + UploadedBookTable.TABLE_NAME)
        onCreate(db)
    }

    companion object {
        val DATABASE_VERSION = BuildConfig.DATABASE_VERSION.toInt()
        const val DATABASE_NAME = BuildConfig.DATABASE_NAME
    }
}