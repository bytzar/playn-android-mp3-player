package tr.bahri.playn;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TapPagerAdapter extends FragmentStateAdapter
{

        private static final int NUM_PAGES = 2;
        private Songs songsFAG;
        private Playlists playFAG;
        public Context goenntext;
        public TapPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, Context p) {
            super(fragmentManager, lifecycle);
            goenntext = p;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return the fragment for the corresponding page
            switch (position) {
                case 1:
                    if (songsFAG == null)
                    {
                        songsFAG = new Songs(goenntext);
                    }
                    return songsFAG;
                case 0:
                    if (playFAG == null)
                    {
                        if (songsFAG == null)
                        {
                            songsFAG = new Songs(goenntext);
                        }
                        playFAG = new Playlists(songsFAG);
                    }
                    return playFAG;
                default:
                    if (songsFAG == null)
                    {
                        songsFAG = new Songs(goenntext);
                    }
                    return songsFAG;
            }
        }

        public Songs getSongsFAG()
        {
            if (songsFAG == null)
            {
                songsFAG = new Songs(goenntext);
            }
            return songsFAG;
        }

    public Playlists PlaylistsFag()
    {
        if (playFAG != null)
        {
            return playFAG;
        }
        else if (songsFAG == null)
        {
            songsFAG = new Songs(goenntext);
            playFAG = new Playlists(songsFAG);
            return playFAG;
        }
        else
        {
            playFAG = new Playlists(songsFAG);
            return playFAG;
        }
    }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
}