package com.scott.airquality

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private var moreInfo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        title = "BC Air Quality Health Index"
        txtRecommendationMsg.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        spnLocations.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateAirQuality(spnLocations.selectedItem as AirQuality)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnMoreInfo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(moreInfo))
            startActivity(intent)
        }
        saveDataAsync()
        loadDataAsync()
    }

    fun saveDataAsync() = async(UI) {
        try {
            val job = async(CommonPool) {
                val db = FirebaseDatabase.getInstance()
                db.getReference(HealthMessage.dbKey).push()
                saveMe(db, HealthMessage.lowRisk())
                saveMe(db, HealthMessage.moderateRisk())
                saveMe(db, HealthMessage.highRisk())
                saveMe(db, HealthMessage.veryHighRisk())

                db.getReference(AirQuality.dbKey).push()
                saveLocation(db, AirQuality.centralOk())
                saveLocation(db, AirQuality.kamloops())

            }
            job.await();
        }
        catch (e: Exception) {
            println("here")
        }
    }

    private fun saveLocation(db: FirebaseDatabase, airQuality: AirQuality) {
        db.getReference("${AirQuality.dbKey}/${airQuality.locationName}").setValue(airQuality)
    }

    private fun saveMe(db: FirebaseDatabase, message: HealthMessage) {
        db.getReference("${HealthMessage.dbKey}/${message.risk}").setValue(message)
    }

    private fun loadDataAsync() {
        val database = FirebaseDatabase.getInstance()
        database.getReference(AirQuality.dbKey).orderByKey().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                val list = mutableListOf<AirQuality>()
                data.children.map {
                    val element = it.getValue(AirQuality::class.java)
                    list.add(element!!)
                }
                val index = list.indexOf(list.find { it.locationName == AirQuality.centralOk().locationName })
                val arrayAdapter = ArrayAdapter(applicationContext, R.layout.spinner_style, list)
                arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style)
                spnLocations.adapter = arrayAdapter
                spnLocations.setSelection(index)
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateAirQuality(data: AirQuality) {
        txtAirQualityIndex.text = data.AQHI
        txtRiskLevel.text = data.riskLevel
        txtUpdated.text = data.calculatedAt
        txtGeneralMsg.text = data.generalMessage
        txtAtRiskMsg.text = data.atRiskMessage
        moreInfo = data.moreInfo
    }

}
