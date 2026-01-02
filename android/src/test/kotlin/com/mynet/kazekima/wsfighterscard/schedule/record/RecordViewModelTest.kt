/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.Application
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val application: Application = mockk()
    private val repository: FightersRepository = mockk()
    private lateinit var viewModel: RecordViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // 修正したコンストラクタを使用してリポジトリをモックに差し替え
        viewModel = RecordViewModel(application, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addGame_callsRepositoryAddGame() = runTest {
        // 準備: リポジトリの動作を定義
        val name = "Test Tournament"
        val date = "2024/02/14"
        val deck = "Test Deck"
        val memo = "Test Memo"
        
        coEvery { repository.addGame(any(), any(), any(), any()) } returns Unit

        // 実行: ViewModel のメソッドを呼ぶ
        var callbackCalled = false
        viewModel.addGame(name, date, deck, memo) {
            callbackCalled = true
        }

        // コルーチンの実行を待機
        testDispatcher.scheduler.advanceUntilIdle()

        // 検証: リポジトリの addGame が正しい引数で呼ばれたか
        coVerify(exactly = 1) {
            repository.addGame(name, date, deck, memo)
        }
        
        confirmVerified(repository)
        assert(callbackCalled)
    }
}
