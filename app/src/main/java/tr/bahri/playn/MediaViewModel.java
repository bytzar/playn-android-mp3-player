package tr.bahri.playn;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MediaViewModel extends ViewModel {
    private MutableLiveData<Long> playbackPosition = new MutableLiveData<>();

    public void setPlaybackPosition(long position) {
        playbackPosition.setValue(position);
    }

    public LiveData<Long> getPlaybackPosition() {
        return playbackPosition;
    }
}
