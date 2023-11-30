package com.app.feature.historyevents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.common.TimeUtils
import com.app.core.model.HistoryEvent
import com.app.core.ui.card.SpaceXCardDetails
import com.app.core.ui.card.SpaceXCardHeader
import com.app.core.ui.card.SpaceXCardTitle
import com.app.core.ui.text.HyperlinkText
import com.app.feature.history.events.R

@Composable
fun HistoryEventCard(historyEvent: HistoryEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 25.dp),
        elevation = 10.dp,
        shape = RoundedCornerShape(5)
    ) {
        Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 20.dp)) {
            HistoryEventHeader(historyEvent)
            SpaceXCardTitle(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                title = historyEvent.title,
            )
            SpaceXCardDetails(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                details = historyEvent.details,
            )
            HistoryEventSourceLink(historyEvent)
        }
    }
}

@Composable
private fun HistoryEventHeader(historyEvent: HistoryEvent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SpaceXCardHeader(header = stringResource(R.string.spacex_app_history_events_overline))
        SpaceXCardHeader(header = TimeUtils.formatDate(historyEvent.date))
    }
}

@Composable
private fun HistoryEventSourceLink(historyEvent: HistoryEvent) {
    historyEvent.link?.let {
        val text = stringResource(R.string.spacex_app_source)
        HyperlinkText(
            fullText = text,
            linkText = listOf(text),
            hyperlinks = listOf(it))
    }
}

@Preview
@Composable
private fun Preview() {
    HistoryEventCard(
        HistoryEvent(
            "",
            "http://www.spacex.com/news/2013/02/11/flight-4-launch-update-0",
            "Falcon reaches Earth orbit",
            1222643700,
            "Falcon 1 becomes the first privately developed liquid-fuel rocket to reach Earth orbit.",
        ))
}