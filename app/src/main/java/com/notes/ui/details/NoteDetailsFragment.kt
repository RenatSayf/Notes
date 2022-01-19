package com.notes.ui.details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.notes.R
import com.notes.data.NoteDbo
import com.notes.databinding.FragmentNoteDetailsBinding
import com.notes.di.DependencyManager
import com.notes.di.ViewModelFactory
import com.notes.ui.RootActivity
import com.notes.ui._base.ViewBindingFragment
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

    private val detailsVM by lazy {
        ViewModelProvider(this, factory)[DetailsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DependencyManager.inject(this)
    }

    override fun onViewBindingCreated(viewBinding: FragmentNoteDetailsBinding, savedInstanceState: Bundle?) {
        super.onViewBindingCreated(viewBinding, savedInstanceState)

        viewBinding.toolbar.title = arguments?.getString(TITLE_KEY)
        viewBinding.toolbar.setNavigationOnClickListener {
            (requireActivity() as RootActivity).onBackPressed()
        }

        val note = arguments?.getSerializable(NOTE_KEY) as? NoteDbo
        if (note != null) {
            viewBinding.titleEditText.setText(note.title)
            viewBinding.contentEditText.setText(note.content)
            viewBinding.createdDateTextView.text = note.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            viewBinding.modifiedDateTextView.text = note.modifiedAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        }
        else {
            viewBinding.createDateLayout.visibility = View.GONE
            viewBinding.modifiedDateLayout.visibility = View.GONE
            viewBinding.deleteButton.visibility = View.GONE
        }

        viewBinding.saveButton.setOnClickListener {
            if (note != null) {
                val newDbo = note.copy(
                    title = viewBinding.titleEditText.text.toString(),
                    content = viewBinding.contentEditText.text.toString(),
                    createdAt = note.createdAt,
                    modifiedAt = LocalDateTime.now()
                )
                detailsVM.saveNote(newDbo, false).observe(viewLifecycleOwner, {
                    if (it) {
                        Toast.makeText(requireContext(), getString(R.string.text_note_has_been_updated), Toast.LENGTH_SHORT).show()
                        (requireActivity() as RootActivity).onBackPressed()
                    }
                })
            }
            else {
                val newDbo = NoteDbo(
                    title = viewBinding.titleEditText.text.toString(),
                    content = viewBinding.contentEditText.text.toString(),
                    createdAt = LocalDateTime.now(),
                    modifiedAt = LocalDateTime.now()
                )
                detailsVM.saveNote(newDbo, true).observe(viewLifecycleOwner, {
                    if (it) {
                        Toast.makeText(requireContext(), getString(R.string.text_note_has_been_created), Toast.LENGTH_SHORT).show()
                        (requireActivity() as RootActivity).onBackPressed()
                    }
                })
            }
        }

        viewBinding.deleteButton.setOnClickListener {
            note?.let { n ->
                detailsVM.deleteNote(n).observe(viewLifecycleOwner, {
                    if (it) {
                        Toast.makeText(requireContext(), getString(R.string.text_note_has_been_removed), Toast.LENGTH_SHORT).show()
                        (requireActivity() as RootActivity).onBackPressed()
                    }
                })

            }
        }
    }

}