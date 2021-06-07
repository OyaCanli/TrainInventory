package com.canli.oya.traininventoryroom.ui.addtrain


import com.canli.oya.traininventoryroom.di.AppComponent
import com.canlioya.core.models.Train
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
        fun bindChosenTrain(chosenTrain : Train?): Builder

        fun appComponent(appComponent: AppComponent) : Builder
    }

    fun inject(target: AddTrainFragment)

}