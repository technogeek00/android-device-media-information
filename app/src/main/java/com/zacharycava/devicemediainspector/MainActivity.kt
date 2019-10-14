package com.zacharycava.devicemediainspector

import android.app.Presentation
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.mediarouter.media.MediaRouter
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import com.zacharycava.devicemediainspector.views.*
import kotlinx.android.synthetic.main.main.*

class MainActivity : AppCompatActivity() {

    private var mediaRouter: MediaRouter? = null
    private var castPresentation: CastingPresentation? = null

    private var mediaRouteChange:MediaRouter.Callback = object:MediaRouter.Callback() {
        override fun onRoutePresentationDisplayChanged(
            router: MediaRouter?,
            route: MediaRouter.RouteInfo?
        ) {
            super.onRoutePresentationDisplayChanged(router, route)

            Log.d("MediaInspector", "${route?.name} presentation display changed to ${route?.presentationDisplayId}")
            checkCasting()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val adapter = InfoFragmentPageAdapter(this, supportFragmentManager)
        pager.adapter = adapter
        tabs.setupWithViewPager(pager)

        mediaRouter = MediaRouter.getInstance(this)
    }

    override fun onResume() {
        super.onResume()

        val routeSelector = MediaRouteSelector.Builder()
            .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
            .build()
        mediaRouter?.addCallback(routeSelector, mediaRouteChange)

        checkCasting()
    }

    override fun onPause() {
        super.onPause()

        mediaRouter?.removeCallback(mediaRouteChange)
    }

    override fun onStop() {
        super.onStop()

        castPresentation?.dismiss()
        castPresentation = null
    }

    private fun checkCasting() {
        val route: MediaRouter.RouteInfo? = mediaRouter?.selectedRoute
        val routeDisplay: Display? = route?.presentationDisplay

        Log.d("MediaInspector", "Running cast_screen logic")
        Log.d("MediaInspector", route?.toString() ?: "")
        Log.d("MediaInspector", routeDisplay?.name ?: "")

        if(route == null || !route.supportsControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)) {
            Log.d("MediaInspector", "Checking cast_screen but no live video route available")
            return
        }

        if(castPresentation != null && castPresentation?.display != routeDisplay) {
            Log.d("MediaInspector", "Currently cast_screen but not on the now active display")
            castPresentation?.dismiss()
            castPresentation = null
        }

        if(castPresentation == null && routeDisplay != null) {
            Log.d("MediaInspector", "Casting to ${routeDisplay.name}")
            castPresentation = CastingPresentation(this, routeDisplay)
            castPresentation?.show()
        }
    }

    inner class CastingPresentation(context: Context, display: Display): Presentation(context, display) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.cast_screen)

            val subtext = findViewById<TextView>(R.id.cast_subtext)
            subtext.text = display.name
        }
    }

    inner class InfoFragmentPageAdapter(context: Context, fm: FragmentManager): FragmentPagerAdapter(fm) {
        private val mContext: Context = context

        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> DeviceInfoFragment()
                1 -> DisplaysFragment()
                2 -> RoutesFragment()
                3 -> CodecsFragment()
                4 -> DRMSystemsFragment()
                else -> DeviceInfoFragment()
            }
        }

        override fun getCount(): Int {
            return 5
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when(position) {
                0 -> mContext.resources.getString(R.string.device_tab)
                1 -> mContext.resources.getString(R.string.display_tab)
                2 -> mContext.resources.getString(R.string.routes_tab)
                3 -> mContext.resources.getString(R.string.codec_tab)
                4 -> mContext.resources.getString(R.string.drm_tab)
                else -> ""
            }
        }
    }
}