package com.therealbluepandabear.pixapencil.customviews.pixelgridview

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.core.view.drawToBitmap
import com.therealbluepandabear.pixapencil.R
import com.therealbluepandabear.pixapencil.activities.canvas.CanvasActivity
import com.therealbluepandabear.pixapencil.activities.canvas.binding
import com.therealbluepandabear.pixapencil.enums.OutputCode
import com.therealbluepandabear.pixapencil.enums.SnackbarDuration
import com.therealbluepandabear.pixapencil.extensions.showSimpleInfoDialog
import com.therealbluepandabear.pixapencil.extensions.showSnackbar
import com.therealbluepandabear.pixapencil.extensions.showSnackbarWithAction
import com.therealbluepandabear.pixapencil.utility.general.FileHelperUtilities
import com.therealbluepandabear.pixapencil.utility.constants.Flags
import java.io.File

lateinit var file: File

fun PixelGridView.extendedSaveAsImage(format: Bitmap.CompressFormat, thisRotation: Int = 0) {
    val formatName = when {
        format == Bitmap.CompressFormat.PNG -> {
            "PNG"
        }

        format == Bitmap.CompressFormat.WEBP -> {
            "WEBP"
        }

        Build.VERSION.SDK_INT >= 30 && ( format == Bitmap.CompressFormat.WEBP_LOSSLESS || format == Bitmap.CompressFormat.WEBP_LOSSY) -> {
            "WEBP"
        }

        else -> {
            "JPG"
        }
    }

    val bitmap = outerCanvasInstance.fragmentHost.drawToBitmap()
    val fileHelperUtilitiesInstance = FileHelperUtilities.createInstance(context)

    var cntxView = if (context is CanvasActivity) {
        binding.clayout
    } else this

    if (cntxView == null) {
        cntxView = this
    }

    fileHelperUtilitiesInstance.saveBitmapAsImage(bitmap, projectTitle,90, format, rotation = thisRotation) { outputCode, _file, exceptionMessage_1 ->
        if (outputCode == OutputCode.Success) {
            file = _file

            cntxView.showSnackbarWithAction(context.getString(R.string.snackbar_image_successfully_saved_in_code_str, projectTitle, formatName), SnackbarDuration.Medium, context.getString(R.string.snackbar_view_exception_info_button_text_in_code_str)) {
                fileHelperUtilitiesInstance.openImageFromUri(Uri.fromFile(file)) { outputCode, exceptionMessage_2 ->
                    if (outputCode == OutputCode.Failure) {
                        if (exceptionMessage_2 != null) {
                            cntxView.showSnackbarWithAction(context.getString(R.string.dialog_view_file_error_title_in_code_str), SnackbarDuration.Default, context.getString(R.string.dialog_exception_info_title_in_code_str)) {
                                (context as Activity).showSimpleInfoDialog(context.getString(R.string.dialog_exception_info_title_in_code_str), exceptionMessage_2)
                            }
                        } else {
                            cntxView.showSnackbar(context.getString(R.string.dialog_view_file_error_title_in_code_str), SnackbarDuration.Default)
                        }
                    } else {
                        Flags.PressedBackFromImg = true
                    }
                }
            }
        } else {
            if (exceptionMessage_1 != null) {
                cntxView.showSnackbarWithAction(context.getString(R.string.dialog_image_exception_when_saving_title_in_code_str, projectTitle, formatName), SnackbarDuration.Default, context.getString(R.string.dialog_exception_info_title_in_code_str)) {
                    (context as Activity).showSimpleInfoDialog(context.getString(R.string.dialog_exception_info_title_in_code_str), exceptionMessage_1)
                }
            } else {
                cntxView.showSnackbar(context.getString(R.string.dialog_image_exception_when_saving_title_in_code_str, projectTitle, formatName), SnackbarDuration.Default)
            }
        }
    }
}