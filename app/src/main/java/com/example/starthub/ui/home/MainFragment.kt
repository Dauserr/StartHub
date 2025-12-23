package com.example.starthub.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.starthub.R

/**
 * MainFragment - Home screen / Landing page
 *
 * Features:
 * - Welcome message
 * - App mission and values
 * - Display company vision
 *
 * This is the first screen users see after login.
 * Users can navigate to other screens via bottom navigation bar.
 */
class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // This fragment is purely informational
        // All navigation is handled by bottom navigation bar
        // No click listeners needed!
    }
}