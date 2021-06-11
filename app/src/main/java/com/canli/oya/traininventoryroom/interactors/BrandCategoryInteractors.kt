package com.canli.oya.traininventoryroom.interactors

import com.canlioya.core.usecases.brandcategory.*


class BrandCategoryInteractors<T : Any> (
    val addItem: AddItem<T>,
    val updateItem: UpdateItem<T>,
    val deleteItem: DeleteItem<T>,
    val getAllItems: GetAllItems<T>,
    val isThisItemUsed: IsThisItemUsed<T>,
    val isThisItemUsedInTrash: IsThisItemUsedInTrash<T>,
    val deleteTrainsInTrashWithThisItem: DeleteTrainsInTrashWithThisItem<T>,
    val getItemNames: GetItemNames<T>,
)