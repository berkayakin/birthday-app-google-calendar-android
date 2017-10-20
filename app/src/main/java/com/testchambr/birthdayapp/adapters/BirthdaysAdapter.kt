package com.testchambr.birthdayapp.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.testchambr.birthdayapp.R
import com.testchambr.birthdayapp.models.Birthday
import com.testchambr.birthdayapp.utils.Utils
import java.lang.ref.WeakReference

/*
 * CREATED BY BERKAY AKIN
 */

@Suppress("UNUSED_PARAMETER")
class BirthdaysAdapter(private var context: Context, private var birthdaysList : MutableList<Birthday>?, private var clickListener: ClickListener) : RecyclerView.Adapter<BirthdaysAdapter.MyViewHolder>() {

    //Click Listener interface for the delete button
    interface ClickListener {
        fun onDeleteClicked(birthdayId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder? {

        //Setting the custom layout
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.birthday_object_layout, parent, false)

        return MyViewHolder(itemView, clickListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {

        //Stopping views from being recycled, which can cause problems on fast scrolls
        holder?.setIsRecyclable(false)

        val birthday = birthdaysList?.get(position)

        try {

            //Adding bottom margin to the last item to create space for FloatActionButton on the activity
            if(birthdaysList != null && birthdaysList!!.size > 0 && position == birthdaysList!!.size-1) {

                //Calculating and converting integer to pixels
                val scale = context.resources.displayMetrics.density
                val pixels = (1 * scale + 0.5f).toInt()

                val lp = holder?.itemView?.layoutParams as RecyclerView.LayoutParams

                //Setting the bottom space for the last item
                lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, 77 * pixels)

                //Applying the changes on the view
                holder.itemView?.layoutParams = lp
                holder.itemView?.requestLayout()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Bind function is setting the items and filling the fields from the data
        (holder as MyViewHolder).bind(context, birthday)
    }

    //Item count
    override fun getItemCount(): Int {
        return birthdaysList?.size as Int
    }

    class MyViewHolder(private var view: View, clickListener: ClickListener) : RecyclerView.ViewHolder(view) {

        //We are using a weak reference to make sure this can be deleted from the memory without problems by GC
        private var clickListenerRef : WeakReference<ClickListener> = WeakReference(clickListener)

        fun bind (context : Context, birthday: Birthday?) {

            //Declaring the views of the custom view
            val fullNameTextView = view.findViewById<TextView>(R.id.fullNameTextView)
            val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
            val deleteBirthdayImageButton = view.findViewById<ImageButton>(R.id.deleteBirthdayImageButton)

            //Filling the fields like name and birthday if the data is not null
            birthday?.fullName.let { fullNameTextView.text = it }
            birthday?.birthDay.let { dateTextView.text = Utils.beautifyDate(it) }

            //Triggering the delete callback with the birthday id
            deleteBirthdayImageButton.setOnClickListener{
                birthday?.id.let {
                    clickListenerRef.get()?.onDeleteClicked(birthday?.id!!)
                }
            }
        }
    }
}