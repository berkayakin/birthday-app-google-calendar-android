package com.testchambr.birthdayapp.utils

import com.testchambr.birthdayapp.models.Birthday
import java.util.*

/*
 * CREATED BY BERKAY AKIN
 */

class BirthdayComparator : Comparator<Birthday> {
    override fun compare(obj1: Birthday, obj2: Birthday): Int {

        return if(obj1.birthDay != null && obj2.birthDay != null) {

            //Setting birthdays' year for the next anniversary and comparing to each others to sort by the closest
            Utils.setYearForNextAnniversary(obj1.birthDay).compareTo(Utils.setYearForNextAnniversary(obj2.birthDay))

        } else {
            0
        }
    }
}