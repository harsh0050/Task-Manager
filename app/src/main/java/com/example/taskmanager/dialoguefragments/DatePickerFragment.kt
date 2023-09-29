package com.example.taskmanager.dialoguefragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerFragment(private val appContext: Context, private val dateSetter: IDateSetter) :
    DialogFragment(), DatePickerDialog.OnDateSetListener {

    var year : Int = 0
    var month : Int = 0
    var day : Int = 0

    companion object{
        fun newInstance(appContext: Context, dateSetter: IDateSetter, year: Int, month: Int, day: Int): DatePickerFragment {
            val instance = DatePickerFragment(appContext,dateSetter)
            instance.year = year
            instance.month = month
            instance.day = day
            return instance
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calender = Calendar.getInstance()

        return DatePickerDialog(
            appContext,
            this,
            year,
            month,
            day
        )

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
//        dateSetter.setDate(dayOfMonth, month, year)
        parentFragmentManager.setFragmentResult(
            "DATE_PICKER",
            bundleOf("day" to dayOfMonth, "month" to month, "year" to year)
        )
    }
}

interface IDateSetter {
    fun setDate(dayOfMonth: Int, month: Int, year: Int)
}