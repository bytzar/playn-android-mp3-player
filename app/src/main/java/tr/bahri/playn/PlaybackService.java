package tr.bahri.playn;

import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.service.quicksettings.PendingIntentActivityWrapper;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import java.util.concurrent.ExecutionException;

public class PlaybackService extends MediaSessionService {
    private static MediaSession mediaSession = null;
    private miniMediaPlayer_Frag miniMediaPlayerFrag;
    private Player_Frag playerFrag;
    private int literallyanyothervariable = 0;
    public boolean dudarfstnicht = false;

    // Create your Player and MediaSession in the onCreate lifecycle event
    @OptIn(markerClass = UnstableApi.class) @Override
    public void onCreate() {
        super.onCreate();
        ExoPlayer player = new ExoPlayer.Builder(this).setWakeMode(C.WAKE_MODE_LOCAL).setHandleAudioBecomingNoisy(true).setAudioAttributes(AudioAttributes.DEFAULT, true).build();
        mediaSession = new MediaSession.Builder(this, player).build(); ///mal sehen...

        Intent i = new Intent(this, Home.class);
        int uniqueRequestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = new PendingIntentActivityWrapper(this, uniqueRequestCode, i, PendingIntent.FLAG_CANCEL_CURRENT, false).getPendingIntent();

        mediaSession.setSessionActivity(pendingIntent);

        mediaSession.getPlayer().addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                if (mediaItem == null) {
                    Toast.makeText(getApplicationContext(), "HALLO UNNULLED", Toast.LENGTH_LONG).show();
                } else {
                    mediaSession.getPlayer().play();
                    //updates here villeicht den listner eine ebene nach oben um texte zu updaten
                }
            }
        });


    }

    public void passMiniPlayerFragment(miniMediaPlayer_Frag pM)
    {
        literallyanyothervariable = 1;
        miniMediaPlayerFrag = pM;

    }

    public void ahfickmichdoch()
    {
        if (mediaSession != null && mediaSession.getPlayer() != null)
        {
            if (!dudarfstnicht)
            {
                literallyanyothervariable = literallyanyothervariable;
                if (miniMediaPlayerFrag != null) {
                    try {
                        miniMediaPlayerFrag.SynchnronizePlayButtonWithNotification(true);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (playerFrag != null) {
                    try {
                        playerFrag.SynchnronizePlayButtonWithNotification(true);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static MediaSession getMediaSession()
    {
        if (mediaSession != null)
        {
            return mediaSession;
        }
        return null;
    }

    public void passPlayerFragment(Player_Frag pM)
    {
        literallyanyothervariable = 1;
        playerFrag = pM;

    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }
    // The user dismissed the app from the recent tasks
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // Stop your media player here
        mediaSession.getPlayer().stop();
        mediaSession.getPlayer().release();
        stopSelf();
    }

    public boolean hasMediaItem() {return mediaSession.getPlayer().getCurrentMediaItem() != null;}
    public static boolean isAtPlay()
    {
        if (mediaSession != null)
        {
            return mediaSession.getPlayer().isPlaying();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        super.onDestroy();
    }
}