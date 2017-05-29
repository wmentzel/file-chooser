package com.randomlychosenbytes.filechooser;

import android.app.Dialog;
import android.app.DialogFragment;

/**
 * Created by willi on 28.05.17.
 */

public class RetainableDialogFragment extends DialogFragment {

    public RetainableDialogFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
