package com.example.ipshita.mymasterdetailtestapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.ipshita.mymasterdetailtestapplication.models.Track;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyService;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public static final String ACTION_PLAY = "com.example.action.PLAY";
    public static final String ACTION_PLAY_PAUSE = "com.example.action.PLAY_PAUSE";
    public static final String ACTION_PREV = "com.example.action.PREVIOUS";
    public static final String ACTION_NEXT = "com.example.action.NEXT";
    public static final String ACTION_SEEKBAR_CHANGED = "com.example.action.SEEK";
    public static final String ACTION_REQUEST_TIME_POSITION = "com.example.action.REQUEST_CURRENT_POSITION";
    public static final String ACTION_FRAGMENT_RESUMED = "com.example.action.RESUMED";
    private static final int NOTIFICATION_ID = 146;
    private static final String LOG_TAG = MusicPlayerService.class.getSimpleName();

    private static OnNotificationEventListener dummyOnNotificationListener = new OnNotificationEventListener() {
        @Override
        public void getDuration(int duration) {

        }

        @Override
        public void nextClicked() {

        }

        @Override
        public void previousClicked() {

        }

        @Override
        public void onMusicPaused() {

        }

        @Override
        public void onMusicStarted() {

        }

        @Override
        public void onMusicResumed() {

        }

        @Override
        public void setCurrentTrackTimePosition(int position) {

        }

        @Override
        public void onTrackCompleted() {

        }

        @Override
        public void getTrackNumber(int trackNumber) {

        }

        @Override
        public void getCurrentState(ArrayList<Track> trackList, int currentTrackNumber, int currentTimeTrackPosition, boolean isMediaPlayerON, boolean isPaused, int duration) {

        }
    };
    private static OnNotificationEventListener listener = dummyOnNotificationListener;
    private static MusicEndListener dummyMusicEndListener = new MusicEndListener() {
        @Override
        public void onMusicEnd() {

        }

        @Override
        public void onMusicStarted(String spotifyExternalURL) {

        }
    };

    private static MusicEndListener iMusicEndListener = dummyMusicEndListener;


    MediaPlayer mediaPlayer = null;
    ArrayList<Track> tracks = null;
    String url = "";
    String trackName = "";

    // track number
    private int position;
    private Notification notification;
    // for the lock screen toggle switch
    private int notificationLockScreenVisibility;
    private Track currentTrack;
    private NotificationCompat.Builder builder;
    private RemoteViews remoteView;
    private NotificationManager nManager;
    private boolean isPaused = false;
    private int seekbarPosition;

    public MusicPlayerService() {

        tracks = new ArrayList<>();
    }

    public static void registerOnMusicEndListener(MusicEndListener listener) {
        iMusicEndListener = listener;

    }

    public static void unRegisterOnMusicEndListener(){
        iMusicEndListener = dummyMusicEndListener;
    }

    public interface MusicEndListener{
        public void onMusicEnd();
        public void onMusicStarted(String spotifyExternalURL);
    }



    public static void registerOnNotificationEventListener(OnNotificationEventListener onNotificationEventListener) {
        listener = onNotificationEventListener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // create all intents for notification
        Intent notificationIntent = new Intent(this, MusicPlayAcitvity.class);
        // for set content intent in notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, MusicPlayerService.class);
        previousIntent.setAction(ACTION_PREV);

        PendingIntent pendingPreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, MusicPlayerService.class);
        playIntent.setAction(ACTION_PLAY_PAUSE);

        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, MusicPlayerService.class);
        nextIntent.setAction(ACTION_NEXT);

        PendingIntent pendingNextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        if (intent != null && intent.getAction() != null) {






            // check intent action
            if (null != intent && null != intent.getAction() && intent.getAction().equals(ACTION_PLAY)) {

                position = intent.getIntExtra(getString(R.string.track_position), -1);

                if (position > -1) {

                    tracks = intent.getParcelableArrayListExtra(getString(R.string.tracklist_key));

                    if (null != tracks && tracks.size() > 0 && null != tracks.get(position)) {

                        currentTrack = tracks.get(position);

                        url = tracks.get(position).getPreviewURL();
                        trackName = tracks.get(position).getTrackName();

                    }

                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer(); // initialize it here
                        // get media player ready
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            if (!url.isEmpty()) {

                                // changes for now playing. otherwise it gives illegalstate exception
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.stop();

                                }
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(url);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        seekbarPosition = intent.getIntExtra(getString(R.string.seekbar_progress_position), 0);
                        mediaPlayer.setOnPreparedListener(this);
                        mediaPlayer.setOnCompletionListener(this);
                        mediaPlayer.setOnErrorListener(this);
                        mediaPlayer.prepareAsync();// prepare async to not block main thread
                        playMedia(url);

                        // build notification
                        remoteView = new RemoteViews(getPackageName(), R.layout.service_music_player_notification);

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                        if (preferences.getBoolean(getString(R.string.preference_lockscreen_key), true)) {
                            notificationLockScreenVisibility = Notification.VISIBILITY_PUBLIC;
                        } else {
                            notificationLockScreenVisibility = Notification.VISIBILITY_PRIVATE;
                        }
                        // TODO check visibility from shared preference;

                        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        builder = new NotificationCompat.Builder(this);
                        builder.setTicker(SpotifyService.class.getSimpleName())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentIntent(pendingIntent)
                                .setContent(remoteView)
                                .setOngoing(true)
                                .setVisibility(notificationLockScreenVisibility);
                        notification = builder.build();
                        startForeground(NOTIFICATION_ID,
                                notification);

                        remoteView.setOnClickPendingIntent(R.id.prev_imagebutton, pendingPreviousIntent);
                        remoteView.setOnClickPendingIntent(R.id.play_pause_imagebutton, pendingPlayIntent);
                        remoteView.setOnClickPendingIntent(R.id.next_imagebutton, pendingNextIntent);
                    }else{
                        try {
                            if (!url.isEmpty()) {

                                // changes for now playing. otherwise it gives illegalstate exception
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.stop();

                                }
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(url);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        playMedia(url);

                    }

                }
            } else if (null != intent.getAction() && intent.getAction().equals(ACTION_PREV)) {




                // update current track and play it
                if (null != tracks && tracks.size() > 0 && (position-1) > -1 && null != tracks.get(--position)) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        remoteView.setImageViewResource(R.id.play_pause_imagebutton, android.R.drawable.ic_media_play);
                        nManager.notify(NOTIFICATION_ID, notification);

                    }
                    currentTrack = tracks.get(position);
                    String url = currentTrack.getPreviewURL();
                    playMedia(url);
                    if (null != listener) {
                        listener.previousClicked();
                        listener.getTrackNumber(position);
                    }
                }

            } else if (null != intent.getAction() && intent.getAction().equals(ACTION_PLAY_PAUSE)) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPaused = true;
                    if (null != listener)
                        listener.onMusicPaused();
                    remoteView.setImageViewResource(R.id.play_pause_imagebutton, android.R.drawable.ic_media_play);
                    nManager.notify(NOTIFICATION_ID, notification);

                } else {

                    mediaPlayer.start();
                    isPaused = false;
                    if (null != listener)
                        listener.onMusicResumed();
                    remoteView.setImageViewResource(R.id.play_pause_imagebutton, android.R.drawable.ic_media_pause);
                    nManager.notify(NOTIFICATION_ID, notification);
                }

            } else if (null != intent.getAction() && intent.getAction().equals(ACTION_NEXT)) {


                if (null != tracks && tracks.size() > 0 && (position+1) < tracks.size() && null != tracks.get(++position)) {

                    if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        remoteView.setImageViewResource(R.id.play_pause_imagebutton, android.R.drawable.ic_media_play);
                        nManager.notify(NOTIFICATION_ID, notification);
                    }
                    currentTrack = tracks.get(position);
                    String url = currentTrack.getPreviewURL();
                    playMedia(url);
                    if (null != listener) {
                        listener.nextClicked();
                        listener.getTrackNumber(position);
                    }
                    //TODO make layout land


                }

            } else if (null != intent.getAction() && intent.getAction().equals(ACTION_SEEKBAR_CHANGED)) {
                int seekBarProgress = intent.getIntExtra(getString(R.string.seekbar_progress_position), 0);
                if (null != mediaPlayer) {
                    mediaPlayer.seekTo(seekBarProgress);
                    mediaPlayer.start();
                }
            } else if (null != intent.getAction() && intent.getAction().equals(ACTION_REQUEST_TIME_POSITION)) {
                if (null != listener && null != mediaPlayer)
                    listener.setCurrentTrackTimePosition(mediaPlayer.getCurrentPosition());
            } else if (null != intent.getAction() && intent.getAction().equals(ACTION_FRAGMENT_RESUMED)) {
                if (null != listener && null != mediaPlayer) {
                    listener.getCurrentState(tracks, position, mediaPlayer.getCurrentPosition(), mediaPlayer != null, isPaused, mediaPlayer.getDuration());
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void playMedia(String url) {
        if (mediaPlayer.isPlaying())
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            if (!url.isEmpty())
                mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mediaPlayer.seekTo(seekbarPosition);
        mediaPlayer.start();
        Intent intent = new Intent();
        intent.setAction(getString(R.string.action_now_playing));
        intent.putExtra(getString(R.string.external_url_key),currentTrack.getSpotifyExternalURL());
        sendBroadcast(intent);
        if (null != listener) {
            listener.getDuration(mediaPlayer.getDuration());
            listener.onMusicStarted();
            listener.getCurrentState(tracks,tracks.indexOf(currentTrack),mediaPlayer.getCurrentPosition(),true,false,mediaPlayer.getDuration());
        }
        remoteView.setImageViewResource(R.id.play_pause_imagebutton, android.R.drawable.ic_media_pause);
        remoteView.setTextViewText(R.id.track_name_textview, currentTrack.getTrackName());
        //nManager.notify(NOTIFICATION_ID, notification);
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Picasso
                        .with(MusicPlayerService.this)
                        .load(currentTrack.getAlbumThumbnailLink())
                        .into(remoteView, R.id.album_thumbnail_imageview, NOTIFICATION_ID, notification);
            }
        });


    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        iMusicEndListener.onMusicEnd();
        if (null != listener)
            listener.onTrackCompleted();
        if (null != mediaPlayer)
            mediaPlayer.release();
        mediaPlayer = null;
        stopForeground(true);


    }

    @Override
    public void onDestroy() {

        if (null != listener)
            listener = dummyOnNotificationListener;
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public interface OnNotificationEventListener {
        void getDuration(int duration);

        void nextClicked();

        void previousClicked();

        void onMusicPaused();

        void onMusicStarted();

        void onMusicResumed();

        void setCurrentTrackTimePosition(int position);

        void onTrackCompleted();

        void getTrackNumber(int trackNumber);

        void getCurrentState(ArrayList<Track> trackList, int currentTrackNumber, int currentTimeTrackPosition, boolean isMediaPlayerON, boolean isPaused, int duration);


    }
}
