# 会员端课程+场地预约（前端已实现）——后端对接说明

## 1. 当前前端实现的页面与流程

### 1) 课程预约（绑定场地）
- 入口：会员侧边栏「课程预约」
- 路由：`/memberLayout/courseBooking`
- 流程：
  - 必须先选择课程（`courseNo`）
  - 展示所有场地（`getAllVenue`）
  - 仅当场地状态 `venueState = 1(可用)` 才允许预约
  - 预约成功后，场地状态应变为 `3(已预订)`，前端会刷新列表看到新状态
  - 预约失败（场地不可用/并发被抢占/其他）统一提示：**“预约失败，场地不可用”**

### 2) 我的预约（预约记录 + 取消）
- 入口：会员侧边栏「我的预约」
- 路由：`/memberLayout/myVenueBookings`
- 功能：
  - 查询当前会员预约记录列表
  - 支持取消预约：取消成功后该场地状态恢复为 `1(可用)`，并刷新预约记录

## 2. 后端需要提供的接口（必须）

> 说明：前端项目的 `request.js` 统一走后端网关地址（与现有接口一致）。  
> 返回风格建议：保持现有 `{ code, message, data }`（或某些接口直接数组）一致即可，但请前后统一。

### 2.1 获取场地列表（已存在/已使用）
- **GET** `/getAllVenue`
- 参数：`page, size`
- 返回：场地数组（建议字段：`venueNo, venueName, venueType, venueLocation, venueCapacity, venueState, openTime, venueMessage`）

### 2.2 预约场地（绑定课程）
- **POST** `/bookVenue`
- 入参：
  - `memberNo`：会员编号
  - `courseNo`：课程编号
  - `venueNo`：场地编号
- 成功返回：`{ code: 200, message: '预约成功' }`
- 失败返回：`{ code: 非200, message: '预约失败，场地不可用' }`

#### 并发/一致性要求（很关键）
- 只允许在 `venue_state = 1(可用)` 时预约成功。
- 推荐用原子更新保证并发安全：
  - `UPDATE venue SET venue_state = 3 WHERE venue_no = ? AND venue_state = 1;`
  - 受影响行数=1 → 预约成功
  - 受影响行数=0 → 预约失败（表示场地已不可用/被别人抢占）

#### 建议：写入预约记录表
- 预约成功后写入 `venue_booking`（见第3节表结构建议）

### 2.3 查询我的预约记录
- **GET** `/getMyVenueBookings`
- 入参：`memberNo`
- 返回：预约记录数组（字段建议见第3节）

### 2.4 取消预约
- **POST** `/cancelVenueBooking`
- 入参：
  - `bookingNo`：预约单号
  - `memberNo`：用于鉴权（只能取消自己的预约）
- 成功返回：`{ code: 200, message: '取消成功' }`
- 失败返回：`{ code: 非200, message: '取消失败' }`

#### 取消的状态流转
- 将预约记录 `status` 置为 `1(已取消)`
- 将对应场地恢复为 `venue_state = 1(可用)`
- 需要考虑：如果同一个场地允许多人排队/分时段预约，则需要更复杂的时段模型；目前前端按“单场地唯一锁”实现。

## 3. 数据库表结构建议（推荐实现）

### 3.1 场地表（已有）
`venue`（你之前的场地管理已用）
- `venue_no` PK
- `venue_state`：`1可用 / 2维护中 / 3已预订 / 4不可用`

### 3.2 预约记录表（新增）
表名：`venue_booking`
- `booking_no` INT PK AUTO_INCREMENT
- `member_no` INT NOT NULL
- `course_no` INT NOT NULL
- `venue_no` INT NOT NULL
- `booking_time` DATETIME DEFAULT CURRENT_TIMESTAMP
- `status` VARCHAR(10) DEFAULT '0'  （`0有效 / 1已取消`）

索引建议：
- `idx_member_no(member_no)`
- `idx_venue_no(venue_no)`
- （可选）唯一约束：如果不支持同一场地多单并存，可加 `UNIQUE(venue_no, status)` 或用业务保证“只有status=0的单才唯一”。

### 3.3 查询“我的预约”的返回字段建议
前端 `MyVenueBookings.vue` 期望字段（可按你们后端返回适配）：
- `bookingNo`
- `courseName`
- `venueName`
- `venueLocation`
- `bookingTime`
- `status`（`0有效/1已取消`）

你可以在后端用 JOIN：
- `venue_booking` + `course` + `venue`

## 4. 规则总结（给后端的业务验收点）
- **必须选课程**才能预约场地（前端已限制，后端也建议校验）
- **只有场地可用**才能预约成功，并且预约成功后场地状态变为 **已预订**
- **取消预约**后场地状态恢复 **可用**，并且预约记录变为 **已取消**
- **并发**下同一场地只能有一个有效预约（按当前模型）


