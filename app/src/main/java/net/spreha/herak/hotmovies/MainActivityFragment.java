package net.spreha.herak.hotmovies;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter mMovieAdapter;
    private GridView mMoviesGrid;

    public MainActivityFragment() {
        super();
    }

    @Override
    public void onStart() {
        updateMovies();
        super.onStart();
    }

    private void updateMovies() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        FetchMoviesTask weatherTask = new FetchMoviesTask();
        weatherTask.execute(sort_by);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMoviesGrid = (GridView) rootView.findViewById(R.id.main_fragment_grid);

        ArrayList<String> movies = new ArrayList<String>();

        mMovieAdapter = new MovieAdapter(getContext(), movies);


        return rootView;
    }

    private class MovieAdapter extends ArrayAdapter {
        private Context mContext;
        private ArrayList<String> mItems;

        public MovieAdapter(Context context, ArrayList<String> objects) {
            super(context, R.layout.grid_item_movie,objects);
            this.mContext = context;
            this.mItems = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            //if the view is null than inflate it otherwise just fill the list with
            if(convertView == null){
                //inflate the layout
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.grid_item_movie, parent, false);
            }
            ImageView image =(ImageView) convertView.findViewById(R.id.grid_item_movie_image);
            Picasso.with(mContext).load(mItems.get(position)).into(image);
            return  convertView;
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {

                mMovieAdapter.clear();
                for(String moviePoster : result) {
                    mMovieAdapter.add(moviePoster);
                }
                mMoviesGrid.setAdapter(mMovieAdapter);
                // New data is back from the server.  Hooray!
            }
        }

        @Override
        protected String[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String sort_by = params[0];

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String DISCOVER_BASE_URL =
                        "https://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(DISCOVER_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sort_by)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        private String[] getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            String MDB_RESULTS = "results";
            String MDB_POSTER_PATH = "poster_path";
            String MDB_POSTER_BASE_PATH = "http://image.tmdb.org/t/p/";
            String MDB_POSTER_SIZE = "w185";

            JSONObject jsonObject = new JSONObject(moviesJsonStr);

            JSONArray movies = jsonObject.getJSONArray(MDB_RESULTS);

            String[] moviePosters = new String[movies.length()];

            for(int i = 0; i < movies.length(); i++){
                JSONObject movie = movies.getJSONObject(i);
                String relPath = movie.getString(MDB_POSTER_PATH);

                try {
                    moviePosters[i] = URLDecoder.decode(MDB_POSTER_BASE_PATH + MDB_POSTER_SIZE + relPath, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            return moviePosters;


        }



    }

}


