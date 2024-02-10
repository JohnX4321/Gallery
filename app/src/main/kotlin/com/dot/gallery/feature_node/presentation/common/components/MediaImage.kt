/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.feature_node.presentation.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Scale
import com.dot.gallery.core.Constants.Animation
import com.dot.gallery.core.presentation.components.CheckBox
import com.dot.gallery.feature_node.domain.model.Media
import com.dot.gallery.feature_node.domain.model.MediaEqualityDelegate
import com.dot.gallery.feature_node.presentation.mediaview.components.video.VideoDurationHeader
import com.dot.gallery.ui.theme.Dimens

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaImage(
    modifier: Modifier = Modifier,
    media: Media,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<Media>,
    onItemClick: (Media) -> Unit,
    onItemLongClick: (Media) -> Unit,
) {
    var isSelected by remember { mutableStateOf(false) }
    LaunchedEffect(selectionState.value, selectedMedia.size) {
        isSelected = if (!selectionState.value) false else {
            selectedMedia.find { it.id == media.id } != null
        }
    }
    val selectedSize by animateDpAsState(
        if (isSelected) 12.dp else 0.dp, label = "selectedSize"
    )
    val scale by animateFloatAsState(
        if (isSelected) 0.5f else 1f, label = "scale"
    )
    val selectedShapeSize by animateDpAsState(
        if (isSelected) 16.dp else 0.dp, label = "selectedShapeSize"
    )
    val strokeSize by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 0.dp, label = "strokeSize"
    )
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val strokeColor by animateColorAsState(
        targetValue = if (isSelected) primaryContainerColor else Color.Transparent,
        label = "strokeColor"
    )
    Box(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    onItemClick(media)
                    if (selectionState.value) {
                        isSelected = !isSelected
                    }
                },
                onLongClick = {
                    onItemLongClick(media)
                    if (selectionState.value) {
                        isSelected = !isSelected
                    }
                },
            )
            .aspectRatio(1f)
            .size(Dimens.Photo())
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .aspectRatio(1f)
                .padding(selectedSize)
                .clip(RoundedCornerShape(selectedShapeSize))
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(selectedShapeSize)
                )
                .border(
                    width = strokeSize,
                    shape = RoundedCornerShape(selectedShapeSize),
                    color = strokeColor
                )
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(media.uri)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .memoryCacheKey(media.toString())
                    .scale(Scale.FILL)
                    .build(),
                modelEqualityDelegate = MediaEqualityDelegate(),
                contentDescription = media.label,
                contentScale = ContentScale.Crop,
            )
        }

        AnimatedVisibility(
            visible = media.duration != null,
            enter = Animation.enterAnimation,
            exit = Animation.exitAnimation,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            VideoDurationHeader(
                modifier = Modifier
                    .padding(selectedSize / 2)
                    .scale(scale),
                media = media
            )
        }

        AnimatedVisibility(
            visible = media.isFavorite,
            enter = Animation.enterAnimation,
            exit = Animation.exitAnimation,
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Image(
                modifier = Modifier
                    .padding(selectedSize / 2)
                    .scale(scale)
                    .padding(8.dp)
                    .size(16.dp),
                imageVector = Icons.Filled.Favorite,
                colorFilter = ColorFilter.tint(Color.Red),
                contentDescription = null
            )
        }

        AnimatedVisibility(
            visible = selectionState.value,
            enter = Animation.enterAnimation,
            exit = Animation.exitAnimation
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                CheckBox(isChecked = isSelected)
            }
        }
    }
}
