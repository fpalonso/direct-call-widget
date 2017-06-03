package com.blaxsoftware.directcallwidget.image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Debug
import com.blaxsoftware.directcallwidget.Constants
import com.blaxsoftware.directcallwidget.util.ImageUtils
import java.io.*

typealias OnImageLoadedListener = (String, Bitmap) -> Unit

class LoadImageTask(private val mContext: Context,
                    private val mReqWidth: Int,
                    private val mReqHeight: Int,
                    private val mCopyBitmapToInternalDir: Boolean) : AsyncTask<Uri, Void, Bitmap?>() {

    private var mOnImageLoadedListener: OnImageLoadedListener? = null
    private var mOutUri: String? = null

    override fun doInBackground(vararg uris: Uri): Bitmap? {
        if (uris.isEmpty()) {
            throw IllegalArgumentException("No image uris found")
        }
        val imageUri = uris[0]
        mOutUri = imageUri.toString()

        var bitmap: Bitmap? = null
        val isFileUri = ImageUtils.isFileUri(mContext, imageUri)
        val source = if (isFileUri) {
            ImageUtils.imageFilePath(mContext, imageUri)
        } else {
            imageUri.toString()
        }
        try {
            bitmap = ImageUtils.loadSampledBitmap(mContext, source, !isFileUri,
                    mReqWidth, mReqHeight)
        } catch (e: FileNotFoundException) {
        }

        if (mCopyBitmapToInternalDir && bitmap != null) {
            try {
                mOutUri = copyTo(mContext, bitmap, Constants.PICTURES_DIRECTORY)
            } catch (e: IOException) {
            }

        }
        return bitmap
    }

    override fun onPostExecute(result: Bitmap?) {
        if (mOutUri != null && result != null) {
            mOnImageLoadedListener?.invoke(mOutUri!!, result)
        }
    }

    /**
     * Copies the given bitmap to an internal directory and returns its URI.
     * @param context
     * *
     * @param bitmap
     * *
     * @param internalDirName
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun copyTo(context: Context, bitmap: Bitmap, internalDirName: String): String {
        var os: OutputStream? = null
        try {
            val dir = File(context.filesDir, internalDirName)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val outFile = File(dir, System.currentTimeMillis().toString())
            os = FileOutputStream(outFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            return outFile.toURI().toString()
        } finally {
            if (os != null) {
                os.close()
            }
        }
    }

    fun setOnImageLoadedListener(listener: OnImageLoadedListener) {
        mOnImageLoadedListener = listener
    }
}
