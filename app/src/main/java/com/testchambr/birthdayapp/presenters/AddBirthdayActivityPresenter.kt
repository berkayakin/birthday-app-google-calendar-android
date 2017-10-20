package com.testchambr.birthdayapp.presenters

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.testchambr.birthdayapp.R
import com.testchambr.birthdayapp.models.Birthday
import com.testchambr.birthdayapp.utils.MyDBHandler
import com.testchambr.birthdayapp.utils.Utils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/*
 * CREATED BY BERKAY AKIN
 */

class AddBirthdayActivityPresenter(context: Context, private var addBirthdayActivityPresenterListener: AddBirthdayActivityPresenterListener?) : BasePresenter(context) {

    //Callback events
    interface AddBirthdayActivityPresenterListener {

        fun addBirthdayReady()
        fun addBirthdayFailed()
    }

    private var myDBHandler : MyDBHandler? = null

    //Creating the Database Handler
    init {
        myDBHandler = MyDBHandler(context)
    }

    fun addBirthday(fullName : String, dateString : String) {

        var date: Date? = null

        //Trying to convert the given string to a Date object with the format
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        try {
            date = format.parse(dateString)
            System.out.println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        //Checking if the Date object has been created successfully
        if(date != null) {

            //Creating a birthday object
            val birthday = Birthday()
            birthday.fullName = fullName
            birthday.birthDay = date

            //Saving the birthday to the app db
            myDBHandler?.addBirthday(birthday)

            //Triggering the callback
            addBirthdayActivityPresenterListener?.addBirthdayReady()

            //Sending the data to Google Calendar app
            addBirthdayToGoogleCalendar(birthday)
        } else {
            //And if date variable is still null, it means the given string format was incorrect
            addBirthdayActivityPresenterListener?.addBirthdayFailed()
        }
    }

    private fun addBirthdayToGoogleCalendar(birthday: Birthday) {

        //Setting the birthdays year for the closest anniversary with a custom function
        birthday.birthDay = Utils.setYearForNextAnniversary(birthday.birthDay)

        //setYearForNextAnniversary function is returning the current date if something goes wrong but it is better to take care
        if(birthday.birthDay == null)
            return

        //Creating the intent for Google Calendar Content Provider
        val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, birthday.birthDay!!.time)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, birthday.birthDay!!.time)
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.Events.TITLE, context?.resources?.getString(R.string.birthday_of, birthday.fullName))
                .putExtra(CalendarContract.Events.DESCRIPTION, context?.resources?.getString(R.string.birthday_of, birthday.fullName))

        //Starting Google Calendar app
        context?.startActivity(intent)

    }

}