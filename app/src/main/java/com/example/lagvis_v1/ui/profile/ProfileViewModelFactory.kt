package com.example.lagvis_v1.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lagvis_v1.core.network.RetroFitProviderKt
import com.example.lagvis_v1.dominio.repositorio.ProfileRepositoryImplKt
import com.example.lagvis_v1.dominio.repositorio.profile.ProfileRepositoryKt

class ProfileViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetroFitProviderKt.profileApi
        val repo: ProfileRepositoryKt = ProfileRepositoryImplKt(api)
        return ProfileViewModel(repo) as T
    }

}