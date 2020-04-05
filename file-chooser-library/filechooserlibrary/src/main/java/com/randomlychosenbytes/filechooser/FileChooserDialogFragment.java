package com.randomlychosenbytes.filechooser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class FileChooserDialogFragment extends RetainableDialogFragment {

    private Activity activity;
    private String extension = null;

    private static final String PARENT_DIR = "..";
    private File currentPath;

    // filter on file extension
    private FileListAdapter adapter;

    public FileChooserDialogFragment() {
    }

    public static FileChooserDialogFragment newInstance(Activity activity, String extension) {

        FileChooserDialogFragment frag = new FileChooserDialogFragment();
        frag.activity = activity;
        frag.extension = (extension == null) ? null : extension.toLowerCase();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_file_chooser, null);
        RecyclerView fileListRecyclerView = view.findViewById(R.id.fileListRecyclerView);
        fileListRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        adapter = new FileListAdapter(getActivity(), new LinkedList<File>(), new FileListAdapter.FileNavigationListener() {
            @Override
            public void onFileSelected(File file) {
                File chosenFile = getChosenFile(file);

                if (chosenFile.isDirectory()) {
                    listDirectory(chosenFile);
                } else {
                    if (fileListener != null) {
                        fileListener.onFileSelected(chosenFile);
                    }
                    FileChooserDialogFragment.this.dismiss();
                }
            }
        });

        fileListRecyclerView.setAdapter(adapter);

        builder.setView(view);

        builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FileChooserDialogFragment.this.dismiss();
            }
        });

        return builder.create();
    }

    /**
     * Called when the Fragment is visible to the user.
     */
    @Override
    public void onStart() {
        super.onStart();

        // full size, otherwise the dialog changes size depending on how much files there are to display
        getDialog().getWindow().setLayout(MATCH_PARENT, MATCH_PARENT);
        listDirectory(Environment.getExternalStorageDirectory());
    }

    /**
     * Sort, filter and display the files for the given path.
     */
    public void listDirectory(File path) {

        if (path.exists()) {
            File[] dirs = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.isDirectory() && file.canRead());
                }
            });

            File[] files = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {

                    if (!file.isDirectory()) {
                        if (!file.canRead()) {
                            return false;
                        } else if (extension == null) {
                            return true;
                        } else {
                            return file.getName().toLowerCase().endsWith(extension);
                        }
                    } else {
                        return false;
                    }
                }
            });

            List<File> dirList;
            List<File> fileList;

            if (dirs == null) {
                dirList = new LinkedList<>();
            } else {
                dirList = Arrays.asList(dirs);
            }

            if (files == null) {
                fileList = new LinkedList<>();
            } else {
                fileList = Arrays.asList(files);
            }

            Log.i("path=", path.getAbsolutePath());
            this.currentPath = path;

            List<File> overallList = new LinkedList<>();

            if (path.getParentFile() != null) {
                overallList.add(new File(PARENT_DIR));
            }

            Collections.sort(dirList);
            Collections.sort(fileList);

            overallList.addAll(dirList);
            overallList.addAll(fileList);

            // listDirectory the user interface
            Dialog dialog = getDialog();
            dialog.setTitle(currentPath.getPath());

            adapter.setFiles(overallList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Convert a relative filename into an actual File object.
     */
    private File getChosenFile(File chosenFile) {
        if (chosenFile.equals(new File(PARENT_DIR))) {
            return currentPath.getParentFile();
        } else {
            return chosenFile;
        }
    }

    private FileSelectionFinishedListener fileListener;

    public void setFileListener(FileSelectionFinishedListener fileListener) {
        this.fileListener = fileListener;
    }

    public interface FileSelectionFinishedListener {
        void onFileSelected(File file);
    }
}
