package com.romanpodymov.whatthecommitapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CommitScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadingDisablesRefresh() {
        composeRule.setContent {
            WhatTheCommitTheme { CommitScreen(CommitUiState.Loading, onRefresh = {}) }
        }
        composeRule.onNodeWithContentDescription("Loading commit message").assertIsDisplayed()
        composeRule.onNodeWithText("New commit").assertIsNotEnabled()
    }

    @Test
    fun successDisplaysMessageAndRefreshes() {
        var refreshes = 0
        composeRule.setContent {
            WhatTheCommitTheme {
                CommitScreen(CommitUiState.Success("Fix all the bugs"), onRefresh = { refreshes++ })
            }
        }
        composeRule.onNodeWithText("Fix all the bugs").assertIsDisplayed()
        composeRule.onNodeWithText("New commit").performClick()
        composeRule.runOnIdle { assertEquals(1, refreshes) }
    }

    @Test
    fun errorOffersRetry() {
        var retries = 0
        composeRule.setContent {
            WhatTheCommitTheme { CommitScreen(CommitUiState.Error, onRefresh = { retries++ }) }
        }
        composeRule.onNodeWithText("Could not load a commit message. Please try again.").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()
        composeRule.runOnIdle { assertEquals(1, retries) }
    }
}
