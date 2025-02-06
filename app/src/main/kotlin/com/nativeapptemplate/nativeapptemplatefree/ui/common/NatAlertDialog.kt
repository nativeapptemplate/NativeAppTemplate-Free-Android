package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun NatAlertDialog(
  dialogTitle: String? = null,
  confirmButtonTitle: String? = null,
  onDismissRequest: (() -> Unit)? = null,
  onConfirmation: (() -> Unit)? = null,
  dialogText: String? = null,
  confirmButtonTitleColor: Color? = null,
  icon: ImageVector? = null,
) {
  AlertDialog(
    icon = {
      icon?.let {
        Icon(icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier
            .size(48.dp)
        )
      }
    },
    title = {
      dialogTitle?.let {
        Text(dialogTitle)
      }
    },
    text = {
      dialogText?.let {
        Text(dialogText)
      }
    },
    onDismissRequest = {
      onDismissRequest?.let {
        it()
      }
    },
    confirmButton = {
      onConfirmation?.let {
        TextButton(
          onClick = {
            it()
          }
        ) {
          Text(
            confirmButtonTitle ?: "OK",
            color = confirmButtonTitleColor ?: Color.Red
          )
        }
      }
    },
    dismissButton = {
      onDismissRequest?.let {
        TextButton(
          onClick = {
            it()
          }
        ) {
          Text("Dismiss")
        }
      }
    }
  )
}
