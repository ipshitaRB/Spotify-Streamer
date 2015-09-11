package com.example.ipshita.mymasterdetailtestapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ipshita.mymasterdetailtestapplication.adapters.TopTracksAdapter;
import com.example.ipshita.mymasterdetailtestapplication.models.Artist;
import com.example.ipshita.mymasterdetailtestapplication.models.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * A fragment representing a single Artist detail screen.
 * This fragment is either contained in a {@link ArtistListActivity}
 * in two-pane mode (on tablets) or a {@link ArtistDetailActivity}
 * on handsets.
 */
public class ArtistDetailFragment extends Fragment {

    // list of top tracks
    public ArrayList<Track> trackList;
    // declare custom adapter for the tracks listview
    public TopTracksAdapter topTracksAdapter;

    public String artistSpotifyId;
    public String artistName;

    public SpotifyApi spotifyApi;
    public SpotifyService spotifyService;
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARTIST_ID = "artist_id";



    private TopTrackCallback mTopTrackCallback = sDummyCallbacks;

    /**
     * A dummy implementation of the {@link com.example.ipshita.mymasterdetailtestapplication.ArtistDetailFragment.TopTrackCallback} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static TopTrackCallback sDummyCallbacks = new TopTrackCallback() {
        @Override
        public void onTopTrackSelected(ArrayList<Track> trackList, int trackPosition) {

        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistDetailFragment() {
        //  initialize tracklist
        trackList = new ArrayList<Track>();
        spotifyApi = new SpotifyApi();
        spotifyService = spotifyApi.getService();
    }

    public interface TopTrackCallback {
        public void onTopTrackSelected(ArrayList<Track> trackList, int trackPosition);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof TopTrackCallback)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mTopTrackCallback = (TopTrackCallback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTopTrackCallback = sDummyCallbacks;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.top_track_parccel_key))) {
            // get artist name from previous intent
            Intent intent = getActivity().getIntent();
            Artist artist = intent.getParcelableExtra(ARTIST_ID);
            if (null != getArguments() && getArguments().containsKey(ARTIST_ID)) {
                artist = getArguments().getParcelable(ARTIST_ID);

            }
            if (null != artist) {

                artistSpotifyId = artist.getArtistId();//intent.getStringExtra(Intent.EXTRA_TEXT);
                artistName = artist.getArtistName();//intent.getStringExtra(getString(R.string.artist_name_key));


                //get top tracks of the artist in an asynctask using spotify api
                new FetchTopTracksTask().execute(artistSpotifyId);
            }

        } else {
            trackList = savedInstanceState.getParcelableArrayList(getString(R.string.top_track_parccel_key));

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.top_track_parccel_key), (ArrayList<? extends Parcelable>) trackList);

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        trackList.clear();
        // initialize tracks adapter
        topTracksAdapter = new TopTracksAdapter(getActivity(), trackList);
        // initialize top tracks list view
        ListView topTracksListView = (ListView) rootView.findViewById(R.id.top_tracks_listview);


        //set adapter to list view
        topTracksListView.setAdapter(topTracksAdapter);

        //set on item click listener
        topTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mTopTrackCallback.onTopTrackSelected(trackList,position);

            }
        });

        return rootView;
    }

    public class FetchTopTracksTask extends AsyncTask<String, Void, List<Track>> {

        private static final int MAX_TOP_TRACKS_LIMIT = 10;
        private final String LOG_TAG = FetchTopTracksTask.class.getSimpleName();
        private Tracks spotifyTopTrackList;

        private Map<String, Object> options;
        private List<Track> newTrackList = new ArrayList<Track>();

        @Override
        protected List<Track> doInBackground(String... params) {

            if (null != params[0]) {

                options = new HashMap<>();
                options.put(getString(R.string.spotify_country_param), getString(R.string.spotify_country_value));
                try {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String countryCode = sharedPrefs.getString(getString(R.string.preference_country_key), getString(R.string.pref_default_country));
                    spotifyTopTrackList = spotifyService.getArtistTopTrack(params[0], countryCode);
                    if (null != spotifyTopTrackList.tracks && !spotifyTopTrackList.tracks.isEmpty()) {
                        int size = spotifyTopTrackList.tracks.size() > MAX_TOP_TRACKS_LIMIT ? MAX_TOP_TRACKS_LIMIT : spotifyTopTrackList.tracks.size();
                        String albumName;
                        String albumThumbnailLink;
                        String trackName;
                        String artistName;
                        String preview_url;

                        kaaes.spotify.webapi.android.models.Track currentTrack;
                        trackList.clear();
                        for (int i = 0; i < size; i++) {
                            currentTrack = spotifyTopTrackList.tracks.get(i);
                            if (null != currentTrack && null != currentTrack.album) {
                                albumName = currentTrack.album.name;
                                if (null != currentTrack.album.images && !currentTrack.album.images.isEmpty()) {
                                    albumThumbnailLink = currentTrack.album.images.get(0).url;
                                    trackName = currentTrack.name;
                                    artistName = currentTrack.artists.get(0).name;
                                    preview_url = currentTrack.preview_url;

                                    newTrackList.add(new Track.Builder().trackName(trackName).albumName(albumName).albumThumbnailLink(albumThumbnailLink).artistName(artistName).previewURL(preview_url).spotifyExternalURL(currentTrack.external_urls.get(getString(R.string.external_url_key))).build());
                                }


                            }
                        }
                    }
                } catch (RetrofitError e) {
                    Log.d(LOG_TAG, "error kind : " + e.getKind().name());

                }

            }

            return newTrackList;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            if (null != tracks) {

                // if no artist was found then display message
                if (tracks.isEmpty())
                    Toast.makeText(getActivity(), getString(R.string.no_top_tracks), Toast.LENGTH_LONG).show();
                else
                    topTracksAdapter.addAll(tracks);
            }
        }
    }
}
