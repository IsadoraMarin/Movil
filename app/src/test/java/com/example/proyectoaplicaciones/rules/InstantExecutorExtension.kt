package com.example.proyectoaplicaciones.rules

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Una regla de JUnit 4 que reemplaza el TaskExecutor de los Componentes de Arquitectura
 * para ejecutar todo de forma síncrona en el mismo hilo.
 */
class InstantExecutorExtension : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }
        })
    }

    override fun finished(description: Description) {
        super.finished(description)
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}
