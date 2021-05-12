package com.ibd.dcdown.tools

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.transition.MaterialElevationScale
import com.ibd.dcdown.R
import com.ibd.dcdown.main.MainActivity


object Utility {
    fun calculateNoOfColumns(
        context: Context,
        columnWidthDp: Float
    ): Int { // For example columnWidthdp=180
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt() // +0.5 for correct rounding to int.
    }
    @ColorInt
    @SuppressLint("Recycle")
    fun Context.themeColor(
        @AttrRes themeAttrId: Int
    ): Int {
        return obtainStyledAttributes(
            intArrayOf(themeAttrId)
        ).use {
            it.getColor(0, Color.MAGENTA)
        }
    }
    fun Fragment.doExitTranslation(){
        this.exitTransition = MaterialElevationScale(false).apply {
            duration = 400
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 400
        }
    }
    fun Fragment.stopExitTranslation(){
        this.exitTransition = MaterialElevationScale(false).apply {
            duration = 0
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 0
        }
    }
    fun MainActivity.doTabInit() {
        binding.bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
        binding.searchBar.visibility = View.GONE
        binding.mainMenu.visibility = View.VISIBLE
        binding.bar.visibility = View.VISIBLE
        binding.bar.performShow()
        binding.fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_search_24))
        isSearch = false
        binding.fab.show()
    }
    fun checkRWPermission(context:Context)
        = checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED

    fun requestRWPermission(activity: Activity)
        = ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

    fun String.removeURIError() = this.replace("#","").replace("%","").replace("?","")

    fun Activity.hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view: View? = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}