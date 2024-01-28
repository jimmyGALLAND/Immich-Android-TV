package nl.giejay.android.tv.immich.album

import android.os.Bundle
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.navigation.fragment.findNavController
import com.zeuskartik.mediaslider.MediaSliderConfiguration
import nl.giejay.android.tv.immich.api.ApiClient
import nl.giejay.android.tv.immich.api.ApiUtil
import nl.giejay.android.tv.immich.api.model.AlbumDetails
import nl.giejay.android.tv.immich.api.model.Asset
import nl.giejay.android.tv.immich.card.Card
import nl.giejay.android.tv.immich.shared.db.LocalStorage
import nl.giejay.android.tv.immich.shared.fragment.VerticalCardGridFragment
import nl.giejay.android.tv.immich.shared.prefs.PreferenceManager
import nl.giejay.android.tv.immich.shared.util.toSliderItems
import retrofit2.Response


class AlbumDetailsFragment : VerticalCardGridFragment<Asset, AlbumDetails>() {
    override fun sortItems(items: List<Asset>): List<Asset> {
        return items.sortedByDescending { it.fileModifiedAt }
    }

    override fun loadItems(apiClient: ApiClient): Response<AlbumDetails> {
        val albumId = AlbumDetailsFragmentArgs.fromBundle(requireArguments()).albumId
        return apiClient.listAssetsFromAlbum(albumId)
    }

    override fun onItemSelected(card: Card) {
        // no use case yet
    }

    override fun onItemClicked(card: Card) {
        // todo find a better way to pass data to other fragment without using the Intent extras (possibly too large)
        LocalStorage.mediaSliderItems = assets.toSliderItems()
        findNavController().navigate(
            AlbumDetailsFragmentDirections.actionDetailsToPhotoSlider(
                MediaSliderConfiguration(
                    PreferenceManager.sliderShowDescription(),
                    PreferenceManager.sliderShowMediaCount(),
                    false,
                    response!!.albumName,
                    "",
                    "",
                    adapter.indexOf(card),
                    PreferenceManager.sliderInterval()
                ), response!!.id
            )
        )
    }

    override fun getPicture(it: Asset): String? {
        return ApiUtil.getThumbnailUrl(it.id)
    }

    override fun setTitle(response: AlbumDetails) {
        title = response.albumName
    }

    override fun mapResponseToItems(response: AlbumDetails): List<Asset> {
        return response.assets
    }

    override fun createCard(a: Asset): Card {
        return Card(
            a.deviceAssetId,
            a.exifInfo?.description ?: "",
            a.id,
            ApiUtil.getThumbnailUrl(a.id),
            ApiUtil.getFileUrl(a.id)
        )
    }
}