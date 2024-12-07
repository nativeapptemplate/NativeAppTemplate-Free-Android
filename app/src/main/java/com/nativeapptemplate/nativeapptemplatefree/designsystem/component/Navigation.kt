/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nativeapptemplate.nativeapptemplatefree.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NatTheme

/**
 * Now in Android navigation bar item with icon and label content slots. Wraps Material 3
 * [NavigationBarItem].
 *
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param icon The item icon content.
 * @param modifier Modifier to be applied to this item.
 * @param selectedIcon The item icon content when selected.
 * @param enabled controls the enabled state of this item. When `false`, this item will not be
 * clickable and will appear disabled to accessibility services.
 * @param label The item text label content.
 * @param alwaysShowLabel Whether to always show the label for this item. If false, the label will
 * only be shown when this item is selected.
 */
@Composable
fun RowScope.NatNavigationBarItem(
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  alwaysShowLabel: Boolean = true,
  icon: @Composable () -> Unit,
  selectedIcon: @Composable () -> Unit = icon,
  label: @Composable (() -> Unit)? = null,
) {
  NavigationBarItem(
    selected = selected,
    onClick = onClick,
    icon = if (selected) selectedIcon else icon,
    modifier = modifier,
    enabled = enabled,
    label = label,
    alwaysShowLabel = alwaysShowLabel,
    colors = NavigationBarItemDefaults.colors(
      selectedIconColor = NatNavigationDefaults.navigationSelectedItemColor(),
      unselectedIconColor = NatNavigationDefaults.navigationContentColor(),
      selectedTextColor = NatNavigationDefaults.navigationSelectedItemColor(),
      unselectedTextColor = NatNavigationDefaults.navigationContentColor(),
      indicatorColor = NatNavigationDefaults.navigationIndicatorColor(),
    ),
  )
}

/**
 * Now in Android navigation bar with content slot. Wraps Material 3 [NavigationBar].
 *
 * @param modifier Modifier to be applied to the navigation bar.
 * @param content Destinations inside the navigation bar. This should contain multiple
 * [NavigationBarItem]s.
 */
@Composable
fun NatNavigationBar(
  modifier: Modifier = Modifier,
  content: @Composable RowScope.() -> Unit,
) {
  NavigationBar(
    modifier = modifier,
    contentColor = NatNavigationDefaults.navigationContentColor(),
    tonalElevation = 0.dp,
    content = content,
  )
}

@ThemePreviews
@Composable
fun NatNavigationBarPreview() {
  val items = listOf("Shops", "Settings")
  val icons = listOf(
    Icons.Outlined.Storefront,
    Icons.Outlined.Settings,
  )
  val selectedIcons = listOf(
    Icons.Rounded.Storefront,
    Icons.Rounded.Settings,
  )

  NatTheme {
    NatNavigationBar {
      items.forEachIndexed { index, item ->
        NatNavigationBarItem(
          icon = {
            Icon(
              imageVector = icons[index],
              contentDescription = item,
            )
          },
          selectedIcon = {
            Icon(
              imageVector = selectedIcons[index],
              contentDescription = item,
            )
          },
          label = { Text(item) },
          selected = index == 0,
          onClick = { },
        )
      }
    }
  }
}

/**
 * Now in Android navigation default values.
 */
object NatNavigationDefaults {
  @Composable
  fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

  @Composable
  fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

  @Composable
  fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}
