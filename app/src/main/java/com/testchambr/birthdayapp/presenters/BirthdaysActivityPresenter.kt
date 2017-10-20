package com.testchambr.birthdayapp.presenters

import android.content.Context
import com.testchambr.birthdayapp.models.Birthday
import com.testchambr.birthdayapp.utils.BirthdayComparator
import com.testchambr.birthdayapp.utils.MyDBHandler
import java.util.*

/*
 * CREATED BY BERKAY AKIN
 */

class BirthdaysActivityPresenter(context: Context, private var birthdaysActivityPresenterListener: BirthdaysActivityPresenterListener?) : BasePresenter(context) {

    //Callback events
    interface BirthdaysActivityPresenterListener {

        fun birthdaysReady(birthdaysList: MutableList<Birthday>?)
        fun birthdaysFailed()
    }

    private var myDBHandler : MyDBHandler? = null

    //Creating the Database Handler
    init {
        myDBHandler = MyDBHandler(context)
    }

    fun getBirthdays() {
        //Getting the birthdays data from the app database (SQLite)
        val birthdaysList = myDBHandler?.getBirthdays()

        birthdaysList.let {
            //Sorting the birthdays by the closest date with a custom comparator class
            Collections.sort(birthdaysList, BirthdayComparator())

            //Triggering the callback with the sorted birthdays list
            birthdaysActivityPresenterListener?.birthdaysReady(it)
        }

        //If the birthdays list is null, triggering the failed event
        if(birthdaysList == null)
            birthdaysActivityPresenterListener?.birthdaysFailed()
    }

    //Deleting the birthday from the app database
    fun deleteBirthday(birthdayId: Int) {
        myDBHandler?.deleteBirthday(birthdayId)
    }
}