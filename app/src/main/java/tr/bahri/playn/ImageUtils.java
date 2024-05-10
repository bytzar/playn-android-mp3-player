package tr.bahri.playn;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.io.OutputStream;

public class ImageUtils {

    public static String saveBitmapToGallery(ContentResolver contentResolver, Bitmap bitmap, String title, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, title);
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, description);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // Change the MIME type if necessary

        // Get the URI of the location to save the image
        final Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        if (imageUri != null) {
            try {
                // Open an OutputStream to write the bitmap data to the content URI
                OutputStream outputStream = contentResolver.openOutputStream(imageUri);

                if (outputStream != null) {
                    // Compress and write the bitmap data to the OutputStream
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    return imageUri.toString(); // Return the URI of the saved image
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null; // Return null if there was an error saving the image
    }
}
