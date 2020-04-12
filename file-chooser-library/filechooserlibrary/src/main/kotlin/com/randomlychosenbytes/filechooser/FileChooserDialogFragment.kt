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
    private var activity: Activity? = null
    private var extension: String? = null
    private var currentPath: File? = null
    lateinit var onFileSelectionFinished: (File) -> Unit

    // filter on file extension
    private var adapter: FileListAdapter? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = getActivity()!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_file_chooser, null)
        val fileListRecyclerView: RecyclerView = view.findViewById(R.id.fileListRecyclerView)
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
        builder.setView(view)
        builder.setNegativeButton(getString(R.string.cancel)) { dialogInterface, i -> dismiss() }
        return builder.create()
    }

    /**
     * Called when the Fragment is visible to the user.
     */
    override fun onStart() {
        super.onStart()

        // full size, otherwise the dialog changes size depending on how much files there are to display
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        listDirectory(Environment.getExternalStorageDirectory())
    }

    /**
     * Sort, filter and display the files for the given path.
     */
    fun listDirectory(path: File) {
        if (path.exists()) {
            val dirs = path.listFiles { file -> file.isDirectory && file.canRead() }
            val files = path.listFiles(FileFilter { file ->
                if (file.isDirectory) {
                    return@FileFilter false
                }
                if (!file.canRead()) {
                    false
                } else if (extension == null) {
                    true
                } else {
                    file.name.toLowerCase().endsWith(extension!!)
                }
            })
            val dirList: List<File>
            val fileList: List<File>
            dirList = if (dirs == null) {
                LinkedList()
            } else {
                Arrays.asList(*dirs)
            }
            fileList = if (files == null) {
                LinkedList()
            } else {
                Arrays.asList(*files)
            }
            Log.i("path=", path.absolutePath)
            currentPath = path
            val overallList: MutableList<File> = LinkedList()
            if (path.parentFile != null) {
                overallList.add(File(PARENT_DIR))
            }
            Collections.sort(dirList)
            Collections.sort(fileList)
            overallList.addAll(dirList)
            overallList.addAll(fileList)

            // listDirectory the user interface
            val dialog = dialog
            dialog!!.setTitle(currentPath!!.path)
            adapter!!.setFiles(overallList)
            adapter!!.notifyDataSetChanged()
        }
    }

    /**
     * Convert a relative filename into an actual File object.
     */
    private fun getChosenFile(chosenFile: File): File {
        return if (chosenFile == File(PARENT_DIR)) {
            currentPath!!.parentFile
        } else {
            chosenFile
        }
    }

    companion object {
        private const val PARENT_DIR = ".."
        fun newInstance(activity: Activity?, extension: String?, onFileSelectionFinished: (File) -> Unit): FileChooserDialogFragment {
            val frag = FileChooserDialogFragment()
            frag.activity = activity
            frag.extension = extension?.toLowerCase()
            frag.onFileSelectionFinished = onFileSelectionFinished
            return frag
        }
    }
}