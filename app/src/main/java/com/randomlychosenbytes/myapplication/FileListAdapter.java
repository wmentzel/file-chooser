package com.randomlychosenbytes.myapplication;

/**
 * Created by Willi Mentzel on 19.01.2017.
 */


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * FoodListAdapter
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ItemViewHolder> {

    private List<File> files;
    private FileNavigationListener navigationListener;

    /**
     * Constructor
     */
    public FileListAdapter(List<File> files, FileNavigationListener navigationListener) {
        this.files = files;
        this.navigationListener = navigationListener;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder vh;

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_file, parent, false);
        vh = new ItemViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        holder.fileNameTextView.setText(files.get(position).getName());

        //
        // Event
        //
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationListener.onFileSelected(files.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView fileNameTextView;
        public View v;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            fileNameTextView = (TextView) v.findViewById(R.id.fileNameTextView);
        }
    }

    public interface FileNavigationListener {
        void onFileSelected(File file);
    }
}