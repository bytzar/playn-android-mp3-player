package tr.bahri.playn;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private static List<AudioFile> mDataList;
    static ListenableFuture<MediaController> controllerFuture;
    public static int curpos = 0;
    private int selectedItemPosition = RecyclerView.NO_POSITION;
    private PlaylistFolder playie;
    private Player_Frag playerFrag;
    MyViewHolder holdie;
    List<AudioFile> asdf;
    public Context fontext;
    public static Context focktext;

    public RecyclerAdapter(List<AudioFile> dataList, Context fantext) {
        this.mDataList = dataList;
        fontext = fantext;
        focktext = fantext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mediarowsongs, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holdie = holder;
        AudioFile data = mDataList.get(position);
        holder.bind(data);


        if (position == selectedItemPosition) {
            holder.itemView.setBackgroundResource(R.drawable.selected_item_background);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.default_item_background);
        }

        holder.itemView.setOnClickListener(v -> {
            // Update the selected item position
            int previousSelectedItemPosition = selectedItemPosition;
            selectedItemPosition = holder.getAdapterPosition();

            // Notify item change to update UI
            notifyItemChanged(previousSelectedItemPosition);
            notifyItemChanged(selectedItemPosition);

            // Perform other actions on item click
            curpos = position;
            if (position != RecyclerView.NO_POSITION)
            {
                AudioFile audioFile = mDataList.get(position);

                Uri imageUri;
                if (audioFile.coverArt == null)
                {
                    imageUri = Uri.parse("android.resource://tr.bahri.playn/" + R.drawable.exageret); //for now gibt auch smaller || if null
                }
                else
                {
                    imageUri = getImageUriFromBitmap(audioFile.coverArt);
                }

                MediaItem mediaItem =
                        new MediaItem.Builder()
                                .setMediaId("media-1")
                                .setUri(audioFile.getFilePath())
                                .setMediaMetadata(
                                        new MediaMetadata.Builder()
                                                .setArtist(audioFile.kuenstler) //audioFile.kuenstler.name
                                                .setArtworkUri(imageUri)
                                                .setTitle(audioFile.title)
                                                .build())
                                .build();
                try {
                    controllerFuture.get().clearMediaItems();
                    miniMediaPlayer_Frag.updateText(audioFile.title);
                    Player_Frag.updateText(audioFile);
                    miniMediaPlayer_Frag.updateCover(audioFile);
                    playerFrag.updateCover(audioFile);
                    controllerFuture.get().addMediaItem(mediaItem);
                    controllerFuture.get().prepare();
                    controllerFuture.get().play();

                    asdf = new ArrayList<>();
                    asdf = playie.StartAtCue(audioFile);

                    for (int i = 0; i < asdf.size(); i++)
                    {
                        audioFile = asdf.get(i);

                        if (audioFile.coverArt == null)
                        {
                            imageUri = Uri.parse("android.resource://tr.bahri.playn/" + R.drawable.exageret); //for now gibt auch smaller || if null
                        }
                        else
                        {
                            imageUri = getImageUriFromBitmap(audioFile.coverArt);
                        }

                        mediaItem =
                                new MediaItem.Builder()
                                        .setMediaId("media-1")
                                        .setUri(audioFile.getFilePath())
                                        .setMediaMetadata(
                                                new MediaMetadata.Builder()
                                                        .setArtist(audioFile.kuenstler) //audioFile.kuenstler.name
                                                        .setArtworkUri(imageUri)
                                                        .setTitle(audioFile.title)
                                                        .build())
                                        .build();
                        try {
                            //miniMediaPlayer_Frag.updateText(audioFile.title);
                            //Player_Frag.updateText(audioFile);
                            controllerFuture.get().addMediaItem(mediaItem);
                            //controllerFuture.get().prepare();
                            //controllerFuture.get().play();
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public List<AudioFile> getCurPlayie()
    {
        if (asdf != null)
        {
            return asdf;
        }
        return null;
    }
/*
    public void moveIndicatorDown() //fürh nach benachrittigung könnte man kalkulieren weil listen für playlist entsprechen die listen wie sie angezeigt werden von daher
    {
        if (holdie != null)
        {
            holdie.itemView.setBackgroundResource(R.drawable.selected_item_background);
            holdie.itemView.setBackgroundResource(R.drawable.default_item_background);
            holdie.itemView.
        }
    }
    public void moveIndicatorToUPABoive() //bin halt einfach unkonventioanl
    {
        if (position == selectedItemPosition) {
            holder.itemView.setBackgroundResource(R.drawable.selected_item_background);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.default_item_background);
        }
    }*/

    public void passCurplasylist(PlaylistFolder p)
    {
        playie = p;
    }
    public Uri getImageUriFromBitmap(Bitmap bitmap) {
        if (fontext != null)
        {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = ImageUtils.saveBitmapToGallery(fontext.getContentResolver(), bitmap, "Title", null);
            return Uri.parse(path);
        }
        return null;
    }

    public boolean isCurrEmp()
    {
        return playie == null;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void addItem(AudioFile newItem) {
        mDataList.add(newItem);
        notifyItemInserted(mDataList.size() - 1);
    }

    public void setAllMkay(List<AudioFile> newLSit) {
        mDataList = newLSit;
        notifyDataSetChanged();
    }

    public void updateTitle() {
        if (curpos >= 0 && curpos < mDataList.size()) {
            notifyItemChanged(curpos);
        }
    }
    public void passPlayerFrag(Player_Frag playgay)
    {
        playerFrag = playgay;
    }

    public void passController(ListenableFuture<MediaController> pCon)
    {
        controllerFuture = pCon;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           textView = itemView.findViewById(R.id.textView4);
            img = itemView.findViewById(R.id.imageView5);
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                curpos = position;
                if (position != RecyclerView.NO_POSITION)
                {
                    AudioFile audioFile = mDataList.get(position);
                    Uri imageUri;
                    if (audioFile.coverArt == null)
                    {
                        imageUri = Uri.parse("android.resource://tr.bahri.playn/" + R.drawable.exageret); //for now gibt auch smaller || if null
                    }
                    else
                    {
                        imageUri = getImageUriFromBitmap(audioFile.coverArt);
                    }

                    MediaItem mediaItem =
                            new MediaItem.Builder()
                                    .setMediaId("media-1")
                                    .setUri(audioFile.getFilePath())
                                    .setMediaMetadata(
                                            new MediaMetadata.Builder()
                                                    .setArtist(audioFile.kuenstler) //audioFile.kuenstler.name
                                                    .setArtworkUri(imageUri)
                                                    .setTitle(audioFile.title)
                                                    .build())
                                    .build();
                    try {
                        controllerFuture.get().clearMediaItems();
                        miniMediaPlayer_Frag.updateText(audioFile.title);
                        Player_Frag.updateText(audioFile);
                        controllerFuture.get().addMediaItem(mediaItem);
                        controllerFuture.get().prepare();
                        controllerFuture.get().play();
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }


        public Uri getImageUriFromBitmap(Bitmap bitmap) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = ImageUtils.saveBitmapToGallery(focktext.getContentResolver(), bitmap, "Title", null);
            return Uri.parse(path);
        }



        public void bind(AudioFile data) {
            textView.setText(data.getTitle());
            if (data.coverArt != null)
            {
                img.setImageBitmap(data.coverArt);//artist fehlt für im recycler view anzeiegn
            }
        }
    }
}
