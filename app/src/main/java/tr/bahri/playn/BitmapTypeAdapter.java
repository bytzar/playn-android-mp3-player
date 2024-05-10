package tr.bahri.playn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapTypeAdapter extends TypeAdapter<Bitmap> {

    @Override
    public void write(JsonWriter out, Bitmap value) throws IOException {
        // Serialize Bitmap to base64 string and write it to JsonWriter
        if (value != null) {
            String base64String = bitmapToBase64(value);
            out.value(base64String);
        } else {
            out.nullValue();
        }
    }

    @Override
    public Bitmap read(JsonReader in) throws IOException {
        // Read base64 string from JsonReader and deserialize it to Bitmap
        String base64String = in.nextString();
        return base64ToBitmap(base64String);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            return null; // or handle the case appropriately
        }
    }


    private Bitmap base64ToBitmap(String base64String) {
        byte[] byteArray = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
