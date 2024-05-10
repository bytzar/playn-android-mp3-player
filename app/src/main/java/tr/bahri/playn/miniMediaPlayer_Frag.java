package tr.bahri.playn;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.session.MediaController;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;


public class miniMediaPlayer_Frag extends Fragment {

    ImageView playpause;
    int switchNumber = 1;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;
    public boolean sync = true;
    public static View view;
    private ListenableFuture<MediaController> controllerFuture;
    private static boolean firstTime = false;
    private SeekBar seekBar;

    public miniMediaPlayer_Frag()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seekBar = requireActivity().findViewById(R.id.imageview2);
    }

    public SeekBar getSeekBar()
    {
        if (seekBar == null)
        {
            seekBar = requireActivity().findViewById(R.id.imageview2);
        }
        return seekBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_mini_media_player, container, false);
        return view;
    }

    public static void updateText(String p)
    {
        if (view != null)
        {
            TextView t = view.findViewById(R.id.textView1453);
            t.setText(p);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void updateCover(AudioFile p) {
        if (view != null) {
            ImageView image = view.findViewById(R.id.imageView31453);
            if (p.coverArt != null) {
                image.setImageBitmap(p.coverArt);
            } else {
                image.setImageDrawable(new ColorDrawable(1));
            }
        }
    }

    public static void onRestart()
    {
        firstTime = true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        playpause = requireActivity().findViewById(R.id.imageView4);

        seekBar = requireActivity().findViewById(R.id.imageview2);

        try {
            SynchnronizePlayButtonWithNotification(firstTime);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        playpause.setOnClickListener(v -> {
            try {
                sync = false;
                animateToggleButton();
                TogglePlay();
                sync = true;
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void SynchnronizePlayButtonWithNotification(boolean pRunit) throws ExecutionException, InterruptedException {
        if (pRunit && sync)
        {
            boolean prox;
            if (controllerFuture != null)
            {
                if (controllerFuture.get() != null)
                {
                    prox = controllerFuture.get().isPlaying();
                    int i = 0;
                }
                else
                {
                    prox = false;
                }
            }
            if ((PlaybackService.isAtPlay() && switchNumber == 1) || (!PlaybackService.isAtPlay() && switchNumber == 0))
            {
                if (PlaybackService.isAtPlay())
                {
                    switchNumber = 1;
                }
                else
                {
                    switchNumber = 0;
                }
                animateToggleButton();
            }
        }
    }

    public void TogglePlay() //frag nicht
    {
        boolean pPlay = PlaybackService.isAtPlay();
        if (!pPlay) {
            try {
                controllerFuture.get().play();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                controllerFuture.get().pause();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ImageView getplaypause()
    {
        return playpause;
    }

    public void animateToggleButton() throws ExecutionException, InterruptedException {
        if ( controllerFuture == null)
        {
            controllerFuture = Home.getTheMediaController();
        }
        if (controllerFuture != null)
        {
            if (switchNumber == 0)
            {
                playpause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pausetoplay, null));
                Drawable drawable = playpause.getDrawable();

                if (drawable instanceof AnimatedVectorDrawableCompat)
                {
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                }
                else if (drawable instanceof AnimatedVectorDrawable)
                {
                    avd2 = (AnimatedVectorDrawable) drawable;
                    avd2.start();
                }
                switchNumber++;
            }
            else if (controllerFuture.get().getCurrentMediaItem() != null)
            {
                playpause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.playtopause, null));
                Drawable drawable = playpause.getDrawable();

                if (drawable instanceof AnimatedVectorDrawableCompat)
                {
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                }
                else if (drawable instanceof AnimatedVectorDrawable)
                {
                    avd2 = (AnimatedVectorDrawable) drawable;
                    avd2.start();
                }
                switchNumber--;/*
                try {
                    controllerFuture.get().play();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }*/
            }
        }
    }
}