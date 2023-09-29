package com.example.taskmanager.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.BaseAdapter
//import android.widget.CheckedTextView
//import android.widget.TextView
//import com.example.taskmanager.R
//
//class ClientSpinnerAdapter(val context: Context, private val clientData: List<String>) :
//    BaseAdapter() {
//
//    private val inflater = LayoutInflater.from(context)
//
//    override fun getCount(): Int {
//        return clientData.size
//    }
//
//    override fun getItem(position: Int): Any {
//        return clientData[position]
//    }
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        val item = clientData[position]
//
//        val view = convertView ?: inflater
//            .inflate(R.layout.custom_spinner_item, parent, false)
//
//        view.findViewById<TextView>(R.id.client_spinner_item_client_name).text = item.client_name
//        return view
//    }
//
//    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        val view = convertView ?: inflater.inflate(
//            android.R.layout.simple_spinner_dropdown_item,
//            parent,
//            false
//        )
//        view.findViewById<CheckedTextView>(android.R.id.text1).text = clientData[position].client_name
//        return view
//    }
//}
//
//data class ClientSpinnerItem(
//    val client_name: String,
//    val clientId: Int,
//)