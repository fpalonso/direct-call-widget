package com.blaxsoftware.directcallwidget.image;

import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.blaxsoftware.directcallwidget.util.ImageUtils;

public class LoadImageTask extends AsyncTask<Uri, Void, Bitmap> {

    private Context mContext;
    private int mReqWidth;
    private int mReqHeight;
    private OnImageLoadedListener mOnImageLoadedListener;

    public interface OnImageLoadedListener {

	void onImageLoaded(Bitmap bitmap);
    }

    public LoadImageTask(Context context, int reqWidth, int reqHeight) {
	mContext = context;
	mReqWidth = reqWidth;
	mReqHeight = reqHeight;
    }

    @Override
    protected Bitmap doInBackground(Uri... params) {
	Uri imageUri = params[0];

	Bitmap bitmap = null;
	boolean isFileUri = ImageUtils.isFileUri(mContext, imageUri);
	String source = isFileUri ? ImageUtils
		.imageFilePath(mContext, imageUri) : imageUri.toString();
	try {
	    bitmap = ImageUtils.loadSampledBitmap(mContext, source, !isFileUri,
		    mReqWidth, mReqHeight);
	} catch (FileNotFoundException e) {
	}
	return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
	if (mOnImageLoadedListener != null) {
	    mOnImageLoadedListener.onImageLoaded(result);
	}
    }

    public void setOnImageLoadedListener(
	    OnImageLoadedListener onImageLoadedListener) {
	mOnImageLoadedListener = onImageLoadedListener;
    }
}
