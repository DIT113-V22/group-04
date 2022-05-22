package com.example.drawer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<SavedInformation> mExampleList;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public EditText savedName;
        public ListView pathList;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            savedName = itemView.findViewById(R.id.savedName);
            pathList = itemView.findViewById(R.id.pathList);
        }
    }

    public ExampleAdapter(ArrayList<SavedInformation> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view_saved_paths, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        SavedInformation currentItem = mExampleList.get(position);

        holder.savedName.setText(currentItem.getLine1());
        //holder.pathList.getAdapter(currentItem.getLine2());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
