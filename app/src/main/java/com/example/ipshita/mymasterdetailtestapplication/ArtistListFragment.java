package com.example.ipshita.mymasterdetailtestapplication;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ipshita.mymasterdetailtestapplication.Util.NetworkUtil;
import com.example.ipshita.mymasterdetailtestapplication.adapters.ArtistAdapter;
import com.example.ipshita.mymasterdetailtestapplication.dummy.DummyContent;
import com.example.ipshita.mymasterdetailtestapplication.models.Artist;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * A list fragment representing a list of Top Tracks. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ArtistDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ArtistListFragment extends Fragment {

    // Declare List of artists
    public ArrayList<Artist> artistList;

    // Declare custom Adapter for Artist results
    public ArtistAdapter artistAdapter;
    String searchKeyword;
    // Declare SpotifyApi object
    private SpotifyApi spotifyApi;
    // Declare SpotifyService object
    private SpotifyService spotifyService;



    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Artist artist);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Artist artist) {
        }
    };



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistListFragment() {
        // initialize list of artists
        artistList = new ArrayList<Artist>();
        // initialize spotify api object
        spotifyApi = new SpotifyApi();
        // initialize spotify service object
        spotifyService = spotifyApi.getService();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: replace with a real list adapter.
       /* setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                DummyContent.ITEMS));*/
        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.artist_parcel_key))) {
            artistList.clear();
        } else {
            artistList = savedInstanceState.getParcelableArrayList(getString(R.string.artist_parcel_key));
        }
    }

   /* @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

  /*  @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
      //  mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
    }
*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.artist_parcel_key), artistList);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

   /* *//**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     *//*
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }
*//*
    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //declare and initialize rootview
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final SearchView searchText = (SearchView) rootView.findViewById(R.id.searchText);

        searchText.setIconifiedByDefault(false);
        searchText.setQueryHint(getResources().getString(R.string.search_artist_hint));
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchKeyword = searchText.getQuery().toString();
                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    FetchArtistTask task = new FetchArtistTask();
                    task.execute(searchKeyword);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }

                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        // initialize custom adapter with the list of artists

        artistAdapter = new ArtistAdapter(getActivity(),artistList);

        // initialize artist list view
        ListView artistListView = (ListView) rootView.findViewById(R.id.artist_listview);
        // set custom adapter to the list view
        artistListView.setAdapter(artistAdapter);

        // add onclick action for listview item
        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //start top ten track activity and pass artist id (to query spotify service for top tracks )and artist name(to show in the action bar)
                /*String artistId = artistAdapter.getItem(position).getArtistId();
                String artistName = artistAdapter.getItem(position).getArtistName();
                Intent topTracksIntent = new Intent(getActivity(), TopTracksActivity.class);
                topTracksIntent.putExtra(Intent.EXTRA_TEXT, artistId);
                topTracksIntent.putExtra(getString(R.string.artist_name_key), artistName);
                startActivity(topTracksIntent);*/
                // TODO send artist object to activity
                mCallbacks.onItemSelected(artistAdapter.getItem(position));

            }
        });

        return rootView;
    }

    /**
     * Created by Ipshita on 09-07-2015.
     */
    public class FetchArtistTask extends AsyncTask<String, Void, List<Artist>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();


        // Create a list of Artists
        List<Artist> artists = new ArrayList<Artist>();

        String artistToSearch;
        // find total number of search results
        int numberOfResults = 0;

        String artistName;
        String artistThumbnailLink;
        String artistId;

        List<kaaes.spotify.webapi.android.models.Artist> artistResultList;

        kaaes.spotify.webapi.android.models.Artist currentArtist;

        List<kaaes.spotify.webapi.android.models.Image> currentArtistImageList;

        ArtistsPager results;

        @Override
        protected List<Artist> doInBackground(String... params) {
            artistToSearch = params[0];

            try {
                results = spotifyService.searchArtists(artistToSearch);
                // clear artist list
                artists.clear();

                artistResultList = results.artists.items;


                if (null != artistResultList && !artistResultList.isEmpty()) {
                    numberOfResults = artistResultList.size();
                    for (int i = 0; i < numberOfResults; i++) {

                        if (null != artistResultList.get(i)) {

                            currentArtist = artistResultList.get(i);
                            artistName = currentArtist.name;
                            currentArtistImageList = currentArtist.images;
                            artistId = currentArtist.id;

                            if (null != currentArtistImageList && !currentArtistImageList.isEmpty() && null != currentArtistImageList.get(0)) {
                                artistThumbnailLink = currentArtistImageList.get(0).url;
                            }

                            artists.add(new Artist.Builder().artistName(artistName).artistThumbnailLink(artistThumbnailLink).artistId(artistId).build());

                        }

                    }
                }

            } catch (RetrofitError e) {
                Log.d(LOG_TAG, "error kind : " + e.getKind().name());

            }


            return artists;
        }

        @Override
        protected void onPostExecute(List<Artist> artistResult) {
            if (null != artistResult) {
                artistAdapter.clear();
                // if no artist was found then display message
                if (artistResult.isEmpty())
                    Toast.makeText(getActivity(), getString(R.string.no_result_found) + "\"" + artistToSearch + "\". " + getString(R.string.no_result_found_suggestion), (Toast.LENGTH_LONG)).show();
                else
                    artistAdapter.addAll(artistResult);
            }
        }
    }
}
