package jr.brian.mynotesnative.notes

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import jr.brian.mynotesnative.databinding.ActivityNoteEditorBinding
import jr.brian.mynotesnative.notes.NoteAdapter.Companion.NOTE_DATA

class NoteEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteEditorBinding
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        initView()
    }

    private fun initView() {
        val note = intent.extras?.get(NOTE_DATA) as Note
        binding.apply {
            titleEt.setText(note.title)
            bodyET.setText(note.body)
        }
    }

    private fun save(note: Note) {
        editor.apply {
            putString(TITLE, note.title)
            putString(BODY, note.body)
            if (commit()) {
                Snackbar.make(binding.root, "Noted saved", Snackbar.LENGTH_SHORT).show()
                startNoteGridActivity(note)
            }
        }
    }

    private fun startNoteGridActivity(note: Note) {
        val intent =
            Intent(this@NoteEditorActivity, NoteEditorActivity::class.java)
        intent.putExtra(NOTE_DATA, note)
        this.startActivity(intent)
    }

    companion object {
        const val TITLE = "title"
        const val BODY = "body"
        const val FILENAME = "Note $TITLE"
    }
}