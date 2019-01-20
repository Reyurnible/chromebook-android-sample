package com.google.codelabs.mdc.kotlin.shrine.component

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.codelabs.mdc.kotlin.shrine.R
import kotlinx.android.synthetic.main.shr_main_activity.*

class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shr_main_activity)
        // NavHostFragment
        navHostFragment = nav_host_fragment as NavHostFragment
    }

    override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean =
        navHostFragment.childFragmentManager.fragments
            .mapNotNull { it as? KeyShortcutDispatch }
            .any { it.dispatchKeyShortcutEvent(event) }
}
