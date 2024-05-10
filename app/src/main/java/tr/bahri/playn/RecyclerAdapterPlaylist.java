package tr.bahri.playn;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecyclerAdapterPlaylist extends RecyclerView.Adapter<RecyclerAdapterPlaylist.MyViewHolder> {
    private static List<PlaylistFolder> mDataList;
    static ListenableFuture<MediaController> controllerFuture;
    Songs sungs;
    private int selectedItemPosition = 0;
    Context strargmusli;

    public RecyclerAdapterPlaylist(List<PlaylistFolder> dataList, Songs p, Context lere) { //ironisch wenn lere null wäre .....................                                                                                                             HAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHH
        sungs = p;
        this.mDataList = new ArrayList<>(dataList);
        strargmusli = lere;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mediarowplaylists, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PlaylistFolder data = mDataList.get(position);
        holder.bind(data);

        // Highlight the selected item
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
            PlaylistFolder clickedPlaylist = mDataList.get(selectedItemPosition);
            sungs.setCurrentPlaylist(clickedPlaylist);
            sungs.updatePlaylist(clickedPlaylist);
        });
    }



    public void setDate(List<PlaylistFolder> rarg)
    {
        mDataList = rarg;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void addItem(PlaylistFolder newItem) {
        mDataList.add(newItem);
        notifyItemInserted(mDataList.size() - 1);
    }
    public void editLatestItem(String arg) {
        if (!mDataList.isEmpty()) {
            int position = mDataList.size() - 1; // Get the index of the last item
            PlaylistFolder latestPlaylist = mDataList.get(position);
            latestPlaylist.title = arg;
            mDataList.set(position, latestPlaylist);
            notifyItemChanged(position);
        }
    }
    public void passController(ListenableFuture<MediaController> pCon)
    {
        controllerFuture = pCon;
    }
    public void updateSongsFrag()
    {
        sungs.updatePlaylist(sungs.getCurrentPlaylist());
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView img;
        private View preView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           textView = itemView.findViewById(R.id.textView4);


            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) //passiert nicht anscheinend
                {
                    PlaylistFolder PlaylistThis = mDataList.get(position);
                    sungs.setCurrentPlaylist(PlaylistThis);
                    sungs.updatePlaylist(PlaylistThis);

                    itemView.setBackgroundColor(Color.argb(50, 5, 50, 5));


                }
            });
        }
        public void bind(PlaylistFolder data) {
            textView.setText(data.getTitle());

        }
    }
}
