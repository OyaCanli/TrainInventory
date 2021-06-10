package com.canli.oya.traininventoryroom

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * An empty activity inheriting [FragmentActivity], annotated with [AndroidEntryPoint].
 *
 * This Activity is used to host Fragment in HiltFragmentScenario.
 */
@AndroidEntryPoint
class EmptyFragmentActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val themeId = intent.getIntExtra(
            HiltFragmentScenario.EXTRA_THEME,
            HiltFragmentScenario.defaultTheme
        )
        setTheme(themeId)

        HiltFragmentScenario.FragmentFactoryViewModel.getInstance(this).fragmentFactory?.let { factory ->
            supportFragmentManager.fragmentFactory = factory
        }

        super.onCreate(savedInstanceState)
    }
}