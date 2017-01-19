package com.randomlychosenbytes.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Activity to test the FileChooserDialogFragment
 */
public class MainActivity extends AppCompatActivity {

    public int WRITE_EXT_STORAGE = 0;
    public int READ_EXT_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onOpenFileChooserDialogButton(View view){
        getPrivileges();
    }

    private void openFileChooserDialog() {

        FileChooserDialogFragment fcDialog = FileChooserDialogFragment.newInstance(this, null);

        fcDialog.setFileListener(new FileChooserDialogFragment.FileSelectionFinishedListener() {
            @Override
            public void onFileSelected(final File file) {
                Log.i("info", file.getAbsolutePath());
                ((TextView) findViewById(R.id.textView)).setText(file.getAbsolutePath());
            }
        });

        fcDialog.show(getFragmentManager(), "File Chooser Dialog");
    }

    private void getPrivileges() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please grant me permission to write the external storage!")
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestWriteExtStoragePermissions(MainActivity.this);
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                requestWriteExtStoragePermissions(this);
            }
        } else {
            openFileChooserDialog();
        }
    }

    /**
     * Handle user decision
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        for (int i = 0; i < permissions.length; i++) {

            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                if (requestCode == WRITE_EXT_STORAGE) {
                    openFileChooserDialog();
                }

                if (requestCode == READ_EXT_STORAGE) {
                    openFileChooserDialog();
                }
            } else {
                if (requestCode == WRITE_EXT_STORAGE) {
                    Toast.makeText(this, "missing privileges", Toast.LENGTH_LONG).show();
                }

                if (requestCode == READ_EXT_STORAGE) {
                    Toast.makeText(this, "missing privileges", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestReadExtStoragePermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXT_STORAGE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestWriteExtStoragePermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXT_STORAGE);
    }
}
