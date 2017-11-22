package com.randomlychosenbytes.filechooser;

import android.app.Dialog;
import android.app.DialogFragment;

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
