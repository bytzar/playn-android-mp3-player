package tr.bahri.playn;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

public class BitmapConverter {

    public static Bitmap uriToBitmap(Uri uri, ContentResolver contentResolver) {
        try {
            // Open an input stream from the URI
            InputStream inputStream = contentResolver.openInputStream(uri);

            // Decode the input stream into a Bitmap
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

            // Close the input stream
            if (inputStream != null) {
                inputStream.close();
            }

            // Crop the bitmap to have an aspect ratio of 1:1
            Bitmap croppedBitmap = cropToSquare(originalBitmap);

            return croppedBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap cropToSquare(Bitmap bitmap) {
        // Determine the dimensions for cropping
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width == height)
        {
            return bitmap;
        }

        int dimension = Math.min(width, height);

        // Calculate the coordinates for cropping
        int cropX = (width - dimension) / 2;
        int cropY = (height - dimension) / 2;

        // Create the cropped bitmap
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, cropX, cropY, dimension, dimension);

        // Recycle the original bitmap to free up memory
        bitmap.recycle();

        return croppedBitmap;
    }
}
