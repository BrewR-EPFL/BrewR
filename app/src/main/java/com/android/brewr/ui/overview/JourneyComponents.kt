package com.android.brewr.ui.overview

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.ui.theme.Purple80

@Composable
fun JourneyImageBox(imageUri: Uri?, imageUrl: String?, onImageClick: () -> Unit, testTag: String) {
  Box(
      modifier =
          Modifier.size(150.dp).border(2.dp, Color.Black).testTag(testTag).clickable {
            onImageClick()
          }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)) {
              Text("Add Photo", color = Color.Black)
              Image(
                  painter = rememberAsyncImagePainter(imageUri ?: imageUrl),
                  contentDescription = "Selected Image",
                  modifier = Modifier.size(120.dp).testTag("selectedImagePreview"))
            }
      }
}

@Composable
fun JourneyDescriptionField(description: String, onDescriptionChange: (String) -> Unit) {
  OutlinedTextField(
      value = description,
      onValueChange = onDescriptionChange,
      label = { Text("Description") },
      placeholder = { Text("Capture your coffee experience") },
      modifier = Modifier.fillMaxWidth().height(150.dp).testTag("inputJourneyDescription"))
}

@Composable
fun CoffeeShopCheckRow(
    isYesSelected: Boolean,
    onCheckChange: () -> Unit,
    expanded: Boolean,
    coffeeShopName: String,
    onCoffeeShopNameChange: (String) -> Unit
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.testTag("coffeeShopCheckRow").clickable { onCheckChange() }) {
        Icon(
            imageVector = if (isYesSelected) Icons.Outlined.Check else Icons.Outlined.Close,
            contentDescription = if (isYesSelected) "Checked" else "Unchecked",
            tint = Color.Black)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "At a coffee shop", color = Color.Black)
      }

  if (expanded) {
    OutlinedTextField(
        value = coffeeShopName,
        onValueChange = onCoffeeShopNameChange,
        label = { Text("Coffee Shop Name") },
        placeholder = { Text("Enter the name") },
        modifier = Modifier.fillMaxWidth().testTag("coffeeShopNameField"))
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeOriginDropdownMenu(
    coffeeOrigin: CoffeeOrigin,
    onCoffeeOriginChange: (CoffeeOrigin) -> Unit
) {
  val focusRequester = remember { FocusRequester() }
  var coffeeOriginExpand by remember { mutableStateOf(false) }
  var expanded by remember { mutableStateOf(false) }

  // Wrap with ExposedDropdownMenuBox for the dropdown functionality
  Text(
      text = "Origin",
      fontSize = 16.sp, // Adjust the font size for the title
      fontWeight = FontWeight.Bold, // Make the title bold
  )
  ExposedDropdownMenuBox(
      expanded = coffeeOriginExpand,
      onExpandedChange = { coffeeOriginExpand = !coffeeOriginExpand }) {
        // TextField displaying the selected coffee origin
        TextField(
            value = coffeeOrigin.name.replace("DEFAULT", "Select the origin"),
            onValueChange = {},
            readOnly = true, // Prevent typing in the TextField
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier =
                Modifier.menuAnchor()
                    .fillMaxWidth() // Set the width of the text field to fill the
                    // parent width
                    .testTag("inputCoffeeOrigin") // Add a test tag for testing
                    .focusRequester(focusRequester) // Attach the FocusRequester
                    .clickable {
                      expanded = true // Trigger dropdown when clicked
                    },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusRequester.requestFocus() }))

        // DropdownMenu with fixed height and scrolling capability
        DropdownMenu(
            expanded = coffeeOriginExpand,
            onDismissRequest = { coffeeOriginExpand = false },
            modifier =
                Modifier.height(200.dp) // Limit height of the dropdown (set a fixed value)
                    .focusRequester(focusRequester) // Attach the FocusRequester
                    .testTag("dropdownMenuCoffeeOrigin") // Add a test tag for testing
            ) {
              CoffeeOrigin.values().drop(1).forEach { origin ->
                DropdownMenuItem(
                    text = { Text(origin.name) },
                    onClick = {
                      onCoffeeOriginChange(origin) // Set the selected coffee origin
                      expanded = false // Close the dropdown
                    },
                    modifier = Modifier.padding(8.dp).testTag("YourJourneyTitle"))
              }
            }
      }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun BrewingMethodField(
    brewingMethod: BrewingMethod,
    onBrewingMethodChange: (BrewingMethod) -> Unit
) {
  Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(2.dp) // Space between title and buttons
      ) {
        Text(
            text = "Brewing Method",
            fontSize = 16.sp, // Adjust the font size for the title
            fontWeight = FontWeight.Bold, // Make the title bold
        )

        FlowRow(modifier = Modifier.padding(16.dp)) {
          BrewingMethod.values().drop(1).forEach { method ->
            // Determine if this method is the currently selected one
            val isSelected = brewingMethod == method

            // Use Button or OutlinedButton based on selection
            if (isSelected) {
              Button(
                  onClick = { onBrewingMethodChange(method) },
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.padding(4.dp).testTag("Button:${method.name}"),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = Purple80, contentColor = Color.White)) {
                    Text(method.name.replace("_", " "), modifier = Modifier.padding(4.dp))
                  }
            } else {
              OutlinedButton(
                  onClick = { onBrewingMethodChange(method) },
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.padding(4.dp).testTag("Button:${method.name}"),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  colors =
                      ButtonDefaults.outlinedButtonColors(
                          contentColor = Color(0xFF000000),
                      )) {
                    Text(method.name.replace("_", " "), modifier = Modifier.padding(4.dp))
                  }
            }
          }
        }
      }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CoffeeTasteField(coffeeTaste: CoffeeTaste, onCoffeeTasteChange: (CoffeeTaste) -> Unit) {
  Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(2.dp) // Space between title and buttons
      ) {
        Text(
            text = "Taste",
            fontSize = 16.sp, // Adjust the font size for the title
            fontWeight = FontWeight.Bold, // Make the title bold
        )

        FlowRow(modifier = Modifier.padding(16.dp)) {
          CoffeeTaste.values().drop(1).forEach { taste ->
            // Determine if this method is the currently selected one
            val isSelected = coffeeTaste == taste

            // Use Button or OutlinedButton based on selection
            if (isSelected) {
              Button(
                  onClick = { onCoffeeTasteChange(taste) },
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.padding(4.dp).testTag("Button:${taste.name}"),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = Purple80, contentColor = Color.White)) {
                    Text(taste.name.replace("_", " "), modifier = Modifier.padding(4.dp))
                  }
            } else {
              OutlinedButton(
                  onClick = { onCoffeeTasteChange(taste) },
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.padding(4.dp).testTag("Button:${taste.name}"),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  colors =
                      ButtonDefaults.outlinedButtonColors(
                          contentColor = Color(0xFF000000),
                      )) {
                    Text(
                        taste.name.replace("_", " "),
                        modifier = Modifier.padding(4.dp).testTag("Button:${taste.name}"))
                  }
            }
          }
        }
      }
}

@Composable
fun CoffeeRateField(coffeeRate: CoffeeRate, onCoffeeRateChange: (CoffeeRate) -> Unit) {
  Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(2.dp) // Space between title and buttons
      ) {
        Text(
            text = "Rate",
            fontSize = 16.sp, // Adjust the font size for the title
            fontWeight = FontWeight.Bold, // Make the title bold
        )

        // Map CoffeeRate to the number of stars
        val starCount =
            coffeeRate.ordinal // ordinal gives you 0-based index, so we don't add 1 due to
        // the default parameter
        Row(
            modifier = Modifier.fillMaxWidth().testTag("rateRow"), // Add a test tag for testing
            horizontalArrangement = Arrangement.Center // Center the star icons
            ) {
              for (i in 1..5) {
                if (i <= starCount) {
                  Icon(
                      imageVector = Icons.Filled.Star,
                      contentDescription = "Filled Star $i",
                      tint = Color(0xFFFFD700), // Gold color for filled star
                      modifier =
                          Modifier.size(40.dp).testTag("FilledStar$i").clickable {
                            // Update the coffeeRate when the star is clicked
                            onCoffeeRateChange(CoffeeRate.values()[i])
                          })
                } else {
                  Icon(
                      imageVector = Icons.Outlined.Star,
                      contentDescription = "Outlined Star $i",
                      tint = Color(0xFF312F2F), // Same gold color for consistency
                      modifier =
                          Modifier.size(40.dp).testTag("OutlinedStar$i").clickable {
                            // Update the coffeeRate when the star is clicked
                            onCoffeeRateChange(CoffeeRate.values()[i])
                          })
                }
              }
            }
      }
}

@Composable
fun DateField(date: String, onDateChange: (String) -> Unit) {
  Column {
    // Label Text
    Text(
        text = "Date",
        modifier = Modifier.padding(bottom = 20.dp),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold)

    // Date Text
    TextField(
        value = date,
        onValueChange = { onDateChange(it) },
        label = { Text("DD/MM/YYYY") },
        placeholder = { Text(date) },
        modifier = Modifier.fillMaxWidth().testTag("inputDate"))
  }
}
