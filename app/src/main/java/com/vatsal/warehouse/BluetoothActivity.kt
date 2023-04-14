package com.vatsal.warehouse

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class BluetoothActivity : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        supportActionBar!!.title = getString(R.string.title_activity_bluetooth)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#000000")))

        if (bluetoothAdapter == null) {
            Toast.makeText(
                applicationContext,
                "Device not supporting bluetooth",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!bluetoothAdapter.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        // bluetooth on button
        val onbtn = findViewById<Button>(R.id.button_turn_on)
        onbtn.setOnClickListener {
            if (!bluetoothAdapter.isEnabled) {
                bluetoothAdapter.enable()
                Toast.makeText(applicationContext, "Bluetooth on", Toast.LENGTH_SHORT).show()
            }
        }

        // bluetooth off button
        val offbtn = findViewById<Button>(R.id.button_turn_off)
        offbtn.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                bluetoothAdapter.disable()
                Toast.makeText(applicationContext, "Bluetooth off", Toast.LENGTH_SHORT).show()
            }
        }

        // button for show list of paired devices
        val pairbtn = findViewById<Button>(R.id.button_paired_devices)
        pairbtn.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                pairedDevices = bluetoothAdapter.bondedDevices
                val devicelist = ArrayList<String>()
                for (device in pairedDevices) {
                    devicelist.add(device.name + "\n" + device.address)
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devicelist)
                val list_paired_devcices = findViewById<ListView>(R.id.list_paired_devices)
                list_paired_devcices.adapter = adapter

            } else {
                Toast.makeText(applicationContext, "Please turn on Bluetooth", Toast.LENGTH_SHORT)
                    .show()

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}