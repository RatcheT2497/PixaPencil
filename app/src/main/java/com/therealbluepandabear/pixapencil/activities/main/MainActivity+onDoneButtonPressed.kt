package com.therealbluepandabear.pixapencil.activities.main

import android.content.Intent
import com.therealbluepandabear.pixapencil.activities.canvas.CanvasActivity
import com.therealbluepandabear.pixapencil.utility.StringConstants

fun MainActivity.extendedOnDoneButtonPressed(projectTitle: String, width: Int, height: Int) {
    startActivity(
        Intent(this, CanvasActivity::class.java)
            .putExtra(StringConstants.ProjectTitleExtra, projectTitle)
            .putExtra(StringConstants.WidthExtra, width)
            .putExtra(StringConstants.HeightExtra, height)
    )

    title = StringConstants.AppName
    currentFragmentInstance = null
}