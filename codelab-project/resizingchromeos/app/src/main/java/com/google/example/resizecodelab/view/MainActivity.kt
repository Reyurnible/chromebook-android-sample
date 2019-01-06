/*      Copyright 2018 Google LLC

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.google.example.resizecodelab.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.support.constraint.ConstraintsChangedListener
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.google.example.resizecodelab.R
import com.google.example.resizecodelab.model.AppData
import com.google.example.resizecodelab.model.DataProvider
import com.google.example.resizecodelab.model.Suggestion
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_shell.*
import java.security.Key

class MainActivity : AppCompatActivity() {
    private object Keys {
        const val productName = "KEY_PRODUCT_NAME"
        const val expand = "KEY_EXPANDED"
    }

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var suggestionAdapter: SuggestionAdapter

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_shell)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        if (viewModel.productName.value.isNullOrEmpty()) {
            viewModel.setProductName(getString(R.string.label_product_name))
        }
        // if cannot using ViewModel, suggest to saved instance state.
//        savedInstanceState?.getString(Keys.productName)?.let {
//            viewModel.setProductName(it)
//        }
//        savedInstanceState?.getBoolean(Keys.expand)?.let {
//            viewModel.setDescriptionExpanded(it)
//        }

        //Set up recycler view for reviews
        reviewAdapter = ReviewAdapter()
        recyclerReviews.apply {
            setHasFixedSize(true)
            adapter = reviewAdapter
        }

        //Set up recycler view for suggested products
        suggestionAdapter = SuggestionAdapter()
        recyclerSuggested.apply {
            setHasFixedSize(true)
            adapter = suggestionAdapter
        }
        suggestionAdapter.updateSuggestions(getSuggestedProducts())

        //Expand/collapse button for product description
        buttonExpand.setOnClickListener { _ ->
            viewModel.appData.value?.let {
                toggleExpandButton()
            }
        }

        viewModel.appData.observe(this, Observer { appData ->
            handleReviewsUpdate(appData)
        })
        viewModel.isDescriptionExpanded.observe(this, Observer {
            if (it == true) {
                buttonExpand.text = getString(R.string.button_collapse)
            } else {
                buttonExpand.text = getString(R.string.button_expand)
            }
            textProductDescription.text = getDescriptionText(viewModel.appData.value)
        })

        buttonPurchase.setOnClickListener {
            AlertDialog.Builder(this)
                    .setMessage(R.string.popup_purchase)
                    .create()
                    .show()
            return@setOnClickListener
            Toast.makeText(this, getString(R.string.popup_purchase), Toast.LENGTH_LONG).show()
            return@setOnClickListener
            val textPopupMessage = TextView(this)
            textPopupMessage.gravity = Gravity.CENTER
            textPopupMessage.text = getString(R.string.popup_purchase)
            TextViewCompat.setTextAppearance(textPopupMessage, R.style.TextAppearance_AppCompat_Title)

            val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER)
            val framePopup = FrameLayout(this)
            framePopup.layoutParams = layoutParams
            framePopup.setBackgroundColor(ContextCompat.getColor(this, R.color.background_floating_material_light))

            framePopup.addView(textPopupMessage)


            //Get window size
//            val displayMetrics = Resources.getSystem().displayMetrics
            val displayMetrics = resources.displayMetrics
            val screenWidthPx = displayMetrics.widthPixels
            val screenHeightPx = displayMetrics.heightPixels

            //Popup should be 50% of window size
            val popupWidthPx = screenWidthPx / 2
            val popupHeightPx = screenHeightPx / 2

            //Place it in the middle of the window
            val popupX = (screenWidthPx / 2) - (popupWidthPx / 2)
            val popupY = (screenHeightPx / 2) - (popupHeightPx / 2)

            //Show the window
            val popupWindow = PopupWindow(framePopup, popupWidthPx, popupHeightPx, true)
            popupWindow.elevation = 10f
            popupWindow.showAtLocation(scrollMain, Gravity.NO_GRAVITY, popupX, popupY)
        }

        constraintMain.setLayoutDescription(R.xml.constraint_states)
        constraintMain.setOnConstraintsChanged(object : ConstraintsChangedListener() {
            override fun preLayoutChange(state: Int, layoutId: Int) {
                progressLoadingReviews.visibility = if (viewModel.appData.value == null) VISIBLE else INVISIBLE

                val changeBounds = ChangeBounds()
                changeBounds.duration = 600
                changeBounds.interpolator = AnticipateOvershootInterpolator(0.2f)

                TransitionManager.beginDelayedTransition(constraintMain, changeBounds)

                when (layoutId) {
                    R.layout.activity_main -> {
                        val reviewLayoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
                        recyclerReviews.layoutManager = reviewLayoutManager

                        val suggestionLayoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
                        recyclerSuggested.layoutManager = suggestionLayoutManager
                    }

                    R.layout.activity_main_land -> {
                        val reviewLayoutManager = GridLayoutManager(baseContext, 2)
                        recyclerReviews.layoutManager = reviewLayoutManager

                        val suggestionLayoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
                        recyclerSuggested.layoutManager = suggestionLayoutManager
                    }

                    R.layout.activity_main_w400 -> {
                        val reviewLayoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
                        recyclerReviews.layoutManager = reviewLayoutManager

                        val suggestionLayoutManager = GridLayoutManager(baseContext, 2)
                        recyclerSuggested.layoutManager = suggestionLayoutManager
                    }

                    R.layout.activity_main_w600_land -> {
                        val reviewLayoutManager = GridLayoutManager(baseContext, 2)
                        recyclerReviews.layoutManager = reviewLayoutManager

                        val suggestionLayoutManager = GridLayoutManager(baseContext, 3)
                        recyclerSuggested.layoutManager = suggestionLayoutManager
                    }
                }
            }

            override fun postLayoutChange(stateId: Int, layoutId: Int) {
                //Request all layout elements be redrawn
                constraintMain.requestLayout()
            }
        })

        configurationUpdate(resources.configuration)
    }

//    override fun onSaveInstanceState(outState: Bundle?) {
//        super.onSaveInstanceState(outState)
//        outState?.putBoolean(Keys.expand, viewModel.isDescriptionExpanded.value == true)
//        outState?.putString(Keys.productName, viewModel.productName.value)
//    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationUpdate(newConfig)
    }

    private fun configurationUpdate(configuration: Configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            constraintMain.setState(R.id.constraintStateLandscape, configuration.screenWidthDp, configuration.screenHeightDp)
        } else {
            constraintMain.setState(R.id.constraintStatePortrait, configuration.screenWidthDp, configuration.screenHeightDp)
        }
    }

    private fun handleReviewsUpdate(appData: AppData?) {
        progressLoadingReviews.visibility = if (appData == null) VISIBLE else GONE
        buttonPurchase.visibility = if (appData != null) VISIBLE else GONE
        buttonExpand.visibility = if (appData != null) VISIBLE else GONE
        appData?.let {
            textProductName.text = it.title
            textProductCompany.text = it.developer
            textProductDescription.text = getDescriptionText(it)
            reviewAdapter.onReviewsLoaded(it.reviews)
        }
    }

    private fun getDescriptionText(appData: AppData?): String =
            if (appData != null)
                if (viewModel.isDescriptionExpanded.value == true) appData.description
                else appData.shortDescription
            else ""

    private fun getSuggestedProducts(): Array<Suggestion> {
        return arrayOf(Suggestion(getString(R.string.label_product_name2), R.drawable.gregarious),
                Suggestion(getString(R.string.label_product_name3), R.drawable.byzantium),
                Suggestion(getString(R.string.label_product_name4), R.drawable.cratankerous),
                Suggestion(getString(R.string.label_product_name5), R.drawable.sunsari),
                Suggestion(getString(R.string.label_product_name6), R.drawable.squiggle),
                Suggestion(getString(R.string.label_product_name7), R.drawable.tenacious),
                Suggestion(getString(R.string.label_product_name8), R.drawable.venemial))
    }

    private fun toggleExpandButton() {
        //Invert isDescriptionExpanded
        viewModel.setDescriptionExpanded(viewModel.isDescriptionExpanded.value == false)
    }

    private fun updateDescription() {
        if (viewModel.appData.value != null) {
            if (viewModel.isDescriptionExpanded.value == true) {
                buttonExpand.text = getString(R.string.button_collapse)
                textProductDescription.text = viewModel.appData.value?.description
            } else {
                buttonExpand.text = getString(R.string.button_expand)
                textProductDescription.text = viewModel.appData.value?.shortDescription
            }
        }
    }
}
