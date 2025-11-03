package tw.edu.pu.csim.tcyang.mole

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 遊戲時間常數
const val GAME_DURATION_SECONDS = 60L
const val COUNTDOWN_INTERVAL = 500L // 0.5 秒更新一次

class MoleViewModel : ViewModel() {
    var counter by mutableLongStateOf(0)
        private set
    var stay by mutableLongStateOf(0) // 時間計數器 (單位: 0.5秒)
        private set

    // 遊戲運行狀態，控制遊戲開始/結束
    var isGameRunning by mutableStateOf(true)
        private set

    var maxX by mutableStateOf(0)
        private set

    var maxY by mutableStateOf(0)
        private set

    var offsetX by mutableStateOf(0)
        private set

    var offsetY by mutableStateOf(0)
        private set

    private var countdownJob: Job? = null // 用於控制計時器的協程 Job

    fun incrementCounter() {
        // 條件：遊戲結束時，不再加分
        if (isGameRunning) {
            counter++
        }
    }

    init {
        startCounting()
    }

    private fun startCounting() {
        countdownJob?.cancel()

        // 總共需要更新 60 秒 * 2 次/秒 = 120 次
        val totalUpdates = GAME_DURATION_SECONDS * (1000L / COUNTDOWN_INTERVAL)

        countdownJob = viewModelScope.launch {
            // 迴圈條件：當 stay 小於總更新次數時持續計時
            while (stay < totalUpdates) {
                delay(COUNTDOWN_INTERVAL)
                stay++ // 計數器持續遞增
                moveMole()
            }

            // 60 秒到達，停止遊戲
            isGameRunning = false
        }
    }

    fun getArea(gameSize: IntSize, moleSize:Int) {
        maxX = gameSize.width - moleSize
        maxY = gameSize.height - moleSize
        // 確保地鼠一開始就出現在正確範圍
        if (offsetX == 0 && offsetY == 0) {
            moveMole()
        }
    }

    fun moveMole() {
        // 條件：地鼠不再隨機移動
        if (isGameRunning) {
            offsetX = (0..maxX).random()
            offsetY = (0..maxY).random()
        } else {
            // 遊戲結束，將地鼠移出畫面 (停止移動)
            offsetX = -200
            offsetY = -200
        }
    }

    // 確保 ViewModel 銷毀時停止協程
    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}