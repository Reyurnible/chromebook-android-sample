/*      Copyright 2018 Google Inc. All rights reserved.

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
package com.google.sample.optimizedforchromeos

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.TooltipCompat
import android.util.Log
import android.util.TypedValue
import android.view.ContextMenu
import android.view.DragEvent
import android.view.KeyEvent
import android.view.MenuItem
import android.view.MotionEvent.ACTION_HOVER_ENTER
import android.view.MotionEvent.ACTION_HOVER_EXIT
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.ArrayDeque

class MainActivity : AppCompatActivity() {
    private object ActionKeys {
        const val messageSent = 1
        const val dinoClicked = 2
    }

    private var undoStack = ArrayDeque<Int>()
    private var redoStack = ArrayDeque<Int>()

    private lateinit var messageCounterText: TextView
    private lateinit var clickCounterText: TextView
    private lateinit var dropText: TextView
    private lateinit var dragText: TextView

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(MainActivity::class.java.simpleName, "onCreate")
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // Initialized Views
        messageCounterText = findViewById(R.id.text_messages_sent)
        clickCounterText = findViewById(R.id.text_dino_clicks)
        dragText = findViewById(R.id.text_drag)
        dropText = findViewById(R.id.text_drop)

        val sendButton = findViewById<Button>(R.id.button_send)
        val dinoImage1 = findViewById<ImageView>(R.id.image_dino1)
        val dinoImage2 = findViewById<ImageView>(R.id.image_dino2)
        val dinoImage3 = findViewById<ImageView>(R.id.image_dino3)
        val dinoImage4 = findViewById<ImageView>(R.id.image_dino4)

        val messageField = findViewById<EditText>(R.id.edit_message)

        // Setting with ViewModel
        undoStack = viewModel.undoStack
        redoStack = viewModel.redoStack
        observeViewModel()

        // Enter Key
        messageField.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                sendButton.performClick();
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        dragText.setOnHoverListener { _, motionEvent ->
            when (motionEvent.actionMasked) {
                ACTION_HOVER_ENTER -> {
                    dragText.setBackgroundResource(R.drawable.hand)
                    true
                }
                ACTION_HOVER_EXIT -> {
                    dragText.setBackgroundResource(0)
                    true
                }
                else -> false
            }
        }
        dragText.setOnLongClickListener(textViewLongClickListener())

        dropText.setOnDragListener(dropTargetListener(this))

        sendButton.backgroundTintList = null
        sendButton.setOnClickListener {
            viewModel.incrementMessageSent()
            messageField.text.clear()

            undoStack.push(ActionKeys.messageSent)
            redoStack.clear()
        }
        sendButton.setOnHoverListener { _, motionEvent ->
            when (motionEvent.actionMasked) {
                ACTION_HOVER_ENTER -> {
                    val colorStateList = ColorStateList(emptyArray(), intArrayOf(Color.argb(127, 0, 255, 0)))
                    sendButton.backgroundTintList = colorStateList
                    true
                }
                ACTION_HOVER_EXIT -> {
                    sendButton.backgroundTintList = null
                    true
                }
                else -> false
            }
        }

        dinoImage1.setOnClickListener(imageOnClickListener(clickCounterText))
        dinoImage2.setOnClickListener(imageOnClickListener(clickCounterText))
        dinoImage3.setOnClickListener(imageOnClickListener(clickCounterText))
        dinoImage4.setOnClickListener(imageOnClickListener(clickCounterText))

//        val highlightValue = TypedValue()
//        theme.resolveAttribute(R.attr.selectableItemBackground, highlightValue, true)
//        dinoImage1.setBackgroundResource(highlightValue.resourceId)
//        dinoImage2.setBackgroundResource(highlightValue.resourceId)
//        dinoImage3.setBackgroundResource(highlightValue.resourceId)
//        dinoImage4.setBackgroundResource(highlightValue.resourceId)

        dinoImage1.setBackgroundResource(R.drawable.box_border)
        dinoImage2.setBackgroundResource(R.drawable.box_border)
        dinoImage3.setBackgroundResource(R.drawable.box_border)
        dinoImage4.setBackgroundResource(R.drawable.box_border)

        // Right Click
        registerForContextMenu(dinoImage1)
        registerForContextMenu(dinoImage2)
        registerForContextMenu(dinoImage3)
        registerForContextMenu(dinoImage4)

        // Tooltip
        TooltipCompat.setTooltipText(dinoImage1, getString(R.string.name_dino_1))

        startActivity(Intent(this, PointerSampleActivity::class.java))
    }

    override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_Z &&
                event.hasModifiers(KeyEvent.META_CTRL_ON)) {
            // Ctrl + z => undo
            undoStack.poll()?.let { lastAction ->
                redoStack.push(lastAction)
                when (lastAction) {
                    ActionKeys.messageSent -> {
                        viewModel.decrementMessageSent()
                    }
                    ActionKeys.dinoClicked -> {
                        viewModel.decrementDinosClicked()
                    }
                    else -> {
                        Log.d("OptimizedChromeOS", "Error on Ctrl-z: Unknown Action")
                    }
                }
            }
            return true
        } else if (event?.keyCode == KeyEvent.KEYCODE_Z &&
                event.hasModifiers(KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON)) {
            // Ctrl + Shift + z => undo
            redoStack.poll()?.let { prevAction ->
                undoStack.push(prevAction)
                when (prevAction) {
                    ActionKeys.messageSent -> {
                        viewModel.incrementMessageSent()
                    }
                    ActionKeys.dinoClicked -> {
                        viewModel.decrementDinosClicked()
                    }
                    else -> {
                        Log.d("OptimizedChromeOS", "Error on Ctrl-z: Unknown Action")
                    }
                }
            }
        }
        return super.dispatchKeyShortcutEvent(event)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (R.id.menu_item_share_dino == item?.getItemId()) {
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.menu_shared_message), Snackbar.LENGTH_SHORT).show()
            return true
        } else {
            return super.onContextItemSelected(item)
        }
    }

    private fun observeViewModel() {
        val messageObserver = Observer<Int> { newValue ->
            messageCounterText.text = newValue.toString()
        }
        viewModel.messagesSent.observe(this, messageObserver)

        val dinosClickedObserver = Observer<Int> { newValue ->
            clickCounterText.text = newValue.toString()
        }
        viewModel.dinosClicked.observe(this, dinosClickedObserver)

        val dropTargetObserver = Observer<String> { newValue ->
            dropText.text = newValue
        }
        viewModel.dropText.observe(this, dropTargetObserver)
    }

    private fun imageOnClickListener(countTextView: TextView): View.OnClickListener =
            View.OnClickListener {
                viewModel.incrementDinosClicked()

                undoStack.push(ActionKeys.dinoClicked)
                redoStack.clear()
            }

    private fun dropTargetListener(activity: AppCompatActivity): View.OnDragListener =
            View.OnDragListener { view, event ->
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        // Limit the types of items that can be received
                        if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                                || event.clipDescription.hasMimeType("application/x-arc-uri-list")) {
                            // Greenify background colour so user knows this is a target
                            view.setBackgroundColor(Color.argb(55, 0, 255, 0));
                            return@OnDragListener true
                        }
                        false
                    }
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        // Increase green background colour when item is over top of target
                        view.setBackgroundColor(Color.argb(150, 0, 255, 0))
                        true
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        true
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        // Increase green background colour when item is over top of target
                        view.setBackgroundColor(Color.argb(55, 0, 255, 0))
                        true
                    }
                    DragEvent.ACTION_DROP -> {
                        requestDragAndDropPermissions(event)
                        val item: ClipData.Item = event.clipData.getItemAt(0)
                        if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            (view as TextView).also { target ->
                                target.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
                                target.text = item.text
                            }
                            return@OnDragListener true
                        } else if (event.clipDescription.hasMimeType("application/x-arc-uri-list")) {
                            //If a file, read the first 200 characters and output them in a new TextView.

                            //Note the use of ContentResolver to resolve the ChromeOS content URI.
                            val contentUri = item.uri
                            val parcelFileDescriptor: ParcelFileDescriptor =
                                    try {
                                        contentResolver.openFileDescriptor(contentUri, "r");
                                    } catch (e: FileNotFoundException) {
                                        e.printStackTrace()
                                        Log.e("OptimizedChromeOS", "Error receiving file: File not found.");
                                        return@OnDragListener false
                                    }

                            val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
                            val MAX_LENGTH = 5000
                            val bytes = ByteArray(MAX_LENGTH)
                            try {
                                val input = FileInputStream(fileDescriptor)
                                input.use { input ->
                                    input.read(bytes, 0, MAX_LENGTH)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            val contents = String(bytes)
                            val CHARS_TO_READ = 200
                            val contentLength =
                                    if (contents.length > CHARS_TO_READ) CHARS_TO_READ
                                    else 0
                            (view as TextView).also { target ->
                                target.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                                target.text = contents.substring(0, contentLength)
                            }
                            return@OnDragListener true
                        } else {
                            return@OnDragListener false
                        }
                    }
                    DragEvent.ACTION_DRAG_ENDED -> {
                        // Increase green background colour when item is over top of target
                        view.setBackgroundColor(Color.argb(0, 255, 255, 255))
                        true
                    }
                    else -> {
                        Log.d("OptimizedChromeOS", "Unknown action type received by DropTargetListener.")
                        false
                    }
                }
            }

    private fun textViewLongClickListener(): View.OnLongClickListener =
            View.OnLongClickListener {
                val targetView = it as TextView
                val dragContent = "Dragged Text: ${targetView.text}"
                val item: ClipData.Item = ClipData.Item(dragContent)
                val dragData: ClipData = ClipData(dragContent, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
                val dragShadow = View.DragShadowBuilder(targetView)
                targetView.startDragAndDrop(dragData, dragShadow, null, View.DRAG_FLAG_GLOBAL)
                false
            }

}
