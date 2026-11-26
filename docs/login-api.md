# 登录接口约定

当前登录通过 Hilt 注入 `LoginRepositoryImpl`，使用 Retrofit 调用后端。联调时使用后端提供的账号。

## 地址与请求

- 方法：`POST`
- 路径：`/login`
- 请求类型：`Content-Type: application/json`
- 电脑上的服务地址：`http://localhost:8080/login`
- Android Studio 官方模拟器访问该服务：`http://10.0.2.2:8080/login`
- 真机通过局域网访问时，在服务器配置页填写电脑实际的局域网 IP 和端口，后端需监听可访问的网络接口。

`HttpClient.kt` 中的顶层变量 `BASE_URL` 决定地址来源。

- 设为 `null` 时启用服务器配置页。点击「保存并退出」后，业务 API 使用保存的 `http://IP:端口/`。
- 填写完整地址时，业务 API 在启动后直接使用该地址，例如 `http://10.0.2.2:8080/`。登录页隐藏配置入口。

客户端只显式设置 `callTimeout` 为 100 秒，限制整次网络调用的累计时长，包括重试与重定向。连接、单次读取和单次写入使用 OkHttp 默认的 10 秒超时，因此某个阶段可能在总上限之前超时。100 秒沿用当前示例，并非所有登录接口的固定标准；后端有明确耗时要求时再调整。超时沿用请求失败提示，不清除 Token。

请求 JSON 示例：

```json
{
  "username": "demo",
  "password": "example-password"
}
```

`username` 和 `password` 均为必填字符串。客户端会去除账号两侧空白，密码保持输入原样；空账号或空密码在页面直接拦截。示例账号不代表后端必须接受的真实账号，认证由后端完成。

## 成功响应

HTTP 状态码：`200`。响应类型：`Content-Type: application/json`。

响应使用统一的 `HttpClient.ApiResponse<LoginResponse>`。业务成功时 `success` 为 `true`，`code` 为整数 `200`，人员信息和 Token 放在 `data` 内。

```json
{
  "success": true,
  "code": 200,
  "message": "登录成功",
  "data": {
    "userName": "示例用户",
    "userRole": "材料员",
    "token": "example-access-token",
    "expiresAt": "2026-11-25T07:10:23.939Z"
  }
}
```

登录成功时 `data` 必须存在，`userName`、`userRole`、`token` 和 `expiresAt` 均为必填字符串。`expiresAt` 是 ISO 8601 UTC 到期时间，当前 Token 有效期为 10 分钟。Token 为空白、到期时间无效或已过期时，客户端展示请求失败，保持在登录页。

客户端将 Token 加密后与服务器地址、到期时间一并保存。冷启动时根据 `expiresAt` 和当前服务器地址恢复会话。会话缺失或过期时进入登录页。

## 业务请求与凭证失效

登录请求不附带已有 Token。对同一基础地址的协议、主机和端口发出的业务请求，客户端统一添加：

```http
Authorization: Bearer example-access-token
```

`/login` 返回 HTTP `401` 时，登录页显示后端错误消息，缺少消息时显示“登录状态失效，请重新登录”。业务请求缺少 Token 时在本地中止；携带 Token 的业务请求返回 HTTP `401` 时，客户端只清除发起该请求时的会话。两种情况均清空旧业务页面并返回登录页，同时显示认证失败提示。

用户主动退出时，客户端清除内存和本地存储中的会话，保留服务器地址。受保护接口返回当前会话的 `401` 时也清除会话。

保存不同的服务器地址会清除原服务器的 Token。已登录时返回登录页，需要在新服务器重新登录。

## 失败响应

| 情况 | HTTP 状态码 | 客户端行为 |
| --- | --- | --- |
| 账号或密码错误 | `401` | 停留在登录页，默认提示“登录状态失效，请重新登录” |
| 请求格式错误 | `400` | 显示“请求参数有误，请检查后重试” |
| 操作被拒绝 | `403` | 显示“当前账号没有执行此操作的权限” |
| 资源不存在 | `404` | 显示“未找到请求的资源” |
| 服务端异常 | `500–599` | 显示“服务器暂时无法处理请求，请稍后重试” |
| 其他 HTTP 错误 | 其他非成功状态码 | 显示“请求失败，请稍后重试” |
| 业务失败 | `200`，但 `success` 为 `false` 或业务 `code` 不为 `200` | 业务异常保留 `code` 和 `message`，默认提示“业务请求失败” |
| 断网、超时、解析错误、成功响应体为空 | 无有效登录结果 | 结束加载，显示对应的前端错误提示 |

HTTP 错误和业务错误优先显示后端的非空白 `message`。消息缺失、为空白或无法解析时，使用表中的默认提示。

后端响应体包含 `success`、`code`、`message` 和 `data` 四个字段。账号错误使用 HTTP `401`，客户端按 HTTP 状态处理。加载期间重复点击不会发起重复请求。

登录和服务器连接测试通过 `HttpClient.request()` 统一校验响应。调试版本在 Logcat 的 `HttpClient` 标签为每次请求记录一行，包含接口名、请求方法、脱敏后的目标地址、结果、可取得的 HTTP 状态码和业务码，以及总耗时。例如：

```text
api=LoginApi.login method=POST target=http://10.0.2.2:8080/login result=BUSINESS http=200 business=403 elapsedMs=42
api=ServerConfigApi.checkHealth method=GET target=http://10.0.2.2:8080/health result=SUCCESS http=200 business=200 elapsedMs=18
```

## 运行前提与边界

- 后端需要先启动并监听 `8080` 端口。
- Android 客户端已具备网络权限和 HTTP 访问配置。
- 登录后，首页通过携带 Bearer Token 的 `GET /user-info` 加载姓名、角色和菜单权限。菜单按接口返回的顺序展示，分组和对应入口均需获准。头像由客户端固定设置。
- 启用服务器配置页时，首次登录前需要保存配置。已保存的服务器地址在应用进程结束后仍可恢复。

## 服务器连接测试

「测试连接」向页面当前输入的地址发送 `GET /health`，例如 `http://10.0.2.2:8080/health`。接口无需登录、请求体或 Token。探测最多等待 5 秒，保存配置仍由「保存并退出」触发。

服务可用时返回 HTTP `200` 和以下 JSON。

```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": true
}
```

服务不可用时，后端可返回 HTTP `503`，响应体使用相同结构，并通过 `message` 提供失败原因。客户端只有收到 HTTP `200` 且 `success`、业务 `code` 和 `data` 均符合上述约定才显示「连接成功」。连接失败时优先展示后端返回的非空错误消息。
