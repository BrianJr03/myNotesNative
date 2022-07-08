package jr.brian.mynotesnative.notes

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.*
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.snackbar.Snackbar
import jr.brian.mynotesnative.LandingActivity
import jr.brian.mynotesnative.R
import jr.brian.mynotesnative.auth_activities.SignInActivity
import jr.brian.mynotesnative.data.Note
import jr.brian.mynotesnative.databinding.ActivityNotesGridBinding
import jr.brian.mynotesnative.databinding.NavHeaderBinding
import jr.brian.mynotesnative.db.DatabaseHelper
import jr.brian.mynotesnative.db.PantryHelper

class NotesGridActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesGridBinding
    private lateinit var navHeaderBinding: NavHeaderBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var pantryHelper: PantryHelper
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteList: ArrayList<Note>
    private lateinit var favList: ArrayList<Note>
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var fullName: String

    private var areAllNotesDisplayed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesGridBinding.inflate(layoutInflater)
        navHeaderBinding = NavHeaderBinding.inflate(layoutInflater)
        navHeaderBinding.animationView.setMinAndMaxFrame(67, 120)
        pantryHelper = PantryHelper()
        setContentView(binding.root)
        init()
        supportActionBar?.hide()
    }

    private fun init() {
        noteList = ArrayList()
        favList = ArrayList()
        setAdapter(noteList)
        fetchSqlData()
        initFullName()
        initDrawer()
        initListeners()
        initNoteOnSwipe()
        initSharedPref()
    }

    private fun fetchSqlData() {
        var note: Note
        databaseHelper = DatabaseHelper(this)
        val cursor = databaseHelper.getNotes()
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
        if (noteList.size < 1) {
            binding.noNotesIv.visibility = View.VISIBLE
        }
    }

    private fun initDrawer() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open, R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.grid_item -> {
                    setGridLayout()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.linear_item -> {
                    setLinearLayout()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.st_grid_item -> {
                    setStaggeredLayout()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.toggle_favorites -> {
                    toggleFavorites()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.sign_out_item -> {
                    signOut()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
    }

    private fun initFullName() {
        fullName =
            intent.extras?.getString("user") ?: "Full Name"
        navHeaderBinding.fullNameTv.text = fullName
    }

    private fun initListeners() {
        binding.menu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.pageTitle.setOnClickListener {
            toggleFavorites()
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

    private fun deleteNote(viewHolder: RecyclerView.ViewHolder) {
        val pos = viewHolder.adapterPosition
        databaseHelper.deleteNote(noteList[pos])
        pantryHelper.deleteNote(noteList[pos], binding.root)
        Log.i("RESPONSE_DELETED", noteList[pos].title)
        noteList.removeAt(pos)
//        favList.apply {
//            if (isNotEmpty()) {
//                if (favList.contains(noteList[pos])) {
//                    favList.removeAt(pos)
//                }
//            }
//        }
        noteAdapter.notifyItemRemoved(pos)
        Snackbar.make(
            binding.notesRecyclerView,
            "Note Deleted",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    @SuppressLint("Range")
    private fun getCurrentNote(cursor: Cursor): Note {
        return Note(
            title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE)),
            body = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BODY)),
            date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE)),
            passcode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PASSCODE)),
            bodyFontSize = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BODY_FONT_SIZE)),
            textColor = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEXT_COLOR)),
            isStarred = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IS_STARRED)),
            isLocked = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IS_LOCKED)),
            index = noteList.size
        )
    }

    private fun toggleFavorites() {
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

    private fun signOut() {
        editor.apply {
            clear()
            apply()
        }
        startActivity(
            Intent(
                this@NotesGridActivity,
                LandingActivity::class.java
            )
        )
        finish()
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(a)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}