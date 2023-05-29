package com.app.organizationpanel.di

import com.app.organizationpanel.ui.addEvent.AddEventViewModel
import com.app.organizationpanel.ui.addProgram.AddProgramViewModel
import com.app.organizationpanel.ui.organizationPanel.ui.event.EventViewModel
import com.app.organizationpanel.ui.organizationPanel.ui.notifications.NotificationsViewModel
import com.app.organizationpanel.ui.organizationPanel.ui.program.ProgramViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val organizationPanelModule = module {
    viewModel { EventViewModel(get()) }
    viewModel { ProgramViewModel(get()) }
    viewModel { NotificationsViewModel() }
    viewModel { AddEventViewModel(get()) }
    viewModel { AddProgramViewModel(get()) }
}