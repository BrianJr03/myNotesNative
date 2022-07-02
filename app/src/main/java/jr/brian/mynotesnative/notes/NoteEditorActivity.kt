package jr.brian.mynotesnative.notes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import jr.brian.mynotesnative.databinding.ActivityNoteEditorBinding
import jr.brian.mynotesnative.databinding.ActivityNotesGridBinding
import jr.brian.mynotesnative.db.DatabaseHelper
import jr.brian.mynotesnative.notes.NoteAdapter.Companion.NOTE_DATA
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NoteEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteEditorBinding
    private lateinit var gridBinding: ActivityNotesGridBinding
    private lateinit var databaseHelper: DatabaseHelper
    private val current = LocalDateTime.now()
    private val formatter = DateTimeFormatter.ofPattern("M/d/yyyy")
    private val now = current.format(formatter)

    private var mode = "save"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        gridBinding = ActivityNotesGridBinding.inflate(layoutInflater)
        databaseHelper = DatabaseHelper(this)
        setContentView(binding.root)
        supportActionBar?.hide()
        initView()
    }

    private fun initView() {
        if (intent.extras != null) {
            val note = intent.extras?.get(NOTE_DATA) as Note
            mode = intent.extras?.getString("mode") ?: mode
            binding.apply {
                titleEt.setText(note.title)
                bodyET.setText(note.body)
            }
        }
        binding.backIcon.setOnClickListener {
            startNoteGridActivity()
        }
        binding.clearIcon.setOnClickListener {
            clear()
        }
        binding.saveIcon.setOnClickListener {
            saveNote(
                Note(
                    title = binding.titleEt.text.toString(),
                    body = binding.bodyET.text.toString(),
                    date = now,
                    passcode = "",
                    bodyFontSize = 14.0f,
                    textColor = 0xFFFFFFFF.toInt(),
                    isStarred = "true",
                    isLocked = "false",
                    index = 0
                )
            )
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

    private fun clear() {
        val title = binding.titleEt.text
        val body = binding.bodyET.text;
        if (title.isNotEmpty() || body.isNotEmpty()) {
            showConfirmClearDialog()
        }
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