package jr.brian.mynotesnative.notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import jr.brian.mynotesnative.databinding.ActivityNotesGridBinding

class NotesGridActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesGridBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteList: ArrayList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesGridBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAdapter()
    }

    private fun setAdapter() {
        initData()
        noteAdapter = NoteAdapter(this, noteList)
        binding.apply {
            notesRecyclerView.layoutManager = GridLayoutManager(this@NotesGridActivity, 2)
            notesRecyclerView.adapter = noteAdapter
        }
    }

    private fun initData() {
        noteList = ArrayList()
        for (i in 1..15) {
            noteList.add(
                Note(
                    title = "Note $i",
                    body = "",
                    date = "6/29/2022",
                    passcode = "",
                    bodyFontSize = 11.0,
                    textColor = 0xFFFFFFFF.toInt(),
                    isStarred = false,
                    isLocked = false,
                    index = i
                )
            )
        }
    }
}