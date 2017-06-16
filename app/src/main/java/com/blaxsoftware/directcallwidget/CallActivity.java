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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.LinearLayout;

public class CallActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout view = new LinearLayout(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(Color.TRANSPARENT);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            call(getIntent().getData());
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
            call(getIntent().getData());
        } else {
            finish();
        }
    }

    private void call(Uri phone) {
        if (phone == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL, phone);
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(callIntent);
            finish();
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
