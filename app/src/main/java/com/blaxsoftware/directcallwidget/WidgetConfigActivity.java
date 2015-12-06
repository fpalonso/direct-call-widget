package com.blaxsoftware.directcallwidget;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.CursorLoader;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider;
import com.blaxsoftware.directcallwidget.image.LoadImageTask;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Spinner mPhoneNumberSpinner;
    private PhoneAdapter mPhoneNumberAdapter;
    private ImageView mThumbnailView;

    private AdView adView;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        mAppWidgetId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        // action bar's custom view
        LayoutInflater inflater = (LayoutInflater) getSupportActionBar()
                .getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customActionBar = inflater.inflate(
                R.layout.actionbar_custom_view_done, null);
        View doneButton = customActionBar.findViewById(R.id.done);
        doneButton.setOnClickListener(this);

        // Set up the action bar
        getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setCustomView(
                customActionBar,
                new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT));

        mWorkerFragment = WorkerFragment.findOrCreate(getFragmentManager());

        mDisplayNameEditText = (EditText) findViewById(R.id.displayName);

        mThumbnailView = (ImageView) findViewById(R.id.thumbnail);
        mThumbnailView.setOnClickListener(this);

        mPhoneNumberSpinner = (Spinner) findViewById(R.id.phoneNumberSpinner);
        mPhoneNumberAdapter = new PhoneAdapter(this, null);
        mPhoneNumberSpinner.setAdapter(mPhoneNumberAdapter);

        // default result for the activity
        setResult(RESULT_CANCELED);

        if (savedInstanceState == null) {
            Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI);
            pickContactIntent
                    .setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
        } else {
            mContactUri = savedInstanceState.getParcelable(STATE_CONTACT_URI);
            mPhotoUri = savedInstanceState.getParcelable(STATE_PHOTO_URI);
            mThumbnail = savedInstanceState.getParcelable(STATE_THUMBNAIL);
            mThumbnailView.setImageBitmap(mThumbnail);

            if (mContactUri != null) {
                initContactLoader(mContactUri);
            }
        }

        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.ad_unit_id));
        adView.setAdSize(AdSize.BANNER);

        if (!BuildConfig.DEBUG) {
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.adContainer);
            adContainer.addView(adView);

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getString(R.string.test_device_id1))
                    .addTestDevice(getString(R.string.test_device_id2)).build();
            adView.loadAd(adRequest);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        adView.destroy();
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
                    mWorkerFragment.loadImage(mPhotoUri, mThumbnailView.getWidth(),
                            mThumbnailView.getHeight());
                }
                break;
            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK) {
                    mPhotoUri = data.getData();
                    mWorkerFragment.loadImage(mPhotoUri, mThumbnailView.getWidth(),
                            mThumbnailView.getHeight());
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
                            mWorkerFragment.loadImage(mPhotoUri,
                                    mThumbnailView.getWidth(),
                                    mThumbnailView.getHeight());
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
                mPhoneNumberAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case CONTACT_LOADER:
                mDisplayNameEditText.setText(null);
                mPhoneNumberAdapter.swapCursor(null);
                mThumbnailView.setImageBitmap(null);
                break;
            case PHONE_NUMBER_LOADER:
                mPhoneNumberAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done:
                accept();
                finish();
                break;
            case R.id.thumbnail:
                if (getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA)) {
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
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraOutput));
            startActivityForResult(intent, TAKE_PHOTO_REQUEST);
        } else {
            // TODO show an error
        }
    }

    private void startPickImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Creates a file that will hold a picture taken from the device's camera.
     *
     * @return the file that has been created
     */
    private File createImageFile() {
        String filename = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        filename = "JPEG_" + filename;
        File fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(filename, ".jpg", fileDir);
        } catch (IOException e) {
            return null;
        }
    }

    void setThumbnail(Bitmap thumbnail) {
        mThumbnail = thumbnail;
        mThumbnailView.setImageBitmap(thumbnail);
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

    private void accept() {
        String displayName = mDisplayNameEditText.getText().toString();
        Cursor phoneCursor = mPhoneNumberAdapter.getCursor();
        String phoneNumber = phoneCursor.getString(phoneCursor
                .getColumnIndex(Phone.NUMBER));

        // Save the data
        SharedPreferences pref = getSharedPreferences(
                Constants.SHAREDPREF_WIDGET, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.SHAREDPREF_WIDGET_DISPLAY_NAME
                + mAppWidgetId, displayName);
        editor.putString(Constants.SHAREDPREF_WIDGET_PHONE + mAppWidgetId,
                phoneNumber);
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
    private static class PhoneAdapter extends SimpleCursorAdapter {

        private final static String[] FROM = {Phone.TYPE, Phone.TYPE,
                Phone.NUMBER};
        private final static int[] TO = {R.id.icon, R.id.phoneType,
                R.id.phoneNumber};

        private Context mContext;

        public PhoneAdapter(Context context, Cursor c) {
            super(context, R.layout.phone_spinner_item, c, FROM, TO, 0);
            mContext = context;
            setDropDownViewResource(R.layout.phone_spinner_dropdown_item);
            setViewBinder(new ViewBinder() {

                @Override
                public boolean setViewValue(View view, Cursor cursor,
                                            int columnIndex) {
                    int phoneType;
                    switch (view.getId()) {
                        case R.id.icon:
                            phoneType = cursor.getInt(columnIndex);
                            switch (phoneType) {
                                case Phone.TYPE_MOBILE:
                                    ((ImageView) view)
                                            .setImageResource(R.drawable.ic_hardware_smartphone_24dp);
                                    break;
                                default:
                                    ((ImageView) view)
                                            .setImageResource(R.drawable.ic_call_grey600_24dp);
                            }
                            break;
                        case R.id.phoneType:
                            phoneType = cursor.getInt(columnIndex);
                            CharSequence phoneTypeLabel = Phone.getTypeLabel(
                                    mContext.getResources(), phoneType, "");
                            ((TextView) view).setText(phoneTypeLabel);
                            break;
                        case R.id.phoneNumber:
                            String phoneNumber = cursor.getString(columnIndex);
                            ((TextView) view).setText(phoneNumber);
                            break;
                    }
                    return true;
                }
            });
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
                    .getApplicationContext(), reqWidth, reqHeight);
            mLoadImageTask.setOnImageLoadedListener(this);
            mLoadImageTask.execute(uri);
        }

        @Override
        public void onImageLoaded(Bitmap bitmap) {
            WidgetConfigActivity activity = (WidgetConfigActivity) getActivity();
            activity.setThumbnail(bitmap);
        }
    }
}
