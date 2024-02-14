package com.nagpal.shivam.vtucslab.screens.repository

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nagpal.shivam.vtucslab.R

@Composable
fun RepositoryCard(
    text: String = "Sample Text",
    onClick: () -> Unit,
) {
    return ElevatedCard(
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier =
        Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(start = 12.dp, top = 6.dp, end = 12.dp, bottom = 6.dp)
            .clickable { onClick.invoke() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
            Modifier
                .padding(8.dp)
                .height(64.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_repository),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(colorResource(id = R.color.colorPrimary)),
                modifier =
                Modifier
                    .size(40.dp),
            )
            Text(
                text = text,
                fontSize = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black,
                modifier =
                Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .fillMaxWidth()
                    .padding(start = 16.dp),
            )
        }
    }
}
