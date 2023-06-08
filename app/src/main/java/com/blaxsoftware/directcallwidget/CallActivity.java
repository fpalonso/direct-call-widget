/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2020 Fer P. A.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.blaxsoftware.directcallwidget;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;

import android.util.Log;
import android.widget.LinearLayout;

public class CallActivity extends Activity {

    boolean killApplicationAfterCall = false;

    @Override
    protected void onResume() {
        super.onResume();
        if(killApplicationAfterCall){
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout view = new LinearLayout(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(Color.TRANSPARENT);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
//            boolean thirdPartyApp = getIntent().getType() != "";
            call(getIntent().getData(), getIntent());
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                new CallPermissionExplanation().show(getFragmentManager(),
                        "callExplanation");
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        Constants.REQUEST_CALL_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CALL_PERMISSION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            boolean thirdPartyApp = getIntent().getType() != "";
            call(getIntent().getData(), getIntent());
        } else {
            finish();
        }
    }

    private void call(Uri phone, Intent intent) {
        if (phone == null) {
            Log.d("m", "null ___ phone");
            return;
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("callIntent", "call Started ____" + phone + " original ID ___" + intent.getData());
            if (phone.toString().startsWith("tel")) {
                Intent callIntent = new Intent(Intent.ACTION_CALL, phone);
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
                finish();
            } else {
                Log.d("whatsapp", "Start WhatsApp Call");
                try {
                    killApplicationAfterCall = true;
                    Intent callIntent = new Intent();
                    callIntent.setAction(Intent.ACTION_VIEW);
                    callIntent.setDataAndType(
                            Uri.parse("content://com.android.contacts/data/" + phone.toString()),
                            "vnd.android.cursor.item/vnd.com.whatsapp.voip.call"
                    );
                    callIntent.setPackage("com.whatsapp");
                    startActivity(callIntent);
                }catch(Exception e){
                    Log.d("exceptionThrown", e.toString());
                }
            }
        }
    }

    /**
     * A dialog informing the user why they need to grant the CALL_PHONE permission.
     */
    public static class CallPermissionExplanation extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme))
                    .setMessage(getActivity()
                            .getString(R.string.request_call_permission_explanation))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[] {Manifest.permission.CALL_PHONE},
                                    Constants.REQUEST_CALL_PERMISSION);
                        }
                    }).create();
        }

        @Override
        public void onStart() {
            super.onStart();
            setCancelable(false);
        }
    }
}
