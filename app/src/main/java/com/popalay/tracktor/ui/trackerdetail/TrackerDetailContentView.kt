package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.ui.dialog.UpdateTrackedValueDialog
import com.popalay.tracktor.ui.trackerdetail.TrackerDetailWorkflow.Action
import com.popalay.tracktor.ui.trackerdetail.TrackerDetailWorkflow.State
import com.popalay.tracktor.ui.widget.AnimatedSnackbar
import com.popalay.tracktor.ui.widget.TrackerCategoryList
import com.popalay.tracktor.utils.inject

@Composable
fun TrackerDetailContentView(
    state: State,
    onAction: (Action) -> Unit
) {
    val insets = WindowInsetsAmbient.current
    Scaffold(
        topBar = {
            TrackerDetailsAppBar(
                requireNotNull(state.trackerWithRecords),
                onArrowClicked = { onAction(Action.CloseScreen) },
                onUndoClicked = { onAction(Action.DeleteLastRecordClicked) },
                onDeleteClicked = { onAction(Action.DeleteTrackerClicked) }
            )
        },
        floatingActionButtonPosition = Scaffold.FabPosition.Center,
        floatingActionButton = {
            val formatter: ValueRecordFormatter by inject()

            Column(horizontalGravity = Alignment.CenterHorizontally) {
                FloatingActionButton(
                    onClick = { onAction(Action.AddRecordClicked) },
                    modifier = Modifier.offset(y = -insets.bottom)
                ) {
                    Icon(Icons.Default.Add)
                }
                val message = state.trackerWithRecords?.tracker?.let { formatter.format(it, state.recordInDeleting) } ?: ""
                AnimatedSnackbar(
                    message = message,
                    actionText = "UNDO",
                    shouldDisplay = state.recordInDeleting != null,
                    onActionClick = { onAction(Action.UndoDeletingClicked) }
                )
            }
        }
    ) {
        Column {
            if (state.isAddRecordDialogShowing) {
                UpdateTrackedValueDialog(
                    tracker = requireNotNull(state.trackerWithRecords).tracker,
                    onCloseRequest = { onAction(Action.TrackDialogDismissed) },
                    onSave = { onAction(Action.NewRecordSubmitted(it)) }
                )
            }
            ChartCard(requireNotNull(state.trackerWithRecords), modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            TrackerCategoryList(
                categories = state.trackerWithRecords.categories,
                availableCategories = state.allCategories,
                isAddCategoryDialogShowing = state.isAddCategoryDialogShowing,
                onSave = { onAction(Action.TrackerCategoriesUpdated(it)) },
                onAddCategoryClicked = { onAction(Action.AddCategoryClicked) },
                onDialogDismissed = { onAction(Action.AddCategoryDialogDismissed) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
            RecordsList(requireNotNull(state.trackerWithRecords))
        }
    }
}

@Composable
private fun RecordsList(trackerWithRecords: TrackerWithRecords) {
    val items = trackerWithRecords.records.reversed()
    val insets = WindowInsetsAmbient.current
    Card(
        shape = MaterialTheme.shapes.medium.copy(bottomLeft = CornerSize(0), bottomRight = CornerSize(0)),
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumnForIndexed(
            items = items,
            contentPadding = InnerPadding(16.dp).copy(bottom = 16.dp + insets.bottom)
        ) { index, item ->
            RecordListItem(trackerWithRecords, item)
            if (items.lastIndex != index) {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }
    }
}