package com.google.codelabs.mdc.kotlin.shrine.component

import android.view.KeyEvent

interface KeyShortcutDispatch {
    fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean
}
