package tr.bahri.playn;

import android.os.Handler;
import android.widget.SeekBar;

import androidx.media3.session.MediaController;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class MediaSeekBarUpdater {
    private static final int UPDATE_INTERVAL = 100; // Update interval in milliseconds
    private Handler handler;
    private SeekBar seekBar;
    private SeekBar playerSeekBar;
    private ListenableFuture<MediaController> mediaController;

    public MediaSeekBarUpdater(SeekBar seekBar, SeekBar pPlayerSeekBar, ListenableFuture<MediaController> controllerFuture) {
        this.playerSeekBar = pPlayerSeekBar;
        this.playerSeekBar.animate();

        this.seekBar = seekBar;
        this.handler = new Handler();
        this.mediaController = controllerFuture;
        this.seekBar.animate();
    }
    public void start() {
        handler.postDelayed(updateSeekBarTask, UPDATE_INTERVAL);
    }

    // Method to stop updating the SeekBar position
    public void stop() {
        handler.removeCallbacks(updateSeekBarTask);
    }

    // Runnable task to update SeekBar position
    private final Runnable updateSeekBarTask = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaController != null && mediaController.get().isPlaying())
                {
                        int currentPosition = (int) mediaController.get().getCurrentPosition() * 1000;
                        seekBar.setProgress(currentPosition, true);
                        playerSeekBar.setProgress(currentPosition, true);

                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            handler.postDelayed(this, UPDATE_INTERVAL); // Schedule next update
        }
    };
}
