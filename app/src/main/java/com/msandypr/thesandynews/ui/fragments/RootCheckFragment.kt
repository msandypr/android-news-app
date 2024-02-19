package com.msandypr.thesandynews.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.msandypr.thesandynews.R
import com.scottyab.rootbeer.RootBeer

class RootCheckFragment : Fragment() {
    private lateinit var toggleButton: ToggleButton
    private lateinit var statusTextView: TextView

    private var originalButtonBackgroundColor: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_root_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleButton = view.findViewById(R.id.toggleButton)
        statusTextView = view.findViewById(R.id.statusTextView)

        originalButtonBackgroundColor = ContextCompat.getColor(requireContext(), R.color.buttonRootCheckBackgroundColor)

        val buttonColors = ContextCompat.getColorStateList(requireContext(), R.color.buttonRootCheckBackgroundColor)
        toggleButton.backgroundTintList = buttonColors

        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkRootStatus()
            } else {
                toggleButton.backgroundTintList = buttonColors
                statusTextView.text = "Perangkat Terdeteksi\n..."
            }
        }
    }

    private fun checkRootStatus() {
        val rootBeer = RootBeer(requireContext())
        val isRooted = rootBeer.isRooted

        val status: String = if (rootBeer.isRooted) {
            // Perangkat sudah di-root
            "Perangkat Terdeteksi\nRooted"
        } else {
            // Perangkat belum di-root
            "Perangkat Terdeteksi\nNon-Rooted"
        }

        displayRootPopup(status, isRooted)

        if (isRooted) {
            showExitConfirmationDialog()
        }
    }

    private fun displayRootPopup(status: String, isRooted: Boolean) {

        val parts = status.split("\n")
        if (parts.size == 2) {
            val rootPart = parts[1]

            val textColorResId = if (isRooted) R.color.textColorRooted else R.color.textColorNonRooted

            val spannableString = SpannableString(status)
            spannableString.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), textColorResId)),
                status.indexOf(rootPart),
                status.indexOf(rootPart) + rootPart.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            statusTextView.text = spannableString

            val buttonColorResId = if (isRooted) R.color.textColorRooted else R.color.textColorNonRooted
            toggleButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), buttonColorResId)
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Perangkat Terdeteksi di Root")
            .setMessage("Perangkat Anda terdeteksi di-root. Anda akan keluar dari aplikasi.")
            .setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                activity?.finish()
            }
            .setCancelable(false)
            .show()
    }
}
