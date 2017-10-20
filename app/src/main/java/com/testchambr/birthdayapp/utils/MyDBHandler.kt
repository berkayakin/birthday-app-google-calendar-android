package com.testchambr.birthdayapp.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.testchambr.birthdayapp.models.Birthday
import java.util.*

/*
 * CREATED BY BERKAY AKIN
 */

@Suppress("unused")
class MyDBHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "birthdays.db"
        val TABLE_BIRTHDAYS = "birthdays"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_DATE = "date"
    }

    //Creating the db
    override fun onCreate(db: SQLiteDatabase?) {

        val query = "CREATE TABLE $TABLE_BIRTHDAYS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_DATE LONG);"

        db?.execSQL(query)
    }

    //Upgrading the table when we want to create again with new fields
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BIRTHDAYS")
        onCreate(db)
    }

    //Adding the birthday to the db
    fun addBirthday(birthday: Birthday) {
        val values = ContentValues()

        //Setting the fields
        values.put(COLUMN_NAME, birthday.fullName)
        values.put(COLUMN_DATE, birthday.birthDay?.time)

        //Getting the db
        val db = writableDatabase

        //Inserting
        db.insert(TABLE_BIRTHDAYS, null, values)
        db.close()
    }

    //Deleting the birthday from the db
    fun deleteBirthday(birthdayId: Int) {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_BIRTHDAYS WHERE $COLUMN_ID = $birthdayId;")
    }

    //Getting the birthdays and returning a list of them
    fun getBirthdays(): MutableList<Birthday>? {

        val birthdaysList: MutableList<Birthday> = ArrayList()

        val db = writableDatabase
        val query = "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_DATE FROM $TABLE_BIRTHDAYS WHERE 1"

        //Executing the query
        val c = db.rawQuery(query, null)

        //Reading the rows
        if (c.moveToFirst()) {
            do {
                val colId = c.getInt(0)
                val colName = c.getString(1)
                val colDate = c.getLong(2)

                val birthday = Birthday()
                birthday.id = colId
                birthday.fullName = colName
                birthday.birthDay = Date(colDate)

                birthdaysList.add(birthday)

            } while (c.moveToNext())
        }

        //Closing the cursor
        c.close()

        //Closing the connection with the db
        db.close()

        return birthdaysList
    }

}