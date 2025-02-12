// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.client

import com.intellij.codeWithMe.ClientId
import com.intellij.ide.plugins.IdeaPluginDescriptorImpl
import com.intellij.openapi.application.Application
import com.intellij.openapi.components.ServiceDescriptor
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.serviceContainer.ComponentManagerImpl
import com.intellij.serviceContainer.PrecomputedExtensionModel
import com.intellij.serviceContainer.throwAlreadyDisposedError
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
abstract class ClientAwareComponentManager constructor(
  internal val parent: ComponentManagerImpl?,
  setExtensionsRootArea: Boolean = parent == null
) : ComponentManagerImpl(parent, setExtensionsRootArea) {
  override fun <T : Any> getServices(serviceClass: Class<T>, includeLocal: Boolean): List<T> {
    val sessionsManager = super.getService(ClientSessionsManager::class.java)!!
    return sessionsManager.getSessions(includeLocal).mapNotNull {
      (it as? ClientSessionImpl)?.doGetService(serviceClass = serviceClass, createIfNeeded = true, fallbackToShared = false)
    }
  }

  override fun <T : Any> postGetService(serviceClass: Class<T>, createIfNeeded: Boolean): T? {
    val sessionsManager = if (containerState.get() == ContainerState.DISPOSE_COMPLETED) {
      if (createIfNeeded) {
        throwAlreadyDisposedError(serviceClass.name, this, ProgressIndicatorProvider.getGlobalProgressIndicator())
      }
      super.doGetService(ClientSessionsManager::class.java, false)
    }
    else {
      super.getService(ClientSessionsManager::class.java)
    }

    val session = sessionsManager?.getSession(ClientId.current) as? ClientSessionImpl
    return session?.doGetService(serviceClass, createIfNeeded, false)
  }

  override fun registerComponents(modules: Sequence<IdeaPluginDescriptorImpl>,
                                  app: Application?,
                                  precomputedExtensionModel: PrecomputedExtensionModel?,
                                  listenerCallbacks: MutableList<in Runnable>?) {
    super.registerComponents(modules, app, precomputedExtensionModel, listenerCallbacks)

    val sessionsManager = super.getService(ClientSessionsManager::class.java)!!
    for (session in sessionsManager.getSessions(true)) {
      (session as? ClientSessionImpl)?.registerComponents(modules, app, precomputedExtensionModel, listenerCallbacks)
    }
  }

  override fun unloadServices(services: List<ServiceDescriptor>, pluginId: PluginId) {
    super.unloadServices(services, pluginId)

    val sessionsManager = super.getService(ClientSessionsManager::class.java)!!
    for (session in sessionsManager.getSessions(true)) {
      (session as? ClientSessionImpl)?.unloadServices(services, pluginId)
    }
  }

  override fun postPreloadServices(modules: Sequence<IdeaPluginDescriptorImpl>,
                                   activityPrefix: String,
                                   syncScope: CoroutineScope,
                                   onlyIfAwait: Boolean) {
    val sessionsManager = super.getService(ClientSessionsManager::class.java)!!
    for (session in sessionsManager.getSessions(true)) {
      session as? ClientSessionImpl ?: continue
      session.preloadServices(modules, activityPrefix, syncScope, onlyIfAwait)
    }
  }

  override fun isPreInitialized(component: Any): Boolean {
    return super.isPreInitialized(component) || component is ClientSessionsManager<*>
  }
}