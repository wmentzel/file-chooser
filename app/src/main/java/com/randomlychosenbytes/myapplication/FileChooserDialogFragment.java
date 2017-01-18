package com.randomlychosenbytes.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FileChooserDialogFragment extends DialogFragment {

    private static final String PARENT_DIR = "..";

    private Activity activity;
    private ListView list;
    private File currentPath;
    private FileSelectionListener fileListener;

    // filter on file extension
    private String extension = null;

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

        list = new ListView(activity);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int which, long id) {

                String fileChosen = (String) list.getItemAtPosition(which);
                File chosenFile = getChosenFile(fileChosen);

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

        builder.setView(list);
        return builder.create();
    }

    /**
     * Called when the Fragment is visible to the user.
     */
    @Override
    public void onStart() {
        super.onStart();

        // full size, otherwise the dialog changes size depending on how much files there are to display
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

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

            Log.i("Current path", path.getAbsolutePath());
            this.currentPath = path;

            List<String> overallList = new LinkedList<>();

            if (path.getParentFile() != null) {
                overallList.add(PARENT_DIR);
            }

            Collections.sort(dirList);
            Collections.sort(fileList);

            for (File dir : dirList) {
                overallList.add(dir.getName());
            }

            for (File file : fileList) {
                overallList.add(file.getName());
            }

            // listDirectory the user interface
            Dialog dialog = getDialog();
            dialog.setTitle(currentPath.getPath());

            list.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, overallList));
        }
    }

    public void setFileListener(FileSelectionListener fileListener) {
        this.fileListener = fileListener;
    }

    /**
     * Convert a relative filename into an actual File object.
     */
    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) {
            return currentPath.getParentFile();
        } else {
            return new File(currentPath, fileChosen);
        }
    }

    public interface FileSelectionListener {
        void onFileSelected(File file);
    }
}
