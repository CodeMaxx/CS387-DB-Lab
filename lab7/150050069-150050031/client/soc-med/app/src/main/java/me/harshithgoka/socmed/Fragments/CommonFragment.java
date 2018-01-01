package me.harshithgoka.socmed.Fragments;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import me.harshithgoka.socmed.Adapters.CommentAdapter;
import me.harshithgoka.socmed.Adapters.PostAdapter;
import me.harshithgoka.socmed.Misc.Constants;
import me.harshithgoka.socmed.R;

/**
 * Created by harshithgoka on 05/10/17.
 */

public class CommonFragment extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    PostAdapter adapter;
    LinearLayoutManager linearLayoutManager;


    public void stopRefresh() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    public void WriteComment(boolean success, Bundle bundle) {
        View rootView = linearLayoutManager.findViewByPosition(bundle.getInt(Constants.POST_POS));
        RecyclerView recyclerView1 = rootView.findViewById(R.id.comments_recycler);
        CommentAdapter commentAdapter = ( (CommentAdapter) recyclerView1.getAdapter());
        commentAdapter.AddComment(bundle);


        Button button = rootView.findViewById(R.id.more);
        if ( commentAdapter.comments.size() > 3 && commentAdapter.more) {
            button.setVisibility(View.VISIBLE);
        }
        else {
            button.setVisibility(View.GONE);
        }

        ImageView imageView = (ImageView) rootView.findViewById(R.id.comment_img);
        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.comment_progress);
        TextInputEditText editText = (TextInputEditText) rootView.findViewById(R.id.write_comment);

        imageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        if (success) {
            editText.setText("");
            Toast.makeText(getContext(), "Comment Successful", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), "Comment unsuccessful. Please contact us on our website.", Toast.LENGTH_LONG).show();
        }
    }
}
