package me.harshithgoka.socmed.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.List;

import me.harshithgoka.socmed.Adapters.PostAdapter;
import me.harshithgoka.socmed.Adapters.SearchAdapter;
import me.harshithgoka.socmed.Misc.Constants;
import me.harshithgoka.socmed.Misc.RecyclerViewScrollListener;
import me.harshithgoka.socmed.Network.NetworkService;
import me.harshithgoka.socmed.R;
import me.harshithgoka.socmed.Storage.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends CommonFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = SearchFragment.class.getName();

    private int sectionNo;

    RelativeLayout followButton;
    ProfileFragment profileFragment;

    Handler handler;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param i Section Number.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(int i) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, i);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sectionNo = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);


        AutoCompleteTextView textView = rootView.findViewById(R.id.search_bar);
        SearchAdapter searchAdapter = new SearchAdapter(getContext());
        textView.setAdapter(searchAdapter);


        followButton = rootView.findViewById(R.id.follow_button);
        followButton.setVisibility(View.GONE);

        swipeRefreshLayout = rootView.findViewById(R.id.profileswiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        return rootView;
    }

    class FollowOnClickListener implements View.OnClickListener {

        User user;
        FollowOnClickListener (User user) {
            this.user = user;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), NetworkService.class);
            if ( ( (TextView) view.findViewById(R.id.follow_button_text)).getText().toString().equals("Follow") )
                intent.putExtra(Constants.WHAT, Constants.FOLLOW);
            else
                intent.putExtra(Constants.WHAT, Constants.UNFOLLOW);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.USER_DATA, user);

            intent.putExtra(Constants.INTENT_DATA, bundle);
            getContext().startService(intent);

            view.findViewById(R.id.follow_button_text).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.follow_progress).setVisibility(View.VISIBLE);
        }
    }

    class SnackbarCallback extends Snackbar.Callback {
        int status, action;
        RelativeLayout followButton;
        SnackbarCallback(RelativeLayout followButton, int action, int status) {
            this.followButton = followButton;
            this.action = action;
            this.status = status;
        }

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);

            ProgressBar progressBar = followButton.findViewById(R.id.follow_progress);
            TextView textView = followButton.findViewById(R.id.follow_button_text);
            ImageView doneImageView = followButton.findViewById(R.id.done);
            ImageView errorImageView = followButton.findViewById(R.id.error);

            textView.setVisibility(View.VISIBLE);

            if ( (action == Constants.FOLLOW && status == Constants.FALSE) || (action == Constants.UNFOLLOW && status == Constants.TRUE) )
                textView.setText("Follow");
            else
                textView.setText("UnFollow");

            progressBar.setVisibility(View.GONE);
            doneImageView.setVisibility(View.GONE);
            errorImageView.setVisibility(View.GONE);

        }
    }

    public void FollowCallback(int action, int status, Bundle bundle) {
        User mUser = (User) bundle.getSerializable(Constants.USER_DATA);

        if (adapter != null && mUser.uid.equals(adapter.user.uid)) {
            ProgressBar progressBar = followButton.findViewById(R.id.follow_progress);
            TextView textView = followButton.findViewById(R.id.follow_button_text);
            ImageView doneImageView = followButton.findViewById(R.id.done);
            ImageView errorImageView = followButton.findViewById(R.id.error);

            textView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.GONE);
            doneImageView.setVisibility(View.GONE);
            Snackbar snackbar;
            if (status == Constants.TRUE) {
                snackbar = Snackbar.make(getView().getRootView(), ((action == Constants.FOLLOW) ? "" : "Un") + "Follow Successful", Snackbar.LENGTH_SHORT);
                doneImageView.setVisibility(View.VISIBLE);
            }
            else {
                if (status == Constants.FALSE) {
                    errorImageView.setVisibility(View.VISIBLE);
                    snackbar = Snackbar.make(getView().getRootView(), "Couldn't " + ((action == Constants.FOLLOW) ? "" : "un")  + "follow this person", Snackbar.LENGTH_SHORT);
                }
                else {
                    errorImageView.setVisibility(View.VISIBLE);
                    if (action == Constants.FOLLOW)
                        snackbar = Snackbar.make(getView().getRootView(), "Already following this person", Snackbar.LENGTH_SHORT);
                    else
                        snackbar = Snackbar.make(getView().getRootView(), "Not following this person", Snackbar.LENGTH_SHORT);
                }
            }
            snackbar.addCallback(new SnackbarCallback(followButton, action, status));
            snackbar.show();
        }
    }


    public void SetPosts(Bundle obj) {
        User user = (User) obj.getSerializable(Constants.USER_DATA);
        String postsStr = obj.getString(Constants.POSTS);

        JsonParser jsonParser = new JsonParser();
        JsonArray jsonElements = jsonParser.parse(postsStr).getAsJsonArray();

        if (getView() == null) {
            return;
        }
        recyclerView = getView().getRootView().findViewById(R.id.profile_recycler);
        swipeRefreshLayout = getView().getRootView().findViewById(R.id.profileswiperefresh);
        swipeRefreshLayout.setRefreshing(false);
        if (adapter != null && adapter.user != null && adapter.user.uid.equals(user.uid)) {
            adapter.addToDataset(jsonElements, obj.getInt(Constants.OFFSET));
        }
        else {

            ((TextView) getView().getRootView().findViewById(R.id.name)).setText(user.name);
            ((TextView) getView().getRootView().findViewById(R.id.username)).setText("(@" + user.uid + ")");

            ((AutoCompleteTextView) getView().getRootView().findViewById(R.id.search_bar)).setText("");
            followButton = getView().getRootView().findViewById(R.id.follow_button);
            followButton.setVisibility(View.VISIBLE);
            if (user.following) {
                ((TextView) followButton.findViewById(R.id.follow_button_text)).setText("UnFollow");
            }
            else {
                ((TextView) followButton.findViewById(R.id.follow_button_text)).setText("Follow");
            }
            followButton.setOnClickListener(new FollowOnClickListener(user));

            InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(((AutoCompleteTextView) getView().getRootView().findViewById(R.id.search_bar)).getWindowToken(), 0);

            if (adapter == null) {

                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (adapter != null) {
                            adapter.refreshDataset();
                        }
                    }
                });


                adapter = new PostAdapter(getContext(), jsonElements, Constants.POSTS_TYPE.USER_POSTS, user);

                linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addOnScrollListener(new RecyclerViewScrollListener(linearLayoutManager));

//                recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//                    @Override
//                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                        adapter.loadMore(totalItemsCount);
//                    }
//                });

            } else {
                adapter.user = user;
                adapter.setData(jsonElements);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

}
