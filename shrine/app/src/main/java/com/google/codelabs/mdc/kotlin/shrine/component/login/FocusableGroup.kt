package com.google.codelabs.mdc.kotlin.shrine.component.login

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class FocusableGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintHelper(context, attrs, defStyleAttr) {

    init {
        visibility = View.GONE
    }

    override fun updatePreLayout(container: ConstraintLayout?) {
        super.updatePreLayout(container)
        container ?: return
        // ConstraintSet
        val constraintSet = ConstraintSet().apply { clone(container) }
        referencedIds
            .map { container.getViewById(it) }
            .forEach { view ->
                view.isFocusable = true
                val constraint = constraintSet.getParameters(view.id)
                // Arrow keys handling
                if (isReferencedSide(constraint.topToBottom)) {
                    // Set ↑ key focus target
                    view.nextFocusUpId = constraint.topToBottom
                    container.findViewById<View>(constraint.topToBottom).nextFocusDownId = view.id
                }
                if (isReferencedSide(constraint.bottomToTop)) {
                    // Set ↓ key focus target
                    view.nextFocusDownId = constraint.bottomToTop
                    container.findViewById<View>(constraint.bottomToTop).nextFocusUpId = view.id
                }
                if (isReferencedSide(constraint.leftToRight)) {
                    // Set ← key focus target
                    view.nextFocusLeftId = constraint.leftToRight
                    container.findViewById<View>(constraint.leftToRight).nextFocusRightId = view.id
                }
                if (isReferencedSide(constraint.rightToLeft)) {
                    // Set → key focus target
                    view.nextFocusRightId = constraint.rightToLeft
                    container.findViewById<View>(constraint.rightToLeft).nextFocusLeftId = view.id
                }
                // Tab keys handling
                // Order : horizontal > vertical
                if (isReferencedSide(constraint.rightToLeft)) {
                    view.nextFocusForwardId = constraint.rightToLeft
                } else if (isReferencedSide(constraint.leftToRight)) {
                    container.findViewById<View>(constraint.leftToRight).nextFocusForwardId = view.id
                } else if (isReferencedSide(constraint.bottomToTop)) {
                    view.nextFocusForwardId = constraint.bottomToTop
                } else if (isReferencedSide(constraint.topToBottom)) {
                    container.findViewById<View>(constraint.topToBottom).nextFocusForwardId = view.id
                }
            }
    }

    private fun isReferencedSide(toConstraintId: Int): Boolean =
        toConstraintId != ConstraintSet.UNSET
            && toConstraintId != ConstraintSet.PARENT_ID
            && referencedIds.contains(toConstraintId)
}
