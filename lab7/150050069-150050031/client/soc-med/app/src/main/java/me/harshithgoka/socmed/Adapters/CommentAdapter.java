package me.harshithgoka.socmed.Adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import me.harshithgoka.socmed.Misc.Constants;
import me.harshithgoka.socmed.Misc.Utils;
import me.harshithgoka.socmed.R;
import me.harshithgoka.socmed.Storage.Comment;
import me.harshithgoka.socmed.Storage.UserStorage;

/**
 * Created by harshithgoka on 06/10/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{


    public static final String TAG = CommentAdapter.class.getName();
    public static final int MAX_COMMENTS = 3;

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
        notifyDataSetChanged();
    }

    public boolean more;
    public JsonArray comments;
    JsonObject object;
    CommentAdapter (JsonObject object, JsonArray data ) {
        this.object = object;
        comments = data;
        more = true;
    }

    public void AddComment(Bundle bundle) {
        if (bundle != null) {
            JsonObject jsonObject = new JsonObject();

            String comment = bundle.getString(Constants.COMMENT_TEXT);
            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String timestamp = format.format(time);

            int postid = bundle.getInt(Constants.POST_ID);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Comment>>() {}.getType();
            List<Comment> commentList = gson.fromJson(comments,listType);
            commentList.add(new Comment("", UserStorage.getName(), postid, comment, timestamp));

            Collections.sort(commentList, new Comparator<Comment>() {
                @Override
                public int compare(Comment comment, Comment t1) {
                    return comment.timestamp.compareTo(t1.timestamp);
                }
            });

            JsonParser jsonParser = new JsonParser();
            comments = jsonParser.parse(gson.toJson(commentList)).getAsJsonArray();

            object.remove("Comment");
            object.add("Comment", comments);

            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (comments != null) {
            JsonObject object = (JsonObject) comments.get(position);
            ((TextView) holder.mLin.findViewById(R.id.comment_name)).setText(object.get("name").getAsString());
            ((TextView) holder.mLin.findViewById(R.id.comment_text)).setText(object.get("text").getAsString());
            ((TextView) holder.mLin.findViewById(R.id.comment_timestamp)).setText(Utils.convertTimestamp(object.get("timestamp").getAsString()));
        }
        else {

        }
    }

    @Override
    public int getItemCount() {
        if (comments != null) {
            if (more)
                return Math.min (comments.size(), MAX_COMMENTS);
            else
                return comments.size();
        }
        else
            return 0;

    }

    // / Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLin;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLin = v;
        }
    }


}
