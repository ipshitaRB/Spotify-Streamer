package com.example.ipshita.mymasterdetailtestapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ipshita.mymasterdetailtestapplication.Util.ServiceUtil;
import com.example.ipshita.mymasterdetailtestapplication.models.Artist;
import com.example.ipshita.mymasterdetailtestapplication.models.Track;

import java.util.ArrayList;


/**
 * An activity representing a list of Top Tracks. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ArtistDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ArtistListFragment} and the item details
 * (if present) is a {@link ArtistDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ArtistListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ArtistListActivity extends AppCompatActivity
        implements ArtistListFragment.Callbacks, ArtistDetailFragment.TopTrackCallback, MusicPlayDialogFragment.iMusicPlayDialogListener {

    FragmentManager fm = getSupportFragmentManager();
    MusicPlayDialogFragment dialog;

    MenuItem nowPlayingMenuItem;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);
        Intent intent = getIntent();
        ActionBar ab = getSupportActionBar();

            ab.setTitle(getString(R.string.top_tracks_title));



        if (findViewById(R.id.artist_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.

            // TODO this may not be required

            /*((ArtistListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.artist_list))
                    .setActivateOnItemClick(true);*/
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link ArtistListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(Artist artist) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(ArtistDetailFragment.ARTIST_ID, artist);
            ArtistDetailFragment fragment = new ArtistDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.artist_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ArtistDetailActivity.class);
            detailIntent.putExtra(ArtistDetailFragment.ARTIST_ID, artist);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onTopTrackSelected(ArrayList<Track> trackList, int trackPosition) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            /*Bundle arguments = new Bundle();
            arguments.putParcelable(ArtistDetailFragment.ARTIST_ID, artist);
            ArtistDetailFragment fragment = new ArtistDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.artist_detail_container, fragment)
                    .commit();*/


            dialog = MusicPlayDialogFragment.newInstance(trackList, trackPosition);
            dialog.show(fm, "fragment_music_play");


        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, MusicPlayAcitvity.class);
            detailIntent.putParcelableArrayListExtra(getString(R.string.tracklist_key), trackList);
            detailIntent.putExtra(getString(R.string.track_position),trackPosition);
            startActivity(detailIntent);
        }
    }


    @Override
    public void onTrackCompleted() {
        nowPlayingMenuItem.setVisible(false);
        dialog.dismiss();
    }

    @Override
    public void onTrackStarted() {
       nowPlayingMenuItem.setVisible(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
        nowPlayingMenuItem = menu.findItem(R.id.action_now_playing);
        if (ServiceUtil.isServiceRunning(MusicPlayerService.class, this))
            nowPlayingMenuItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_now_playing:
                if (ServiceUtil.isServiceRunning(MusicPlayerService.class,this) ){
                    if (mTwoPane) {
                        // In two-pane mode, show the detail view in this activity by
                        // adding or replacing the detail fragment using a
                        // fragment transaction.
            /*Bundle arguments = new Bundle();
            arguments.putParcelable(ArtistDetailFragment.ARTIST_ID, artist);
            ArtistDetailFragment fragment = new ArtistDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.artist_detail_container, fragment)
                    .commit();*/


                       if (null!= dialog)
                        dialog.show(fm, "fragment_music_play");


                    } else {
                        // In single-pane mode, simply start the detail activity
                        // for the selected item ID.
                        Intent detailIntent = new Intent(this, MusicPlayAcitvity.class);/*
                        detailIntent.putParcelableArrayListExtra(getString(R.string.tracklist_key), trackList);
                        detailIntent.putExtra(getString(R.string.track_position),trackPosition);*/
                        startActivity(detailIntent);
                    }
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
