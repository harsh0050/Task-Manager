package com.example.taskmanager.dialoguefragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.taskmanager.R

class SubServiceDialogFragment : DialogFragment() {
    private lateinit var parentService: String
    private lateinit var subServicesLinearLayout: LinearLayout
    private lateinit var serviceTenureRadioGroup: RadioGroup
    private lateinit var saveButton: Button
    private lateinit var serviceRate: EditText
    private val availedSubServices = ArrayList<String>()
    private var isSaved = false
    private val REQUEST_KEY = "sub_services_request_key"

    companion object {
        fun newInstance(service: String): SubServiceDialogFragment {
            val instance = SubServiceDialogFragment()
            val bundle = Bundle()
            bundle.putString("service", service)
            instance.arguments = bundle
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            parentService = it.getString("service", "other")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.dialog_sub_service_selection, container, false)
        subServicesLinearLayout = view.findViewById(R.id.subServicesLinearLayout)
        serviceTenureRadioGroup = view.findViewById(R.id.serviceTenureRadioGroup)
        populateSubServices(parentService)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.curved_white_background)
        saveButton = view.findViewById(R.id.subServiceSaveButton)
        serviceRate = view.findViewById(R.id.serviceRateEditText)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("parentService", parentService)

            bundle.putString("serviceRate", serviceRate.text.toString())
            bundle.putStringArrayList("availedSubServices", availedSubServices)
            val checkRadioButtonId = serviceTenureRadioGroup.checkedRadioButtonId
            if (checkRadioButtonId != -1) {
                val tenure =
                    view.findViewById<RadioButton>(checkRadioButtonId)
                bundle.putString("serviceTenure", tenure.text as String)
            }
            isSaved = true
            bundle.putBoolean("isSaved", isSaved)

            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundle)

            dismiss()
        }
    }

    override fun onDestroyView() {
        println("onDestroyView()")
        if(!isSaved){
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf("isSaved" to isSaved, "parentService" to parentService)
            )
        }
        super.onDestroyView()
    }

    private fun populateSubServices(parentService: String) {
        when (parentService) {
            "GST" -> {
                addCheckBox(
                    subServicesLinearLayout,
                    listOf("GSTR-1", "GSTR-3B", "GSTR-9", "GST Register", "GST Update")
                )
                addRadioButton(serviceTenureRadioGroup, listOf("Monthly", "Quarterly"))
            }

            "Income" -> {
                addCheckBox(
                    subServicesLinearLayout,
                    listOf("IT Return", "Tax Audit", "Statutory Audit", "IT Notice")
                )
                addRadioButton(serviceTenureRadioGroup, listOf("Yearly"))
            }

            "Cost Audit" -> {
                addRadioButton(serviceTenureRadioGroup, listOf("Yearly"))
            }

            "Cost Records" -> {
                addRadioButton(serviceTenureRadioGroup, listOf("Yearly"))
            }

            "TDS Filling" -> {
                addRadioButton(serviceTenureRadioGroup, listOf("Quarterly"))
            }

            "TDS Payment" -> {
                addRadioButton(serviceTenureRadioGroup, listOf("Quarterly"))
            }

            "Accounting" -> {
                addRadioButton(
                    serviceTenureRadioGroup,
                    listOf("Monthly", "Quarterly", "15 Days", "Weekly", "Yearly")
                )
            }


        }
    }

    private fun addRadioButton(radioGroup: RadioGroup, list: List<String>) {
        for (i in list.indices) {
            val temp = list[i]
            val radioButton = RadioButton(radioGroup.context)
            radioButton.layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            radioButton.id = 150 + i
            radioButton.text = temp
            radioGroup.addView(radioButton)
        }
    }

    private fun addCheckBox(viewGroup: ViewGroup, list: List<String>) {
        for (i in list.indices) {
            val temp = list[i]

            val checkBox = CheckBox(viewGroup.context)
            checkBox.layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            checkBox.text = temp
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    availedSubServices.add(temp)
                } else {
                    availedSubServices.remove(temp)
                }
            }
            viewGroup.addView(checkBox)
        }
    }
}