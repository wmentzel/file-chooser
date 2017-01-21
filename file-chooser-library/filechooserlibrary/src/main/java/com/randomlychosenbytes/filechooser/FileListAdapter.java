package com.randomlychosenbytes.filechooser;

/**
 * Created by Willi Mentzel on 19.01.2017.
 */


import android.content.Context;
import android.support.v4.content.ContextCompat;
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
    private Context context;
    /**
     * Constructor
     */
    public FileListAdapter(Context context, List<File> files, FileNavigationListener navigationListener) {
        this.context = context;
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

        final File file = files.get(position);

        holder.fileNameTextView.setText(files.get(position).getName());

        if(file.isFile()) {
            holder.fileNameTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        //
        // Event
        //
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationListener.onFileSelected(file);
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