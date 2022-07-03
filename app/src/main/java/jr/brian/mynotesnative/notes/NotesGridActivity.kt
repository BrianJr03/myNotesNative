package jr.brian.mynotesnative.notes

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.snackbar.Snackbar
import jr.brian.mynotesnative.R
import jr.brian.mynotesnative.auth_activities.SignInActivity
import jr.brian.mynotesnative.databinding.ActivityNotesGridBinding
import jr.brian.mynotesnative.db.DatabaseHelper

class NotesGridActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesGridBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteList: ArrayList<Note>
    private lateinit var favList: ArrayList<Note>
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var areAllNotesDisplayed = true
//    private lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        supportActionBar?.hide()
    }

    private fun deleteNote(viewHolder: RecyclerView.ViewHolder) {
        val pos = viewHolder.adapterPosition
        databaseHelper.deleteNote(noteList[pos])
        noteList.removeAt(pos)
        favList.removeAt(pos)
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

    @SuppressLint("Range")
    private fun getCurrentNote(cursor: Cursor) = Note(
        title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE)),
        body = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BODY)),
        date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE)),
        passcode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PASSCODE)),
        bodyFontSize = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.TITLE)),
        textColor = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TITLE)),
        isStarred = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IS_STARRED)),
        isLocked = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IS_LOCKED)),
        index = noteList.size
    )

    private fun init() {
        binding = ActivityNotesGridBinding.inflate(layoutInflater)
//        bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        databaseHelper = DatabaseHelper(this)
        noteList = ArrayList()
        favList = ArrayList()
        setAdapter(noteList)
        initData()
        if (noteList.size < 1) {
            binding.noNotesIv.visibility = View.VISIBLE
        }
        initListeners()
        initNoteOnSwipe()
        initSharedPref()
        enableSignOut()
    }

    private fun initData() {
        val cursor = databaseHelper.getNotes()
        var note: Note
        if (cursor != null) {
            if (cursor.count != 0) {
                cursor.moveToFirst()
                note = getCurrentNote(cursor)
                if (note.isStarred == "true") {
                    favList.add(note)
                }
                noteList.add(note)
                while (cursor.moveToNext()) {
                    note = getCurrentNote(cursor)
                    if (note.isStarred == "true") {
                        favList.add(note)
                    }
                    noteList.add(note)
                }
                noteAdapter.notifyItemInserted(noteList.size)
            }
        }
    }

    private fun initListeners() {
        binding.pageTitle.setOnClickListener {
            areAllNotesDisplayed = !areAllNotesDisplayed
            if (areAllNotesDisplayed) {
                binding.pageTitle.text = getString(R.string.mynotesnative)
                setAdapter(noteList)
                if (noteList.size < 1) {
                    binding.noNotesIv.visibility = View.VISIBLE
                } else {
                    binding.noNotesIv.visibility = View.INVISIBLE
                }
            } else {
                binding.pageTitle.text = getString(R.string.favorites)
                setAdapter(favList)
                if (favList.size < 1) {
                    binding.noNotesIv.visibility = View.VISIBLE
                } else {
                    binding.noNotesIv.visibility = View.INVISIBLE
                }
            }
        }
        binding.menu.setOnClickListener {
            showMenuOptions()
        }
        binding.fab.setOnClickListener {
            val intent =
                Intent(this, NoteEditorActivity::class.java)
            intent.putExtra("index", noteList.size)
            startActivity(intent)
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
            SignInActivity.FILENAME,
            mainKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        editor = sp.edit()
    }

    private fun setGridLayout() {
        binding.notesRecyclerView.layoutManager =
            GridLayoutManager(this@NotesGridActivity, 2)
        binding.notesRecyclerView.adapter = noteAdapter
    }

    private fun setStaggeredLayout() {
        binding.notesRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.notesRecyclerView.adapter = noteAdapter
    }

    private fun setLinearLayout() {
        binding.notesRecyclerView.layoutManager =
            LinearLayoutManager(this@NotesGridActivity)
        binding.notesRecyclerView.adapter = noteAdapter
    }

    private fun setAdapter(list: List<Note>) {
        noteAdapter = NoteAdapter(this, list)
        setGridLayout()
    }

    private fun showMenuOptions() {
        val options = arrayOf("Grid", "Linear", "Staggered")
        val builder = AlertDialog.Builder(this)
            .setTitle("Choose Note layout")
            .setSingleChoiceItems(options, -1) { d, pos ->
                when (options[pos]) {
                    "Grid" -> setGridLayout()
                    "Linear" -> setLinearLayout()
                    "Staggered" -> setStaggeredLayout()
                }
                d.dismiss()
            }
        builder.create().show()
    }

    private fun signOut() {
        editor.apply {
            clear()
            apply()
        }
        startActivity(
            Intent(
                this@NotesGridActivity,
                SignInActivity::class.java
            )
        )
        finish()
    }
}