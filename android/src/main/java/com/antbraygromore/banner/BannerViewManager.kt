package com.antbraygromore.banner

import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.antbraygromore.banner.activity.BannerFragment

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp

class BannerViewManager(private val reactContext: ReactApplicationContext) : ViewGroupManager<FrameLayout>() {

  override fun getName(): String {
    return NAME
  }
  companion object {
    const val NAME = "BannerViewManager"
    private const val COMMAND_CREATE = 1
  }


  override fun createViewInstance(reactContext: ThemedReactContext) = FrameLayout(reactContext)

  override fun getCommandsMap() = mapOf("create" to COMMAND_CREATE)

  override fun receiveCommand(
    root: FrameLayout,
    commandId: String,
    args: ReadableArray?
  ) {
    super.receiveCommand(root, commandId, args)
    val reactNativeViewId = requireNotNull(args).getInt(0)

    when (commandId.toInt()) {
      COMMAND_CREATE -> createFragment(root, reactNativeViewId)
    }
  }

  private fun createFragment(root: FrameLayout, reactNativeViewId: Int) {
    val parentView = root.findViewById<ViewGroup>(reactNativeViewId)
    setupLayout(parentView)

    val myFragment = BannerFragment()
    val activity = reactContext.currentActivity as FragmentActivity
    activity.supportFragmentManager
      .beginTransaction()
      .replace(reactNativeViewId, myFragment, reactNativeViewId.toString())
      .commit()
  }

  private fun setupLayout(view: View) {
    Choreographer.getInstance().postFrameCallback(object: Choreographer.FrameCallback {
      override fun doFrame(frameTimeNanos: Long) {
        manuallyLayoutChildren(view)
        view.viewTreeObserver.dispatchOnGlobalLayout()
        Choreographer.getInstance().postFrameCallback(this)
      }
    })
  }

  private fun manuallyLayoutChildren(view: View) {
    val width = requireNotNull(300)
    val height = requireNotNull(250)

    view.measure(
      View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))

    view.layout(0, 0, width, height)
  }


  @ReactProp(name = "codeId")
  fun setCodeId(view: FrameLayout, codeId: String) {

  }

  @ReactProp(name = "imageSize")
  fun setImageSize(view: FrameLayout, imageSize: ReadableMap) {
//    if (imageSize.hasKey("width") && imageSize.hasKey("height")) {
//      val width = imageSize.getInt("width")
//      val height = imageSize.getInt("height")
//    }
  }

  override fun getExportedCustomBubblingEventTypeConstants(): Map<String, Any>? {
    return MapBuilder.builder<String, Any>()
      .put("onAdClick", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onAdClick")))
      .put("onAdError", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onAdError")))
      .put("onAdClose", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onAdClose")))
      .build()
  }
}
