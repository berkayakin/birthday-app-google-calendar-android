package com.testchambr.birthdayapp.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.testchambr.birthdayapp.R
import com.testchambr.birthdayapp.presenters.AddBirthdayActivityPresenter

import net.danlew.android.joda.JodaTimeAndroid

import kotlinx.android.synthetic.main.content_add_birthday.*
import android.speech.RecognizerIntent
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

/*
 * CREATED BY BERKAY AKIN
 */

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class AddBirthdayActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, AddBirthdayActivityPresenter.AddBirthdayActivityPresenterListener {

    private var addBirthdayActivityPresenter: AddBirthdayActivityPresenter? = null

    companion object {
        val REQUEST_VOICE_RECOGNITION_NAME_CODE = 2001
        val REQUEST_VOICE_RECOGNITION_DATE_CODE = 2002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_birthday)

        //Adding back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        JodaTimeAndroid.init(this)

        //Creating the presenter
        addBirthdayActivityPresenter = AddBirthdayActivityPresenter(this, this)

        val pm = packageManager
        val activities = pm.queryIntentActivities(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0)

        //Checking if there is any voice recognition service on the device
        if (activities.size == 0) {
            nameVoiceImageButton.isEnabled = false
            dateVoiceImageButton.isEnabled = false

            Log.e("Error", "no voice recognition service has found")
        }

        nameVoiceImageButton.setOnClickListener {
            startVoiceRecognitionActivity(REQUEST_VOICE_RECOGNITION_NAME_CODE)
        }

        dateVoiceImageButton.setOnClickListener {
            startVoiceRecognitionActivity(REQUEST_VOICE_RECOGNITION_DATE_CODE)
        }

        //Creating a date picker dialog
        datePickerImageButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                    this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
        }

        //Checking if the fields are empty and showing a toast if the fields are not satisfied
        addToGoogleCalendarButton.setOnClickListener {

            //Clearing unnecessary spaces
            val fullName = nameEditText.text.trim().toString()
            val dateString = dateEditText.text.trim().toString()

            if(!fullName.isEmpty() && !dateString.isEmpty()) {

                //Creating an are you sure dialog if the fields are not empty
                AlertDialog.Builder(this)
                        .setTitle(applicationContext.resources.getString(R.string.are_you_sure))
                        .setMessage(applicationContext.resources.getString(R.string.add_to_warning))
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                            addBirthday(fullName, dateString)
                        }
                        .create()
                        .show()
            } else {
                Toast.makeText(this, this.resources.getString(R.string.fill_the_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addBirthday(fullName : String, dateString : String) {

        //Calling the function from the presenter
        addBirthdayActivityPresenter?.addBirthday(fullName, dateString)
    }

    //Callback ready
    override fun addBirthdayReady() {

        //Disabling the views if successful
        nameEditText.isEnabled = false
        dateEditText.isEnabled = false
        nameVoiceImageButton.isEnabled = false
        dateVoiceImageButton.isEnabled = false
        datePickerImageButton.isEnabled = false

        addToGoogleCalendarButton.setBackgroundColor(applicationContext.resources.getColor(R.color.gray))
        addToGoogleCalendarButtonTitle.text = applicationContext.resources.getString(R.string.done_tap_to_go_back)

        //Finishing the activity on button click after creating the event successfully
        addToGoogleCalendarButton.setOnClickListener {
            finish()
        }
    }

    //Callback failed
    override fun addBirthdayFailed() {
        if(applicationContext != null)
            Toast.makeText(applicationContext, applicationContext.resources?.getString(R.string.date_format_invalid), Toast.LENGTH_SHORT).show()
    }

    //Creating the voice recognition activity
    private fun startVoiceRecognitionActivity(requestCode: Int) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition...")

        startActivityForResult(intent, requestCode)
    }

    //Getting the results of voice recognition
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_VOICE_RECOGNITION_NAME_CODE) {
                val matches = data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS)
                nameEditText.setText(matches?.get(0))
            } else if(requestCode == REQUEST_VOICE_RECOGNITION_DATE_CODE) {
                val matches = data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS)
                dateEditText.setText(matches?.get(0))
            }
        }
    }

    //Callback function of date picker dialog
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        //Formatting the date which comes from the date picker
        val date = DateTime().withYear(year).withMonthOfYear(month+1).withDayOfMonth(dayOfMonth).toDate()
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val dateString = format.format(date)

        dateEditText.setText(dateString)
    }

    //Back button
    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }
}