package com.nativeapptemplate.nativeapptemplatefree.di.modules

import android.annotation.SuppressLint
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepositoryImpl
import com.nativeapptemplate.nativeapptemplatefree.data.login.AccountPasswordRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.AccountPasswordRepositoryImpl
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepositoryImpl
import com.nativeapptemplate.nativeapptemplatefree.data.login.SignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.SignUpRepositoryImpl
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepositoryImpl
import com.nativeapptemplate.nativeapptemplatefree.utils.ConnectivityManagerNetworkMonitor
import com.nativeapptemplate.nativeapptemplatefree.utils.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
@SuppressLint("VisibleForTests")
abstract class DataModule {
  @Binds
  internal abstract fun bindSignUpRepository(signUpRepositoryImpl: SignUpRepositoryImpl): SignUpRepository

  @Binds
  internal abstract fun bindLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository

  @Binds
  internal abstract fun bindAccountPasswordRepository(accountPasswordRepositoryImpl: AccountPasswordRepositoryImpl): AccountPasswordRepository

  @Binds
  internal abstract fun bindShopRepository(shopRepositoryImpl: ShopRepositoryImpl): ShopRepository

  @Binds
  internal abstract fun bindItemTagRepository(itemTagRepositoryImpl: ItemTagRepositoryImpl): ItemTagRepository

  @Binds
  internal abstract fun bindsNetworkMonitor(
    networkMonitor: ConnectivityManagerNetworkMonitor,
  ): NetworkMonitor
}
