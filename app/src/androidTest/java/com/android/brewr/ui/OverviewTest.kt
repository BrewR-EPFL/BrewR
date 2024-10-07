package com.android.brewr.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.brewr.MainActivity
import com.android.brewr.ui.screens.OverviewScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun test() = run {
    step("Start Main Activity") {
      ComposeScreen.onComposeScreen<OverviewScreen>(composeTestRule) {
        assertIsDisplayed()
        appTitle {
          assertIsDisplayed()
          assertTextEquals("BrewR")
        }
        addButton { performClick() }
        accountButton { performClick() }
      }
    }
  }
}
