package com.app.wecollabs.di

import com.app.wecollabs.ui.articleDetail.ArticleDetailViewModel
import com.app.wecollabs.ui.eventDetail.DetailEventViewModel
import com.app.wecollabs.ui.listArticle.ListArticleViewModel
import com.app.wecollabs.ui.listEvent.ListEventViewModel
import com.app.wecollabs.ui.listGoal.ListGoalViewModel
import com.app.wecollabs.ui.listOrganization.ListOrganizationViewModel
import com.app.wecollabs.ui.main.fragments.favorite.FavoriteViewModel
import com.app.wecollabs.ui.main.fragments.home.HomeViewModel
import com.app.wecollabs.ui.organizationDetail.OrganizationDetailViewModel
import com.app.wecollabs.ui.programDetail.ProgramDetailViewModel
import com.app.wecollabs.ui.searchEvent.SearchEventViewModel
import com.app.wecollabs.ui.start.fragments.login.LoginViewModel
import com.app.wecollabs.ui.start.fragments.register.forOrganization.ChooseGoalViewModel
import com.app.wecollabs.ui.start.fragments.register.forUser.RegisterUserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterUserViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { ListEventViewModel(get()) }
    viewModel { ListOrganizationViewModel(get()) }
    viewModel { ChooseGoalViewModel(get(), get()) }
    viewModel { FavoriteViewModel(get()) }
    viewModel { DetailEventViewModel(get()) }
    viewModel { OrganizationDetailViewModel(get()) }
    viewModel { ArticleDetailViewModel(get()) }
    viewModel { ListArticleViewModel(get()) }
    viewModel { ProgramDetailViewModel(get()) }
    viewModel { SearchEventViewModel(get()) }
    viewModel { ListGoalViewModel(get()) }
}