package net.spreha.herak.hotmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        PlaceholderFragment fragment = new PlaceholderFragment();
        fragment.setArguments(getIntent().getExtras());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

    }



    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_details, container, false);


            Bundle arguments = getArguments();
            String original_title = arguments.getString("original_title");
            String release_date = arguments.getString("release_date");
            String poster_path = arguments.getString("poster_path");
            String vote_average = "Rating: " + arguments.getString("vote_average");
            String overview = arguments.getString("overview");

            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster);
            Picasso.with(getContext()).load(poster_path).into(posterImageView);

            TextView titleTextView = (TextView) rootView.findViewById(R.id.title);
            titleTextView.setText(original_title);

            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date);
            releaseDateTextView.setText(release_date);

            TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview);
            overviewTextView.setText(overview);

            TextView ratingTextView = (TextView) rootView.findViewById(R.id.rating);
            ratingTextView.setText(vote_average);

            return rootView;
        }
    }
}
