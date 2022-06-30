package jr.brian.mynotesnative.notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jr.brian.mynotesnative.databinding.ActivityNoteEditorBinding

class NoteEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}