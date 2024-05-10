package tr.bahri.playn;


import android.content.Context;

import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;

public class ExoPlayerManager {
    private static ExoPlayer player;
    private static MediaSession mediaSession;
    public static ExoPlayer getPlayer(Context c) {
        if (player == null && c != null)
        {
            player = new ExoPlayer.Builder(c).build();
        }
        return player;
    }

    public static MediaSession getconnectMediaSession(Context c)
    {
        if (player != null && c != null)
        {
            mediaSession = new MediaSession.Builder(c, player).build();
            return mediaSession;
        }
        return null;
    }
}

