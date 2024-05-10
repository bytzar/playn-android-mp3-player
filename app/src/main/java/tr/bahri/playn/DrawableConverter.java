package tr.bahri.playn;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

public class DrawableConverter {

    public static Drawable uriToDrawable(Uri uri, Context context) {
        try {
            // Open an input stream from the URI
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            // Decode the input stream into a Bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            // Determine the desired width and height (use the smaller dimension)
            int desiredWidth, desiredHeight;
            if (originalWidth < originalHeight) {
                desiredWidth = originalWidth;
                desiredHeight = originalWidth;
            } else {
                desiredWidth = originalHeight;
                desiredHeight = originalHeight;
            }

            // Open a new input stream from the URI
            inputStream = context.getContentResolver().openInputStream(uri);

            // Decode the input stream into a Bitmap with the calculated scale factor
            options = new BitmapFactory.Options();
            options.inSampleSize = calculateInSampleSize(options, desiredWidth, desiredHeight);
            Bitmap scaledBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Crop the scaled bitmap to the desired dimensions
            int startX = (scaledBitmap.getWidth() - desiredWidth) / 2;
            int startY = (scaledBitmap.getHeight() - desiredHeight) / 2;
            Bitmap croppedBitmap = Bitmap.createBitmap(scaledBitmap, startX, startY, desiredWidth, desiredHeight);

            // Convert the cropped Bitmap to a Drawable
            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), croppedBitmap);

            return drawable;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
