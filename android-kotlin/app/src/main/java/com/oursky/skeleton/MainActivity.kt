package com.oursky.skeleton

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.facebook.drawee.backends.pipeline.Fresco
import com.oursky.skeleton.MainApplication.Companion.store
import com.oursky.skeleton.redux.ViewStore
import com.oursky.skeleton.ui.MainScreen
import com.oursky.skeleton.ui.SplashScreen

class MainActivity : AppCompatActivity() {
    private var mStoreRetained: Boolean = false
    private var mRouter: Router? = null
    private var mInSplash: Boolean = false

    //region Lifecycle
    //---------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        if (!mStoreRetained) {
            mStoreRetained = true
            MainApplication.retainStore(this)
        }
        val layout = FrameLayout(this)
        setContentView(layout)
        mRouter = Conductor.attachRouter(this, layout, savedInstanceState)
        mRouter?.setRoot(RouterTransaction.with(SplashScreen()))
        mInSplash = true
    }
    override fun onStart() {
        // NOTE: We need to get store ready before super.onStart(),
        //       otherwise Conductor will re-create our view and cause NPE upon using store
        if (!mStoreRetained) {
            mStoreRetained = true
            MainApplication.retainStore(this)
        }
        super.onStart()
    }
    override fun onResume() {
        super.onResume()
        if (mInSplash) {
            // Show main screen after a delay, can also do things like login to server
            window.decorView.postDelayed({ showAppContent() }, 3000)
        }
    }
    override fun onPause() {
        super.onPause()
        // TODO: We going background, release resource here
    }
    override fun onStop() {
        super.onStop()
        if (mStoreRetained) {
            mStoreRetained = false
            MainApplication.releaseStore(this)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
    //---------------------------------------------------------------
    //endregion

    //region UI Events
    //---------------------------------------------------------------
    override fun onBackPressed() {
        if (mRouter?.handleBack() != true) {
            super.onBackPressed()
        }
    }
    //---------------------------------------------------------------
    //endregion

    private fun showAppContent() {
        if (mInSplash) {
            mInSplash = false
            mRouter?.replaceTopController(RouterTransaction.with(MainScreen()))
            store().dispatch(ViewStore.Action.SetTitle("Hello World"))
        }
    }
}
