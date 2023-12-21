package com.ibd.dcdown.main.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.tools.C.ConPackMenuClickType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConMenuBottomSheet(
    sheetState: SheetState,
    data: ConPack,
    onClick: (@ConPackMenuClickType Int, ConPack) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismiss) {
        Text("MODAL", modifier = Modifier.padding(16.dp))
    }
}