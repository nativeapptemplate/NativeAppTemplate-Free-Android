package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Space below the last row of a list on a screen with a floating action button, so the last row
 * (and the actions it reveals when swiped) can scroll above the button instead of staying under it.
 * The tallest button is SubmitFab (64.dp; FloatingActionButton is 56.dp), the Scaffold places it
 * 16.dp above the bottom, and 16.dp more keeps the row clear of it: 64 + 16 + 16 = 96.
 */
val FabClearance = 96.dp

/** The lists' 24.dp margin, without the bottom: the bottom is [fabClearingContentPadding]. */
val fabListModifier: Modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)

/** Content padding for a list on a screen with a floating action button; see [FabClearance]. */
val fabClearingContentPadding = PaddingValues(bottom = FabClearance)
