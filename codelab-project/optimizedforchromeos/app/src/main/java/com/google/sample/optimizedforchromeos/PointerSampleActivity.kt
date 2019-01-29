package com.google.sample.optimizedforchromeos

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.PointerIcon
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class PointerSampleActivity : AppCompatActivity() {
    private lateinit var containerLayout: LinearLayout
    private lateinit var viewModel: PointerSampleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arrow)
        containerLayout = findViewById(R.id.container_layout)
        viewModel = ViewModelProviders.of(this).get(PointerSampleViewModel::class.java)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.arrowTypeList.observe(this, Observer<List<Pair<String, PointerIcon>>> { values ->
            containerLayout.removeAllViews()
            values?.forEach { (name, icon) ->
                TextView(this@PointerSampleActivity)
                        .apply {
                            text = name
                            pointerIcon = icon
                        }.let {
                            containerLayout.addView(
                                    it,
                                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelOffset(R.dimen.pointer_sample_cell_height))
                            )
                        }
            }
        })
    }
}
