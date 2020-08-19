package com.popalay.tracktor.feature.trackerdetail

import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.popalay.tracktor.utils.navigationBarHeight

private enum class RecordsListAnimationState {
    STATE_START, STATE_END
}

private val offsetKey = FloatPropKey()

private val tweenDefinition = transitionDefinition<RecordsListAnimationState> {
    state(RecordsListAnimationState.STATE_START) {
        this[offsetKey] = 1F
    }
    state(RecordsListAnimationState.STATE_END) {
        this[offsetKey] = 0F
    }
    transition(RecordsListAnimationState.STATE_START, RecordsListAnimationState.STATE_END) {
        offsetKey using tween(
            durationMillis = 500,
            delayMillis = 400
        )
    }
}

@Composable
fun RecordsList(trackerWithRecords: TrackerWithRecords) {
    val items = trackerWithRecords.records.reversed()
    val transitionState = transition(
        definition = tweenDefinition,
        toState = RecordsListAnimationState.STATE_END,
        initState = RecordsListAnimationState.STATE_START
    )

    WithConstraints {
        val offsetValue = transitionState[offsetKey].let { maxHeight * it }

        Card(
            shape = MaterialTheme.shapes.medium.copy(bottomLeft = CornerSize(0), bottomRight = CornerSize(0)),
            modifier = Modifier.fillMaxSize().offset(y = offsetValue)
        ) {
            LazyColumnForIndexed(
                items = items,
                contentPadding = InnerPadding(16.dp).copy(bottom = 16.dp)
            ) { index, item ->
                RecordListItem(trackerWithRecords, item)
                if (items.lastIndex != index) {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    Spacer(Modifier.navigationBarHeight())
                }
            }
        }
    }
}