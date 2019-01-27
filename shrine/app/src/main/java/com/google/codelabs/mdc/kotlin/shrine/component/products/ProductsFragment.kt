package com.google.codelabs.mdc.kotlin.shrine.component.products

import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.codelabs.mdc.kotlin.shrine.R
import com.google.codelabs.mdc.kotlin.shrine.component.KeyShortcutDispatch
import com.google.codelabs.mdc.kotlin.shrine.component.products.product_card.StaggeredProductCardRecyclerViewAdapter
import com.google.codelabs.mdc.kotlin.shrine.network.ImageRequester
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry
import kotlinx.android.synthetic.main.shr_products_fragment.view.*

class ProductsFragment : Fragment(),
    StaggeredProductCardRecyclerViewAdapter.StaggeredProductCardRecyclerViewAdapterListener,
    KeyShortcutDispatch {
    private lateinit var viewModel: ProductsViewModel
    @IdRes
    private var lastCartProductContextMenuClickedItem: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProductsViewModel::class.java)
        observeViewModelValues()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment with the ProductGrid theme
        val view = inflater.inflate(R.layout.shr_products_fragment, container, false)
        // Set up the tool bar
        (activity as AppCompatActivity).setSupportActionBar(view.app_bar)
        view.app_bar.setNavigationOnClickListener(
            NavigationIconClickListener(
                requireActivity(),
                view.product_grid,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(requireContext(), R.drawable.shr_branded_menu), // Menu open icon
                ContextCompat.getDrawable(requireContext(), R.drawable.shr_close_menu)) // Menu close icon
        )
        // Set up the RecyclerView
        view.recycler_view.apply {
            setHasFixedSize(true)
            val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
                .apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int =
                            if (position % 3 == 2) 2 else 1
                    }
                }
            layoutManager = gridLayoutManager
            adapter = StaggeredProductCardRecyclerViewAdapter(listener = this@ProductsFragment)
            val largePadding = resources.getDimensionPixelSize(R.dimen.shr_staggered_product_grid_spacing_large)
            val smallPadding = resources.getDimensionPixelSize(R.dimen.shr_staggered_product_grid_spacing_small)
            addItemDecoration(ProductGridItemDecoration(largePadding, smallPadding))
        }
        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.product_grid.background =
                context?.getDrawable(R.drawable.shr_product_grid_background_shape)
            view.cart_layout.background =
                context?.getDrawable(R.drawable.shr_cart_background_shape)
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?) {
        menuInflater?.inflate(R.menu.shr_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean =
        if (event?.keyCode == KeyEvent.KEYCODE_Z &&
            event.hasModifiers(KeyEvent.META_CTRL_ON)) {
            // Ctrl + z => undo
            viewModel.onUndoKeyShortcut()
            true
        } else if (event?.keyCode == KeyEvent.KEYCODE_Z &&
            event.hasModifiers(KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON)) {
            // Ctrl + Shift + z => redo
            viewModel.onRedoKeyShortcut()
            true
        } else {
            false
        }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = activity?.menuInflater ?: return
        when (v?.id) {
            R.id.cart_product_1, R.id.cart_product_2, R.id.cart_product_3 -> {
                // Create cart product context menu.
                inflater.inflate(R.menu.shr_cart_product_context_menu, menu)
                lastCartProductContextMenuClickedItem = v.id
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean =
        if (item?.itemId == R.id.delete) {
            when (lastCartProductContextMenuClickedItem) {
                R.id.cart_product_1 -> {
                    viewModel.onClickDeleteCart(viewModel.cartProductList.value?.get(0))
                    true
                }
                R.id.cart_product_2 -> {
                    viewModel.onClickDeleteCart(viewModel.cartProductList.value?.get(1))
                    true
                }
                R.id.cart_product_3 -> {
                    viewModel.onClickDeleteCart(viewModel.cartProductList.value?.get(2))
                    true
                }
                else -> false
            }
        }
        else {
            super.onContextItemSelected(item)
        }

    private fun observeViewModelValues() {
        viewModel.productList.observe(this, Observer<List<ProductEntry>> { productList ->
            (view?.recycler_view?.adapter as? StaggeredProductCardRecyclerViewAdapter)
                ?.apply { this.productList = productList }
                ?.notifyDataSetChanged()
        })
        viewModel.cartProductList.observe(this, Observer<List<ProductEntry>> { productList ->
            arrayOf(view?.cart_product_1, view?.cart_product_2, view?.cart_product_3)
                .filterNotNull()
                .forEachIndexed { index, view ->
                    productList.getOrNull(index)?.let {
                        ImageRequester.setImageFromUrl(view, it.url)
                        view.visibility = View.VISIBLE
                        registerForContextMenu(view)
                        /* 別形式
                        view.setOnCreateContextMenuListener { menu, v, menuInfo ->
                        }
                        */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            view.setOnContextClickListener {
                                true
                            }
                        }
                    } ?: let {
                        view.visibility = View.GONE
                    }
                }
        })
        viewModel.isLoading.observe(this, Observer<Boolean> { isLoading ->
            view?.progress_bar?.visibility =
                if (isLoading) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
        })
    }

    override fun onProductAddCartClicked(product: ProductEntry) {
        viewModel.onClickAddCart(product)
    }
}
