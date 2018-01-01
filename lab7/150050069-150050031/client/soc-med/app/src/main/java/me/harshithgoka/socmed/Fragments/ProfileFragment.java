package me.harshithgoka.socmed.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import me.harshithgoka.socmed.Activities.LoginActivity;
import me.harshithgoka.socmed.Adapters.PostAdapter;
import me.harshithgoka.socmed.Misc.Constants;
import me.harshithgoka.socmed.Misc.RecyclerViewScrollListener;
import me.harshithgoka.socmed.Network.MyCookieStore;
import me.harshithgoka.socmed.Network.NetworkService;
import me.harshithgoka.socmed.R;
import me.harshithgoka.socmed.Storage.UserStorage;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends CommonFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = ProfileFragment.class.getName();

    // TODO: Rename and change types of parameters
    private int itemNumber;



    private OnFragmentInteractionListener mListener;

    public static JsonArray myPosts;
    Button logout;


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param itemNumber Item Number.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(int itemNumber, Context context) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, itemNumber);
        fragment.setArguments(args);

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(Constants.WHAT, Constants.GET_MY_POSTS);
        context.startService(intent);


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    public void stopRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view.getRootView(), "Person Info", Snackbar.LENGTH_SHORT).show();
            }
        });

        TextView textView = rootView.findViewById(R.id.name);
        textView.setText(UserStorage.getName());

        logout = (Button) rootView.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyCookieStore(getContext()).removeAll();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.putExtra(Constants.INTENT_DATA, Constants.GET_NETWORK_STATE);
                startActivity(intent);
                getActivity().finish();
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.profile_recycler);
        if (adapter == null)
            adapter = new PostAdapter(getContext(), ProfileFragment.myPosts, Constants.POSTS_TYPE.MY_POSTS);

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerViewScrollListener(linearLayoutManager));
//        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                adapter.loadMore(totalItemsCount);
//            }
//        });



        swipeRefreshLayout = rootView.findViewById(R.id.profileswiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "Refresh received");
                adapter.refreshDataset();
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setData(Bundle bundle) {

        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = jsonParser.parse(bundle.getString(Constants.POSTS)).getAsJsonArray();

        int offset = bundle.getInt(Constants.OFFSET);
        myPosts = jsonArray;
        if (adapter != null) {
            adapter.addToDataset(jsonArray, offset);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
