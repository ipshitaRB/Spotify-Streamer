package com.example.ipshita.mymasterdetailtestapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ipshita.mymasterdetailtestapplication.Util.MusicStartListener;
import com.example.ipshita.mymasterdetailtestapplication.models.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MusicPlayActivityFragment extends Fragment implements MusicPlayerService.OnNotificationEventListener {

    private static final int IMAGE_WIDTH = 800;
    private static final int IMAGE_HEIGHT = 800;
    private static final long SEEK_BAR_UPDATE_INTERVAL = 200;
    private static final int MILISECONDS_IN_ONE_SECOND = 1000;
    private static final int SECONDS_IN_ONE_MINUTE = 60;
    public List<Track> trackList;
    public int currentTrackPosition;
    TextView artistNameTextView;
    TextView albumNameTextView;
    TextView trackNameTextView;
    ImageView albumThumbnailImageView;
    private boolean isPlaying = false;
    private ImageButton playPauseButton, previousButton, nextButton;
    private SeekBar seekBar;
    private Track currentTrack;
    private TextView totalDurationTextView, elapsedTimeTextView;
    private int trackTimePosition;
    private int trackDuration = 0;
    private View rootView;
    private boolean isServiceOn = false;
        private int currentTimeTrackPosition;

    public MusicPlayActivityFragment() {

        MusicPlayerService.registerOnNotificationEventListener(this);

    }
/*    public interface iMusicPlayDialogListener{
        public void onTrackCompleted();
        public void onTrackStarted(String spotifyExternalURL);
    }

    private static iMusicPlayDialogListener dummyListener = new iMusicPlayDialogListener() {
        @Override
        public void onTrackCompleted() {

        }

        @Override
        public void onTrackStarted(String spotifyExternalURL) {

        }


    };

    private iMusicPlayDialogListener musicCompletedListener = dummyListener;*/


    private static MusicStartListener dummyListener = new MusicStartListener() {
        @Override
        public void onTrackCompleted() {

        }

        @Override
        public void onTrackStarted(String spotifyExternalURL) {

        }


    };

    private static MusicStartListener musicCompletedListener = dummyListener;
   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MusicStartListener){
            musicCompletedListener = (MusicStartListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        musicCompletedListener = dummyListener;
    }*/

    public static void registerMusicStartListener(MusicStartListener listener){
        musicCompletedListener = listener;
    }

    public static void unregisterMusicStartListener(){
        musicCompletedListener = dummyListener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.tracklist_parcel_key), (ArrayList<? extends Parcelable>) trackList);
        outState.putInt(getString(R.string.track_number_key), currentTrackPosition);
        outState.putParcelable(getString(R.string.current_track_key), currentTrack);
        // ispaused
        // seekbarposition
        // elapsed time
        // total duration
        outState.putBoolean(getString(R.string.is_playing_key), isPlaying);
        outState.putInt(getString(R.string.seekbar_progress_position), trackTimePosition);
        outState.putInt(getString(R.string.track_duration),trackDuration);

        super.onSaveInstanceState(outState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            Intent startServiceIntent = new Intent(getActivity(), MusicPlayerService.class);
            // pass the entire top track list and position
            startServiceIntent.putParcelableArrayListExtra(getString(R.string.tracklist_key), (ArrayList<? extends Parcelable>) trackList);
            startServiceIntent.putExtra(getString(R.string.track_position), currentTrackPosition);
            // set action play
        startServiceIntent.setAction(MusicPlayerService.ACTION_FRAGMENT_RESUMED);

            getActivity().startService(startServiceIntent);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_music_play_acitvity, container, false);

        artistNameTextView = (TextView) rootView.findViewById(R.id.artist_name_textview);

        albumNameTextView = (TextView) rootView.findViewById(R.id.album_name_textview);

        trackNameTextView = (TextView) rootView.findViewById(R.id.track_name_textview);

        albumThumbnailImageView = (ImageView) rootView.findViewById(R.id.album_thumbnail_imageview);



        elapsedTimeTextView = (TextView) rootView.findViewById(R.id.elapsed_time_textview);
        totalDurationTextView = (TextView) rootView.findViewById(R.id.total_duration_textview);
        playPauseButton = (ImageButton) rootView.findViewById(R.id.play_pause_button);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    // if the song is finished playing
                    if (trackTimePosition == trackDuration && trackTimePosition > 0) {
                        // restart the service
                        // get artist name from previous intent
                        Intent intent = getActivity().getIntent();
                        trackList = intent.getParcelableArrayListExtra(getString(R.string.tracklist_key));
                        currentTrackPosition = intent.getIntExtra(getString(R.string.track_position), -1);
// start music player Service
                        Intent startServiceIntent = new Intent(getActivity(), MusicPlayerService.class);
                        // pass the entire top track list and position
                        startServiceIntent.putParcelableArrayListExtra(getString(R.string.tracklist_key), (ArrayList<? extends Parcelable>) trackList);
                        startServiceIntent.putExtra(getString(R.string.track_position), currentTrackPosition);
                        // set action play
                        startServiceIntent.setAction(MusicPlayerService.ACTION_PLAY);

                        getActivity().startService(startServiceIntent);
                    }


                } else {
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);

                }
                // start music player Service
                Intent startServiceIntent = new Intent(getActivity(), MusicPlayerService.class);
                // set action play
                startServiceIntent.setAction(MusicPlayerService.ACTION_PLAY_PAUSE);
                getActivity().startService(startServiceIntent);
            }
        });

        previousButton = (ImageButton) rootView.findViewById(R.id.prev_button);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start music player Service
                Intent startServiceIntent = new Intent(getActivity(), MusicPlayerService.class);
                // set action play
                startServiceIntent.setAction(MusicPlayerService.ACTION_PREV);
                getActivity().startService(startServiceIntent);
            }
        });

        nextButton = (ImageButton) rootView.findViewById(R.id.next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // start music player Service
                Intent startServiceIntent = new Intent(getActivity(), MusicPlayerService.class);
                // set action play
                startServiceIntent.setAction(MusicPlayerService.ACTION_NEXT);
                getActivity().startService(startServiceIntent);
            }
        });

        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // if the song is finished playing
                if (trackTimePosition == trackDuration && trackTimePosition > 0) {
                    // restart the service
                    // get artist name from previous intent
                    Intent intent = getActivity().getIntent();
                    trackList = intent.getParcelableArrayListExtra(getString(R.string.tracklist_key));
                    currentTrackPosition = intent.getIntExtra(getString(R.string.track_position), -1);
// start music player Service
                    Intent startServiceIntent = new Intent(getActivity(), MusicPlayerService.class);
                    // pass the entire top track list and position
                    startServiceIntent.putParcelableArrayListExtra(getString(R.string.tracklist_key), (ArrayList<? extends Parcelable>) trackList);
                    startServiceIntent.putExtra(getString(R.string.track_position), currentTrackPosition);
                    startServiceIntent.putExtra(getString(R.string.seekbar_progress_position), seekBar.getProgress());
                    // set action play
                    startServiceIntent.setAction(MusicPlayerService.ACTION_PLAY);

                    getActivity().startService(startServiceIntent);

                } else {
                    // start music player Service
                    Intent startServiceIntent = new Intent(getActivity(), MusicPlayerService.class);
                    startServiceIntent.putExtra(getString(R.string.seekbar_progress_position), seekBar.getProgress());
                    // set action play
                    startServiceIntent.setAction(MusicPlayerService.ACTION_SEEKBAR_CHANGED);
                    getActivity().startService(startServiceIntent);
                }

            }
        });

        if (!isServiceOn) {

            if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.tracklist_parcel_key))) {
                // get artist name from previous intent
                Intent intent = getActivity().getIntent();
                trackList = intent.getParcelableArrayListExtra(getString(R.string.tracklist_key));
                currentTrackPosition = intent.getIntExtra(getString(R.string.track_position), -1);
// start music player Service
                Intent startServiceIntent = new Intent(getActivity(), MusicPlayerService.class);
                // pass the entire top track list and position
                startServiceIntent.putParcelableArrayListExtra(getString(R.string.tracklist_key), (ArrayList<? extends Parcelable>) trackList);
                startServiceIntent.putExtra(getString(R.string.track_position), currentTrackPosition);
                // set action play
                startServiceIntent.setAction(MusicPlayerService.ACTION_PLAY);

                getActivity().startService(startServiceIntent);
            } else {
                trackList = savedInstanceState.getParcelableArrayList(getString(R.string.tracklist_parcel_key));
                currentTrackPosition = savedInstanceState.getInt(getString(R.string.track_number_key), -1);
                currentTrack = savedInstanceState.getParcelable(getString(R.string.current_track_key));
                trackDuration = savedInstanceState.getInt(getString(R.string.track_duration));
                seekBar.setProgress(savedInstanceState.getInt(getString(R.string.seekbar_progress_position)));
                elapsedTimeTextView.setText(getDurationInMinutes(seekBar.getProgress()));
                totalDurationTextView.setText(getDurationInMinutes(trackDuration));
                isPlaying = savedInstanceState.getBoolean(getString(R.string.is_playing_key));
                Intent startServiceIntent = new Intent(getActivity(), MusicPlayerService.class);
                // set action play
                startServiceIntent.setAction(MusicPlayerService.ACTION_FRAGMENT_RESUMED);

                getActivity().startService(startServiceIntent);

            }
            if (null != trackList && trackList.size() > 0) {
                currentTrack = trackList.get(currentTrackPosition);
                if (null != currentTrack) {
                    artistNameTextView.setText(currentTrack.getArtistName());
                    albumNameTextView.setText(currentTrack.getAlbumName());
                    trackNameTextView.setText(currentTrack.getTrackName());
                    if (null != currentTrack.getAlbumThumbnailLink() && !currentTrack.getAlbumThumbnailLink().isEmpty())
                        Picasso.with(getActivity()).load(currentTrack.getAlbumThumbnailLink()).resize(IMAGE_WIDTH, IMAGE_HEIGHT).centerCrop().into(albumThumbnailImageView);
                }
            }

        } else {
            updateUI();
        }


        return rootView;

    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void getDuration(int duration) {
        // set durationt
        trackDuration = duration;
        seekBar.setMax(duration);
        if (isAdded())
            totalDurationTextView.setText(getDurationInMinutes(duration));
        Log.i(MusicPlayActivityFragment.class.getSimpleName(), String.valueOf(duration));

    }


    private String getDurationInMinutes(int duration) {

        int durationInSeconds = duration / MILISECONDS_IN_ONE_SECOND;
        int minutes = durationInSeconds / SECONDS_IN_ONE_MINUTE;
        int seconds = durationInSeconds % SECONDS_IN_ONE_MINUTE;
        if (isAdded()) {
            return String.valueOf(minutes) + getString(R.string.colon) + String.valueOf(seconds);
        }else
            return "";
    }

    @Override
    public void nextClicked() {

        updateUI();
    }

    @Override
    public void previousClicked() {
        updateUI();
    }

    private void updateUI() {
        currentTrack = trackList.get(currentTrackPosition);
        artistNameTextView.setText(currentTrack.getArtistName());
        albumNameTextView.setText(currentTrack.getAlbumName());
        trackNameTextView.setText(currentTrack.getTrackName());
        if (null != currentTrack.getAlbumThumbnailLink() && !currentTrack.getAlbumThumbnailLink().isEmpty())
            Picasso.with(getActivity()).load(currentTrack.getAlbumThumbnailLink()).resize(IMAGE_WIDTH, IMAGE_HEIGHT).centerCrop().into(albumThumbnailImageView);
    }

    @Override
    public void onMusicPaused() {
        isPlaying = false;

    }


    @Override
    public void onMusicStarted() {
        isPlaying = true;
        playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        new UpdateSeekBarTask().execute();
        musicCompletedListener.onTrackStarted(currentTrack.getSpotifyExternalURL());

    }

    @Override
    public void onMusicResumed() {
        isPlaying = true;
    }




    @Override
    public void setCurrentTrackTimePosition(int position) {
        trackTimePosition = position;
        seekBar.setProgress(trackTimePosition);
        elapsedTimeTextView.setText(getDurationInMinutes(trackTimePosition));
        updateUI();

    }

    @Override
    public void onTrackCompleted() {
        playPauseButton.setImageResource(android.R.drawable.ic_media_play);
        trackTimePosition = trackDuration;
        setCurrentTrackTimePosition(trackTimePosition);
        musicCompletedListener.onTrackCompleted();
    }

    @Override
    public void getTrackNumber(int trackNumber) {
        currentTrackPosition = trackNumber;
    }

    @Override
    public void getCurrentState(ArrayList<Track> trackList, int currentTrackNumber, int currentTimeTrackPosition, boolean isMediaPlayerON, boolean isPaused, int duration) {
        this.isServiceOn = isMediaPlayerON;
        if (isMediaPlayerON) {
            this.trackList = trackList;
            if (null != trackList && currentTrackNumber > -1 && trackList.size() > 0) {
                currentTrack = trackList.get(currentTrackNumber);
                this.currentTrackPosition = currentTrackNumber;
                trackTimePosition = currentTimeTrackPosition;
                this.isPlaying = !isPaused;
                this.trackDuration = duration;
                setUIOnNowPlaying();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_song_currently_playing), Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

    private void setUIOnNowPlaying() {
        artistNameTextView.setText(currentTrack.getArtistName());
        albumNameTextView.setText(currentTrack.getAlbumName());
        trackNameTextView.setText(currentTrack.getTrackName());
        if (null != currentTrack.getAlbumThumbnailLink() && !currentTrack.getAlbumThumbnailLink().isEmpty())
            Picasso.with(getActivity()).load(currentTrack.getAlbumThumbnailLink()).resize(IMAGE_WIDTH, IMAGE_HEIGHT).centerCrop().into(albumThumbnailImageView);
        seekBar.setMax(trackDuration);
        totalDurationTextView.setText(getDurationInMinutes(trackDuration));
        if (isPlaying)
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        new UpdateSeekBarTask().execute();
    }


    public class UpdateSeekBarTask extends AsyncTask<Void, Void, Void> {

        // start music player Service



        @Override
        protected Void doInBackground(Void... arg0) {
            if (isAdded()) {
                Intent startServiceIntent = new Intent(getActivity(), MusicPlayerService.class);

                while (trackTimePosition <= trackDuration && isAdded()) {
                    try {
                        Thread.sleep(SEEK_BAR_UPDATE_INTERVAL);
                        // set action play
                        if (null != startServiceIntent && isAdded()) {
                            startServiceIntent.setAction(MusicPlayerService.ACTION_REQUEST_TIME_POSITION);
                            if (null != getActivity())
                                getActivity().startService(startServiceIntent);
                        }
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }


                }
            }
            return null;
        }


    }

}
