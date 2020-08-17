package com.popalay.tracktor.ui.list

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.data.model.toListItem
import com.popalay.tracktor.ui.dialog.UpdateTrackedValueDialog
import com.popalay.tracktor.ui.list.ListWorkflow.Action
import com.popalay.tracktor.ui.list.ListWorkflow.Rendering
import com.popalay.tracktor.ui.list.ListWorkflow.State
import com.popalay.tracktor.ui.widget.AllCategoryList
import com.popalay.tracktor.ui.widget.AnimatedSnackbar
import com.popalay.tracktor.utils.Faker
import com.squareup.workflow.ui.compose.composedViewFactory

@OptIn(ExperimentalLayout::class)
val ListBinding = composedViewFactory<Rendering> { rendering, _ ->
    ListScreen(rendering.state, rendering.onAction)
}

class ListStatePreviewProvider : PreviewParameterProvider<State> {
    override val values: Sequence<State>
        get() = sequenceOf(State(List(5) { Faker.fakeTrackerWithRecords() }.map { it.toListItem() }))
}

@Preview
@Composable
fun ListScreen(
    @PreviewParameter(ListStatePreviewProvider::class) state: State,
    onAction: (Action) -> Unit = {}
) {
    Scaffold(
        topBar = {
            LogoAppBar(
                menuItems = state.menuItems,
                onMenuItemClicked = { onAction(Action.MenuItemClicked(it)) }
            )
        },
        floatingActionButtonPosition = Scaffold.FabPosition.Center,
        floatingActionButton = {
            Column(horizontalGravity = Alignment.CenterHorizontally) {
                FloatingActionButton(
                    onClick = { onAction(Action.CreateTrackerClicked) },
                    modifier = Modifier.offset(y = -WindowInsetsAmbient.current.bottom)
                ) {
                    Icon(Icons.Default.Add)
                }
                AnimatedSnackbar(
                    message = state.itemInDeleting?.tracker?.title?.let { "$it was removed" } ?: "",
                    actionText = "UNDO",
                    shouldDisplay = state.itemInDeleting != null,
                    onActionClick = { onAction(Action.UndoDeletingClicked) }
                )
            }
        }
    ) {
        Stack(modifier = Modifier.fillMaxSize()) {
            when {
                state.itemInEditing != null -> {
                    UpdateTrackedValueDialog(
                        tracker = state.itemInEditing,
                        onCloseRequest = { onAction(Action.TrackDialogDismissed) },
                        onSave = { onAction(Action.NewRecordSubmitted(state.itemInEditing, it)) }
                    )
                }
            }
            if (state.showEmptyState) {
                Text(
                    text = "Click on the button below and let's track!",
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.gravity(Alignment.Center)
                )
            } else {
                TrackerList(state, onAction)
            }
        }
    }
}

@Composable
private fun TrackerList(
    state: State,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val insets = WindowInsetsAmbient.current
    LazyColumnForIndexed(
        items = state.filteredItems,
        contentPadding = InnerPadding(top = 16.dp, bottom = insets.bottom + 16.dp),
        modifier = modifier
    ) { index, item ->
        if (index == 0 && state.statistic != null) {
            StatisticWidget(
                state.statistic,
                state.animate,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            if (state.allCategories.isNotEmpty()) {
                AllCategoryList(
                    categories = state.allCategories,
                    selectedCategory = state.selectedCategory,
                    onCategoryClick = { onAction(Action.CategoryClick(it)) },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        TrackerListItem(
            item.copy(animate = state.animate),
            modifier = Modifier.padding(horizontal = 16.dp),
            contentModifier = Modifier.clickable(onClick = { onAction(Action.TrackerClicked(item.data)) }),
            onAddClicked = { onAction(Action.AddRecordClicked(item.data)) },
            onRemoveClicked = { onAction(Action.DeleteTrackerClicked(item.data)) }
        )
        if (index != state.items.lastIndex) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        onActive {
            onAction(Action.AnimationProceeded)
        }
    }
}