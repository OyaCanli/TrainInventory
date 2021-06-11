package com.canli.oya.traininventoryroom.interactors

import com.canlioya.core.usecases.trains.*


class TrainInteractors(
    val addTrain: AddTrain,
    val updateTrain: UpdateTrain,
    val sendTrainToTrash: SendTrainToTrash,
    val getAllTrains: GetAllTrains,
    val getChosenTrain: GetChosenTrain,
    val deleteTrainPermanently: DeleteTrainPermanently,
    val getAllTrainsInTrash: GetAllTrainsInTrash,
    val restoreTrainFromTrash: RestoreTrainFromTrash,
    val verifyUniquenessOfTrainName: VerifyUniquenessOfTrainName,
    val searchInTrains: SearchInTrains
)