package com.blueduck.dajumgum.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.blueduck.dajumgum.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomSpinner(
    categoryTags: List<String>,
    selectedCategoryTags: List<String>,
    tagAsString: Boolean = false,
    onTagSelection: (String) -> Unit,
    onTagRemove: (String) -> Unit,
) {

    val mTextFieldSize = remember { mutableStateOf(Size.Zero) }

    Box {
        var expanded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxWidth().
                        onGloballyPositioned { coordinates ->
                            mTextFieldSize.value = coordinates.size.toSize()
                        }
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { expanded = !expanded }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Select")
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "")
                }

                if (tagAsString) {
                    val categoryString = selectedCategoryTags.joinToString(separator = "")
                    if (categoryString.isNotEmpty()) {
                        AssistChip(
                            shape = RoundedCornerShape(20.dp),
                            onClick = {},
                            label = {
                                Text(
                                    text = categoryString,
                                    color = colorResource(id = R.color.orange)
                                )
                            },
                            trailingIcon = { Icon(
                                imageVector = Icons.Default.Close,
                                modifier = Modifier.clickable { onTagRemove("") },
                                contentDescription = "",
                                tint = colorResource(id = R.color.orange)
                            )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(end = 4.dp)
                        )

                    }
                } else {
                    FlowRow {
                        selectedCategoryTags.forEach { tag ->
                            AssistChip(
                                shape = RoundedCornerShape(20.dp),
                                onClick = {},
                                label = {
                                    Text(
                                        text = tag,
                                        color = colorResource(id = R.color.orange)
                                    )
                                },
                                trailingIcon = { Icon(
                                    imageVector = Icons.Default.Close,
                                    modifier = Modifier.clickable { onTagRemove(tag) },
                                    contentDescription = "",
                                    tint = colorResource(id = R.color.orange)
                                )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }

                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { mTextFieldSize.value.width.toDp()})
        ) {
            FlowRow {
                categoryTags.forEach { tag ->
                    AssistChip(
                        shape = RoundedCornerShape(20.dp),
                        label = {
                            Text(
                                text = tag,
                                color = colorResource(id = R.color.orange)
                            )
                        },
                        onClick = {
                            onTagSelection(tag)
                            expanded = false
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}