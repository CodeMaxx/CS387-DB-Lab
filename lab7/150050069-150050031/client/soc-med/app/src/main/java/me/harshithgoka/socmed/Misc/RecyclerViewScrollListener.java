package me.harshithgoka.socmed.Misc;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import me.harshithgoka.socmed.Adapters.PostAdapter;

/**
 * Created by harshithgoka on 12/10/17.
 */

public class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    public static final String TAG = RecyclerViewScrollListener.class.getName();

    LinearLayoutManager linearLayoutManager;
    public RecyclerViewScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        PostAdapter adapter = (PostAdapter) recyclerView.getAdapter();
        if ( newState == RecyclerView.SCROLL_STATE_SETTLING && linearLayoutManager != null && adapter != null) {
            int totalNum = linearLayoutManager.getItemCount();
            int currNum = linearLayoutManager.getChildCount();
            int currFirstPos = linearLayoutManager.findFirstVisibleItemPosition();

            if ( (currFirstPos + currNum >= totalNum) && !( (PostAdapter) recyclerView.getAdapter()).loading ) {
                adapter.loading = true;
                if (adapter.type == Constants.POSTS_TYPE.FEED) {
                    ( (PostAdapter) recyclerView.getAdapter() ).loadMore(-1);
                    Snackbar.make(recyclerView.getRootView(), "Checking for new posts", Snackbar.LENGTH_LONG).show();
                }
                else {
                    ( (PostAdapter) recyclerView.getAdapter() ).loadMore(totalNum);
                    Snackbar.make(recyclerView.getRootView(), "Checking for any older posts", Snackbar.LENGTH_LONG).show();
                }
            }
        }

        if (newState == RecyclerView.SCROLL_STATE_DRAGGING && linearLayoutManager != null && adapter != null) {
            int totalNum = linearLayoutManager.getItemCount();
            int currNum = linearLayoutManager.getChildCount();
            int currFirstPos = linearLayoutManager.findFirstVisibleItemPosition();

            if (adapter.type == Constants.POSTS_TYPE.FEED) {
                if ( currFirstPos <= 0 ) {
                    ((PostAdapter) recyclerView.getAdapter()).loadMore(totalNum);
                    Snackbar.make(recyclerView.getRootView(), "Loading older posts", Snackbar.LENGTH_LONG).show();
                }
            }
        }

//        Log.d(TAG, "ScrollState - " + newState);
//        switch (newState) {
//            case RecyclerView.SCROLL_STATE_SETTLING:
//                Log.d(TAG, "ScrollState - Settling");
//                break;
//
//            case RecyclerView.SCROLL_STATE_IDLE:
//                Log.d(TAG, "ScrollState - Idle");
//                break;
//
//            case RecyclerView.SCROLL_STATE_DRAGGING:
//                Log.d(TAG, "ScrollState - Dragging");
//                break;
//        }
    }
}
