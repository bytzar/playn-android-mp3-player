package tr.bahri.playn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.res.ResourcesCompat;
import androidx.media3.session.MediaController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Player_Frag extends BottomSheetDialogFragment {


    ImageView playpause;
    int switchNumber = 1;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;
    public boolean sync = true;
    private ListenableFuture<MediaController> controllerFuture;
    private static boolean firstTime = false;
    private SeekBar seekBar;
    public static View v;
    public static AudioFile curAud;
    public static CustomEditableTextView yknow;
    public static ImageView img;
    private Home mainActivity;
    private static Context idkman;

    public Player_Frag()
    {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        idkman = getContext();
        seekBar = requireActivity().findViewById(R.id.seekBar2);
    }
    public static Player_Frag newInstance()
    {
        return new Player_Frag();
    }

    public SeekBar getSeekBar()
    {
        if (seekBar == null)
        {
            seekBar = requireActivity().findViewById(R.id.seekBar2);
        }
        return seekBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.fragment_player, container, false);
        mainActivity = (Home) getActivity();
        yknow = v.findViewById(R.id.customTextView);
        img = v.findViewById(R.id.imageView3);
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), " long", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                String[] mimeTypes = {"image/*"};
                i.setType("*/*");
                i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                someActivityResultLauncher.launch(i);
            }
        });
        yknow.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable s) {
                curAud.title = s.toString();
                miniMediaPlayer_Frag.updateText(s.toString());
                RecyclerView recv = requireActivity().findViewById(R.id.recyclie);
                Objects.requireNonNull(recv.getAdapter()).notifyDataSetChanged();
                mainActivity.saveStateNOW(null);
            }
        });
        return v;
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            ContentResolver resolver = requireContext().getContentResolver();
                            Bitmap fickmatte = BitmapConverter.uriToBitmap(imageUri, resolver);
                            curAud.coverArt = fickmatte;
                            img.setImageBitmap(fickmatte);
                        }
                    }
                    if (mainActivity != null)
                    {
                        mainActivity.saveStateNOW(null);
                    }
                }
            });

    public static void updateText(AudioFile p)
    {
        curAud = p;
        if (v != null)
        {
            yknow.setText(p.title);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void updateCover(AudioFile p) {
        curAud = p;
        if (v != null) {
            if (curAud.coverArt != null) {
                img.setImageBitmap(curAud.coverArt);
            } else {
                img.setImageDrawable(requireActivity().getDrawable(R.drawable.roundcuboid));
            }
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmap)
    {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, Math.min(originalWidth, originalHeight), Math.min(originalWidth, originalHeight), true);

        return scaledBitmap;
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
        playpause = requireActivity().findViewById(R.id.imageView1923);

        seekBar = requireActivity().findViewById(R.id.seekBar2);

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
            else if (controllerFuture.get().getCurrentMediaItem() != null )
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