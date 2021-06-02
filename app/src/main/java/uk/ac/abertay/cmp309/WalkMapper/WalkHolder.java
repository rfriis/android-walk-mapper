package uk.ac.abertay.cmp309.WalkMapper;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

public class WalkHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView textID, textName, textDistance, walkCoordinates, walkDate;
    private RatingBar ratingBar;
    OnWalkListener onWalkListener;

    public WalkHolder (View itemView, OnWalkListener onWalkListener) {
        super(itemView);

        textID = itemView.findViewById(R.id.walkID);
        textName = itemView.findViewById(R.id.textName);
        ratingBar = itemView.findViewById(R.id.walkRatingBar);
        textDistance = itemView.findViewById(R.id.textDistance);
        walkCoordinates = itemView.findViewById(R.id.walkCoordinatesHolder);
        walkDate = itemView.findViewById(R.id.walkDate);
        this.onWalkListener = onWalkListener;
        itemView.setOnClickListener(this);
    }

    public void setDetails(Walk walk) {
        textID.setText(String.valueOf(walk.getWalkID()));
        walkCoordinates.setText(walk.getCoordinates());
        textName.setText(walk.getWalkName());
        walkDate.setText(walk.getDate());
        ratingBar.setRating(walk.getRating());
        if (walk.getDistance() < 1) {
            textDistance.setText((int) (walk.getDistance() * 1000) + " m");
        } else {
            DecimalFormat kmFormat = new DecimalFormat("#.#");
            textDistance.setText(Double.parseDouble(kmFormat.format(walk.getDistance())) + " km");
        }
    }

    @Override
    public void onClick(View v) {
        onWalkListener.onWalkClick(getAdapterPosition());
    }

    public interface OnWalkListener {
        void onWalkClick(int position);
    }
}
