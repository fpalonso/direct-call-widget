package com.blaxsoftware.directcallwidget

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.app.FragmentManager
import android.app.LoaderManager.LoaderCallbacks
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ContextThemeWrapper
import android.view.*
import android.view.View.OnClickListener
import android.widget.*
import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider
import com.blaxsoftware.directcallwidget.image.LoadImageTask
import com.blaxsoftware.directcallwidget.image.OnImageLoadedListener
import java.io.File
import java.io.IOException
import java.util.*

class WidgetConfigActivity : AppCompatActivity(), LoaderCallbacks<Cursor>, OnClickListener {

    private var mAppWidgetId: Int = 0

    private var mContactUri: Uri? = null
    private var mPhotoUri: Uri? = null
    private var mThumbnail: Bitmap? = null

    private var mWorkerFragment: WorkerFragment? = null

    private var mDisplayNameEditText: TextView? = null
    private var mPhoneNumberAdapter: PhoneAdapter? = null
    private var mThumbnailView: ImageView? = null
    private var mDefaultPictureView: View? = null
    private var mPhoneNumberSpinner: Spinner? = null

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config)

        mAppWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)

        mWorkerFragment = WorkerFragment.findOrCreate(fragmentManager)

        mDisplayNameEditText = findViewById(R.id.displayName) as EditText?

        mThumbnailView = findViewById(R.id.thumbnail) as ImageView
        mThumbnailView!!.setOnClickListener(this)

        mDefaultPictureView = findViewById(R.id.defaultPicture)

        mPhoneNumberAdapter = PhoneAdapter(this, null)
        mPhoneNumberSpinner = findViewById(R.id.phoneNumberSpinner) as Spinner
        mPhoneNumberSpinner!!.adapter = mPhoneNumberAdapter

        // default result for the activity
        setResult(Activity.RESULT_CANCELED)

        if (savedInstanceState == null) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                listContacts()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    ReadContactsPermissionExplanation().show(supportFragmentManager,
                            "readContactsExplanation")
                } else {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_CONTACTS),
                            Constants.REQUEST_READ_CONTACTS_PERMISSION)
                }
            }
        } else {
            mContactUri = savedInstanceState.getParcelable<Uri>(STATE_CONTACT_URI)
            mPhotoUri = savedInstanceState.getParcelable<Uri>(STATE_PHOTO_URI)
            mThumbnail = savedInstanceState.getParcelable<Bitmap>(STATE_THUMBNAIL)
            setThumbnailBitmap(mThumbnail)

            if (mContactUri != null) {
                initContactLoader(mContactUri!!)
            }
        }
    }

    private fun listContacts() {
        val pickContactIntent = Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI)
        pickContactIntent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_READ_CONTACTS_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listContacts()
            } else {
                finish()
            }
            Constants.REQUEST_EXTERNAL_STORAGE_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPickImageIntent()
            }
        }
    }

    override fun onDestroy() {
        mDisplayNameEditText!!.isCursorVisible = false
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_config, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ok -> {
                accept()
                finish()
                return true
            }
            R.id.discard -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICK_CONTACT_REQUEST -> if (resultCode == Activity.RESULT_OK) {
                mContactUri = data?.data
                if (mContactUri != null) {
                    initContactLoader(mContactUri!!)
                }
            } else {
                finish()
            }
            TAKE_PHOTO_REQUEST -> if (resultCode == Activity.RESULT_OK) {
                if (mPhotoUri != null) {
                    mThumbnailView!!.post {
                        mWorkerFragment!!.loadImage(mPhotoUri!!, mThumbnailView!!.width,
                                mThumbnailView!!.height)
                    }
                }
            }
            PICK_IMAGE_REQUEST -> if (resultCode == Activity.RESULT_OK) {
                if (mPhotoUri != null) {
                    mPhotoUri = data?.data
                    mThumbnailView!!.post {
                        mWorkerFragment!!.loadImage(mPhotoUri!!, mThumbnailView!!.width,
                                mThumbnailView!!.height)
                    }
                }
            }
        }
    }

    private fun initContactLoader(contactUri: Uri) {
        val loaderArgs = Bundle()
        loaderArgs.putParcelable(ARG_CONTACT_URI, contactUri)
        loaderManager.initLoader(CONTACT_LOADER, loaderArgs, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor>? {
        when (id) {
            CONTACT_LOADER -> {
                val contactUri = args.getParcelable<Uri>(ARG_CONTACT_URI)

                val contactProjection = arrayOf(Contacts.DISPLAY_NAME, Contacts.PHOTO_URI,
                        Contacts.LOOKUP_KEY)
                return CursorLoader(this, contactUri, contactProjection, null, null, null)
            }
            PHONE_NUMBER_LOADER -> {
                val lookupKey = args.getString(ARG_CONTACT_LOOKUP)

                val phoneProjection = arrayOf(Phone._ID, Phone.TYPE, Phone.NUMBER)
                val phoneSelection = ContactsContract.Data.LOOKUP_KEY + " = ? AND " +
                        ContactsContract.Data.MIMETYPE + " = ?"
                val phoneSelectionArgs = arrayOf(lookupKey, Phone.CONTENT_ITEM_TYPE)
                return CursorLoader(this, ContactsContract.Data.CONTENT_URI,
                        phoneProjection, phoneSelection, phoneSelectionArgs,
                        Phone.TYPE)
            }
            else -> return null
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        when (loader.id) {
            CONTACT_LOADER -> if (data != null && data.moveToFirst()) {
                val displayName = data.getString(data
                        .getColumnIndex(Contacts.DISPLAY_NAME))
                mDisplayNameEditText!!.text = displayName

                if (mPhotoUri == null) {
                    val photoUriString = data.getString(data
                            .getColumnIndex(Contacts.PHOTO_URI))
                    if (photoUriString != null) {
                        mPhotoUri = Uri.parse(photoUriString)
                        if (mPhotoUri != null) {
                            mThumbnailView!!.post {
                                mWorkerFragment!!.loadImage(mPhotoUri!!,
                                        mThumbnailView!!.width,
                                        mThumbnailView!!.height)
                            }
                        }
                    }
                }

                // Request contact's phone numbers
                val lookup = data.getString(data
                        .getColumnIndex(Contacts.LOOKUP_KEY))
                val phoneLoaderArgs = Bundle()
                phoneLoaderArgs.putString(ARG_CONTACT_LOOKUP, lookup)
                loaderManager.initLoader(PHONE_NUMBER_LOADER,
                        phoneLoaderArgs, this)
            }
            PHONE_NUMBER_LOADER -> {
                mPhoneNumberAdapter!!.setData(data)
                mPhoneNumberAdapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            CONTACT_LOADER -> {
                mDisplayNameEditText!!.text = null
                mPhoneNumberAdapter!!.setData(null)
                mPhoneNumberAdapter!!.notifyDataSetChanged()
                setThumbnailBitmap(null)
            }
            PHONE_NUMBER_LOADER -> {
                mPhoneNumberAdapter!!.setData(null)
                mPhoneNumberAdapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.thumbnail -> if (packageManager.hasSystemFeature(
                    PackageManager.FEATURE_CAMERA)) {
                val changePhotoPopupMenu = PopupMenu(
                        this@WidgetConfigActivity, mThumbnailView)
                changePhotoPopupMenu.menuInflater.inflate(
                        R.menu.change_picture, changePhotoPopupMenu.menu)
                changePhotoPopupMenu
                        .setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.fromCamera -> {
                                    startCameraIntent()
                                    true
                                }
                                R.id.fromGallery -> {
                                    startPickImageIntent()
                                    true
                                }
                                else -> false
                            }
                        }
                changePhotoPopupMenu.show()
            } else {
                startPickImageIntent()
            }
        }
    }

    private fun startCameraIntent() {
        val cameraOutput = createImageFile()
        if (cameraOutput != null) {
            mPhotoUri = Uri.fromFile(cameraOutput)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraOutput))
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        } else {
            Toast.makeText(this, R.string.error_using_camera, Toast.LENGTH_LONG).show()
        }
    }

    private fun startPickImageIntent() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ReadExternalStoragePermissionExplanation().show(supportFragmentManager,
                    "readExternalStorageExplanation")
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.REQUEST_CALL_PERMISSION)
        }
    }

    /**
     * Creates a file that will hold a picture taken from the device's camera.

     * @return the file that has been created
     */
    private fun createImageFile(): File? {
        val filename = "JPEG_" + System.currentTimeMillis()
        val fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        try {
            return File.createTempFile(filename, ".jpg", fileDir)
        } catch (e: IOException) {
            return null
        }

    }

    internal fun setThumbnail(uri: String, thumbnail: Bitmap) {
        mPhotoUri = Uri.parse(uri)
        mThumbnail = thumbnail
        setThumbnailBitmap(thumbnail)
    }

    private fun setThumbnailBitmap(thumbnail: Bitmap?) {
        mThumbnailView!!.setImageBitmap(thumbnail)
        mDefaultPictureView!!.visibility = if (thumbnail == null) View.VISIBLE else View.GONE
    }

    override fun onBackPressed() {
        accept()
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(STATE_CONTACT_URI, mContactUri)
        outState.putParcelable(STATE_PHOTO_URI, mPhotoUri)
        outState.putParcelable(STATE_THUMBNAIL, mThumbnail)
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("CommitPrefEdits")
    private fun accept() {
        val displayName = mDisplayNameEditText!!.text.toString()
        val selectedNumber = mPhoneNumberSpinner!!.selectedItem as PhoneNumber

        // Save the data
        val pref = getSharedPreferences(
                Constants.SHAREDPREF_WIDGET, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(Constants.SHAREDPREF_WIDGET_DISPLAY_NAME + mAppWidgetId, displayName)
        editor.putString(Constants.SHAREDPREF_WIDGET_PHONE + mAppWidgetId,
                selectedNumber.number)
        editor.putInt(Constants.SHAREDPREF_WIDGET_PHONE_TYPE + mAppWidgetId,
                selectedNumber.type)
        if (mPhotoUri != null) {
            editor.putString(Constants.SHAREDPREF_WIDGET_PHOTO_URL + mAppWidgetId,
                    mPhotoUri!!.toString())
        }
        editor.commit()

        // Update the widget
        val widgetMngr = AppWidgetManager.getInstance(this)
        DirectCallWidgetProvider.updateWidget(applicationContext,
                widgetMngr, mAppWidgetId)

        val acceptIntent = Intent()
        acceptIntent
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, acceptIntent)
    }

    /**
     * An `adapter` for the phone number spinner
     */
    private inner class PhoneAdapter internal constructor(context: Context,
                                                          phoneCursor: Cursor?) : BaseAdapter() {

        private val mLayoutInflater: LayoutInflater
        private var mPhoneNumbers: MutableList<PhoneNumber>? = null

        init {
            mLayoutInflater = LayoutInflater.from(context)
            setData(phoneCursor)
        }

        internal fun setData(cursor: Cursor?) {
            if (mPhoneNumbers == null) {
                mPhoneNumbers = ArrayList<PhoneNumber>()
            } else {
                mPhoneNumbers!!.clear()
            }
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val type = cursor.getInt(cursor.getColumnIndex(Phone.TYPE))
                    val number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER))
                    val item = PhoneNumber(type, number)
                    if (!mPhoneNumbers!!.contains(item)) {
                        mPhoneNumbers!!.add(item)
                    }
                } while (cursor.moveToNext())
            }
        }

        override fun getCount(): Int {
            return if (mPhoneNumbers != null) mPhoneNumbers!!.size else 0
        }

        override fun getItem(i: Int): Any? {
            return if (mPhoneNumbers != null) mPhoneNumbers!![i] else null
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            var view = view
            val viewHolder: ViewHolder
            if (view == null) {
                view = mLayoutInflater.inflate(R.layout.phone_spinner_item, viewGroup, false)
                viewHolder = ViewHolder()
                viewHolder.iconView = view!!.findViewById(R.id.icon) as ImageView
                viewHolder.typeView = view.findViewById(R.id.phoneType) as TextView
                viewHolder.numberView = view.findViewById(R.id.phoneNumber) as TextView
                view.tag = viewHolder
            } else {
                viewHolder = view.tag as ViewHolder
            }

            // Get the item
            val number = getItem(i) as PhoneNumber?

            // Set the view
            viewHolder.iconView!!.setImageResource(iconFor(number))
            viewHolder.typeView!!.text = typeNameFor(viewGroup.context, number!!)
            viewHolder.numberView!!.text = number!!.number

            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.phone_spinner_dropdown_item, parent,
                        false)
            }
            val number = getItem(position) as PhoneNumber?
            (convertView!!.findViewById(R.id.icon) as ImageView).setImageResource(iconFor(number))
            (convertView.findViewById(R.id.phoneType) as TextView).text = typeNameFor(
                    parent.context, number!!)
            (convertView.findViewById(R.id.phoneNumber) as TextView).text = number!!.number
            return convertView
        }

        private fun iconFor(number: PhoneNumber?): Int {
            when (number?.type) {
                Phone.TYPE_MOBILE, Phone.TYPE_WORK_MOBILE -> return R.drawable.ic_hardware_smartphone_24dp
                else -> return R.drawable.ic_call_grey600_24dp
            }
        }

        private fun typeNameFor(context: Context, number: PhoneNumber): CharSequence {
            return Phone.getTypeLabel(context.resources, number.type, "")
        }

        private inner class ViewHolder {

            internal var iconView: ImageView? = null
            internal var typeView: TextView? = null
            internal var numberView: TextView? = null
        }
    }

    private inner class PhoneNumber internal constructor(internal val type: Int,
                                                         internal val number: String?) {

        override fun equals(obj: Any?): Boolean {
            if (obj is PhoneNumber) {
                if (number != null && number == obj.number) {
                    return true
                }
            }
            return super.equals(obj)
        }
    }

    class WorkerFragment : Fragment() {
        private var mLoadImageTask: LoadImageTask? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        fun loadImage(uri: Uri, reqWidth: Int, reqHeight: Int) {
            mLoadImageTask?.cancel(true)
            mLoadImageTask = LoadImageTask(activity.applicationContext, reqWidth, reqHeight, true)
            mLoadImageTask!!.setOnImageLoadedListener { uri, bitmap ->
                (activity as WidgetConfigActivity).setThumbnail(uri, bitmap)
            }
            mLoadImageTask!!.execute(uri)
        }

        companion object {
            private val TAG = "Worker"

            fun findOrCreate(fragMngr: FragmentManager): WorkerFragment {
                var frag: WorkerFragment? = fragMngr.findFragmentByTag(TAG) as WorkerFragment?
                if (frag == null) {
                    frag = WorkerFragment()
                    fragMngr.beginTransaction().add(frag, TAG).commit()
                }
                return frag
            }
        }
    }

    /**
     * A dialog informing the user why they need to grant the READ_CONTACT permission.
     */
    class ReadContactsPermissionExplanation : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            @SuppressLint("InflateParams")
            val view = LayoutInflater.from(activity)
                    .inflate(R.layout.dialog_text, null) as TextView
            view.text = activity
                    .getString(R.string.request_readContacts_permission_explanation)
            return AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AppTheme))
                    .setView(view)
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        ActivityCompat.requestPermissions(activity,
                                arrayOf(Manifest.permission.READ_CONTACTS),
                                Constants.REQUEST_READ_CONTACTS_PERMISSION)
                    }.create()
        }
    }

    /**
     * A dialog informing the user why they need to grant the READ_EXTERNAL_STORAGE permission.
     */
    class ReadExternalStoragePermissionExplanation : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            @SuppressLint("InflateParams")
            val view = LayoutInflater.from(activity)
                    .inflate(R.layout.dialog_text, null) as TextView
            view.text = activity
                    .getString(R.string.request_external_storage_explanation)
            return AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AppTheme))
                    .setView(view)
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        ActivityCompat.requestPermissions(activity,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                Constants.REQUEST_EXTERNAL_STORAGE_PERMISSION)
                    }.create()
        }
    }

    companion object {

        // intent request codes
        private val PICK_CONTACT_REQUEST = 1
        private val TAKE_PHOTO_REQUEST = 2
        private val PICK_IMAGE_REQUEST = 3

        // cursor loader ids
        private val CONTACT_LOADER = 1
        private val PHONE_NUMBER_LOADER = 2

        // loader callbacks' arguments
        private val ARG_CONTACT_URI = "contactUri"
        private val ARG_CONTACT_LOOKUP = "lookup"

        // activity state
        private val STATE_CONTACT_URI = "contactUri"
        private val STATE_PHOTO_URI = "photoUri"
        private val STATE_THUMBNAIL = "thumbnail"
    }
}
