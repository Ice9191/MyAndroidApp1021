# AUTOID UTouch RFID 接入

## 当前 PDA 接入（2026-11-17）

当前业务链路为 `domain/scanner/RfidReaderFactory` → `data/scanner/RfidReaderFactoryImpl` → `utils/RfidScanner`。Hilt 只提供工厂，每个前台扫描页面拥有一个会话。扫描工具的唯一公开控制方法为 `start()`，连续点击按实际扫描状态切换开始/停止；RFID、扫描状态和错误通过构造参数回调，业务 ViewModel 再暴露只读 StateFlow。

`ScannerLifecycle` 只在页面 RESUMED 且允许扫描时提供会话作用域；页面离开、切到后台、打开确认抽屉或提交时自动取消。扫描器内部在作用域结束时停止盘点、关闭 UHF 并注销实体键，无需页面调用 init、stop、release 或 clear。所有会话使用同一硬件线程，旧会话的延迟释放不会关闭新会话的设备。按钮停止时也会关闭 UHF，页面仍在前台时保留实体键监听。

开始一轮扫描会清空设备 EPC 缓存；同一轮相同 EPC 只回调一次，业务读取状态仍由 ViewModel 管理。默认功率固定为 20 dBm，保留现有固件的打开间隔、功率重试和 Binder 重连处理。`TestRfidScan` 继续保留为日志测试工具，业务页面没有调用它。

工单拣选、工单归还上架、普通上架、其他拣选和其他上架已接入真实 EPC 事件及扫描按钮。临时借还按文档保留人工逐件确认，不能用扫描动作代替服务端提交。支持现有扳机和侧边 SCAN 回调；具体键码、固件重连和读区表现仍以实机验证为准。

`uhf.jar`、`scankey.jar` 使用 `compileOnly`，Manifest 的两个厂商共享库均为 `required=false`。普通设备可进入页面；缺少 UHF 库时显示设备不可用提示，可使用页面的人工核对或外接键盘输入能力。

本次仅完成编译和静态检查，未打包、安装或重新执行 RFID 实机验证。下文为早期接入和设备验证历史，原页面路径、必需共享库声明及提交占位描述不代表当前实现。

## 早期接入记录

本项目直接使用现有 `view_model/RFID.kt` 和 `components/RFIDTest.kt`。

## 接入方式

- `app/libs/uhf.jar` 是包含 `Stub!` 的编译接口，必须使用 `compileOnly`，运行时由 PDA 的 `com.seuic.uhf` 系统共享库提供实现。
- Manifest 中声明 `<uses-library android:name="com.seuic.uhf" android:required="true" />`。普通手机不支持安装此硬件专用版本。
- SDK 实际提供的是 `getTagIDs(): List<EPC>` 和 `EPC.getId()`。压缩包的示例使用 `getTag()/epc`，与其中 JAR 的接口不一致，不照搬该示例。
- 读取链路：`open → setPower → inventoryStart → getTagIDs → EPC.getId → StateFlow → Compose Text`。
- SDK 操作在专用单线程执行；每 100ms 轮询，编码去重。页面暂停时停止并关闭硬件，恢复时重新打开。
- 清空按钮同时清设备 EPC 缓存和 UI 集合；仍在读区的标签允许重新读入。

## 使用

Android Studio 打开本项目，同步后运行到 UTouch。进入现有工具送检页面，等待“RFID 已就绪”，将 UHF 标签放入读区，点击开始扫描。编码显示在“已读取工器具”列表。再次点击停止，清空按钮重置列表。

支持屏幕按钮以及 ScanKeyService 实体按键回调：按住扳机 / 侧边 SCAN 开始扫描，松开最后一个按键时停止。回调在页面恢复时注册，页面暂停或销毁时注销；多次按下同一个键不会重复启动，过期回调会被丢弃。界面显示最近按键码及按下 / 松开状态。Manifest 同时声明 `com.seuic.scankey` 系统共享库，`scankey.jar` 保持 `compileOnly`。实机系统服务确认 cookie 使用原始键码列表，当前注册 `142,248,249,250,252`（该机 gpio-keys 包含 249/250/252；142/248 兼容 SDK 的其他扳机配置）。读取内容是 EPC，不是 TID 或用户区。默认功率 20 dBm，界面保留原来的 5–30 dBm 调节范围，设置失败会显示设备返回错误。

## 验证边界

2026-10-30：`:app:assembleDebug` 构建成功，APK 已安装至 ADB 连接的 UTouch。验证了初始化、开始扫描、停止扫描、后台返回重新初始化。实际读到 EPC `464C30310000000000000100`，Compose 列表显示共 1 件并在停止后保留。现有送检表单的提交按钮仍是占位逻辑。


实体键接入验证：assembleDebug 成功，已安装至 UTouch；系统 ScanKeyManagerService 日志确认注册了上述键码，页面显示实体键已连接。物理按键回调与实际标签读取需配合按键实测。

2026-10-30 实体按键实测：用户完成扳机和侧边 SCAN 操作并确认看到页面反馈；日志收到 key=250 的 down=true / false（10:18:15 至 10:18:26）及 key=249 的 down=true / false（10:18:27 至 10:18:40）。检查时界面显示扫描已停止、实体键 249 松开，无 RfidViewModel 错误日志。本轮标签列表为 0，实体按键链路通过；尚未确认 UE003991 对应的 EPC。

## UHF 服务崩溃后的重连修复（2026-10-30）

实机日志显示 10:48:16 系统 com.seuic.uhfserver 进程崩溃，10:48:17 自动重启。应用随后出现 DeadObjectException；当前固件 UHFService 的 binderDied() 只记录日志，不刷新其单例中的 Binder，open() 又会吞掉 RemoteException 并返回 false，因此旧提示不能据此判断是其他应用占用。

现对该固件增加兼容处理：打开前检查厂商 UHFService.server 的 isBinderAlive，失效时调用其私有 init() 重新获取系统服务；反射仅用于此厂商库，其他版本没有 server 字段时走标准接口。该方式依赖当前固件实现，升级系统后需重新验证。扫描中检测 Binder 死亡后停止，提示重新按键重连；不重启或终止其他应用和系统服务。

验证：assembleDebug 通过，修复版已安装；实机初始化、扫描、停止、后台返回均成功，读到 EPC 3134323936372D4130310000。没有人为再次触发系统服务崩溃，因此尚未完成真实服务重启下的回归测试。系统服务最初崩溃的原因仍未确定。
