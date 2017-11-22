package com.randomlychosenbytes.filechooser;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ItemViewHolder> {

    private List<File> files;
    private FileNavigationListener navigationListener;
    private Context context;

    FileListAdapter(Context context, List<File> files, FileNavigationListener navigationListener) {
        this.context = context;
        this.files = files;
        this.navigationListener = navigationListener;
    }

    void setFiles(List<File> files) {
        this.files = files;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_file, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        final File file = files.get(position);

        holder.fileNameTextView.setText(files.get(position).getName());

        if(file.isFile()) {
            holder.fileNameTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.iconImageView.setVisibility(View.GONE);
        }
        else{
            holder.fileNameTextView.setTextColor(ContextCompat.getColor(context, android.R.color.tertiary_text_dark));
            holder.iconImageView.setVisibility(View.VISIBLE);
        }

        //
        // Event
        //
        holder.v.setOnClickListener(new OnClickListener() {
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

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView fileNameTextView;
        View v;

        ItemViewHolder(View v) {
            super(v);
            this.v = v;
            iconImageView = v.findViewById(R.id.iconImageView);
            fileNameTextView = v.findViewById(R.id.fileNameTextView);
        }
    }

    public interface FileNavigationListener {
        void onFileSelected(File file);
    }
}