package org.app.inflo.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import inflo.composeapp.generated.resources.Res
import inflo.composeapp.generated.resources.ic_arrow_back
import inflo.composeapp.generated.resources.ic_inflo_secondary
import moe.tlaster.precompose.ui.LocalBackDispatcherOwner
import org.app.inflo.core.theme.AppTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(
    backIcon: DrawableResource? = Res.drawable.ic_arrow_back,
    backClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val backDispatcher = LocalBackDispatcherOwner.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.color.white)
            .displayCutoutPadding()
            .statusBarsPadding()
            .padding(vertical = AppTheme.dimens.medium1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            backIcon?.let {
                IconButton(
                    onClick = {
                        if (backClicked != null) {
                            backClicked.invoke()
                        } else {
                            backDispatcher?.backDispatcher?.onBackPress()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(backIcon),
                        contentDescription = null
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_inflo_secondary),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

        }
    }

//    TopAppBar(
//        title = {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Image(
//                    painter = painterResource(Res.drawable.ic_inflo_secondary),
//                    contentDescription = null,
//                    contentScale = ContentScale.Fit
//                )
//            }
//        },
//        modifier = modifier.fillMaxWidth(),
//        navigationIcon = {
//            backIcon?.let {
//                IconButton(
//                    onClick = {
//                        if (backClicked != null) {
//                            backClicked.invoke()
//                        } else {
//                            backDispatcher?.backDispatcher?.onBackPress()
//                        }
//                    }
//                ) {
//                    Icon(
//                        painter = painterResource(backIcon),
//                        contentDescription = null
//                    )
//                }
//            }
//        }
//    )

}