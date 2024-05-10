package tr.bahri.playn;

import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFolder {

    List<AudioFile> arc;
    String title;
    public PlaylistFolder(String p) {
        title = p;
        arc = new ArrayList<>();
    }

    public List<AudioFile> StartAtCue(AudioFile pQ) {
        List<AudioFile> rarck = arc;
        for (int i = 0; i < rarck.size(); i++)
        {
            if (rarck.get(i) == pQ)
            {
                if (i == 0)
                {
                    return rarck.subList(1, rarck.size());
                }
                List<AudioFile> res = new ArrayList<>();
                res.addAll(rarck.subList(i + 1, rarck.size()));
                res.addAll(rarck.subList(0, i));
                return res;
            }
        }
        return null;
    }

    public AudioFile AlreadyPresent(String filePath) //zwei funktionen gucken ob file generell schon einmal importiert wurde in dem Fall bereits vorhandenes in ordner tun aber wenn acuh schon im ornder nichts machen
    {
        AudioFile r = null;
        for (int i = 0; i < arc.size(); i++)
        {
            if (arc.get(i).getStringFilename().equals(filePath))
            {
                r = arc.get(i);
                break;
            }
        }
        return r;
    }

    public String getTitle()
    {
        return title;
    }

    public void addItem(AudioFile p)
    {
        arc.add(arc.size(), p);
    }



    public List<AudioFile> getFolderFiles()
    {
        return arc;
    }
}