package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CornerSize
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredSize
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.width
import androidx.ui.material.Card
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Add
import androidx.ui.material.icons.filled.Delete
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextAlign
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import androidx.ui.unit.dp
import com.popalay.tracktor.data.featureflags.FeatureFlagsManager
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.gradient
import com.popalay.tracktor.model.TrackerListItem
import com.popalay.tracktor.ui.widget.ChartWidget
import com.popalay.tracktor.ui.widget.ProgressTextField
import com.popalay.tracktor.ui.widget.SimpleChartWidget
import com.popalay.tracktor.utils.Faker
import com.popalay.tracktor.utils.inject

class TrackerListItemPreviewProvider : PreviewParameterProvider<TrackerListItem> {
    override val values: Sequence<TrackerListItem>
        get() = sequenceOf(
            TrackerListItem(Faker.fakeTrackerWithRecords().copy(records = emptyList())),
            TrackerListItem(Faker.fakeTrackerWithRecords())
        )
}

@Preview
@Composable
fun TrackerListItem(
    @PreviewParameter(TrackerListItemPreviewProvider::class) item: TrackerListItem,
    modifier: Modifier = Modifier,
    onAddClicked: () -> Unit = {},
    onRemoveClicked: () -> Unit = {}
) {
    val featureFlagsManager by inject<FeatureFlagsManager>()

    if (featureFlagsManager.isSmallTrackerListItemEnabled()) {
        SimpleTrackerListItem(item, modifier, onAddClicked, onRemoveClicked)
    } else {
        DetailedTrackerListItem(item, modifier, onAddClicked, onRemoveClicked)
    }
}

@Preview
@Composable
fun SimpleTrackerListItem(
    @PreviewParameter(TrackerListItemPreviewProvider::class) item: TrackerListItem,
    modifier: Modifier = Modifier,
    onAddClicked: () -> Unit = {},
    onRemoveClicked: () -> Unit = {}
) {
    val gradient = remember(item) { item.data.tracker.compatibleUnit.gradient }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.preferredHeight(120.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            SimpleBody(item, gradient)
            SimpleFooter(onAddClicked, onRemoveClicked, item)
        }
    }
}

@Preview
@Composable
fun DetailedTrackerListItem(
    @PreviewParameter(TrackerListItemPreviewProvider::class) item: TrackerListItem,
    modifier: Modifier = Modifier,
    onAddClicked: () -> Unit = {},
    onRemoveClicked: () -> Unit = {}
) {
    val gradient = remember(item) { item.data.tracker.compatibleUnit.gradient }
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp))
    ) {
        Column {
            Header(item)
            Body(item, gradient)
            Footer(item, onAddClicked, onRemoveClicked)
        }
    }
}

@Composable
private fun Footer(
    item: TrackerListItem,
    onAddClicked: () -> Unit,
    onRemoveClicked: () -> Unit
) {
    val formatter: ValueRecordFormatter by inject()

    Row(verticalGravity = Alignment.CenterVertically) {
        IconButton(onClick = onAddClicked) {
            Icon(asset = Icons.Default.Add)
        }
        IconButton(onClick = onRemoveClicked) {
            Icon(asset = Icons.Default.Delete)
        }
        Spacer(modifier = Modifier.weight(1F))
        Box(paddingEnd = 16.dp) {
            Text("Current: ${formatter.format(item.data.tracker, item.data.currentValue)}")
        }
    }
}

@Composable
private fun Header(item: TrackerListItem) {
    Row(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalGravity = Alignment.CenterVertically
    ) {
        Text(item.data.tracker.title, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1F))
        ProgressTextField(item.data.progress())
    }
}

@Composable
private fun Body(
    item: TrackerListItem,
    gradient: List<Color>
) {
    val formatter: ValueRecordFormatter by inject()

    if (item.data.records.size > 1) {
        ChartWidget(
            data = item.data.records.map { it.value },
            gradient = gradient,
            touchable = false,
            animate = item.animate
        )
    } else {
        Row(modifier = Modifier.height(100.dp).fillMaxWidth()) {
            Text(
                text = formatter.format(item.data.tracker, item.data.currentValue),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h2.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.gravity(Alignment.CenterVertically).fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SimpleFooter(
    onAddClicked: () -> Unit,
    onRemoveClicked: () -> Unit,
    item: TrackerListItem
) {
    val formatter: ValueRecordFormatter by inject()

    Row(verticalGravity = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
        IconButton(onClick = onAddClicked) {
            Icon(asset = Icons.Default.Add)
        }
        IconButton(onClick = onRemoveClicked) {
            Icon(asset = Icons.Default.Delete)
        }
        Spacer(modifier = Modifier.weight(1F))
        if (item.data.records.size > 1) {
            ProgressTextField(item.data.progress())
            Spacer(modifier = Modifier.width(8.dp))
            Text(formatter.format(item.data.tracker, item.data.currentValue))
        } else {
            Text("Start tracking now", style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
private fun SimpleBody(item: TrackerListItem, gradient: List<Color>) {
    val formatter: ValueRecordFormatter by inject()

    Row(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalGravity = Alignment.CenterVertically
    ) {
        Text(
            item.data.tracker.title,
            style = MaterialTheme.typography.h5,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1F)
        )
        Spacer(modifier = Modifier.width(16.dp))
        if (item.data.records.size > 1) {
            SimpleChartWidget(
                data = item.data.records.map { it.value },
                gradient = gradient,
                animate = item.animate,
                modifier = Modifier.preferredSize(100.dp, 50.dp)
            )
        } else {
            Text(
                text = formatter.format(item.data.tracker, item.data.currentValue),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.gravity(Alignment.CenterVertically).preferredWidth(100.dp)
            )
        }
    }
}