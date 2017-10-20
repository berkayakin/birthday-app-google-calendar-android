package com.testchambr.birthdayapp.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.testchambr.birthdayapp.R
import com.testchambr.birthdayapp.adapters.BirthdaysAdapter
import com.testchambr.birthdayapp.models.Birthday
import com.testchambr.birthdayapp.presenters.BirthdaysActivityPresenter

import kotlinx.android.synthetic.main.activity_birthdays.*
import kotlinx.android.synthetic.main.content_birthdays.*
import android.support.v7.app.AlertDialog
import net.danlew.android.joda.JodaTimeAndroid

/*
 * CREATED BY BERKAY AKIN
 */

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class BirthdaysActivity : AppCompatActivity(), BirthdaysActivityPresenter.BirthdaysActivityPresenterListener, BirthdaysAdapter.ClickListener {

    private var birthdaysActivityPresenter : BirthdaysActivityPresenter? = null

    companion object {
        val ADD_BIRTHDAY_REQUEST_CODE = 1009
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_birthdays)

        JodaTimeAndroid.init(this)

        //Creating the presenter
        birthdaysActivityPresenter = BirthdaysActivityPresenter(this, this)

        //Setting the layout for the Recycler View
        val linearLayoutManager = LinearLayoutManager(this)
        birthdaysRecyclerView.layoutManager = linearLayoutManager

        //Setting the refresh event of the Swipe Refresh Layout
        birthdaysSwipeRefreshLayout.setOnRefreshListener {
            getBirthdays()
        }

        addBirthdayFAB.setOnClickListener {
            val addBirthdayIntent = Intent(this, AddBirthdayActivity::class.java)
            //A result code could be necessary in the future
            startActivityForResult(addBirthdayIntent, ADD_BIRTHDAY_REQUEST_CODE)
        }
    }

    //Refreshing the birthdays on activity has been resumed
    override fun onResume() {
        super.onResume()

        getBirthdays()
    }

    private fun getBirthdays() {

        //Calling the function from the presenter
        birthdaysActivityPresenter?.getBirthdays()
    }

    //Callback ready
    override fun birthdaysReady(birthdaysList: MutableList<Birthday>?) {

        //Setting the adapter of Birthdays Recycler View
        val birthdaysAdapter = BirthdaysAdapter(this, birthdaysList, this)
        birthdaysRecyclerView.adapter = birthdaysAdapter

        if(birthdaysSwipeRefreshLayout != null)
            birthdaysSwipeRefreshLayout.isRefreshing = false
    }

    //Callback failed
    override fun birthdaysFailed() {

        if(birthdaysSwipeRefreshLayout != null)
            birthdaysSwipeRefreshLayout.isRefreshing = false
    }

    //Birthday delete button click callback which is connected with the adapter
    override fun onDeleteClicked(birthdayId: Int) {

        //Creating an are you sure dialog
        AlertDialog.Builder(this)
                .setTitle(applicationContext.resources.getString(R.string.are_you_sure))
                .setMessage(applicationContext.resources.getString(R.string.are_you_sure_delete))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    birthdaysActivityPresenter?.deleteBirthday(birthdayId)
                    birthdaysActivityPresenter?.getBirthdays()
                }
                .create()
                .show()
    }
}