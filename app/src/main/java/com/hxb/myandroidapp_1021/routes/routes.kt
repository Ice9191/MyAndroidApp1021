package com.hxb.myandroidapp_1021.routes

import com.hxb.myandroidapp_1021.models.InventoryRecord
import com.hxb.myandroidapp_1021.models.PickingOrder
import com.hxb.myandroidapp_1021.models.ReturnOrder
import com.hxb.myandroidapp_1021.models.ReturnStatus
import com.hxb.myandroidapp_1021.models.ScrapOrder
import kotlinx.serialization.Serializable

@Serializable
data object Dashboard

@Serializable
data object Login

@Serializable
data object ServerConfig

@Serializable
data object PickingList

@Serializable
data class PickingScan(
    val orderNo: String,
    val status: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val expectedTime: String
)

@Serializable
data class PickingConfirm(
    val orderNo: String,
    val status: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val expectedTime: String
)

fun PickingOrder.toPickingScan(): PickingScan = PickingScan(
    orderNo = orderNo,
    status = status,
    team = team,
    person = person,
    toolCount = toolCount,
    expectedTime = expectedTime
)

fun PickingScan.toPickingConfirm(): PickingConfirm = PickingConfirm(
    orderNo = orderNo,
    status = status,
    team = team,
    person = person,
    toolCount = toolCount,
    expectedTime = expectedTime
)

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
    team = team,
    person = person,
    totalCount = totalCount,
    returnedCount = returnedCount,
    unreturnedCount = unreturnedCount
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
