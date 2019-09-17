/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2019 Fer P. A.
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

package com.blaxsoftware.directcallwidget.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageUtils {
    private static MessageDigest md5;

    public static Bitmap loadSampledBitmap(Context context, String source,
                                           boolean fromContentProvider, int reqWidth, int reqHeight)
            throws FileNotFoundException {
        Uri imageUri = fromContentProvider ? Uri.parse(source) : null;

        // Calculate sample size
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        if (fromContentProvider) {
            decodeBitmapStream(context, imageUri, bmOptions);
        } else {
            BitmapFactory.decodeFile(source, bmOptions);
        }
        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, reqWidth,
                reqHeight);

        // Load sampled bitmap
        Bitmap sampledBitmap = null;
        bmOptions.inJustDecodeBounds = false;
        if (fromContentProvider) {
            sampledBitmap = decodeBitmapStream(context, imageUri, bmOptions);
        } else {
            sampledBitmap = BitmapFactory.decodeFile(source, bmOptions);
        }
        return sampledBitmap;
    }

    /**
     * Loads a {@code bitmap} from the given {@code URI}.
     *
     * @param context
     * @param imageContentUri
     * @param options
     * @return
     * @throws FileNotFoundException
     */
    private static Bitmap decodeBitmapStream(Context context,
                                             Uri imageContentUri, BitmapFactory.Options options)
            throws FileNotFoundException {
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(imageContentUri);
            return BitmapFactory.decodeStream(is, null, options);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Returns the real file path form the given {@code URI}, or {@code null}.
     *
     * @param context
     * @param imageUri
     * @return
     */
    public static String imageFilePath(Context context, Uri imageUri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            Cursor c = context.getContentResolver().query(imageUri, projection,
                    null, null, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    path = c.getString(c
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                }
                c.close();
            }
        } catch (Exception e) {
        }
        if (path == null) {
            path = imageUri.getPath();
        }
        return path;
    }

    /**
     * Returns whether the given {@code URI} points to a file.
     *
     * @param context
     * @param uri
     * @return
     */
    public static boolean isFileUri(Context context, Uri uri) {
        if (uri == null) {
            return false;
        }
        String path = imageFilePath(context, uri);
        return new File(path).exists();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

	    /*
         * Calculate the largest inSampleSize value that is a power of 2 and
	     * keeps both height and width larger than the requested height and
	     * width.
	     */
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
