package jr.brian.mynotesnative.notes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import jr.brian.mynotesnative.R
import jr.brian.mynotesnative.databinding.ActivityNoteEditorBinding
import jr.brian.mynotesnative.databinding.ActivityNotesGridBinding
import jr.brian.mynotesnative.databinding.PasscodeDialogBinding
import jr.brian.mynotesnative.db.DatabaseHelper
import jr.brian.mynotesnative.notes.NoteAdapter.Companion.INDEX
import jr.brian.mynotesnative.notes.NoteAdapter.Companion.NOTE_DATA
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NoteEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteEditorBinding
    private lateinit var gridBinding: ActivityNotesGridBinding
    private lateinit var passcodeDialogBinding: PasscodeDialogBinding
    private lateinit var databaseHelper: DatabaseHelper
//    private lateinit var bundle: Bundle

    private val current = LocalDateTime.now()
    private val formatter = DateTimeFormatter.ofPattern("M/d/yyyy")
    private val now = current.format(formatter)

    private var mode = "save"
    private var index = 0
    private var isStarred = false
    private var isLocked = false
    private var textColor = 0xFFFFFFFF.toInt()
    private var bodyTextSize = 14.0f
    private var passcode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        gridBinding = ActivityNotesGridBinding.inflate(layoutInflater)
        passcodeDialogBinding = PasscodeDialogBinding.inflate(layoutInflater)
        databaseHelper = DatabaseHelper(this)
        setContentView(binding.root)
//        bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        supportActionBar?.hide()
        initView()
        initListeners()
    }

    private fun clear() {
        val title = binding.titleEt.text
        val body = binding.bodyET.text
        if (title.isNotEmpty() || body.isNotEmpty()) {
            showConfirmClearDialog()
        }
    }

    private fun initView() {
        if (intent.extras != null) {
            val note = intent.extras?.get(NOTE_DATA) as Note?
            if (note != null) {
                mode = intent.extras?.getString("mode") ?: mode
                binding.apply {
                    titleEt.setText(note.title)
                    titleEt.setTextColor(textColor)
                    bodyET.setText(note.body)
                    bodyET.setTextColor(textColor)
                    dateModified.text = note.date
                    colorLens.setColorFilter(textColor)
                    when (note.isStarred) {
                        "true" -> {
                            starIcon.setImageResource(R.drawable.starred_36)
                            isStarred = true
                        }
                        "false" -> {
                            starIcon.setImageResource(R.drawable.star_icon_36)
                            isStarred = false
                        }
                    }
                    when (note.isLocked) {
                        "true" -> {
                            lockIcon.setImageResource(R.drawable.locked_36)
                            isLocked = true
                        }
                        "false" -> {
                            lockIcon.setImageResource(R.drawable.unlock_36)
                            isLocked = false
                        }
                    }
                }
            }
            index = intent.extras?.get(INDEX) as Int
        }
    }

    private fun initListeners() {
        binding.clearIcon.setOnClickListener {
            clear()
        }
        binding.saveIcon.setOnClickListener {
            saveNote(
                Note(
                    title = binding.titleEt.text.toString(),
                    body = binding.bodyET.text.toString(),
                    date = now,
                    passcode = passcode,
                    bodyFontSize = bodyTextSize,
                    textColor = textColor,
                    isStarred = isStarred.toString(),
                    isLocked = isLocked.toString(),
                    index = index
                )
            )
        }
        binding.starIcon.setOnClickListener {
            isStarred = !isStarred
            when (isStarred) {
                true -> binding.starIcon.setImageResource(R.drawable.starred_36)
                false -> binding.starIcon.setImageResource(R.drawable.star_icon_36)
            }
        }
        binding.lockIcon.setOnClickListener {
            isLocked = !isLocked
            when (isLocked) {
                true -> binding.lockIcon.setImageResource(R.drawable.locked_36)
                false -> binding.lockIcon.setImageResource(R.drawable.unlock_36)
            }
        }
    }

    private fun saveNote(note: Note) {
        when (mode) {
            "update" ->
                databaseHelper.updateNote(note)
            "save" ->
                databaseHelper.addNote(note)
        }
        startNoteGridActivity()
    }

    private fun showColorOptions() {
        val options = arrayOf("Blueish-idk", "Pink", "Green", "Red", "White")
        val builder = AlertDialog.Builder(this)
            .setTitle("Choose Text Color")
            .setSingleChoiceItems(options, -1) { d, pos ->
                when (options[pos]) {
                    "Blueish-idk" -> {
                        setTextColor(binding, color = R.color.blueish_idk)
                    }
                    "Pink" -> {
                        setTextColor(binding, color = R.color.pink)
                    }
                    "Green" -> {
                        setTextColor(binding, color = R.color.green)
                    }
                    "Red" -> {
                        setTextColor(binding, color = R.color.red)
                    }
                    "White" -> {
                        setTextColor(binding, color = R.color.white)
                    }
                }
                d.dismiss()
            }
        builder.create().show()
    }

    private fun setTextColor(binding: ActivityNoteEditorBinding, color: Int) {
        textColor = color
        binding.titleEt.setTextColor(color)
        binding.bodyET.setTextColor(color)
        binding.colorLens.setColorFilter(color)
    }

    private fun showConfirmClearDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Clear Fields")
            .setMessage("This will clear the title and body fields")
            .setPositiveButton("Clear") { _, _ ->
                binding.titleEt.text.clear()
                binding.bodyET.text.clear()
                Snackbar.make(binding.root, "Fields cleared", Snackbar.LENGTH_SHORT).show()
            }
        builder.create().show()
    }

    private fun startNoteGridActivity() {
        this.startActivity(
            Intent(
                this@NoteEditorActivity,
                NotesGridActivity::class.java
            )
        )
        finish()
    }
}