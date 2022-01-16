package com.notes.ui.details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.notes.R
import com.notes.data.NoteDbo
import com.notes.databinding.FragmentNoteDetailsBinding
import com.notes.di.DependencyManager
import com.notes.di.ViewModelFactory
import com.notes.ui.RootActivity
import com.notes.ui._base.ViewBindingFragment
import com.notes.ui.list.NoteListFragment
import com.notes.ui.list.NoteListViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class NoteDetailsFragment : ViewBindingFragment<FragmentNoteDetailsBinding>(
    FragmentNoteDetailsBinding::inflate
) {

    companion object {
        const val NOTE_KEY = "note_key"
        const val TITLE_KEY = "title_key"
    }

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, factory)[NoteListViewModel::class.java]
    }

    private val detailsVM by lazy {
        ViewModelProvider(this)[DetailsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DependencyManager.inject(this)

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (requireActivity() as RootActivity).navigateTo(NoteListFragment())
            }
        })
    }

    override fun onViewBindingCreated(viewBinding: FragmentNoteDetailsBinding, savedInstanceState: Bundle?) {
        super.onViewBindingCreated(viewBinding, savedInstanceState)

        viewBinding.toolbar.setNavigationOnClickListener {
            (requireActivity() as RootActivity).navigateTo(NoteListFragment())
        }

        val note = arguments?.getSerializable(NOTE_KEY) as? NoteDbo
        if (note != null) {
            detailsVM.setState(DetailsViewModel.State.Update(note))
        }
        else detailsVM.setState(DetailsViewModel.State.New)

        detailsVM.state.observe(viewLifecycleOwner, { state ->
            when(state) {
                is DetailsViewModel.State.New -> {
                    viewBinding.createDateLayout.visibility = View.GONE
                    viewBinding.modifiedDateLayout.visibility = View.GONE
                }
                is DetailsViewModel.State.Update -> {
                    viewBinding.titleEditText.setText(state.note.title)
                    viewBinding.contentEditText.setText(state.note.content)
                    viewBinding.createdDateTextView.text = state.note.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                    viewBinding.modifiedDateTextView.text = state.note.modifiedAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                }
            }
        })

        viewBinding.toolbar.title = arguments?.getString(TITLE_KEY)

        viewBinding.saveButton.setOnClickListener {
            if (note != null) {
                val newDbo = NoteDbo(
                    id = note.id,
                    title = viewBinding.titleEditText.text.toString(),
                    content = viewBinding.contentEditText.text.toString(),
                    createdAt = note.createdAt,
                    modifiedAt = LocalDateTime.now()
                )
                viewModel.saveNote(newDbo, false).observe(viewLifecycleOwner, {
                    if (it) Toast.makeText(requireContext(), getString(R.string.text_note_has_been_updated), Toast.LENGTH_SHORT).show()
                })
            }
            else {
                val newDbo = NoteDbo(
                    title = viewBinding.titleEditText.text.toString(),
                    content = viewBinding.contentEditText.text.toString(),
                    createdAt = LocalDateTime.now(),
                    modifiedAt = LocalDateTime.now()
                )
                viewModel.saveNote(newDbo, true).observe(viewLifecycleOwner, {
                    if (it) Toast.makeText(requireContext(), getString(R.string.text_note_has_been_created), Toast.LENGTH_SHORT).show()
                    (requireActivity() as RootActivity).navigateTo(NoteListFragment())
                })
            }
        }

        viewBinding.deleteButton.setOnClickListener {
            note?.let { n ->
                viewModel.deleteNote(n).observe(viewLifecycleOwner, {
                    if (it) {
                        Toast.makeText(requireContext(), getString(R.string.text_note_has_been_removed), Toast.LENGTH_SHORT).show()
                        (requireActivity() as RootActivity).navigateTo(NoteListFragment())
                    }
                })

            }
        }
    }


}