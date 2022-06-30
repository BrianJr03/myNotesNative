package jr.brian.mynotesnative.notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jr.brian.mynotesnative.databinding.ActivityNoteEditorBinding
import jr.brian.mynotesnative.notes.NoteAdapter.Companion.NOTE_DATA

class NoteEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val note = intent.extras?.get(NOTE_DATA) as Note
        binding.apply {
            titleEt.setText(note.title)
            bodyET.setText(note.body)
        }
    }
}