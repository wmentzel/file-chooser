package com.randomlychosenbytes.filechooser

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileFilter
import java.util.*

class FileChooserDialogFragment : RetainableDialogFragment() {
    lateinit var activity: Activity
    var extension: String? = null
    lateinit var currentPath: File
    lateinit var onFileSelectionFinished: (File) -> Unit
    lateinit var adapter: FileListAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_file_chooser, null)

        val fileListRecyclerView = view.findViewById<RecyclerView>(R.id.fileListRecyclerView)
        fileListRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = FileListAdapter(getActivity(), LinkedList()) { file: File ->
            val chosenFile = getChosenFile(file)
            if (chosenFile.isDirectory) {
                listDirectory(chosenFile)
            } else {
                onFileSelectionFinished(chosenFile)
                dismiss()
            }
        }
        fileListRecyclerView.adapter = adapter

        return AlertDialog.Builder(activity).apply {
            setView(view)
            setNegativeButton(getString(R.string.cancel)) { _, i -> dismiss() }
        }.create()
    }

    /**
     * Called when the Fragment is visible to the user.
     */
    override fun onStart() {
        super.onStart()

        // full size, otherwise the dialog changes size depending on how much files there are to display
        requireDialog().window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        listDirectory(Environment.getExternalStorageDirectory())
    }

    /**
     * Sort, filter and display the files for the given path.
     */
    private fun listDirectory(path: File) {

        if (!path.exists()) {
            return
        }

        val dirList = path.listFiles { file -> file.isDirectory && file.canRead() }?.filterNotNull()
                ?: emptyList()

        val fileList = path.listFiles(FileFilter(fun(file: File): Boolean {
            if (file.isDirectory) {
                return false
            }

            if (!file.canRead()) {
                return false
            }

            return extension.let {

                if (it == null) {
                    return true
                } else {
                    file.name.toLowerCase(Locale.getDefault()).endsWith(it)
                }
            }
        }))?.filterNotNull() ?: emptyList()

        Log.i("path=", path.absolutePath)

        currentPath = path

        val overallList = mutableListOf<File>()

        if (path.parentFile != null) {
            overallList += File(PARENT_DIR)
        }

        overallList += dirList.sorted()
        overallList += fileList.sorted()

        // listDirectory the user interface
        requireDialog().setTitle(currentPath.path)
        adapter.files = overallList
        adapter.notifyDataSetChanged()
    }

    /**
     * Convert a relative filename into an actual File object.
     */
    private fun getChosenFile(chosenFile: File) = if (chosenFile == File(PARENT_DIR)) {
        currentPath.parentFile
    } else {
        chosenFile
    }

    companion object {
        private const val PARENT_DIR = ".."
        fun newInstance(activity: Activity, extension: String?, onFileSelectionFinished: (File) -> Unit): FileChooserDialogFragment {
            val frag = FileChooserDialogFragment()
            frag.activity = activity
            frag.extension = extension?.toLowerCase(Locale.getDefault())
            frag.onFileSelectionFinished = onFileSelectionFinished
            return frag
        }
    }
}