package com.android.brewr.ui.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class NavigationActionsTest {

  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)
  }

  @Test
  fun navigateToCallsController() {
    navigationActions.navigateTo(TopLevelDestinations.OVERVIEW)
    verify(navHostController).navigate(eq(Route.OVERVIEW), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(Screen.OVERVIEW)
    verify(navHostController).navigate(Screen.OVERVIEW)
  }

  @Test
  fun goBackCallsController() {
    navigationActions.goBack()
    verify(navHostController).popBackStack()
  }

  @Test
  fun currentRouteWorksWithDestination() {
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(Route.OVERVIEW)

    assertThat(navigationActions.currentRoute(), `is`(Route.OVERVIEW))
  }

  @Test
  fun navigateToPopUpToCorrect() {
    // Mock the NavGraph and NavDestination
    val mockGraph = mock<NavGraph>()
    val mockStartDestination = mock<NavDestination>()

    // Setup return values for the mock graph and start destination
    whenever(navHostController.graph).thenReturn(mockGraph)
    whenever(mockGraph.findStartDestination()).thenReturn(mockStartDestination)
    whenever(mockStartDestination.id).thenReturn(123)

    // Capture the lambda passed to navigate() using argumentCaptor
    val navOptionsCaptor = argumentCaptor<NavOptionsBuilder.() -> Unit>()

    // Use doNothing() to mock the navigate method
    doNothing()
        .whenever(navHostController)
        .navigate(eq(TopLevelDestinations.OVERVIEW.route), navOptionsCaptor.capture())

    // Call the method under test: navigating to the Overview destination
    navigationActions.navigateTo(TopLevelDestinations.OVERVIEW)

    // Verify that navigate() was called with the correct route and lambda
    verify(navHostController)
        .navigate(eq(TopLevelDestinations.OVERVIEW.route), any<NavOptionsBuilder.() -> Unit>())

    // Simulate execution of the captured lambda to verify popUpTo and other options
    val capturedLambda = navOptionsCaptor.firstValue
    val mockNavOptionsBuilder = mock<NavOptionsBuilder>()
    capturedLambda.invoke(mockNavOptionsBuilder)

    // Verify the interactions inside the lambda block
    val popUpToCaptor = argumentCaptor<Int>()
    val popUpToLambdaCaptor = argumentCaptor<PopUpToBuilder.() -> Unit>()

    // Capture the popUpTo invocation and verify its arguments
    verify(mockNavOptionsBuilder).popUpTo(popUpToCaptor.capture(), popUpToLambdaCaptor.capture())

    // Check that popUpTo was called with the correct start destination ID
    assert(popUpToCaptor.firstValue == 123)

    // Simulate execution of the popUpTo lambda and verify its logic
    val popUpToLambda = popUpToLambdaCaptor.firstValue
    val mockPopUpToBuilder = mock<PopUpToBuilder>()
    popUpToLambda.invoke(mockPopUpToBuilder)

    // Verify the lambda's internal behavior
    verify(mockPopUpToBuilder).saveState = eq(true)
    verify(mockPopUpToBuilder).inclusive = eq(true)

    // Verify the other options set in the lambda
    verify(mockNavOptionsBuilder).launchSingleTop = eq(true)

    // Verify that restoreState is only set when the route is not AUTH
    verify(mockNavOptionsBuilder).restoreState = eq(true)
  }
}
