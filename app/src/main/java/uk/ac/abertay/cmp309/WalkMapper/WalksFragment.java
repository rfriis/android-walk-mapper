package uk.ac.abertay.cmp309.WalkMapper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WalksFragment extends Fragment implements WalkHolder.OnWalkListener{

    private RecyclerView recyclerView;
    private WalkAdapter adapter;
    private ArrayList<Walk> walkArrayList;
    private ArrayList<Object[]> walksUnformatted;
    SQLiteHelper sqLiteHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Walks");
        View view = inflater.inflate(R.layout.fragment_walks, container, false);
//        sqLiteHelper = SQLiteHelper.getInstance(getContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        walkArrayList = new ArrayList<>();
        createListData();
        adapter = new WalkAdapter(view.getContext(), walkArrayList, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void createListData() {
        sqLiteHelper = new SQLiteHelper(getActivity());
        walkArrayList = sqLiteHelper.loadAllWalks();
    }

    @Override
    public void onWalkClick(int position) {
        String coordinates = walkArrayList.get(position).getCoordinates();
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        intent.putExtra("EXTRA_COORDINATES", coordinates);
        startActivity(intent);
    }
}
