# 停用文件归档

本目录位于 Android 源码目录之外，不参与编译。原始 package 保留，便于以后迁回。

- `ui/inspection/`：旧送检独立申请流程，原位置 `app/src/main/java/com/hxb/myandroidapp_1021/ui/inspection/`。
- `ui/repair/`：旧送修独立申请流程，原位置为相同源码根目录下的 `ui/repair/`。
- `ui/scrap/`：旧报废独立申请流程，原位置为相同源码根目录下的 `ui/scrap/`。
- `ui/scrapout/`：旧报废出库独立流程，原位置为相同源码根目录下的 `ui/scrapout/`。
- `navigation/LegacyRoutes.kt`：以上流程移出的路由声明。
- `navigation/LegacyNavGraph.kt.txt`：以上流程移出的导航注册片段；原依赖在对应模块目录中。
- `experiments/MainActivity.kt`：原先单独展示 TopAppBar 的临时入口备份；当前入口恢复为 AppNavHost。

首页仅保留入库管理、工单作业、其他作业。送检、送修、报废、退货来源仍由其他作业入口展示。
共享组件及仍被当前模块引用的模型保留在源码目录。

归档后 InfoRow、ToolScanItem 只被盘点使用，已从 ui/common/Model.kt 移至 ui/inventory/Model.kt；恢复旧模块时需同步调整这些引用。

- `components/TaskPage.kt`：停用的页面外壳，原位置为源码根目录下的 `components/TaskPage.kt`；当前五个页面直接使用 Scaffold 和 TopAppBar。

- `ui/inventory/`：停用的盘点单模块，包含列表、扫描、结果页面及 Model、ViewModel，原位置为源码根目录下的 `ui/inventory/`。
- `navigation/InventoryRoutes.kt`、`navigation/InventoryNavGraph.kt.txt`：盘点模块移出的路由和导航注册片段。
