package com.antbraygromore.banner
import com.antbraygromore.banner.view.GroMoreBannerView
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp


@ReactModule(name = "GroMoreBannerView") // 必须与 JavaScript 中使用的名称一致
class GroMoreBannerViewManager(private val reactContext: ReactApplicationContext) : SimpleViewManager<GroMoreBannerView>() {

  override fun getName() = "GroMoreBannerView"

  override fun createViewInstance(themedReactContext: ThemedReactContext): GroMoreBannerView {
    return GroMoreBannerView(themedReactContext)
  }

  @ReactProp(name = "mediaId")
  fun setMediaId(view: GroMoreBannerView, mediaId: String?) {
    view.mediaId = mediaId
  }

  @ReactProp(name = "width")
  fun setWidth(view: GroMoreBannerView, width: Int?) {
    width?.let { view.adWidth = it }
  }

  @ReactProp(name = "height")
  fun setHeight(view: GroMoreBannerView, height: Int?) {
    height?.let { view.adHeight = it }
  }

  @ReactProp(name = "muted")
  fun setMuted(view: GroMoreBannerView, muted: Boolean?) {
    muted?.let { view.muted = it }
  }

  @ReactProp(name = "useSurfaceView")
  fun setUseSurfaceView(view: GroMoreBannerView, useSurfaceView: Boolean?) {
    useSurfaceView?.let { view.useSurfaceView = it }
  }

  @ReactProp(name = "extra")
  fun setExtra(view: GroMoreBannerView, extra: ReadableMap?) {
    view.extra = extra
  }

  @ReactProp(name = "showCloseBtn")
  fun setShowCloseBtn(view: GroMoreBannerView, showCloseBtn: Boolean?) {
    showCloseBtn?.let { view.showCloseBtn = it }
  }

  override fun getCommandsMap(): Map<String, Int>? {
    return MapBuilder.of("loadAd", COMMAND_LOAD_AD)
  }

  override fun receiveCommand(root: GroMoreBannerView, commandId:Int, args: ReadableArray?) {
    when (commandId) {
      COMMAND_LOAD_AD -> {
        val mediaId = args?.getString(0)
        val options = args?.getMap(1)
        if (mediaId != null && options != null) {
          root.loadAd(mediaId, options)
        }
      }
    }
  }

  companion object {
    private const val COMMAND_LOAD_AD = 1
  }
}
