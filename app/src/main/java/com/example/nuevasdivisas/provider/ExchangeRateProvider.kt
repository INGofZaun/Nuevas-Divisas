package com.example.nuevasdivisas.provider

import android.content.*
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import com.example.nuevasdivisas.data.AppDatabase
import com.example.nuevasdivisas.data.ExchangeRate

class ExchangeRateProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.example.nuevasdivisas.provider"
        private const val TABLE_NAME = "exchange_rates"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?
    ): Cursor? {
        val db = context?.let { AppDatabase.getDatabase(it) } ?: return null
        return db.query("SELECT * FROM $TABLE_NAME", null)
    }

    override fun getType(uri: Uri): String? = "vnd.android.cursor.dir/$TABLE_NAME"
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
