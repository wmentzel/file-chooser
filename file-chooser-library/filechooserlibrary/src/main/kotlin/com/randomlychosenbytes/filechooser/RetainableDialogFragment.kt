package com.randomlychosenbytes.filechooser

import androidx.fragment.app.DialogFragment

open class RetainableDialogFragment : DialogFragment() {
    override fun onDestroyView() {
        val dialog = dialog
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    init {
        retainInstance = true
    }
}