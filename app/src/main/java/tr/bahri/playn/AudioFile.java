package tr.bahri.playn;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

public class AudioFile {
    public Bitmap coverArt;
    private String uri;
    public String title;
    public String kuenstler;
    public AudioFile(String pUri, String pTitle, String pAudioArtist, Bitmap CoverA)
    {
        uri = pUri;
        title = pTitle;
        kuenstler = pAudioArtist;
        coverArt = CoverA;
    }
    public void setCoverArt(Bitmap coverArt) {
        this.coverArt = coverArt;
    }

    public Uri getFilePath() {
        Uri buri = Uri.parse(uri);
        return buri;
    }
/*
    public void setCoverArt(Context p)
    {
        if (CoverArt != null)
        {
            Uri imageUri = CoverArt;
            ContentResolver resolver = p.getContentResolver();
            coverArt = BitmapConverter.uriToBitmap(imageUri, resolver);
        }
    }*/

    public String getStringFilename()
    {
        return uri;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
