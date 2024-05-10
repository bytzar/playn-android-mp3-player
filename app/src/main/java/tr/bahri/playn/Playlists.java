package tr.bahri.playn;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.media3.session.MediaController;
import androidx.media3.session.MediaSession;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Playlists extends Fragment {

    private MediaSession med;
    RecyclerAdapterPlaylist rec;
    Songs sngs;
    List<PlaylistFolder> arc;
    RecyclerView recyclerView;

    public Playlists(Songs p) {
        sngs = p;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        med = PlaybackService.getMediaSession();
        if (arc==null)
        {
            arc = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);
        recyclerView = view.findViewById(R.id.recycliePlaylost);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rec);
        return view;
    }

    public void setRootNer(PlaylistFolder plague, Context xoxk)
    {
        if (arc == null)
        {
            arc = new ArrayList<>();
        }
        arc.add(plague);
        Songs.currentPlaylist = arc.get(0);
        if (rec == null)
        {
            rec = new RecyclerAdapterPlaylist(arc, sngs, xoxk);
        }
    }

    public void setAllDAte(List<PlaylistFolder> frarg, Context xoxk)
    {
        if (rec == null)
        {
            rec = new RecyclerAdapterPlaylist(frarg, sngs, xoxk);
        }
        rec.setDate(frarg);
    }

    public void passController(ListenableFuture<MediaController> pCon)
    {
        rec.passController(pCon);
    }
    public void addItem(PlaylistFolder pAud)
    {
        if (rec != null && pAud != null)
        {
            rec.addItem(pAud);
        }
    }



    public void edititem(String newName)
    {
        rec.editLatestItem(newName);
    }
    public void updateSongsFrag()
    {
        rec.updateSongsFrag();
    }

}