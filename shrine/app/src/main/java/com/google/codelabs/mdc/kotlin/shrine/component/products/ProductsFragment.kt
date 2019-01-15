package com.google.codelabs.mdc.kotlin.shrine.component.products

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.codelabs.mdc.kotlin.shrine.R
import com.google.codelabs.mdc.kotlin.shrine.component.products.product_card.StaggeredProductCardRecyclerViewAdapter
import com.google.codelabs.mdc.kotlin.shrine.network.ImageRequester
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry
import com.google.codelabs.mdc.kotlin.shrine.network.ProductRepository
import kotlinx.android.synthetic.main.shr_products_fragment.view.*

class ProductsFragment : Fragment(), StaggeredProductCardRecyclerViewAdapter.StaggeredProductCardRecyclerViewAdapterListener {
    private lateinit var repository: ProductRepository
    private val cartProductList: MutableList<ProductEntry> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        repository = ProductRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
            adapter = StaggeredProductCardRecyclerViewAdapter(repository.fetchList(), this@ProductsFragment)
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

        updateCartProductList(emptyList())

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?) {
        menuInflater?.inflate(R.menu.shr_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onProductAddCartClicked(product: ProductEntry) {
        cartProductList.add(product)
        updateCartProductList(cartProductList)
    }

    private fun updateCartProductList(productList: List<ProductEntry>) {
        arrayOf(view?.cart_product_1, view?.cart_product_2, view?.cart_product_3)
            .filterNotNull()
            .forEachIndexed { index, view ->
                productList.getOrNull(index)?.let {
                    ImageRequester.setImageFromUrl(view, it.url)
                    view.visibility = View.VISIBLE
                } ?: let {
                    view.visibility = View.GONE
                }
            }
    }
}
