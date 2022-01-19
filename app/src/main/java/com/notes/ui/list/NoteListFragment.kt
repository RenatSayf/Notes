package com.notes.ui.list

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.notes.R
import com.notes.databinding.FragmentNoteListBinding
import com.notes.databinding.ListItemNoteBinding
import com.notes.di.DependencyManager
import com.notes.di.ViewModelFactory
import com.notes.ui.RootActivity
import com.notes.ui._base.ViewBindingFragment
import com.notes.ui.details.NoteDetailsFragment
import javax.inject.Inject


class NoteListFragment : ViewBindingFragment<FragmentNoteListBinding>(
    FragmentNoteListBinding::inflate
)  {
    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel: NoteListViewModel by lazy {
        ViewModelProvider(this, factory)[NoteListViewModel::class.java]
    }

    private val recyclerViewAdapter = RecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DependencyManager.inject(this)
    }

    override fun onViewBindingCreated(
        viewBinding: FragmentNoteListBinding,
        savedInstanceState: Bundle?
    ) {
        super.onViewBindingCreated(viewBinding, savedInstanceState)

        viewBinding.toolbar.inflateMenu(R.menu.details_screen_menu)

        if (savedInstanceState == null) {
            viewModel.getNotes()
        }

        viewBinding.list.adapter = recyclerViewAdapter
        viewBinding.list.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayout.VERTICAL
            )
        )

        viewBinding.createNoteButton.setOnClickListener {
            val noteDetailsFragment = NoteDetailsFragment()
            (requireActivity() as RootActivity).navigateTo(noteDetailsFragment.apply {
                this.arguments = Bundle().apply {
                    putString(NoteDetailsFragment.TITLE_KEY, this@NoteListFragment.getString(R.string.new_note))
                }
            })
        }

        viewModel.notes.observe(viewLifecycleOwner, {
                if (it != null) {
                    recyclerViewAdapter.setItems(it)

                }
            }
        )

        viewBinding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.sort_by_modified_date_asc -> {
                    viewModel.getSortedNotes(true).observe(viewLifecycleOwner, { list ->
                        list?.let { notes ->
                            viewModel.setNotes(notes)
                        }
                    })
                }
                R.id.sort_by_modified_date_desc -> {
                    viewModel.getSortedNotes(false).observe(viewLifecycleOwner, { list ->
                        list?.let { notes ->
                            viewModel.setNotes(notes)
                        }
                    })
                }
            }
            true
        }
    }

    private inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        private val items = mutableListOf<NoteListItem>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ) = ViewHolder(
            ListItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {
            holder.bind(items[position])
        }

        override fun getItemCount() = items.size

        fun setItems(
            items: List<NoteListItem>
        ) {
            this.items.clear()
            this.items.addAll(items)
            notifyDataSetChanged()
        }

        private inner class ViewHolder(
            private val binding: ListItemNoteBinding
        ) : RecyclerView.ViewHolder(
            binding.root
        ) {

            fun bind(
                note: NoteListItem
            ) {
                binding.titleLabel.text = note.title
                binding.contentLabel.text = note.content

                binding.root.setOnClickListener {

                    viewModel.getNote(note.id).observe(this@NoteListFragment, { dbo ->
                        dbo?.let {
                            val noteDetailsFragment = NoteDetailsFragment()
                            (requireActivity() as RootActivity).navigateTo(noteDetailsFragment.apply {
                                this.arguments = Bundle().apply {
                                    putSerializable(NoteDetailsFragment.NOTE_KEY, dbo)
                                    putString(NoteDetailsFragment.TITLE_KEY, note.title)
                                }
                            })
                        }
                    })
                }
            }

        }

    }

}