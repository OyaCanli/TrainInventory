package com.canli.oya.traininventoryroom.di

class AndroidTestApplication : TrainApplication() {

    override fun initAppComponent(): AppComponent {
        return DaggerTestComponent.builder()
                .testAppModule(TestAppModule(this))
                .build()
    }
}