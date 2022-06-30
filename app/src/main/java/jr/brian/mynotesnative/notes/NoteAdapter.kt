package jr.brian.mynotesnative.notes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jr.brian.mynotesnative.databinding.NoteBinding

class NoteAdapter(private val context: Context, private val notes: List<Note>) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private lateinit var binding: NoteBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = NoteBinding.inflate(layoutInflater, parent, false)
        return NoteViewHolder(binding.root)

    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.apply {
            val note = notes[position]
            bind(note)
            itemView.setOnClickListener {
                val intent =
                    Intent(context, NoteEditorActivity::class.java)
                intent.putExtra(NOTE_DATA, note)
                context.startActivity(intent)
            }
        }
    }

    inner class NoteViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bind(note: Note) {
            binding.apply {
                noteTitle.text = note.title
                date.text = note.date
            }

        }
    }

    companion object {
        const val NOTE_DATA = "note"
    }
}