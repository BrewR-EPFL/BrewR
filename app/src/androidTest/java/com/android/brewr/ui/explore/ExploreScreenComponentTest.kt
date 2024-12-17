package com.android.brewr.ui.explore

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExploreScreenComponentTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun toggleDropdownMenuDisplaysOptions() {
    composeTestRule.setContent { ListToggleRow(selectedOption = "Nearby", onOptionSelected = {}) }

    // Assert the dropdown button exists
    composeTestRule.onNodeWithTag("toggleDropdownMenu").assertIsDisplayed().performClick()

    // Assert dropdown options are displayed
    composeTestRule.onNodeWithTag("dropdownMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dropdownMenuItemNearby").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dropdownMenuItemCurated").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dropdownMenuItemOpened").assertIsDisplayed()
  }

  @Test
  fun emptyCoffeeListDisplaysClosedMessage() {
    composeTestRule.setContent { CoffeeList(coffees = emptyList(), onCoffeeClick = {}) }

    // Assert the message for closed coffee shops is displayed
    composeTestRule
        .onNodeWithTag("noOpenCoffeeShopsMessage")
        .assertIsDisplayed()
        .assertTextEquals("No coffee shops found!")
  }
}
