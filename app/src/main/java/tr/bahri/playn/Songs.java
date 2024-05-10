package tr.bahri.playn;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.media3.session.MediaController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Songs extends Fragment {
    RecyclerAdapter rec;
    RecyclerView recyclerView;
    public static PlaylistFolder currentPlaylist;
    public static PlaylistFolder rootFolder;
    View songFragView;
    private PlaylistFolder rootny;
    public Context vontext;


    public Songs(Context p) {
        vontext = p;
    }

    public static void takeMYFolder(PlaylistFolder plag)
    {
        currentPlaylist = plag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        songFragView = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = songFragView.findViewById(R.id.recyclie);
        updatePlaylist();
        return songFragView;
    }

    public PlaylistFolder getCurrentPlaylist()
    {
        return currentPlaylist;
    }
    public PlaylistFolder getRootFolder()
    {
        return rootFolder;
    }
    public void setRootFolder(PlaylistFolder p) {
        rootFolder = p;
        rootny = p;
    }

    public void passPlayer(Player_Frag fg)
    {
        if (rec == null)
        {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

            rec = new RecyclerAdapter(null, vontext);


        }
        rec.passPlayerFrag(fg);
    }

    public List<PlaylistFolder> getAllPlaylist()
    {
        return  null;
    }

    public void setAllBySaved(List<AudioFile> frargy)
    {

    }

    public void passController(ListenableFuture<MediaController> pCon)
    {
        rec.passController(pCon);
    }
    public void addItem(AudioFile pAud)
    {
        if (rec != null && pAud != null)
        {
            rec.addItem(pAud);
        }
    }

    public void setCurrentPlaylist(PlaylistFolder p)
    {
        currentPlaylist = p;
        if (rec != null)
        {
            rec.passCurplasylist(currentPlaylist);
        }
    }

    public void updatePlaylist(@NonNull PlaylistFolder p)
    {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView = songFragView.findViewById(R.id.recyclie);
            recyclerView.setLayoutManager(layoutManager);

            List<AudioFile> arc = p.getFolderFiles();
            //rec = new RecyclerAdapter(arc);
        if (rec == null)
        {
            List<AudioFile> barc = arc;
            rec = new RecyclerAdapter(barc, vontext);
        }
        else
        {
            List<AudioFile> barc = arc;
            rec.setAllMkay(barc);
        }

        recyclerView.setAdapter(rec);
        if (rec.isCurrEmp())
        {
            PlaylistFolder neg = rootny;
            rec.passCurplasylist(neg);
        }
    }

    public void updatePlaylist()
    {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView = songFragView.findViewById(R.id.recyclie);
            recyclerView.setLayoutManager(layoutManager);

            List<AudioFile> arc = currentPlaylist.getFolderFiles();
            //rec = new RecyclerAdapter(arc);
        if (rec == null)
        {
            List<AudioFile> barc = arc;
            rec = new RecyclerAdapter(barc, vontext);
        }
        else
        {
            List<AudioFile> barc = arc;
            rec.setAllMkay(barc);
        }
        recyclerView.setAdapter(rec);
        if (rec.isCurrEmp())
        {
            PlaylistFolder neg = rootny;
            rec.passCurplasylist(neg);
        }
    }
}