package com.google.sample.optimizedforchromeos

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.PointerIcon
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
        viewModel.arrowTypeList.observe(this, object : Observer<List<Pair<String, PointerIcon>>> {
            override fun onChanged(values: List<Pair<String, PointerIcon>>?) {
                containerLayout.removeAllViews()
                values?.forEach { (name, icon) ->
                    TextView(this@PointerSampleActivity)
                        .apply {
                            text = name
                            pointerIcon = icon
                        }.let {
                            containerLayout.addView(it)
                        }
                }
            }
        })
    }
}
