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
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider;
import com.blaxsoftware.directcallwidget.image.LoadImageTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WidgetConfigActivity extends AppCompatActivity implements
        LoaderCallbacks<Cursor>, OnClickListener {

    // intent request codes
    private static final int PICK_CONTACT_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private static final int PICK_IMAGE_REQUEST = 3;

    // cursor loader ids
    private static final int CONTACT_LOADER = 1;
    private static final int PHONE_NUMBER_LOADER = 2;

    // loader callbacks' arguments
    private static final String ARG_CONTACT_URI = "contactUri";
    private static final String ARG_CONTACT_LOOKUP = "lookup";

    // activity state
    private static final String STATE_CONTACT_URI = "contactUri";
    private static final String STATE_PHOTO_URI = "photoUri";
    private static final String STATE_THUMBNAIL = "thumbnail";

    private int mAppWidgetId;

    private Uri mContactUri;
    private Uri mPhotoUri;
    private Bitmap mThumbnail;

    private WorkerFragment mWorkerFragment;

    private TextView mDisplayNameEditText;
    private PhoneAdapter mPhoneNumberAdapter;
    private ImageView mThumbnailView;
    private View mDefaultPictureView;
    private Spinner mPhoneNumberSpinner;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        mAppWidgetId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        mWorkerFragment = WorkerFragment.findOrCreate(getFragmentManager());

        mDisplayNameEditText = (EditText) findViewById(R.id.displayName);

        mThumbnailView = (ImageView) findViewById(R.id.thumbnail);
        mThumbnailView.setOnClickListener(this);

        mDefaultPictureView = findViewById(R.id.defaultPicture);

        mPhoneNumberAdapter = new PhoneAdapter(this, null);
        mPhoneNumberSpinner = (Spinner) findViewById(R.id.phoneNumberSpinner);
        mPhoneNumberSpinner.setAdapter(mPhoneNumberAdapter);

        // default result for the activity
        setResult(RESULT_CANCELED);

        if (savedInstanceState == null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                listContacts();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    new ReadContactsPermissionExplanation().show(getSupportFragmentManager(),
                            "readContactsExplanation");
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[] {Manifest.permission.READ_CONTACTS},
                            Constants.REQUEST_READ_CONTACTS_PERMISSION);
                }
            }
        } else {
            mContactUri = savedInstanceState.getParcelable(STATE_CONTACT_URI);
            mPhotoUri = savedInstanceState.getParcelable(STATE_PHOTO_URI);
            mThumbnail = savedInstanceState.getParcelable(STATE_THUMBNAIL);
            setThumbnailBitmap(mThumbnail);

            if (mContactUri != null) {
                initContactLoader(mContactUri);
            }
        }
    }

    private void listContacts() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQUEST_READ_CONTACTS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listContacts();
                } else {
                    finish();
                }
                break;
            case Constants.REQUEST_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPickImageIntent();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mDisplayNameEditText.setCursorVisible(false);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                accept();
                finish();
                return true;
            case R.id.discard:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_CONTACT_REQUEST:
                if (resultCode == RESULT_OK) {
                    mContactUri = data.getData();
                    initContactLoader(mContactUri);
                } else {
                    finish();
                }
                break;
            case TAKE_PHOTO_REQUEST:
                if (resultCode == RESULT_OK) {
                    mThumbnailView.post(new Runnable() {
                        @Override
                        public void run() {
                            mWorkerFragment.loadImage(mPhotoUri, mThumbnailView.getWidth(),
                                    mThumbnailView.getHeight());
                        }
                    });
                }
                break;
            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK) {
                    mPhotoUri = data.getData();
                    mThumbnailView.post(new Runnable() {
                        @Override
                        public void run() {
                            mWorkerFragment.loadImage(mPhotoUri, mThumbnailView.getWidth(),
                                    mThumbnailView.getHeight());
                        }
                    });
                }
                break;
        }
    }

    private void initContactLoader(Uri contactUri) {
        Bundle loaderArgs = new Bundle();
        loaderArgs.putParcelable(ARG_CONTACT_URI, contactUri);
        getLoaderManager().initLoader(CONTACT_LOADER, loaderArgs, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CONTACT_LOADER:
                Uri contactUri = args.getParcelable(ARG_CONTACT_URI);

                String[] contactProjection = {Contacts.DISPLAY_NAME,
                        Contacts.PHOTO_URI, Contacts.LOOKUP_KEY};
                return new CursorLoader(this, contactUri, contactProjection, null,
                        null, null);
            case PHONE_NUMBER_LOADER:
                String lookupKey = args.getString(ARG_CONTACT_LOOKUP);

                String[] phoneProjection = {Phone._ID, Phone.TYPE, Phone.NUMBER};
                String phoneSelection = ContactsContract.Data.LOOKUP_KEY + " = ?"
                        + " AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] phoneSelectionArgs = {lookupKey, Phone.CONTENT_ITEM_TYPE};
                return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
                        phoneProjection, phoneSelection, phoneSelectionArgs,
                        Phone.TYPE);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case CONTACT_LOADER:
                if ((data != null) && data.moveToFirst()) {
                    String displayName = data.getString(data
                            .getColumnIndex(Contacts.DISPLAY_NAME));
                    mDisplayNameEditText.setText(displayName);

                    if (mPhotoUri == null) {
                        String photoUriString = data.getString(data
                                .getColumnIndex(Contacts.PHOTO_URI));
                        if (photoUriString != null) {
                            mPhotoUri = Uri.parse(photoUriString);
                            mThumbnailView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mWorkerFragment.loadImage(mPhotoUri,
                                            mThumbnailView.getWidth(),
                                            mThumbnailView.getHeight());
                                }
                            });
                        }
                    }

                    // Request contact's phone numbers
                    String lookup = data.getString(data
                            .getColumnIndex(Contacts.LOOKUP_KEY));
                    Bundle phoneLoaderArgs = new Bundle();
                    phoneLoaderArgs.putString(ARG_CONTACT_LOOKUP, lookup);
                    getLoaderManager().initLoader(PHONE_NUMBER_LOADER,
                            phoneLoaderArgs, this);
                }
                break;
            case PHONE_NUMBER_LOADER:
                mPhoneNumberAdapter.setData(data);
                mPhoneNumberAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case CONTACT_LOADER:
                mDisplayNameEditText.setText(null);
                mPhoneNumberAdapter.setData(null);
                mPhoneNumberAdapter.notifyDataSetChanged();
                setThumbnailBitmap(null);
                break;
            case PHONE_NUMBER_LOADER:
                mPhoneNumberAdapter.setData(null);
                mPhoneNumberAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.thumbnail:
                if (Device.canUseCamera(this)) {
                    PopupMenu changePhotoPopupMenu = new PopupMenu(
                            WidgetConfigActivity.this, mThumbnailView);
                    changePhotoPopupMenu.getMenuInflater().inflate(
                            R.menu.change_picture, changePhotoPopupMenu.getMenu());
                    changePhotoPopupMenu
                            .setOnMenuItemClickListener(new OnMenuItemClickListener() {

                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.fromCamera:
                                            startCameraIntent();
                                            return true;
                                        case R.id.fromGallery:
                                            startPickImageIntent();
                                            return true;
                                        default:
                                            return false;
                                    }
                                }
                            });
                    changePhotoPopupMenu.show();
                } else {
                    startPickImageIntent();
                }
        }
    }

    private void startCameraIntent() {
        File cameraOutput = createImageFile();
        if (cameraOutput != null) {
            mPhotoUri = Uri.fromFile(cameraOutput);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri outputFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", cameraOutput);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, TAKE_PHOTO_REQUEST);
        } else {
            Toast.makeText(this, R.string.error_using_camera, Toast.LENGTH_LONG).show();
        }
    }

    private void startPickImageIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new ReadExternalStoragePermissionExplanation().show(getSupportFragmentManager(),
                    "readExternalStorageExplanation");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.REQUEST_CALL_PERMISSION);
        }
    }

    /**
     * Creates a file that will hold a picture taken from the device's camera.
     *
     * @return the file that has been created
     */
    private File createImageFile() {
        String filename = "JPEG_" + System.currentTimeMillis();
        File fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(filename, ".jpg", fileDir);
        } catch (IOException e) {
            return null;
        }
    }

    void setThumbnail(String uri, Bitmap thumbnail) {
        mPhotoUri = Uri.parse(uri);
        mThumbnail = thumbnail;
        setThumbnailBitmap(thumbnail);
    }

    private void setThumbnailBitmap(Bitmap thumbnail) {
        mThumbnailView.setImageBitmap(thumbnail);
        mDefaultPictureView.setVisibility(thumbnail == null ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        accept();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CONTACT_URI, mContactUri);
        outState.putParcelable(STATE_PHOTO_URI, mPhotoUri);
        outState.putParcelable(STATE_THUMBNAIL, mThumbnail);
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("CommitPrefEdits")
    private void accept() {
        String displayName = mDisplayNameEditText.getText().toString();
        PhoneNumber selectedNumber = (PhoneNumber) mPhoneNumberSpinner.getSelectedItem();

        // Save the data
        SharedPreferences pref = getSharedPreferences(
                Constants.SHAREDPREF_WIDGET, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.SHAREDPREF_WIDGET_DISPLAY_NAME
                + mAppWidgetId, displayName);
        editor.putString(Constants.SHAREDPREF_WIDGET_PHONE + mAppWidgetId,
                selectedNumber.getNumber());
        editor.putInt(Constants.SHAREDPREF_WIDGET_PHONE_TYPE + mAppWidgetId,
                selectedNumber.getType());
        if (mPhotoUri != null) {
            editor.putString(Constants.SHAREDPREF_WIDGET_PHOTO_URL + mAppWidgetId,
                    mPhotoUri.toString());
        }
        editor.commit();

        // Update the widget
        AppWidgetManager widgetMngr = AppWidgetManager.getInstance(this);
        DirectCallWidgetProvider.updateWidget(getApplicationContext(),
                widgetMngr, mAppWidgetId);

        Intent acceptIntent = new Intent();
        acceptIntent
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, acceptIntent);
    }

    /**
     * An {@code adapter} for the phone number spinner
     */
    private class PhoneAdapter extends BaseAdapter {

        private LayoutInflater mLayoutInflater;
        private List<PhoneNumber> mPhoneNumbers;

        PhoneAdapter(Context context, Cursor phoneCursor) {
            mLayoutInflater = LayoutInflater.from(context);
            setData(phoneCursor);
        }

        void setData(Cursor cursor) {
            if (mPhoneNumbers == null) {
                mPhoneNumbers = new ArrayList<>();
            } else {
                mPhoneNumbers.clear();
            }
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int type = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));
                    String number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    PhoneNumber item = new PhoneNumber(type, number);
                    if (!mPhoneNumbers.contains(item)) {
                        mPhoneNumbers.add(item);
                    }
                } while (cursor.moveToNext());
            }
        }

        @Override
        public int getCount() {
            return mPhoneNumbers != null ? mPhoneNumbers.size() : 0;
        }

        @Override
        public Object getItem(int i) {
            return mPhoneNumbers != null ? mPhoneNumbers.get(i) : null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = mLayoutInflater.inflate(R.layout.phone_spinner_item, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.iconView = (ImageView) view.findViewById(R.id.icon);
                viewHolder.typeView = (TextView) view.findViewById(R.id.phoneType);
                viewHolder.numberView = (TextView) view.findViewById(R.id.phoneNumber);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            // Get the item
            PhoneNumber number = (PhoneNumber) getItem(i);

            // Set the view
            viewHolder.iconView.setImageResource(iconFor(number));
            viewHolder.typeView.setText(typeNameFor(viewGroup.getContext(), number));
            viewHolder.numberView.setText(number.getNumber());

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.phone_spinner_dropdown_item, parent,
                        false);
            }
            PhoneNumber number = (PhoneNumber) getItem(position);
            ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(iconFor(number));
            ((TextView) convertView.findViewById(R.id.phoneType))
                    .setText(typeNameFor(parent.getContext(), number));
            ((TextView) convertView.findViewById(R.id.phoneNumber)).setText(number.getNumber());
            return convertView;
        }

        private int iconFor(PhoneNumber number) {
            switch (number.getType()) {
                case Phone.TYPE_MOBILE:
                case Phone.TYPE_WORK_MOBILE:
                    return R.drawable.ic_hardware_smartphone_24dp;
                default:
                    return R.drawable.ic_call_grey600_24dp;
            }
        }

        private CharSequence typeNameFor(Context context, PhoneNumber number) {
            return Phone.getTypeLabel(context.getResources(), number.getType(), "");
        }

        private class ViewHolder {

            ImageView iconView;
            TextView typeView;
            TextView numberView;
        }
    }

    private class PhoneNumber {

        private int mType;
        private String mNumber;

        PhoneNumber(int type, String number) {
            mType = type;
            mNumber = number;
        }

        int getType() {
            return mType;
        }

        String getNumber() {
            return mNumber;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof PhoneNumber) {
                PhoneNumber otherNumber = (PhoneNumber) obj;
                if (mNumber != null
                        && mNumber.equals(otherNumber.getNumber())) {
                    return true;
                }
            }
            return super.equals(obj);
        }
    }

    public static class WorkerFragment extends Fragment implements
            LoadImageTask.OnImageLoadedListener {

        private static final String TAG = "Worker";

        private LoadImageTask mLoadImageTask;

        public WorkerFragment() {
            setRetainInstance(true);
        }

        public static WorkerFragment findOrCreate(FragmentManager fragMngr) {
            WorkerFragment frag = (WorkerFragment) fragMngr
                    .findFragmentByTag(TAG);
            if (frag == null) {
                frag = new WorkerFragment();
                fragMngr.beginTransaction().add(frag, TAG).commit();
            }
            return frag;
        }

        public void loadImage(Uri uri, int reqWidth, int reqHeight) {
            if (mLoadImageTask != null) {
                mLoadImageTask.cancel(true);
            }
            mLoadImageTask = new LoadImageTask(getActivity()
                    .getApplicationContext(), reqWidth, reqHeight, true);
            mLoadImageTask.setOnImageLoadedListener(this);
            mLoadImageTask.execute(uri);
        }

        @Override
        public void onImageLoaded(String uri, Bitmap bitmap) {
            WidgetConfigActivity activity = (WidgetConfigActivity) getActivity();
            activity.setThumbnail(uri, bitmap);
        }
    }

    /**
     * A dialog informing the user why they need to grant the READ_CONTACT permission.
     */
    public static class ReadContactsPermissionExplanation extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            @SuppressLint("InflateParams")
            TextView view = (TextView) LayoutInflater.from(getActivity())
                    .inflate(R.layout.dialog_text, null);
            view.setText(getActivity()
                    .getString(R.string.request_readContacts_permission_explanation));
            return new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme))
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[] {Manifest.permission.READ_CONTACTS},
                                    Constants.REQUEST_READ_CONTACTS_PERMISSION);
                        }
                    }).create();
        }
    }

    /**
     * A dialog informing the user why they need to grant the READ_EXTERNAL_STORAGE permission.
     */
    public static class ReadExternalStoragePermissionExplanation extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            @SuppressLint("InflateParams")
            TextView view = (TextView) LayoutInflater.from(getActivity())
                    .inflate(R.layout.dialog_text, null);
            view.setText(getActivity()
                    .getString(R.string.request_external_storage_explanation));
            return new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme))
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                    Constants.REQUEST_EXTERNAL_STORAGE_PERMISSION);
                        }
                    }).create();
        }
    }
}
