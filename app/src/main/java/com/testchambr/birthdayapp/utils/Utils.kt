package com.testchambr.birthdayapp.utils

import org.joda.time.DateTime
import java.util.*

/*
 * CREATED BY BERKAY AKIN
 */

class Utils() {

    companion object {
        fun beautifyDate (date: Date?): String {

            if(date == null)
                return ""

            //Extracting day of the month, month name and year from the date
            val stringDay = android.text.format.DateFormat.format("dd", date)
            val stringMonth = android.text.format.DateFormat.format("MMMM", date)
            val stringYear = android.text.format.DateFormat.format("yyyy", date)

            return "$stringDay $stringMonth $stringYear"
        }

        fun setYearForNextAnniversary(date: Date?): Date {

            //Setting the birthday's year as current year because we will create an event
            var mDate = DateTime(date).withYear(Calendar.getInstance().get(Calendar.YEAR))

            //But this date still can be earlier than today, so if it is, we are setting the year as next year
            if(Date().after(mDate.toDate())) {
                mDate = mDate.withYear(Calendar.getInstance().get(Calendar.YEAR)+1)
            }

            //Converting the Joda DateTime object to a Java Date object if it is null
            return if(mDate.toDate() != null) {
                mDate.toDate()
            } else {
                //If it is null, returning the current date
                Date()
            }
        }
    }

}