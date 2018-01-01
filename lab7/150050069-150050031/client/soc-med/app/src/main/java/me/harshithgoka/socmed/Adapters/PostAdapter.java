package me.harshithgoka.socmed.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.harshithgoka.socmed.Misc.Constants;
import me.harshithgoka.socmed.Misc.Utils;
import me.harshithgoka.socmed.Network.NetworkService;
import me.harshithgoka.socmed.R;
import me.harshithgoka.socmed.Storage.DiskCache;
import me.harshithgoka.socmed.Storage.Post;
import me.harshithgoka.socmed.Storage.User;

/**
 * Created by harshithgoka on 05/10/17.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public static final String TAG = PostAdapter.class.getName();

    public String[] dummy_dataset;
    public JsonArray data = null;
    Context context;
    public Constants.POSTS_TYPE type;
    public User user;
    ProgressBar imageLoading;
    boolean clearNext = false;

    public boolean loading = true;
    RecyclerView recyclerView;


    public void setData(JsonArray dataset) {
        if (dataset != null) {
            data = null;
            addToDataset(dataset, 0);
        }
    }

    public void setRecyclerView (RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }


    // / Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RecyclerView recyclerView;
        public CommentAdapter commentAdapter;
        public LinearLayout mLin;
        public boolean more;
        public Uri imageUri;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLin = v;
            more = true;
            imageUri = null;
        }

        public void setImage(Bitmap bitmap) {
            ImageView imageView = null;
            mLin.findViewById(R.id.image_loading).setVisibility(View.GONE);
            imageView = mLin.findViewById(R.id.image);
            if (mLin != null && imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            else if (imageView != null){
                imageView.setImageResource(0);
            }
        }
    }

    class AddDatasetAsyncTask extends AsyncTask<JsonArray, Void, Boolean> {

        int offset;
        AddDatasetAsyncTask(int offset) {
            this.offset = offset;
        }

        @Override
        protected Boolean doInBackground(JsonArray... params) {
            JsonArray dataset = params[0];
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Post>>() {}.getType();

            List<Post> posts = gson.fromJson(dataset, listType);
            if (type == Constants.POSTS_TYPE.FEED) {
                Collections.sort(posts, new Comparator<Post>() {
                    @Override
                    public int compare(Post post, Post t1) {
                        return post.timestamp.compareTo(t1.timestamp);
                    }
                });
            }
            else {
                Collections.sort(posts, new Comparator<Post>() {
                    @Override
                    public int compare(Post post, Post t1) {
                        return -post.timestamp.compareTo(t1.timestamp);
                    }
                });
            }
            JsonParser jsonParser = new JsonParser();
            dataset = jsonParser.parse(gson.toJson(posts)).getAsJsonArray();

            if (offset == -1) {
                if (data != null && data.size() > 0) {
                    for (int i = 0; i != dataset.size(); i++) {
                        data.add(dataset.get(i));
                    }
                }
                else {
                    data = dataset;
                }
            }
            else {
                if (data != null && data.size() > 0) {

                    if (type == Constants.POSTS_TYPE.FEED) {
                        if (data.get(0).getAsJsonObject().get("timestamp").getAsString().compareTo(dataset.get(dataset.size() - 1).getAsJsonObject().get("timestamp").getAsString()) > 0) {
                            for (int i = 0; i != data.size(); i++) {
                                dataset.add(data.get(i));
                            }
                            data = dataset;
                            return true;
                        }
                    }

                    JsonArray firstPart = new JsonArray();
                    for (int i = 0; i != offset; i ++)
                        if (data.size() > 0)
                            firstPart.add(data.remove(0));


                    for (int i = 0; i != dataset.size(); i++) {
                        if (data.size() > 0)
                            data.remove(0);
                        else
                            break;
                    }

                    for (int i = 0; i != dataset.size(); i++)
                        firstPart.add(dataset.get(i));

                    for (int i = 0; i != data.size(); i++)
                        firstPart.add(data.get(i));

                    data = firstPart;
                }
                else {
                    data = new JsonArray();
                    for (int i = 0; i != dataset.size(); i++)
                        data.add(dataset.get(i));
                }
            }

//        Log.d(TAG, type.toString());

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            notifyDataSetChanged();
            loading = false;
        }
    }

    public void addToDataset(JsonArray dataset, int offset) {

        if (clearNext) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Post>>() {}.getType();

            List<Post> posts = gson.fromJson(dataset, listType);
            if (type == Constants.POSTS_TYPE.FEED) {
                Collections.sort(posts, new Comparator<Post>() {
                    @Override
                    public int compare(Post post, Post t1) {
                        return post.timestamp.compareTo(t1.timestamp);
                    }
                });
            }
            else {
                Collections.sort(posts, new Comparator<Post>() {
                    @Override
                    public int compare(Post post, Post t1) {
                        return -post.timestamp.compareTo(t1.timestamp);
                    }
                });
            }
            JsonParser jsonParser = new JsonParser();
            dataset = jsonParser.parse(gson.toJson(posts)).getAsJsonArray();


            if (type == Constants.POSTS_TYPE.FEED && dataset != null) {
                if (offset != -1) {
                    if (data != null)
                        for (int i = 0; i != data.size(); i++)
                            dataset.add(data.get(i));
                    data = dataset;
                    notifyDataSetChanged();
                }
                else {
                    clearNext = false;
                    data = dataset;
                    notifyDataSetChanged();
                }
            }
            else {
                if (offset == 0) {
                    clearNext = false;
                }
                data = dataset;
                notifyDataSetChanged();
            }
            loading = false;
        }
        else if ( (dataset == null || dataset.size() == 0)){
            if (offset == 0) {
                if (data != null ) {
                    data = null;
                }
            }
            notifyDataSetChanged();
            loading = false;
        }
        else {
            AddDatasetAsyncTask task = new AddDatasetAsyncTask(offset);
            task.execute(dataset);
        }
//        notifyDataSetChanged();
//        loading = false;
    }

//    public void addDataToDataset (JsonArray extraData) {
//        data.addAll(extraData);
//        notifyDataSetChanged();
//    }

    public PostAdapter(Context context, JsonArray jsonElements, Constants.POSTS_TYPE userPosts, User user) {
        this.type = userPosts;
        this.context = context;
        setData(jsonElements);
        this.user = user;
    }

    public PostAdapter(Context context, JsonArray argdata, Constants.POSTS_TYPE type) {
        this.type = type;
        this.context = context;
        setData(argdata);

        dummy_dataset = new String[]{"Zhang", "Hey! What's up? Ya dawg.",
                "Dude", "I'm good dawg.",
                "Zhang", "Noice! This is just me wanting to write a big passage to fill up the space, I know I could use lorem ipsum but I don't understand Latin or whatever language that is in. So bear with me. :P"};

    }

    public void loadMore(int offset) {
        Intent intent = new Intent(context, NetworkService.class);
        switch (type) {
            case FEED:
                intent.putExtra(Constants.WHAT, Constants.GET_FEED);
                break;
            case MY_POSTS:
                intent.putExtra(Constants.WHAT, Constants.GET_MY_POSTS);
                break;
            case USER_POSTS:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.USER_DATA, user);
                intent.putExtra(Constants.WHAT, Constants.GET_USER_POSTS);
                intent.putExtra(Constants.INTENT_DATA, bundle);
                break;
        }
        intent.putExtra(Constants.OFFSET, offset);
        intent.putExtra(Constants.LIMIT, Constants.MAX_LIMIT);
        context.startService(intent);
    }

    public void refreshDataset(boolean clear) {
        clearNext = clear;
        Intent intent = new Intent(context, NetworkService.class);
        switch (type) {
            case FEED:
            intent.putExtra(Constants.WHAT, Constants.GET_FEED);
            break;
            case MY_POSTS:
            intent.putExtra(Constants.WHAT, Constants.GET_MY_POSTS);
            break;
            case USER_POSTS:
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.USER_DATA, user);
            intent.putExtra(Constants.WHAT, Constants.GET_USER_POSTS);
            intent.putExtra(Constants.INTENT_DATA, bundle);
            break;
        }
        context.startService(intent);
    }

    public void refreshDataset() {
        refreshDataset(true);
    }

    public void loadOlderPosts() {
        loadMore(getItemCount());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (data == null) {
            ((TextView) holder.mLin.findViewById(R.id.post_name)).setText(dummy_dataset[position * 2]);
            ((TextView) holder.mLin.findViewById(R.id.post_text)).setText(dummy_dataset[position * 2 + 1]);
            ((TextView) holder.mLin.findViewById(R.id.timestamp)).setText("3h ago");
        }
        else {
            JsonObject object = (JsonObject) data.get(position);
            ((TextView) holder.mLin.findViewById(R.id.post_name)).setText(object.get("name").getAsString());
            JsonElement text;
            ((TextView) holder.mLin.findViewById(R.id.post_text)).setText( ((text = object.get("text")) != null) ? text.getAsString() : "");
            ((TextView) holder.mLin.findViewById(R.id.timestamp)).setText(Utils.convertTimestamp(object.get("timestamp").getAsString()));

            JsonElement imageid;
            ImageView image = holder.mLin.findViewById(R.id.image);
            if((imageid = object.get("imageid")) != null) {
                image.setImageResource(0);
                holder.mLin.findViewById(R.id.image_loading).setVisibility(View.VISIBLE);
                DiskCache.getImage(imageid.getAsString(), holder);
            }
            else {
                holder.mLin.findViewById(R.id.image_loading).setVisibility(View.GONE);
                image.setImageResource(0);
            }


            JsonArray comments = object.getAsJsonArray("Comment");

            LinearLayout linearLayout = holder.mLin.findViewById(R.id.comments);

            holder.recyclerView = holder.mLin.findViewById(R.id.comments_recycler);
            holder.commentAdapter = new CommentAdapter(object, comments);
            holder.commentAdapter.setMore(holder.more);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            holder.recyclerView.setLayoutManager(linearLayoutManager);
            holder.recyclerView.setAdapter(holder.commentAdapter);
            Button button = holder.mLin.findViewById(R.id.more);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.commentAdapter.setMore(false);
                    holder.more = false;
                    view.setVisibility(View.GONE);
                }
            });

            RelativeLayout commentButton = (RelativeLayout) holder.mLin.findViewById(R.id.post_button);
            ImageView imageView = (ImageView) holder.mLin.findViewById(R.id.comment_img);
            ProgressBar progressBar = (ProgressBar) holder.mLin.findViewById(R.id.comment_progress);
            TextInputEditText editText = (TextInputEditText) holder.mLin.findViewById(R.id.write_comment);

            commentButton.setOnClickListener(new CommentClickListener(position, object.get("postid").getAsInt(), imageView, progressBar, editText));

//            Log.d(TAG, "Comments size: " + comments.size() + " More: " + holder.commentAdapter.more);

            if (comments.size() > 3 && holder.commentAdapter.more) {
                button.setVisibility(View.VISIBLE);
            }
            else {
                button.setVisibility(View.GONE);
            }

            if (comments.size() == 0) {
                ( (TextView) holder.mLin.findViewById(R.id.comments_text)).setText("No comments yet");
            }
            else {
                ( (TextView) holder.mLin.findViewById(R.id.comments_text)).setText("Comments");
            }
        }
    }

    public class CommentClickListener implements View.OnClickListener {
        int postid;
        int position;
        ImageView imageView;
        ProgressBar progressBar;
        TextInputEditText editText;


        CommentClickListener(int position, int postid, ImageView imageView, ProgressBar progressBar, TextInputEditText editText){
            this.position = position;
            this.postid = postid;
            this.imageView = imageView;
            this.progressBar = progressBar;
            this.editText = editText;
        }

        @SuppressLint("ResourceType")
        @Override
        public void onClick(View view) {
            String comment = editText.getText().toString();
            comment = comment.trim();
            if (!comment.equals("")) {
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                Intent intent = new Intent(context, NetworkService.class);
                intent.putExtra(Constants.WHAT, Constants.WRITE_COMMENT);

                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.SRC_FRAGMENT, type);
                bundle.putInt(Constants.POST_ID, postid);
                bundle.putString(Constants.COMMENT_TEXT, comment);
                bundle.putInt(Constants.POST_POS, position);

                intent.putExtra(Constants.INTENT_DATA, bundle);

                context.startService(intent);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        else
            return 0;
    }


}