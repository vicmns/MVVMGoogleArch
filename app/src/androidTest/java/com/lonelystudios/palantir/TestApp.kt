package com.lonelystudios.palantir

import android.app.Application

/**
 * We use a separate App for tests to prevent initializing dependency injection.
 *
 * See {@link com.lonelystudios.palantir.utils.PalantirTestRunner}.
 */
class TestApp: Application() {
}