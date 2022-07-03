package jr.brian.mynotesnative.notes

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import jr.brian.mynotesnative.R
import jr.brian.mynotesnative.databinding.NoteBinding
import jr.brian.mynotesnative.databinding.PasscodeDialogBinding

class NoteAdapter(private val context: Context, private val notes: List<Note>) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private lateinit var binding: NoteBinding
    private lateinit var passcodeDialogBinding: PasscodeDialogBinding
//    private var bundle: Bundle =
//        ActivityOptions.makeSceneTransitionAnimation(context as AppCompatActivity).toBundle()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = NoteBinding.inflate(layoutInflater, parent, false)
        passcodeDialogBinding =
            PasscodeDialogBinding.inflate(layoutInflater, parent, false)
        return NoteViewHolder(binding.root)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, index: Int) {
        holder.apply {
            val note = notes[index]
            bind(note)
            itemView.setOnClickListener {
                startNoteEditorActivity(note, index)
            }
        }
    }

    inner class NoteViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bind(note: Note) {
            binding.apply {
                noteTitle.text = note.title
                date.text = note.date
                if (note.isStarred == "true") {
                    starIcon.setImageResource(R.drawable.full_star_icon)
                }
                if (note.isLocked == "true") {
                    lockIcon.setImageResource(R.drawable.lock_icon)
                }
            }
        }
    }

    private fun startNoteEditorActivity(note: Note, index: Int) {
        val intent =
            Intent(context, NoteEditorActivity::class.java)
        intent.putExtra(NOTE_DATA, note)
        intent.putExtra("mode", "update")
        intent.putExtra("index", index)
        context.startActivity(intent)
    }

    companion object {
        const val NOTE_DATA = "note"
        const val INDEX = "index"
    }
}