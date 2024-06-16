package com.serpentech.legacyreader;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.serpentech.legacyreader.placeholder.MusicEntries.MusicEntry;
import com.serpentech.legacyreader.databinding.FragmentFilechooseBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MusicEntry}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyChooseFileRecyclerViewAdapter extends RecyclerView.Adapter<MyChooseFileRecyclerViewAdapter.ViewHolder> {

    private final List<MusicEntry> mValues;

    public MyChooseFileRecyclerViewAdapter(List<MusicEntry> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentFilechooseBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).filename);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public MusicEntry mItem;

        public ViewHolder(FragmentFilechooseBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}