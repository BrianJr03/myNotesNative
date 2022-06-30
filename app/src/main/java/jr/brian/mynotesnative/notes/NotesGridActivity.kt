package jr.brian.mynotesnative.notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import jr.brian.mynotesnative.databinding.ActivityNotesGridBinding

class NotesGridActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesGridBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteList: ArrayList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesGridBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setAdapter()
    }

    private fun setAdapter() {
        initData()
        noteAdapter = NoteAdapter(this, noteList)
        binding.apply {
            notesRecyclerView.layoutManager = GridLayoutManager(this@NotesGridActivity, 2)
            notesRecyclerView.adapter = noteAdapter
        }
        initNoteOnSwipe()
    }

    private fun deleteNote(viewHolder: RecyclerView.ViewHolder) {
        val pos = viewHolder.adapterPosition
        noteList.removeAt(pos)
        noteAdapter.notifyItemRemoved(pos)
        Snackbar.make(
            binding.notesRecyclerView,
            "Note Deleted",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun initNoteOnSwipe() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteNote(viewHolder)
            }
        }).attachToRecyclerView(binding.notesRecyclerView)

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteNote(viewHolder)
            }
        }).attachToRecyclerView(binding.notesRecyclerView)
    }

    private fun initData() {
        noteList = ArrayList()
        noteList.add(
            Note(
                title = "Note 1",
                body = "",
                date = "6/29/2022",
                passcode = "",
                bodyFontSize = 11.0,
                textColor = 0xFFFFFFFF.toInt(),
                isStarred = false,
                isLocked = false,
                index = 0
            )
        )

        binding.fab.setOnClickListener {
            noteList.add(
                Note(
                    title = "Note ${noteList.size}",
                    body = "",
                    date = "6/29/2022",
                    passcode = "",
                    bodyFontSize = 11.0,
                    textColor = 0xFFFFFFFF.toInt(),
                    isStarred = false,
                    isLocked = false,
                    index = noteList.size
                )
            )
            noteAdapter.notifyItemInserted(noteList.size)
        }

    }
}