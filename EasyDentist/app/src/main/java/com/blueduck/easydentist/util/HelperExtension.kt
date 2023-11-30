package com.blueduck.easydentist.util

 import android.app.Activity
import android.app.ProgressDialog
 import android.content.Context
 import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
 import com.blueduck.easydentist.R
 import com.permissionx.guolindev.PermissionX


// activity extension
fun Activity.logInfo(message: String) {
    Log.i(this::class.java.simpleName, message)
}

fun Activity.logError(message: String) {
    Log.e(this::class.java.simpleName, message)
}

fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}


// fragment extension
fun Fragment.logInfo(message: String) {
    Log.i(this::class.java.simpleName, message)
}

fun Fragment.logError(message: String) {
    Log.e(this::class.java.simpleName, message)
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), message, duration).show()
}


fun Fragment.checkForPermissions(permission: String, isSuccess: (Boolean) -> Unit) {
    PermissionX.init(requireActivity())
        .permissions(permission)
        .request { allGranted, _, deniedList ->
            if (allGranted) {
                isSuccess(true)
            } else {
                println("denied Permissions $deniedList")
                Toast.makeText(requireContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
            }
        }
}

// this extension function to show the loading progress  dialog
fun Fragment.getProgressDialog(context: Context): ProgressDialog {
    val progressDialog = ProgressDialog(context)
    progressDialog.setTitle("Loading...")
    progressDialog.setMessage("Please wait while we load your data.")
    progressDialog.setCancelable(false)
    val inflater = layoutInflater
    val dialogView: View = inflater.inflate(R.layout.progress_dialog, null)
    progressDialog.setContentView(dialogView)
    return progressDialog
}


// this extension function to show the uploading progress dialog
fun Fragment.getUploadImageProgressDialog(context: Context): ProgressDialog {
    val progressDialog = ProgressDialog(context)
    progressDialog.setTitle("Uploading...")
    progressDialog.setMessage("Please wait while we upload your Image.")
    progressDialog.setCancelable(false)
    val inflater = layoutInflater
    val dialogView: View = inflater.inflate(R.layout.progress_dialog, null)
    progressDialog.setContentView(dialogView)
    return progressDialog
}


// this function to show simple progress dialog
fun Fragment.getSimpleProgressDialog(context: Context): ProgressDialog {
    val progressDialog = ProgressDialog(context)
    progressDialog.setMessage("Please wait.")
    progressDialog.setCancelable(false)
    val inflater = layoutInflater
    val dialogView: View = inflater.inflate(R.layout.progress_dialog, null)
    progressDialog.setContentView(dialogView)
    return progressDialog
}
