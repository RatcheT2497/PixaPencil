package com.therealbluepandabear.pixapencil.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.therealbluepandabear.pixapencil.R
import com.therealbluepandabear.pixapencil.converters.BitmapConverter
import com.therealbluepandabear.pixapencil.database.AppData
import com.therealbluepandabear.pixapencil.databinding.RecentCreationsLayoutBinding
import com.therealbluepandabear.pixapencil.enums.SnackbarDuration
import com.therealbluepandabear.pixapencil.extensions.showSnackbar
import com.therealbluepandabear.pixapencil.listeners.RecentCreationsListener
import com.therealbluepandabear.pixapencil.models.PixelArt
import com.therealbluepandabear.pixapencil.viewholders.RecentCreationsViewHolder

class RecentCreationsAdapter(private val data: List<PixelArt>, private val listener: RecentCreationsListener) : RecyclerView.Adapter<RecentCreationsViewHolder>() {
    private lateinit var binding: RecentCreationsLayoutBinding

    var userHasLongPressed = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentCreationsViewHolder {
        binding = RecentCreationsLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return RecentCreationsViewHolder(binding.recentCreationsLayoutRootLayout)
    }

    override fun onBindViewHolder(holder: RecentCreationsViewHolder, position: Int) = data.forEach { _ ->
        binding.recentCreationsLayoutMaterialCardView.apply parent@{
            val item = data[position]

            binding.apply {
                recentCreationsLayoutImageView.setImageBitmap(BitmapConverter.convertStringToBitmap(item.coverBitmap))
                recentCreationsLayoutSubtitle.text = item.dateCreated

                recentCreationsLayoutTitle.apply {
                    if (data[position].title.length > 6) {
                        ellipsize = TextUtils.TruncateAt.MARQUEE
                        isSelected = true
                        isSingleLine = true
                        (item.title + " ".repeat(10)).repeat(200).also { text = it }
                    } else {
                        text = item.title
                    }
                }

                this@parent.setOnClickListener {
                    if (!userHasLongPressed) listener.onCreationTapped(item)
                }

                this@parent.setOnLongClickListener {
                    listener.onCreationLongTapped(item)
                    true
                }

                changeStarredIndicator(recentCreationsLayoutFavoriteButton, item)

                recentCreationsLayoutFavoriteButton.setOnClickListener {
                    if (item.starred) {
                        unFavouriteRecentCreation(this@parent, item)
                        AppData.pixelArtDB.pixelArtCreationsDao().updatePixelArtCreationStarred(false, item.objId)
                    } else {
                        favouriteRecentCreation(this@parent, item)
                        AppData.pixelArtDB.pixelArtCreationsDao().updatePixelArtCreationStarred(true, item.objId)
                    }
                    changeStarredIndicator((it as ImageButton), item)
                }
            }
        }
    }

    private fun changeStarredIndicator(imageButton: ImageButton, pixelArt: PixelArt) {
        imageButton.apply {
            if (pixelArt.starred) setImageResource(R.drawable.ic_baseline_star_24)
            else setImageResource(R.drawable.ic_baseline_star_border_24)
        }
    }
    private fun favouriteRecentCreation(contextView: View, pixelArt: PixelArt) {
        contextView.showSnackbar("Saved ${pixelArt.title} to starred items.", SnackbarDuration.Default)
        pixelArt.starred = true
    }

    private fun unFavouriteRecentCreation(contextView: View, pixelArt: PixelArt) {
        contextView.showSnackbar("You have removed ${pixelArt.title} from your starred items.", SnackbarDuration.Default)
        pixelArt.starred = false
    }

    override fun getItemCount(): Int {
        return data.size
    }
}