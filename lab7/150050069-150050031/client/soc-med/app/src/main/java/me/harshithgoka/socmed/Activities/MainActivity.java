package me.harshithgoka.socmed.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.net.CookieManager;

import me.harshithgoka.socmed.Fragments.CommonFragment;
import me.harshithgoka.socmed.Fragments.MainFragment;
import me.harshithgoka.socmed.Fragments.ProfileFragment;
import me.harshithgoka.socmed.Fragments.SearchFragment;
import me.harshithgoka.socmed.Misc.Constants;
import me.harshithgoka.socmed.Network.MyCookieStore;
import me.harshithgoka.socmed.Network.NetworkService;
import me.harshithgoka.socmed.R;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Handler mHandler;

    private CookieManager cookieManager;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cookieManager = new CookieManager();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == Constants.GET_NETWORK_STATE) {
                    Constants.NETWORK_STATE state = (Constants.NETWORK_STATE) msg.obj;
                    if (state == Constants.NETWORK_STATE.NOT_LOGGED_IN) {
                        new MyCookieStore(getApplicationContext()).removeAll();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra(Constants.INTENT_DATA, Constants.GET_NETWORK_STATE);
                        startActivity(intent);
                        finish();
                    }
                    else if (state == Constants.NETWORK_STATE.NOT_CONNECTED) {
                        ( (CommonFragment) mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem())).stopRefresh();
                        Snackbar snackbar = Snackbar.make(mViewPager.getRootView(), "You are not connected to the Internet", Snackbar.LENGTH_LONG);
                        snackbar.setAction("WiFi Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        });
                        snackbar.show();
                    }
                }
                else if (msg.what == Constants.GET_FEED) {
                    ( (MainFragment) mSectionsPagerAdapter.getItem(0)).setData((Bundle) msg.obj);
                }
                else if (msg.what == Constants.GET_MY_POSTS) {
                    ( (ProfileFragment) mSectionsPagerAdapter.getItem(2)).setData((Bundle) msg.obj);
                }
                else if (msg.what == Constants.GET_USER_POSTS) {
                    ((SearchFragment) mSectionsPagerAdapter.getItem(1)).SetPosts((Bundle) msg.obj);
                }
                else if (msg.what == Constants.WRITE_POST) {
                    if (msg.arg1 == Constants.TRUE) {
                        ((MainFragment) mSectionsPagerAdapter.getItem(0)).WritePost(true);
                    }
                    else if (msg.arg1 == Constants.FALSE) {
                        ((MainFragment) mSectionsPagerAdapter.getItem(0)).WritePost(false);
                    }
                }
                else if (msg.what == Constants.WRITE_COMMENT) {
                    Bundle bundle = (Bundle) msg.obj;
                    Constants.POSTS_TYPE type = (Constants.POSTS_TYPE) bundle.getSerializable(Constants.SRC_FRAGMENT);
                    if (type == Constants.POSTS_TYPE.FEED) {
                        if (msg.arg1 == Constants.TRUE) {
                            ((MainFragment) mSectionsPagerAdapter.getItem(0)).WriteComment(true, bundle);
                        } else if (msg.arg1 == Constants.FALSE) {
                            ((MainFragment) mSectionsPagerAdapter.getItem(0)).WriteComment(false, bundle);
                        }
                    }
                    else if (type == Constants.POSTS_TYPE.MY_POSTS) {
                        if (msg.arg1 == Constants.TRUE) {
                            ((ProfileFragment) mSectionsPagerAdapter.getItem(2)).WriteComment(true, bundle);
                        } else if (msg.arg1 == Constants.FALSE) {
                            ((ProfileFragment) mSectionsPagerAdapter.getItem(2)).WriteComment(false, bundle);
                        }
                    }
                    else if (type == Constants.POSTS_TYPE.USER_POSTS) {
                        if (msg.arg1 == Constants.TRUE) {
                            ((SearchFragment) mSectionsPagerAdapter.getItem(1)).WriteComment(true, bundle);
                        } else if (msg.arg1 == Constants.FALSE) {
                            ((SearchFragment) mSectionsPagerAdapter.getItem(1)).WriteComment(false, bundle);
                        }
                    }
                }
                else if (msg.what == Constants.FOLLOW || msg.what == Constants.UNFOLLOW) {
                    ( (SearchFragment) mSectionsPagerAdapter.getItem(1)).FollowCallback(msg.what, msg.arg1 ,(Bundle) msg.obj);
                }
            }
        };

        Constants.currHandler = mHandler;

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(getApplicationContext(), NetworkService.class);
        intent.putExtra(Constants.WHAT, Constants.GET_NETWORK_STATE);
        startService(intent);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        int count = 3;

        MainFragment mainFragment;
        SearchFragment searchFragment;
        ProfileFragment profileFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mainFragment = MainFragment.newInstance(1, getApplicationContext());
            searchFragment = SearchFragment.newInstance(2);
            profileFragment = ProfileFragment.newInstance(3, getApplicationContext());
        }



        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a MainFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return mainFragment;
                case 1:
                    return searchFragment;
                case 2:
                    return profileFragment;
                default:
                    return mainFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return count;
        }
    }
}
