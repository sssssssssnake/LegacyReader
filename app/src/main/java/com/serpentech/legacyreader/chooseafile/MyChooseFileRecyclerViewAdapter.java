package com.serpentech.legacyreader.chooseafile;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.serpentech.legacyreader.R;
import com.serpentech.legacyreader.StaticStuff;
import com.serpentech.legacyreader.chooseafile.MusicEntries.MusicEntry;
import com.serpentech.legacyreader.databinding.FragmentFilechooseBinding;
import com.serpentech.legacyreader.filemanagement.ConfigXmlJson;
import com.serpentech.legacyreader.filemanagement.xmlmanage.XmlGrab;

import java.util.ArrayList;
import java.util.List;


public class MyChooseFileRecyclerViewAdapter extends RecyclerView.Adapter<MyChooseFileRecyclerViewAdapter.ViewHolder> {

    private final List<MusicEntry> mValues;
    XmlGrab xmlGrab = new XmlGrab();

    public MyChooseFileRecyclerViewAdapter(List<MusicEntry> items) {
        ConfigXmlJson.workingFileList = ConfigXmlJson.readConfigXml();
        List<MusicEntry> musicEntries = new ArrayList<>();
        for (ConfigXmlJson.decompressedXmlFile file : ConfigXmlJson.workingFileList) {
            musicEntries.add(
                    new MusicEntry(
                            (musicEntries.size() + 1)+ "",
                            file.musicFileName,
                            file.fullPath
                    )
            );
        }
//        if (!(items == null)) {
//            mValues = items;
//        } else {
//            mValues = musicEntries;
//        }
        mValues = musicEntries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                FragmentFilechooseBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).filename);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticStuff.chosenFile = holder.mItem;
                Log.d("AdapterLogic", StaticStuff.chosenFile.filepath);

                // go to SecondFragment
                Navigation.findNavController(v).navigate(R.id.action_ChooseToSecond);
            }
        }
        );
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mDescriptionView;
        public MusicEntry mItem;

        public ViewHolder(FragmentFilechooseBinding binding) {
            super(binding.getRoot());
            mIdView = binding.number;
            mContentView = binding.name;
            mDescriptionView = binding.description;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
    public int getMusicEntryCount() {
        return mValues.size();
    }
}