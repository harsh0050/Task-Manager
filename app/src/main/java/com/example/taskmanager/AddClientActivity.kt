package com.example.taskmanager

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentResultListener
import com.example.taskmanager.dao.ClientDao
import com.example.taskmanager.databinding.ActivityAddClientBinding
import com.example.taskmanager.dialoguefragments.SubServiceDialogFragment
import com.example.taskmanager.models.Client
import com.example.taskmanager.models.Service
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class AddClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddClientBinding
    private lateinit var roomDB: RoomDB
    private lateinit var clientDao: ClientDao
    private val enabledServices = HashMap<String, Service>()
    private lateinit var context: AppCompatActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addClientActivityProgressBar.visibility = View.GONE

        setSupportActionBar(binding.addClientActivityAppbar)
        supportActionBar?.title = "Add Client"


        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        context = this

        roomDB = RoomDB.getInstance(this)
        clientDao = roomDB.getClientDao()

        debug()


        val checkBoxList = HashMap<String, CheckBox>()
        checkBoxList["GST"] = binding.gstCheckBox
        checkBoxList["Income Tax"] = binding.incomeTaxCheckBox
        checkBoxList["Cost Audit"] = binding.costAuditCheckBox
        checkBoxList["Cost Records"] = binding.costRecordCheckBox
        checkBoxList["TDS Filling"] = binding.tdsFillingCheckBox
        checkBoxList["TDS Payment"] = binding.tdsPaymentCheckBox
        checkBoxList["Accounting"] = binding.accountingCheckBox
        checkBoxList["DSC"] = binding.dscCheckBox
//        checkBoxList["other"] = binding.otherCheckBox

        supportFragmentManager.setFragmentResultListener(
            "sub_services_request_key",
            context,
            object : FragmentResultListener {
                override fun onFragmentResult(requestKey: String, result: Bundle) {
                    val wasSaved = result.getBoolean("isSaved")
                    if (wasSaved) {
                        println(wasSaved)
                        var subServicesList = ArrayList<String>()
                        result.getStringArrayList("availedSubServices")?.let {
                            subServicesList = it
                        }
                        val serviceTenure = result.getString("serviceTenure", "dMonthly")
                        val serviceRate = result.getString("serviceRate", "0")
                        val parentService = result.getString("parentService", "other")
                        println(subServicesList)
                        println(serviceTenure)
                        println(serviceRate)
                        val service = Service(
                            parentService,
                            subServicesList,
                            serviceTenure,
                            serviceRate.toInt()
                        )
                        enabledServices[parentService] = service
                    } else {
                        result.getString("parentService")?.let {
                            checkBoxList[it]?.isChecked = false
                        }
                    }
                }
            })


        checkBoxList.forEach {
            it.value.setOnCheckedChangeListener { _, isChecked ->
                launchFragment(it.key, isChecked)
            }
        }



        binding.saveClientButton.setOnClickListener {
            saveClient()
        }
    }


    private fun launchFragment(serviceName: String, isChecked: Boolean) {
        if (isChecked) {
            val dialogFragment = SubServiceDialogFragment.newInstance(serviceName)
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val prevDialog = supportFragmentManager.findFragmentByTag("subServiceDialog")
            prevDialog?.let {
                fragmentTransaction.remove(prevDialog)
            }
            fragmentTransaction.add(dialogFragment, "subServiceDialog")
            fragmentTransaction.commitNow()
        } else {
            enabledServices.remove(serviceName)
        }

    }

    private fun saveClient() {
        binding.addClientActivityProgressBar.visibility = View.VISIBLE

        val clientName = binding.clientNameEditText.text.toString()
        val clientNumber = binding.clientNumberEditText.text.toString()
        val referenceName = binding.referenceNameEditText.text.toString()
        val contactPersonName = binding.contactPersonNameEditText.toString()
        val address = binding.addressEditText.text.toString()
        val servicesList = ArrayList<Service>()
        enabledServices.forEach {
            servicesList.add(it.value)
        }
        val client = Client(
            clientName,
            clientNumber,
            referenceName,
            contactPersonName,
            address,
            servicesList
        )
        GlobalScope.launch(Dispatchers.IO) {
            clientDao.addClient(client)
            runOnUiThread {
                finish()
            }
        }

    }


    private fun debug() {
        clientDao.getAllClientsLiveData().observe(context) {
            it.forEach { client ->
                client.services.forEach { service ->
                    println(service.service + ": \n" + service.subServices)
                }
            }
        }
    }


}