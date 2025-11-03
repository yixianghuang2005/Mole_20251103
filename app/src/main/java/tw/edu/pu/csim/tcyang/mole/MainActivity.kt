package tw.edu.pu.csim.tcyang.mole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tw.edu.pu.csim.tcyang.mole.ui.theme.MoleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoleTheme {
                MoleScreen()
            }
        }
    }
}

@Composable
fun MoleScreen(moleViewModel: MoleViewModel = viewModel()) {
    val counter = moleViewModel.counter
    val stay = moleViewModel.stay
    val isGameRunning = moleViewModel.isGameRunning

    // 將 stay 單位 (0.5秒) 轉換為已經過去的秒數 (遞增計時)
    val elapsedSeconds = stay / 2L

    val authorName = "黃義祥"

    // DP-to-pixel轉換
    val density = LocalDensity.current
    val moleSizeDp = 150.dp
    val moleSizePx = with(density) { moleSizeDp.roundToPx() }


    Box (
        modifier = Modifier.fillMaxSize()
            .onSizeChanged { intSize ->  // 用來獲取全螢幕尺寸px
                moleViewModel.getArea(intSize, moleSizePx) },
        // 保持置中
        Alignment.Center
    ) {
        // 根據遊戲狀態，準備結束提示文字
        val gameStatusText = if (!isGameRunning) {
            "\n\n\n遊戲結束！\n最終分數: $counter"
        } else {
            ""
        }

        // 顯示遞增時間和所有文字資訊
        Text(
            text = "打地鼠遊戲(黃義祥)\n分數: $counter \n時間: ${elapsedSeconds} 秒" + gameStatusText
        )
    }

    Image(
        painter = painterResource(id = R.drawable.mole),
        contentDescription = "地鼠",
        modifier = Modifier
            .offset { IntOffset(moleViewModel.offsetX, moleViewModel.offsetY) }
            .size(moleSizeDp)
            // 條件：遊戲結束時禁用點擊 (不會再加分)
            .clickable(enabled = isGameRunning) {
                moleViewModel.incrementCounter()
            }
    )
}