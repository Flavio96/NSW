package flavio.com.nsw.others;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import flavio.com.nsw.R;

public class ytDialog extends YouTubeBaseActivity {

    private YouTubePlayerView ytPlayer;
    private YouTubePlayer youTubePlayer;

    public static ytDialog newInstance(int myIndex) {
        ytDialog yourDialogFragment = new ytDialog();

        //example of passing args
        Bundle args = new Bundle();

        return yourDialogFragment;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.dialog_exercise_form);
        ytPlayer = findViewById(R.id.ytPlayer);
        Intent i = getIntent();

        initializeYoutubePlayer(i.getStringExtra("url"), i.getIntExtra("sec", 0));
    }

    private void initializeYoutubePlayer(final String url, final int sec) {

        ytPlayer.initialize(getResources().getString(R.string.api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(url, sec*1000);
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }


}
