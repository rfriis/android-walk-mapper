package uk.ac.abertay.cmp309.WalkMapper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WalkAdapter extends RecyclerView.Adapter<WalkHolder>{

    private Context context;
    private ArrayList<Walk> walks;
    private WalkHolder.OnWalkListener mOnWalkListener;

    public WalkAdapter(Context context, ArrayList<Walk> walks, WalkHolder.OnWalkListener onWalkListener) {
        this.context = context;
        this.walks = walks;
        this.mOnWalkListener = onWalkListener;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_row;
    }

    @NonNull
    @Override
    public WalkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new WalkHolder(view, mOnWalkListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WalkHolder holder, int position) {
        Walk walk = walks.get(position);
        holder.setDetails(walk);
    }

    @Override
    public int getItemCount() {
        return walks.size();
    }
}
