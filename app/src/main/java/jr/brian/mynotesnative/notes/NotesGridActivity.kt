package jr.brian.mynotesnative.notes

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.snackbar.Snackbar
import jr.brian.mynotesnative.MainActivity
import jr.brian.mynotesnative.databinding.ActivityNotesGridBinding

class NotesGridActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesGridBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteList: ArrayList<Note>
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesGridBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        initSharedPref()
        initData()
        setAdapter()
        enableSignOut()
        editor.apply {
            clear()
            apply()
        }
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

    private fun enableSignOut() {
        binding.signOut.setOnClickListener {
            signOut()
        }
    }

    private fun initData() {
        noteList = ArrayList()
        for (i in 1..3) {

            noteList.add(
                Note(
                    title = "Note $i",
                    body = "This is a test note.",
                    date = "6/30/2022",
                    passcode = "",
                    bodyFontSize = 11.0,
                    textColor = 0xFFFFFFFF.toInt(),
                    isStarred = false,
                    isLocked = false,
                    index = i
                )
            )
        }

        binding.fab.setOnClickListener {
            noteList.add(
                Note(
                    title = "Note ${noteList.size + 1}",
                    body = "This is a test note.",
                    date = "6/30/2022",
                    passcode = "",
                    bodyFontSize = 11.0,
                    textColor = 0xFFFFFFFF.toInt(),
                    isStarred = false,
                    isLocked = false,
                    index = noteList.size + 1
                )
            )
            noteAdapter.notifyItemInserted(noteList.size)
        }
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

    private fun initSharedPref() {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        sp = EncryptedSharedPreferences.create(
            MainActivity.FILENAME,
            mainKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

        editor = sp.edit()
    }

    private fun setAdapter() {
        noteAdapter = NoteAdapter(this, noteList)
        binding.apply {
            notesRecyclerView.layoutManager = GridLayoutManager(this@NotesGridActivity, 2)
            notesRecyclerView.adapter = noteAdapter
        }
        initNoteOnSwipe()
    }

    private fun signOut() {
        editor.apply {
            clear()
            apply()
        }
        Snackbar.make(binding.root, "Signed Out", Snackbar.LENGTH_SHORT).show()
        startActivity(Intent(this@NotesGridActivity, MainActivity::class.java))
    }
}