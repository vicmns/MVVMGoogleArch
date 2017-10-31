package com.lonelystudios.palantir.di.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider


import com.lonelystudios.palantir.di.scope.PerApp

import java.lang.reflect.InvocationTargetException

import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by vicmns on 8/1/17.
 */
@PerApp
class PalantirViewModelFactory @Inject
constructor(private val mApplication: Application, private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]
        if (creator == null) {
            if (AndroidViewModel::class.java.isAssignableFrom(modelClass)) {
                try {
                    return modelClass.getConstructor(Application::class.java).newInstance(mApplication)
                } catch (e: NoSuchMethodException) {
                    throw RuntimeException("Cannot create an instance of " + modelClass, e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException("Cannot create an instance of " + modelClass, e)
                } catch (e: InstantiationException) {
                    throw RuntimeException("Cannot create an instance of " + modelClass, e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException("Cannot create an instance of " + modelClass, e)
                }

            } else {
                for ((key, value) in creators) {
                    if (modelClass.isAssignableFrom(key)) {
                        creator = value
                        break
                    }
                }
            }
        }
        if (creator == null) {
            throw IllegalArgumentException("unknown model class " + modelClass)
        }
        try {
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}
