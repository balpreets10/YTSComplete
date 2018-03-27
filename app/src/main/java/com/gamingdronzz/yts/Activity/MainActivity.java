package com.gamingdronzz.yts.Activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gamingdronzz.yts.Adapter.RecyclerViewAdapterRight;
import com.gamingdronzz.yts.CustomItems.CardDrawerLayout;
import com.gamingdronzz.yts.Fragments.FragmentHome;
import com.gamingdronzz.yts.Models.MovieCardModel;
import com.gamingdronzz.yts.R;
import com.gamingdronzz.yts.Tools.Helper;
import com.gamingdronzz.yts.Tools.PreferencesManager;
import com.gamingdronzz.yts.Tools.VolleyHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView rightTitle;
    boolean isList = false;
    FrameLayout frameLayout;
    CardDrawerLayout drawer;
    VolleyHelper volleyHelper;
    final String TAG = "Main";
    private List<MovieCardModel> modelListRecent;
    private List<MovieCardModel> modelListFavorites;
    RecyclerViewAdapterRight adapter;
    RecyclerView recyclerView;
    PreferencesManager preferencesManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferencesManager = new PreferencesManager(this);
        rightTitle = findViewById(R.id.title_right);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_main);

        navigationView.setNavigationItemSelectedListener(this);
        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setRadius(Gravity.START, 35);
        drawer.setViewElevation(Gravity.START, 20);
        frameLayout = findViewById(R.id.contentPanel);
        onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
        init();

    }

    private void init() {
        recyclerView = findViewById(R.id.recycler_view_right);
        modelListRecent = new ArrayList<>();
        modelListFavorites = new ArrayList<>();
        adapter = new RecyclerViewAdapterRight(modelListRecent);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        CardDrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the FragmentHome/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_change_view:
                changeView(item);
                return true;
            case R.id.action_favorites:
                showFavorites();
                rightTitle.setText("Favorites");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    rightTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(AppCompatResources.getDrawable(this, R.drawable.ic_favorite_border_black_24dp), null, null, null);
                    rightTitle.setCompoundDrawablePadding(10);
                }
                return true;
            case R.id.action_recently_viewed:
                showRecentlyViewed();
                rightTitle.setText("Recents");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    rightTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(AppCompatResources.getDrawable(this, R.drawable.ic_access_recent_24dp), null, null, null);
                    rightTitle.setCompoundDrawablePadding(10);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeView(MenuItem item) {
        Log.d(TAG, "Changing to " + isList);
        if (isList) {
            item.setIcon(R.drawable.ic_list_black_24dp);
            isList = false;
        } else {
            item.setIcon(R.drawable.ic_grid_black_24dp);
            isList = true;
        }
    }

    private void showFavorites() {
        if(modelListFavorites!=null) {
            modelListFavorites.clear();
        }
        adapter.notifyDataSetChanged();
        drawer.openDrawer(Gravity.END);
        Object[][] MovieCardArray = preferencesManager.getFavorites();
        AddToList(MovieCardArray,modelListFavorites);

    }

    private void AddToList(Object[][] MovieCardArray, List<MovieCardModel> modelList)
    {
        if(modelList==null)
        {
            modelList = new ArrayList<>();
        }
        modelList.clear();

        Object[] MovieIds,MovieTitles,MovieYears,MovieCoverURL,MovieTime;

        MovieIds = MovieCardArray[0];
        MovieTitles = MovieCardArray[1];
        MovieYears = MovieCardArray[2];
        MovieTime = MovieCardArray[3];
        MovieCoverURL = MovieCardArray[4];

        if(MovieIds!=null) {
            int length = MovieIds.length;
            for (int i = 0; i < length; i++) {
                Log.d(TAG,"Recent ID = " + MovieIds[i].toString());
                Log.d(TAG,"Recent Title = " + MovieTitles[i].toString());
                Log.d(TAG,"Recent Year = " + MovieYears[i].toString());
                Log.d(TAG,"Recent Time = " + MovieTime[i].toString());
                Log.d(TAG,"Recent Cover = " + MovieCoverURL[i].toString());
//                MovieCardModel movieCardModel = new MovieCardModel();
//                movieCardModel.setMovieID(MovieIds[i].toString());
//                movieCardModel.setMovieTitle(MovieTitles[i].toString());
//                movieCardModel.setMovieReleaseYear(MovieYears[i].toString());
//                movieCardModel.setMovieTime(MovieTime[i].toString());
//                movieCardModel.setMovieCoverURL(MovieCoverURL[i].toString());
//                modelList.add(movieCardModel);

            }
            adapter.notifyDataSetChanged();
        }

    }

    private void showRecentlyViewed() {
        modelListRecent.clear();
        adapter.notifyDataSetChanged();
        drawer.openDrawer(Gravity.END);
        //volleyHelper = new VolleyHelper(this, this);
//        Object[] MovieIds = preferencesManager.getRecents();
//        if(MovieIds!=null) {
//            int length = MovieIds.length;
//
//            for (int i = 0; i < length; i++) {
//                String movieid = MovieIds[i].toString();
//                Log.d(TAG,"ID = " + movieid);
//                volleyHelper.makeStringRequest(Helper.getInstance().buildQueryByMovieID(movieid), "Recent-" + movieid);
//            }
//        }

        Object[][] MovieCardArray = preferencesManager.getRecents();
        AddToList(MovieCardArray,modelListRecent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        Bundle bundle;
        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            fragment = new FragmentHome();
            getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
        }
        if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public void onError(VolleyError volleyError) {
//
//    }
//
//    @Override
//    public void onResponse(String str, String tag) {
//
//        Log.d(TAG, "Response = " + str.toString());
//        if (str != null) {
//
//            try {
//                JSONObject jsonObject = new JSONObject(str);
//                //Log.d(TAG, "Status = " + jsonObject.getString(Helper.getInstance().getTorrentAPIKey(Helper.API_KEY.STATUS)));
//                JSONObject data = new JSONObject(jsonObject.getString(Helper.getInstance().getTorrentAPIKey(Helper.API_KEY.DATA)));
//                JSONObject movie = data.getJSONObject(Helper.getInstance().getTorrentAPIKey(Helper.API_KEY.MOVIE));
//                MovieData movieData = Helper.getInstance().createMovieData(movie);
//                Log.d(TAG,"Adding movie - " + movieData.getTitle());
//                if(tag.contains("Fav-"))
//                {
//                    modelListFavorites.add(0,movieData);
//                }
//                else if (tag.contains("Recent-"))
//                {
//                    modelListRecent.add(0,movieData);
//                }
//                adapter.notifyItemInserted(0);
//            } catch (JSONException jse) {
//                jse.printStackTrace();
//            }
//        }
//    }
}
