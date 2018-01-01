package me.harshithgoka.socmed.Storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.harshithgoka.socmed.Adapters.PostAdapter;
import me.harshithgoka.socmed.Misc.Constants;
import me.harshithgoka.socmed.R;
import me.harshithgoka.socmed.Misc.Utils;

/**
 * Created by akashtrehan on 09/10/17.
 */

public class DiskCache {


    public static final String TAG = DiskCache.class.getName();
    private static Map<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();

    static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight/2 || width > reqWidth/2) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfWidth / inSampleSize) >= reqWidth / 2) {
                inSampleSize *= 2;
            }
        }

        Log.d(TAG, "inSampleSize - " + inSampleSize + " " + width + " " + height + " " + reqWidth + " " + reqHeight);

        return inSampleSize;
    }

    static Bitmap decodeSampledBitmapFile(File file, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    static void loadBitmapInMemory (String fileid, Uri imageUri, PostAdapter.ViewHolder holder) {
        ImageView imageView;
        if (holder.mLin != null && (imageView = holder.mLin.findViewById(R.id.image)) != null ) {
//                Log.d(TAG,  "MaxWidth = "  + imageView.getMaxWidth() + " or " + imageView.getRootView().getMeasuredWidth());
            try {
                bitmaps.put(fileid, decodeSampledBitmapFile(new File(imageUri.getPath()), imageView.getRootView().getMeasuredWidth(), 0));
            }
            catch (Exception e) {
                bitmaps.put(fileid, BitmapFactory.decodeFile(new File(imageUri.getPath()).getAbsolutePath()));
            }
        }
    }

    public static int getImage(String fileid, PostAdapter.ViewHolder holder) {

        if (bitmaps.containsKey(fileid) ) {
            holder.setImage(bitmaps.get(fileid));
            return 0;
        }
        else {
            File cacheDir = UserStorage.getContext().getCacheDir();
            File file = new File(cacheDir, "img" + fileid + ".png");
            if (file.exists()) {
                DiskImageAsyncTask asyncTask = new DiskImageAsyncTask(fileid, holder);
                asyncTask.execute(fileid);
                return 0;
            } else {
                ImageAsyncTask asyncTask = new ImageAsyncTask(fileid, holder);
                asyncTask.execute(fileid);
                return 1;
            }
        }
    }

    static class DiskImageAsyncTask extends AsyncTask<String, Void, Boolean> {

        PostAdapter.ViewHolder holder;
        String fileid;
        DiskImageAsyncTask (String fileid, PostAdapter.ViewHolder holder) {
            this.fileid = fileid;
            this.holder = holder;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            File cacheDir = UserStorage.getContext().getCacheDir();
            File file = new File(cacheDir, "img" + fileid + ".png");
            if (holder != null)
                loadBitmapInMemory(params[0], Uri.fromFile(file), holder);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (b && holder != null && bitmaps.containsKey(fileid))
                holder.setImage(bitmaps.get(fileid));
            else
                holder.setImage(null);
        }

    }

    static class ImageAsyncTask extends AsyncTask<String, Void, Boolean> {

        public static final String TAG = ImageAsyncTask.class.getName();

        PostAdapter.ViewHolder holder;
        String fileid;
        Uri uri;
        ImageAsyncTask (String fileid, PostAdapter.ViewHolder holder) {
            this.fileid = fileid;
            this.holder = holder;
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            boolean b = false;

            if (strings[0] != null) {
                Log.d(TAG, "FileID: " + strings[0]);

                try {
                    URL url = new URL(Constants.URL + "Image");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    try {
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setDoInput(true);

                        List<AbstractMap.SimpleEntry> list = new ArrayList<AbstractMap.SimpleEntry>();
                        list.add(new AbstractMap.SimpleEntry("id", strings[0]));

                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        writer.write(Utils.getQuery(list));
                        writer.flush();
                        writer.close();
                        os.close();

                        connection.getHeaderFields();

                        if (!url.getHost().equals(connection.getURL().getHost())) {
                            // we were redirected! Kick the user out to the browser to sign on?
                            throw new Exception("Login to your internet provider");
                        }


                        Bitmap response = BitmapFactory.decodeStream(connection.getInputStream());
                        File cacheDir = UserStorage.getContext().getCacheDir();
                        if (response != null) {
                            // make a new bitmap from your file
                            File file = new File(cacheDir, "img" + strings[0] + ".png");

                            FileOutputStream outStream = new FileOutputStream(file);
                            response.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                            outStream.flush();
                            outStream.close();

                            uri = Uri.fromFile(file);
                            loadBitmapInMemory(fileid, uri, holder);
                            b = true;
                        }

                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                    finally {
                        connection.disconnect();
                    }


                } catch (MalformedURLException e) {
                    Log.d(TAG, e.toString());
                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                }

            }

            return b;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (b && holder != null && bitmaps.containsKey(fileid))
                holder.setImage(bitmaps.get(fileid));
            else
                holder.setImage(null);

        }
    }

}
