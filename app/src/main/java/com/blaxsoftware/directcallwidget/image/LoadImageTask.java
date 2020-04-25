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

package com.blaxsoftware.directcallwidget.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.blaxsoftware.directcallwidget.Constants;
import com.blaxsoftware.directcallwidget.util.ImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LoadImageTask extends AsyncTask<Uri, Void, Bitmap> {

    private Context mContext;
    private int mReqWidth;
    private int mReqHeight;
    private OnImageLoadedListener mOnImageLoadedListener;
    private boolean mCopyBitmapToInternalDir;
    private String mOutUri;

    public LoadImageTask(Context context, int reqWidth, int reqHeight, boolean copyBitmapToInternalDir) {
        mContext = context;
        mReqWidth = reqWidth;
        mReqHeight = reqHeight;
        mCopyBitmapToInternalDir = copyBitmapToInternalDir;
    }

    @Override
    protected Bitmap doInBackground(Uri... params) {
        Uri imageUri = params[0];
        mOutUri = imageUri.toString();

        Bitmap bitmap = null;
        boolean isFileUri = ImageUtils.isFileUri(mContext, imageUri);
        String source = isFileUri ? ImageUtils
                .imageFilePath(mContext, imageUri) : imageUri.toString();
        try {
            bitmap = ImageUtils.loadSampledBitmap(mContext, source, !isFileUri,
                    mReqWidth, mReqHeight);
        } catch (FileNotFoundException e) {
        }
        if (mCopyBitmapToInternalDir && bitmap != null) {
            try {
                mOutUri = copyTo(mContext, bitmap, Constants.PICTURES_DIRECTORY);
            } catch (IOException e) {}
        }
        return bitmap;
    }

    /**
     * Copies the given bitmap to an internal directory and returns its URI.
     * @param context
     * @param bitmap
     * @param internalDirName
     * @return
     * @throws IOException
     */
    private static String copyTo(Context context, Bitmap bitmap, String internalDirName) throws IOException {
        OutputStream os = null;
        try {
            File dir = new File(context.getFilesDir(), internalDirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File outFile = new File(dir, String.valueOf(System.currentTimeMillis()));
            os = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            return outFile.toURI().toString();
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (mOnImageLoadedListener != null) {
            mOnImageLoadedListener.onImageLoaded(mOutUri, result);
        }
    }

    public void setOnImageLoadedListener(
            OnImageLoadedListener onImageLoadedListener) {
        mOnImageLoadedListener = onImageLoadedListener;
    }

    public interface OnImageLoadedListener {

        void onImageLoaded(String uri, Bitmap bitmap);
    }
}
