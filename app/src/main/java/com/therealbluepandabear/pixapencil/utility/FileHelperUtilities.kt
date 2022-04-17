@file:Suppress("DEPRECATION")
@file:SuppressLint("InflateParams")

package com.therealbluepandabear.pixapencil.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.view.drawToBitmap
import com.google.android.material.textfield.TextInputLayout
import com.therealbluepandabear.pixapencil.R
import com.therealbluepandabear.pixapencil.activities.canvas.outerCanvasInstance
import com.therealbluepandabear.pixapencil.activities.canvas.projectTitle
import com.therealbluepandabear.pixapencil.enums.OutputCode
import com.therealbluepandabear.pixapencil.extensions.activity
import com.therealbluepandabear.pixapencil.extensions.showDialog
import java.io.File
import java.io.FileOutputStream


class FileHelperUtilities(private val context: Context) {
    companion object {
        fun createInstanceFromContext(context: Context) = FileHelperUtilities(context)
    }

    private fun commonDocumentDirPath(): File? {
        /** Thank you to to Noor Hossain on StackOverflow for this solution.
         *
         * - [Link to Noor Hossain's profile](https://stackoverflow.com/users/7608371/noor-hossain)
         * - [Original StackOverFlow post](https://stackoverflow.com/questions/65637610/saving-files-in-android-11-to-external-storagesdk-30)
         * **/

        var dir: File?
        dir =
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/" + StringConstants.AppName
            )


        if (!dir.exists()) {
            val success = dir.mkdirs()
            if (!success) {
                dir = null
            }
        }
        return dir
    }

    fun saveBitmapAsImage(compressionOutputQuality: Int,
                          compressionFormat: Bitmap.CompressFormat,
                          onTaskFinished: (OutputCode, File, String?) -> Unit) {
        /** Thank you to to javatar on StackOverflow - quite a bit of the code here is based off of their solution.
         *
         * - [Link to javatar's profile](https://stackoverflow.com/users/2033223/javatar)
         * - [Original StackOverFlow post](https://stackoverflow.com/users/3571603/)
         * **/

        var exceptionMessage: String? = null
        var outputCode = OutputCode.Success
        val pathData = "image/jpeg"
        var outputName = if (compressionFormat == Bitmap.CompressFormat.PNG) "$projectTitle.png" else "$projectTitle.jpg"

        val directory: File? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            commonDocumentDirPath()
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + StringConstants.AppName)
        }

        directory?.mkdirs()

        var file = File(directory, outputName)

        val textInput: TextInputLayout =
            this.context.activity()?.layoutInflater?.inflate(R.layout.save_file_under_new_name_alert, null)
                as TextInputLayout

        // Nested function? Is it a good practice? I don't think so, but it works.
        fun createNewFile(file_: File, bmp: Bitmap =  outerCanvasInstance.fragmentHost.drawToBitmap()) {
            try {
                val outputStream = FileOutputStream(file_)
                bmp.compress(compressionFormat, compressionOutputQuality, outputStream)
                outputStream.close()
            } catch (exception: Exception) {
                exceptionMessage = exception.message
                outputCode = OutputCode.Failure
            } finally {
                onTaskFinished(outputCode, file_, exceptionMessage)
            }

            MediaScannerConnection.scanFile(context, arrayOf(file_.path), arrayOf(pathData), null)
        }

        if (file.exists()) {
            this.context.activity()?.showDialog(
                this.context.getString(R.string.dialog_file_exists_title_in_code_str),
                this.context.getString(R.string.dialog_file_exists_message_in_code_str, outputName),
                this.context.getString(R.string.dialog_file_exists_positive_button_text_in_code_str), { _, _ ->
                    createNewFile(file)
                },
                this.context.getString(R.string.dialog_file_exists_negative_button_text_in_code_str), { _, _ ->
                    val bmp = outerCanvasInstance.fragmentHost.drawToBitmap()
                this.context.activity()?.showDialog(
                    this.context.getString(R.string.dialog_save_under_new_name_title_in_code_str),
                    this.context.getString(R.string.dialog_save_under_new_name_text_in_code_str),
                    this.context.getString(R.string.dialog_positive_button_text_in_code_str),
                    { _, _ ->
                        val input: String = textInput.editText?.text.toString()
                        outputName = if (compressionFormat == Bitmap.CompressFormat.PNG) "$input.png" else "$input.jpg"

                        file = File(directory, outputName)
                        createNewFile(file, bmp)
                    },
                    this.context.getString(R.string.dialog_negative_button_text_in_code_str), { _, _ -> },
                    textInput)
                }, null)
        } else {
            createNewFile(file)
        }
    }

    fun openImageFromUri(uri: Uri,
                         onTaskFinished: (OutputCode, String?) -> Unit) {
        var exceptionMessage: String? = null
        var outputCode = OutputCode.Success
        val intentAction = Intent.ACTION_VIEW
        val type = "image/*"

        val intent = Intent().apply {
            action = intentAction
            setDataAndType(uri, type)
        }

        try {
            context.startActivity(intent)
        } catch (exception: Exception) {
            exceptionMessage = exception.message
            outputCode = OutputCode.Failure
        } finally {
            onTaskFinished(outputCode, exceptionMessage)
        }
    }
}
