package com.canli.oya.traininventoryroom.ui.addtrain

import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.di.AppComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AddTrainScope

@AddTrainScope
@Component(dependencies = [AppComponent::class])
interface AddTrainComponent{

    @Component.Builder
    interface Builder {
        fun build(): AddTrainComponent

        @BindsInstance
        fun bindChosenTrain(chosenTrain : TrainEntry?): Builder

        fun appComponent(appComponent: AppComponent) : Builder
    }

    fun inject(target: AddTrainFragment)

}