package com.msandypr.thesandynews.ui.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleButton = view.findViewById(R.id.toggleButton)
        statusTextView = view.findViewById(R.id.statusTextView)

        originalButtonBackgroundColor = ContextCompat.getColor(requireContext(), R.color.buttonRootCheckBackgroundColor)

        val buttonColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.textColorRooted),
                originalButtonBackgroundColor
            )
        )

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
    }

    private fun displayRootPopup(status: String, isRooted: Boolean) {
        Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()

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
}