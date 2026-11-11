package com.hxb.myandroidapp_1021.navigation

import com.hxb.myandroidapp_1021.data.models.InventoryRecord
import com.hxb.myandroidapp_1021.data.models.ScrapOrder
import com.hxb.myandroidapp_1021.ui.returns.ReturnOrder
import com.hxb.myandroidapp_1021.ui.returns.ReturnStatus
import kotlinx.serialization.Serializable

@Serializable
data object Dashboard

@Serializable
data object Login

@Serializable
data object ServerConfig

/**
 * 拣选
 */
@Serializable
data object PickingList

@Serializable
data object PickingScan

@Serializable
data object PickingManual

@Serializable
data object PickingConfirm

@Serializable
data object ReturnList

@Serializable
data class ReturnScan(
    val orderNo: String,
    val status: ReturnStatus,
    val team: String,
    val person: String,
    val totalCount: Int,
    val returnedCount: Int,
    val unreturnedCount: Int
)

@Serializable
data class ReturnConfirm(
    val orderNo: String,
    val status: ReturnStatus,
    val team: String,
    val person: String,
    val totalCount: Int,
    val returnedCount: Int,
    val unreturnedCount: Int
)

fun ReturnOrder.toReturnScan(): ReturnScan = ReturnScan(
    orderNo = orderNo,
    status = status,
    team = workshop,
    person = returner,
    totalCount = totalCount,
    returnedCount = returnedCount,
    unreturnedCount = pendingCount
)

fun ReturnScan.toReturnConfirm(): ReturnConfirm = ReturnConfirm(
    orderNo = orderNo,
    status = status,
    team = team,
    person = person,
    totalCount = totalCount,
    returnedCount = returnedCount,
    unreturnedCount = unreturnedCount
)

@Serializable
data object InspectionList

@Serializable
data object InspectionScan

@Serializable
data object InspectionConfirm

@Serializable
data object RepairList

@Serializable
data object RepairScan

@Serializable
data object RepairConfirm

@Serializable
data object ScrapList

@Serializable
data object ScrapScan

@Serializable
data object ScrapConfirm

@Serializable
data object ScrapOutList

@Serializable
data class ScrapOutCheck(
    val orderNo: String,
    val applicant: String,
    val warehouse: String,
    val toolCount: Int,
    val outboundStatus: String
)

fun ScrapOrder.toScrapOutCheck(): ScrapOutCheck = ScrapOutCheck(
    orderNo = orderNo,
    applicant = applicant,
    warehouse = warehouse,
    toolCount = toolCount,
    outboundStatus = outboundStatus
)

@Serializable
data object InventoryList

@Serializable
data class InventoryScan(
    val orderNo: String,
    val team: String,
    val person: String,
    val inventoryDate: String
)

fun InventoryRecord.toInventoryScan(): InventoryScan = InventoryScan(
    orderNo = orderNo,
    team = team,
    person = person,
    inventoryDate = inventoryDate
)

@Serializable
data object InventoryResult
