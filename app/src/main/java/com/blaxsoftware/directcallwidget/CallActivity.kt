package com.blaxsoftware.directcallwidget

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.view.ContextThemeWrapper
import android.widget.LinearLayout

class CallActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LinearLayout(this)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        view.setBackgroundColor(Color.TRANSPARENT)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            call(intent.data)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                CallPermissionExplanation().show(fragmentManager,
                        "callExplanation")
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        Constants.REQUEST_CALL_PERMISSION)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CALL_PERMISSION
                && grantResults?.get(0) == PackageManager.PERMISSION_GRANTED) {
            call(intent.data)
        } else {
            finish()
        }
    }

    private fun call(phone: Uri?) {
        if (phone == null) {
            return
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val callIntent = Intent(Intent.ACTION_CALL, phone)
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(callIntent)
            finish()
        }
    }

    /**
     * A dialog informing the user why they need to grant the CALL_PHONE permission.
     */
    class CallPermissionExplanation : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AppTheme))
                    .setMessage(activity
                            .getString(R.string.request_call_permission_explanation))
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        ActivityCompat.requestPermissions(activity,
                                arrayOf(Manifest.permission.CALL_PHONE),
                                Constants.REQUEST_CALL_PERMISSION)
                    }.create()
        }

        override fun onStart() {
            super.onStart()
            isCancelable = false
        }
    }
}
