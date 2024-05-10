package tr.bahri.playn;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.media.MediaMetadataRetriever;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;


import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;


import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class Home extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener { //auf selbe art und weise playlisten. eine liste an liste. also eine list voller playlisten
    private View fraggy;
    private AtomicReference<Float> lastY;
    ViewPager2 viewPager;
    private AtomicReference<Float> firstY;
    private AtomicReference<Float> lastY2;
    private AtomicReference<Float> firstY2;
    private AtomicReference<Float> lastX;
    private AtomicReference<Float> firstX;
    private boolean isTap = true;
    private boolean isTap2 = true;
    private View miniPlayerBar; //im moment überlappt das und sieht hässlich aus aber am ende wird das beides full weiß sein und unsichtbar also perfekt alles gut
    private Uri test = Uri.parse("");
    private MediaItem mediaItem;
    static ListenableFuture<MediaController> controllerFuture;
    private PlaybackService playbackService;
    private View whiteWiew;
    private SeekBar seekBar;
    private SeekBar playerSeekBar;
    private MediaSeekBarUpdater mediaSeekBarUpdater;
    private miniMediaPlayer_Frag mediaPlayerFrag;
    private Player_Frag playerfrag;
    private boolean playerIsUp = false;
    private CardView addicon;
    private TapPagerAdapter taper;
    private List<PlaylistFolder> allofTheFolds;

    public Home() {
    }//TODO ACHTUNG. beim file select wenn man zu schnell fle selected dann wird gelaub ich nicht onresater oder destroyed. dann klappt sync vom play ding unten nicht, da der nutzer aber file nur zum importierren und nicht zum sofort playn benutzten wird sollte das kein problem sein aber nur dass du bescheid weißt
    //TODO für carplay contentlibrary provider maessig ist erklart auf androidwebseite in diesem video
    //TODO klasse song. weil der soll zb. song hat, uri, und hat preview image zb. titel auch. der soll titel ändern und image ämdern können und auch erstes resukltat runterladen und setten können. also erstes google bild vom kjünstler. weil bei lied könnte yoututbe thumnbail kommmen zb. am besten da ist zb monstercat logo auf 1:1 gecroppt. immer gucken dass die uri aus der liste existiert und fragen yo repopulate neue uri alter titel und so sonst delete entry

    @OptIn(markerClass = UnstableApi.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        miniPlayerBar = findViewById(R.id.button3);
        whiteWiew = findViewById(R.id.whiteView);
        fraggy = findViewById(R.id.fragmentContainerView);
        addicon = findViewById(R.id.cardView444);


        ViewTreeObserver viewTreeObserver = fraggy.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // This code will run when the view is measured and laid out
                fraggy.setY(fraggy.getHeight());
                fraggy.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });




        playbackService = new PlaybackService();

        ViewTreeObserver viewTreeObserver2 = miniPlayerBar.getViewTreeObserver();
        viewTreeObserver2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // This code will run when the view is measured and laid out
                miniPlayerBar.setTranslationY((float) (miniPlayerBar.getHeight()));
                addicon.setTranslationY((float) (miniPlayerBar.getHeight()));
                miniPlayerBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });



        getSupportFragmentManager();
        mediaPlayerFrag = ((miniMediaPlayer_Frag) getSupportFragmentManager().findFragmentById(R.id.button3));
        playbackService.passMiniPlayerFragment(mediaPlayerFrag);

        getSupportFragmentManager();
        playerfrag = ((Player_Frag) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView));
        playbackService.passPlayerFragment(playerfrag);




        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, PlaybackService.class));
        controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        // MediaController is available here with controllerFuture.get()
        controllerFuture.addListener(this::RollupMenuonRestartIfPlaying, MoreExecutors.directExecutor());
        PullMenuPrep();


        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if(playerIsUp)
                {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(fraggy, "y", fraggy.getHeight());
                    animator.setDuration(400);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.start();

                    animator = ObjectAnimator.ofFloat(miniPlayerBar, "y", fraggy.getHeight() - miniPlayerBar.getHeight());
                    animator.setDuration(400);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.start();

                    animator = ObjectAnimator.ofFloat(whiteWiew, "y", fraggy.getHeight() - miniPlayerBar.getHeight());
                    animator.setDuration(400);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.start();
                    playerIsUp = false;
                }
                else
                {
                    finish();
                }
            }
        });

        CosWaveView cosWaveView = findViewById(R.id.sinewave);
        ObjectAnimator animator = ObjectAnimator.ofFloat(cosWaveView, "phase", 0, 200*2 * (float) Math.PI);
        animator.setDuration(4000); // Duration in milliseconds
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();



        com.google.android.material.tabs.TabLayout mytaps = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);

        if (taper == null)
        {
            taper = new TapPagerAdapter(getSupportFragmentManager(), getLifecycle(), getApplicationContext());
        }
        viewPager.setAdapter(taper);
        viewPager.setUserInputEnabled(true);

        new TabLayoutMediator(mytaps, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Folders");
                    break;
                case 1:
                    tab.setText("Contents");
                    break;
                // Add more cases for additional tabs as needed
                default:
                    tab.setText("error " + (position + 1));
                    break;
            }
        }).attach();

        viewPager.setCurrentItem(1);
        viewPager.setCurrentItem(0);

        allofTheFolds = new ArrayList<>();
        loadSaveState(null);

        if (allofTheFolds == null)
        {
            allofTheFolds = new ArrayList<>();
            PlaylistFolder pflag = new PlaylistFolder("Root");
            //Songs.rootFolder = pflag;
            taper.PlaylistsFag().setRootNer(pflag, this);
            taper.getSongsFAG().setRootFolder(pflag);
            allofTheFolds.add(pflag);
            saveStateNOW(null);
        }
        else
        {
            taper.getSongsFAG().setRootFolder(allofTheFolds.get(0));
            List<PlaylistFolder> ausirgendeinemingrundistderandereeinpointerhiersagtderacnichtsoinlinevariableundschei = new ArrayList<>(allofTheFolds);
            taper.PlaylistsFag().setAllDAte(ausirgendeinemingrundistderandereeinpointerhiersagtderacnichtsoinlinevariableundschei, this);
            Songs.takeMYFolder(ausirgendeinemingrundistderandereeinpointerhiersagtderacnichtsoinlinevariableundschei.get(0));
        }
        taper.getSongsFAG().passPlayer(playerfrag);
//COSINEEWAVE ALS GIF WGEN PERFORMANCE TODO DAS VON HIERVOR, unterSTES LIED IST NICHT ERREICHBAR WEGEN MINIÜPLAYER ERLEDOGT
    }

    public void test(View v)
    {
        Toast.makeText(this, ((TextView)(v.findViewById(R.id.textView4))).getText(), Toast.LENGTH_LONG).show();
    }


    public void AnimateUp()
    {
        ObjectAnimator.ofFloat(fraggy, "y", 0).setDuration(300).start();
        ObjectAnimator.ofFloat(miniPlayerBar, "y", -miniPlayerBar.getHeight()).setDuration(300).start();
        ObjectAnimator.ofFloat(whiteWiew, "y", -miniPlayerBar.getHeight()).setDuration(300).start();
        playerIsUp = true;
    }

    public void AnimateDown()
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(fraggy, "y", fraggy.getHeight());
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();

        animator = ObjectAnimator.ofFloat(whiteWiew, "y", fraggy.getHeight() - miniPlayerBar.getHeight());
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();

        animator = ObjectAnimator.ofFloat(miniPlayerBar, "y", fraggy.getHeight() - miniPlayerBar.getHeight());
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        playerIsUp = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        viewPager.setCurrentItem(0);
        viewPager.setCurrentItem(1);
        RollupMenuonRestartIfPlaying();
        miniMediaPlayer_Frag.onRestart();
        Player_Frag.onRestart();
        getSupportFragmentManager();
        playbackService.passMiniPlayerFragment((miniMediaPlayer_Frag) getSupportFragmentManager().findFragmentById(R.id.button3));
        playbackService.passPlayerFragment((Player_Frag) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView));

        seekBar = mediaPlayerFrag.getSeekBar();
        playerSeekBar = playerfrag.getSeekBar();
        long[] progress = new long[1];
        if (!playerIsUp)
        {
            if (playbackService.hasMediaItem())
            {
                miniPlayerBar.setTranslationY(0);
                addicon.setTranslationY(0);
                whiteWiew.setVisibility(View.VISIBLE);
            }
            else
            {
                miniPlayerBar.setTranslationY(miniPlayerBar.getHeight());
                addicon.setTranslationY(miniPlayerBar.getHeight());
                whiteWiew.setVisibility(View.INVISIBLE);
            }
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int pProgress, boolean fromUser) {
                // Seek to the specified position when the user changes the seek bar progress
                if (fromUser) {
                    progress[0] = pProgress;
                    // Seek to the specified position
                    // mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                    playbackService.dudarfstnicht = true;
                    //controllerFuture.get().play(); einfach nicht afnassen was solll schon passieren
                    mediaPlayerFrag.sync = false;
                    playerfrag.sync = false;
                    mediaSeekBarUpdater.stop();

            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    controllerFuture.get().seekTo(progress[0] / 1000);
                    long sclong = controllerFuture.get().getDuration();
                    mediaSeekBarUpdater.start(); //stop für heute, thx for the day!!!!!!!!!
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                playbackService.dudarfstnicht = false;
                mediaPlayerFrag.sync = true;
                playerfrag.sync = true;
                try {
                    controllerFuture.get().play();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Initialize ViewModel and observe playback position
        MediaViewModel mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        mediaViewModel.getPlaybackPosition().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long position) {
                // Update the SeekBar progress with the playback position
                seekBar.setProgress((int) position.longValue() * 1000);
                playerSeekBar.setProgress((int) position.longValue() * 1000);
            }
        });

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int pProgress, boolean fromUser) {
                // Seek to the specified position when the user changes the seek bar progress
                if (fromUser) {
                    progress[0] = pProgress;
                    // Seek to the specified position
                    // mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                playbackService.dudarfstnicht = true;
                //controllerFuture.get().play(); einfach nicht afnassen was solll schon passieren
                mediaPlayerFrag.sync = false;
                playerfrag.sync = false;
                mediaSeekBarUpdater.stop();

            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    controllerFuture.get().seekTo(progress[0] / 1000);
                    mediaSeekBarUpdater.start(); //stop für heute, thx for the day!!!!!!!!!
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                playbackService.dudarfstnicht = false;
                mediaPlayerFrag.sync = true;
                playerfrag.sync = true;
                try {
                    controllerFuture.get().play();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Initialize ViewModel and observe playback position
        MediaViewModel mediaViewModel2 = new ViewModelProvider(this).get(MediaViewModel.class);
        mediaViewModel2.getPlaybackPosition().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long position) {
                // Update the SeekBar progress with the playback position
                seekBar.setProgress((int) position.longValue() * 1000);
                playerSeekBar.setProgress((int) position.longValue() * 1000);
            }
        });

        if (controllerFuture != null)
        {
            taper.getSongsFAG().passController(controllerFuture);
        }
    }

//TODO fix seekbar in benachrichtuígugn da sstoppt, seekbarr soll mit laufen und muss stylen

    public void toggleView(View v)
    {
        CosWaveView sin = findViewById(R.id.sinewave);
        if (sin.getVisibility() == View.VISIBLE)
        {
            sin.setVisibility(View.INVISIBLE);
        }
        else
        {
            sin.setVisibility(View.VISIBLE);
        }
    }

    public void RollupMenuonRestartIfPlaying() {
        try {
            if (controllerFuture.get().isPlaying())
            {
                Handler handler = new Handler();
                handler.postDelayed(this::PullUpIfPlayingOnReloadDoTheNationState, 400);
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        taper.getSongsFAG().passController(controllerFuture);
        try {
            controllerFuture.get().addListener(new Player.Listener() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    Player.Listener.super.onIsPlayingChanged(isPlaying);
                    playbackService.ahfickmichdoch();
                    if (isPlaying)
                    {
                        try {
                            dotheceremony(controllerFuture.get().getDuration());
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void PullUpIfPlayingOnReloadDoTheNationState()
    {
        @SuppressLint("Recycle") ObjectAnimator anima = ObjectAnimator.ofFloat(fraggy, "y", 0);
        anima.setInterpolator(new FastOutSlowInInterpolator());
        anima.setDuration(700);
        anima.start();

        anima = ObjectAnimator.ofFloat(miniPlayerBar, "y", -miniPlayerBar.getHeight());
        anima.setInterpolator(new FastOutSlowInInterpolator());
        anima.setDuration(700);
        anima.start();

        anima = ObjectAnimator.ofFloat(whiteWiew, "y", -miniPlayerBar.getHeight());
        anima.setInterpolator(new FastOutSlowInInterpolator());
        anima.setDuration(700);
        anima.start();

        playerIsUp = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void PullMenuPrep() //TODO ALLES GUT BENNEN DIESMAL MACHEN WIR DIESE RICHTIG
    {
        lastY = new AtomicReference<>(0f);
        firstY = new AtomicReference<>(0f);
        lastX = new AtomicReference<>(0f);
        firstX = new AtomicReference<>(0f);

        AtomicBoolean lockY = new AtomicBoolean(false);
        AtomicBoolean lockX = new AtomicBoolean(false);

        fraggy.setOnTouchListener((v, event) -> { //man kann nur touchen wenn oben ist, ergo ich muss nicht differenzieren ob das oben ist oder unten. kann ja nur aktiviren wenn oben sasselbe für den kleinen nur umgedreht
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: //daumen landet
                    lastY.set(event.getRawY());
                    firstY.set(event.getRawY());
                    lastX.set(event.getRawX());
                    firstX.set(event.getRawX());
                    isTap = true;
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(fraggy.getWindowToken(), 0);
                    break;
                case MotionEvent.ACTION_MOVE: //ist eine bewegung und kein tap
                    float deltaY = event.getRawY() - lastY.get();
                    float deltaX = event.getRawX() - lastX.get();
                    if (Math.abs(deltaX) < Math.abs(deltaY) && (!lockY.get() && !lockY.get()))
                    {
                        lockX.set(true);
                    }
                    else if (!lockY.get() && !lockX.get()) //wenn die gleich sind piss ich mich ein, hat sich erledigt
                    {
                        lockY.set(true);
                    }
                    if (!(lastY.get() - firstY.get() < -80) && !lockY.get())
                    {
                        fraggy.setTranslationY(fraggy.getTranslationY() + deltaY);
                        miniPlayerBar.setTranslationY(fraggy.getTranslationY() + deltaY - fraggy.getHeight());
                        whiteWiew.setTranslationY(fraggy.getTranslationY() + deltaY - fraggy.getHeight());
                    }
                    if ((lastX.get() - firstX.get() < 250) && (lastX.get() - firstX.get() > -250) && !lockX.get())
                    {
                        fraggy.setTranslationX(fraggy.getTranslationX() + deltaX);
                    }
                    lastY.set(event.getRawY());
                    lastX.set(event.getRawX());
                    isTap = false;
                    break;
                case MotionEvent.ACTION_UP:
                    v.performClick();
                    if (!isTap) {
                        if (lastY.get() - firstY.get() < 100) //generell: black? greentext? setting???
                        {
                            AnimateUp();
                        } else {
                            AnimateDown();
                        }

                        if (lockY.get())
                        {


                            new Handler().postDelayed(() -> {
                                if ((lastX.get() - firstX.get() > 100))
                                {
                                    try {
                                        controllerFuture.get().seekToPrevious(); //Async
                                    } catch (ExecutionException | InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                else if ((lastX.get() - firstX.get() < -100))
                                {
                                    try {
                                        controllerFuture.get().seekToNext();

                                    } catch (ExecutionException | InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }, 300);
                            ObjectAnimator anim;
                            anim = ObjectAnimator.ofFloat(fraggy, "translationX", 0);
                            anim.setInterpolator(new LinearOutSlowInInterpolator());
                            anim.setDuration(300);
                            anim.start();
                        }



                        lastY.set(0f);
                        firstY.set(0f);
                        lockY.set(false);
                        lockX.set(false);
                    }
                    break;
            }
            return true;
        });

        lastY2 = new AtomicReference<>(0f);
        firstY2 = new AtomicReference<>(0f);
        miniPlayerBar.setOnTouchListener((v, event) -> { //man kann nur touchen wenn oben ist, ergo ich muss nicht differenzieren ob das oben ist oder unten. kann ja nur aktiviren wenn oben sasselbe für den kleinen nur umgedreht
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: //daumen landet
                    lastY2.set(event.getRawY());
                    firstY2.set(event.getRawY());
                    isTap2 = true;
                    break;
                case MotionEvent.ACTION_MOVE: //ist eine bewegung und kein tap
                    float deltaY = event.getRawY() - lastY2.get();
                    if ((lastY2.get() - firstY2.get() < -30))
                    {
                        fraggy.setTranslationY(fraggy.getTranslationY() + deltaY);
                        miniPlayerBar.setTranslationY(fraggy.getTranslationY() + deltaY - fraggy.getHeight());
                        whiteWiew.setTranslationY(fraggy.getTranslationY() + deltaY - fraggy.getHeight());
                    }
                    lastY2.set(event.getRawY());
                    isTap2 = false;
                    break;
                case MotionEvent.ACTION_UP:
                    v.performClick();
                    if (!isTap2) {
                        if (!(lastY2.get() - firstY2.get() > -100)) //generell: black? greentext? setting??? -100 gleich nichts für größeres minus spring
                        {
                            AnimateUp();
                        } else {
                            AnimateDown();
                        }
                        lastY2.set(0f);
                        firstY2.set(0f);
                    }
                    else
                    {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(fraggy, "y", 0);
                        animator.setDuration(500);
                        animator.setInterpolator(new FastOutSlowInInterpolator());
                        animator.start();

                        animator = ObjectAnimator.ofFloat(miniPlayerBar, "y", -miniPlayerBar.getHeight());
                        animator.setDuration(500);
                        animator.setInterpolator(new FastOutSlowInInterpolator());
                        animator.start();

                        animator = ObjectAnimator.ofFloat(whiteWiew, "y", -miniPlayerBar.getHeight());
                        animator.setDuration(500);
                        animator.setInterpolator(new FastOutSlowInInterpolator());
                        animator.start();

                        playerIsUp = true;
                    }
                    break;
            }
            return true;
        });
    }
public void saveStateNOW(View v)
{
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Bitmap.class, new BitmapTypeAdapter())
            .create();
    List<PlaylistFolder> audioFileList = allofTheFolds;
    String json = gson.toJson(audioFileList);

    SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString("myKey", json);
    editor.apply();

}

public void loadSaveState(View v)
{
    SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
    String json = prefs.getString("myKey", null);

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Bitmap.class, new BitmapTypeAdapter())
            .create();

    Type type = new TypeToken<List<PlaylistFolder>>() {}.getType();
    allofTheFolds = gson.fromJson(json, type);

}

    public void ImportFiles2(View v) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(i, "select penis"), 1);
        } catch (Exception e) {
            Toast.makeText(this, "Its ramadan and im chilling with my mom", Toast.LENGTH_LONG).show();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            Log.v("tag", e.toString());
        }

    }

    public void ImportFiles(View v) {
        if (viewPager.getCurrentItem() == 1)
        {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            String[] mimeTypes = {"audio/*"};
            i.setType("*/*");
            i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            someActivityResultLauncher.launch(i);

        }
        else
        {
            getInput(this, "Rename Folder", input -> {
                taper.PlaylistsFag().edititem(input);
                saveStateNOW(null);
            });
            PlaylistFolder playie = new PlaylistFolder("New Folder");
            allofTheFolds.add(playie);
            taper.PlaylistsFag().addItem(playie);
        }
    }

    public void getInput(Context context, String prompt, final OnInputListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(prompt);

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String userInput = input.getText().toString();
            if (listener != null) {
                listener.onInputReceived(userInput);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public interface OnInputListener {
        void onInputReceived(String input);
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                int count = clipData.getItemCount();
                                for (int i = 0; i < count; i++) {
                                    Intent singleData = new Intent();
                                    singleData.setData(clipData.getItemAt(i).getUri());
                                    Verarbeiten(singleData);
                                }
                                Toast.makeText(getApplicationContext(), "Received " + count + " files", Toast.LENGTH_LONG).show();
                            } else {
                                Verarbeiten(data);
                                Toast.makeText(getApplicationContext(), "Received 1 file", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    saveStateNOW(null);
                }
            });


    public void Verarbeiten(Intent data) {
        Uri uri = data.getData();
        // Load the MP3 file
        assert uri != null;
        AudioFile letslookifpresent = taper.getSongsFAG().getCurrentPlaylist().AlreadyPresent(uri.toString());
        AudioFile looksifrootpresent = taper.getSongsFAG().getRootFolder().AlreadyPresent(uri.toString());
        if (looksifrootpresent == null) //fall: in beiden, root und current
        {
            getApplicationContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            String artist = null;
            String title = null;
            byte[] albumArt = null;
            try {
                retriever.setDataSource(getApplicationContext(), uri);

                title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                albumArt = retriever.getEmbeddedPicture();

                if (title == null)
                {
                    title = getFileNameFromUri(uri);
                }

                if (title.length() > 48)
                {
                    title = title.substring(0, 48);
                }
                AudioFile theNew;
                if (albumArt == null)
                {
                    theNew = new AudioFile(uri.toString(), title, artist, null);
                }
                else
                {
                    theNew = new AudioFile(uri.toString(), title, artist, BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length));
                }

                taper.getSongsFAG().getCurrentPlaylist().addItem(theNew);
                if (taper.getSongsFAG().getCurrentPlaylist() != taper.getSongsFAG().getRootFolder())
                {
                    taper.getSongsFAG().getRootFolder().addItem(theNew);
                }

                taper.getSongsFAG().updatePlaylist();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (letslookifpresent == null)
        {
            taper.getSongsFAG().getCurrentPlaylist().addItem(looksifrootpresent);
            taper.getSongsFAG().updatePlaylist();
        }
    }

    public String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex != -1) {
                        fileName = cursor.getString(displayNameIndex);
                    } else {
                        // Handle the case where DISPLAY_NAME column doesn't exist
                        fileName = "Unknown";
                    }
                }
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }
        return fileName;
    }

    public void dotheceremony(Long schlong) throws ExecutionException, InterruptedException {

        if (!playerIsUp)
        {
            miniPlayerBar.setAlpha(1f);
            ObjectAnimator animator = ObjectAnimator.ofFloat(miniPlayerBar, "y", fraggy.getHeight() - miniPlayerBar.getHeight());
            animator.setDuration(600);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.start();

            animator = ObjectAnimator.ofFloat(addicon, "translationY", 0);
            animator.setDuration(600);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.start();

            Handler handler = new Handler();
            handler.postDelayed(this::FUUUUUUUUUCK, 800);
        }


        assert mediaPlayerFrag != null;
        seekBar = mediaPlayerFrag.getSeekBar();

        assert playerfrag != null;
        playerSeekBar = playerfrag.getSeekBar();


        //long schlong = 1; //kann mich nicht dazu bringen zu löschen... du bist für immer meine nummer 1 long schlong
        seekBar.setMax(Math.toIntExact(schlong) * 1000);
        playerSeekBar.setMax(Math.toIntExact(schlong) * 1000);
        mediaSeekBarUpdater = new MediaSeekBarUpdater(seekBar, playerSeekBar, controllerFuture);
        mediaSeekBarUpdater.start();

        long[] progress = new long[1];
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int pProgress, boolean fromUser) {
                // Seek to the specified position when the user changes the seek bar progress
                if (fromUser) {
                    progress[0] = pProgress;
                    // Seek to the specified position
                    // mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                playbackService.dudarfstnicht = true;
                //controllerFuture.get().play(); einfach nicht afnassen was solll schon passieren
                mediaPlayerFrag.sync = false;
                playerfrag.sync = false;
                mediaSeekBarUpdater.stop();

            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    controllerFuture.get().seekTo(progress[0] / 1000);
                    mediaSeekBarUpdater.start(); //stop für heute, thx for the day!!!!!!!!!
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                playbackService.dudarfstnicht = false;
                mediaPlayerFrag.sync = true;
                playerfrag.sync = true;
                try {
                    controllerFuture.get().play();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Initialize ViewModel and observe playback position
        MediaViewModel mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        mediaViewModel.getPlaybackPosition().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long position) {
                // Update the SeekBar progress with the playback position
                seekBar.setProgress((int) position.longValue() * 1000);
                playerSeekBar.setProgress((int) position.longValue() * 1000);
            }
        });

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int pProgress, boolean fromUser) {
                // Seek to the specified position when the user changes the seek bar progress
                if (fromUser) {
                    progress[0] = pProgress;
                    // Seek to the specified position
                    // mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                playbackService.dudarfstnicht = true;
                //controllerFuture.get().play(); einfach nicht afnassen was solll schon passieren
                mediaPlayerFrag.sync = false;
                playerfrag.sync = false;
                mediaSeekBarUpdater.stop();

            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    controllerFuture.get().seekTo(progress[0] / 1000);
                    mediaSeekBarUpdater.start(); //stop für heute, thx for the day!!!!!!!!!
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                playbackService.dudarfstnicht = false;
                mediaPlayerFrag.sync = true;
                playerfrag.sync = true;
                try {
                    controllerFuture.get().play();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }



    public void FUUUUUUUUUCK()
    {
        whiteWiew.setVisibility(View.VISIBLE);
    }

    public static  ListenableFuture<MediaController> getTheMediaController()
    {
        return controllerFuture;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}