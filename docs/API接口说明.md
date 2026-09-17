# API接口说明

基础地址：`http://localhost:8080`。统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 1. 文件上传

### 上传图片

- 请求方式：`POST`
- URL：`/api/files/upload`
- Content-Type：`multipart/form-data`
- 参数：`file`，图片文件
- 返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "url": "/uploads/2026/09/15/demo.jpg"
  }
}
```

说明：用于隐患上报图片、工单处理后图片上传。

## 2. AI图片识别

### 隐患图片识别

- 请求方式：`POST`
- URL：`/api/ai/analyze-image`
- 参数：

```json
{
  "imageUrl": "/uploads/2026/09/15/demo.jpg"
}
```

- 返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hasHazard": true,
    "hazardType": "漂浮物",
    "riskLevel": "中风险",
    "confidence": 0.86,
    "description": "航道附近发现漂浮物",
    "suggestion": "建议记录位置并安排清理"
  }
}
```

### 处理前后图片复核

- 请求方式：`POST`
- URL：`/api/ai/review-images`
- 参数：

```json
{
  "beforeImageUrl": "/uploads/before.jpg",
  "afterImageUrl": "/uploads/after.jpg",
  "hazardType": "漂浮物",
  "riskLevel": "高风险",
  "handleRemark": "已完成清理"
}
```

说明：调用千问多模态 API，对处置前后图片进行智能复核。

## 3. 隐患管理

### 创建隐患

- 请求方式：`POST`
- URL：`/api/hazards`
- 参数：上报人、位置、经纬度、图片地址、隐患类型、风险等级、描述、AI结果
- 说明：用于人工确认后的隐患入库。

### AI识别并上报

- 请求方式：`POST`
- URL：`/api/hazards/ai-report`
- Content-Type：`multipart/form-data`
- 参数：`file`、`reportUser`、`location`、`longitude`、`latitude`
- 说明：移动端拍照后上传，后端完成图片保存、AI识别和隐患记录创建。

### 隐患列表

- 请求方式：`GET`
- URL：`/api/hazards`
- 说明：PC列表、地图和Dashboard复用该接口。

### 隐患详情

- 请求方式：`GET`
- URL：`/api/hazards/{id}`

### 管理员审核

- 请求方式：`PUT`
- URL：`/api/hazards/{id}/approve`
- 参数：

```json
{
  "hazardType": "漂浮物",
  "riskLevel": "高风险",
  "description": "主航道存在漂浮物，需优先处置"
}
```

## 4. 工单管理

### 创建工单

- 请求方式：`POST`
- URL：`/api/work-orders/create`
- 参数：

```json
{
  "hazardId": 1,
  "title": "处置航道漂浮物",
  "handler": "处置人员01",
  "requirement": "请到场清理并上传处理后照片"
}
```

### 工单列表

- 请求方式：`GET`
- URL：`/api/work-orders`
- 可选参数：`handler`
- 说明：移动端“我的工单”可按处置人员过滤。

### 工单详情

- 请求方式：`GET`
- URL：`/api/work-orders/{id}`

### 开始处理

- 请求方式：`PUT`
- URL：`/api/work-orders/{id}/start`

### 提交处理结果

- 请求方式：`PUT`
- URL：`/api/work-orders/{id}/finish`
- 参数：

```json
{
  "handleRemark": "已完成漂浮物清理",
  "handleImage": "/uploads/2026/09/15/after.jpg"
}
```

### 审核完成

- 请求方式：`PUT`
- URL：`/api/work-orders/{id}/approve`

## 5. 地图和统计

地图和Dashboard当前复用以下真实数据库接口：

- 隐患数据：`GET /api/hazards`
- 工单数据：`GET /api/work-orders`

说明：前端基于接口返回数据计算隐患总数、风险分布、状态分布、趋势和高风险列表。

## 6. 知识库

### 上传知识文档

- 请求方式：`POST`
- URL：`/api/knowledge/documents`
- Content-Type：`multipart/form-data`
- 参数：`file`、可选 `documentName`

### 知识文档列表

- 请求方式：`GET`
- URL：`/api/knowledge/documents`

### 删除知识文档

- 请求方式：`DELETE`
- URL：`/api/knowledge/documents/{id}`

### 重新解析文档

- 请求方式：`POST`
- URL：`/api/knowledge/documents/{id}/reparse`

### 导入课程演示知识库

- 请求方式：`POST`
- URL：`/api/knowledge/demo-import`

## 7. RAG问答

### 提问

- 请求方式：`POST`
- URL：`/api/chat/ask`
- 参数：

```json
{
  "question": "发现航道漂浮物应该如何处理？",
  "userName": "演示用户",
  "sessionId": ""
}
```

- 返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionId": "uuid",
    "answer": "根据知识库材料，应记录位置和照片...",
    "sources": [
      {
        "documentId": 1,
        "documentName": "航道安全巡检规范",
        "chunkIndex": 1,
        "score": 0.82
      }
    ]
  }
}
```

### 会话列表

- 请求方式：`GET`
- URL：`/api/chat/sessions`

### 会话消息

- 请求方式：`GET`
- URL：`/api/chat/sessions/{sessionId}/messages`

说明：所有接口均不需要在请求中传递 API Key，AI Key 只在后端环境变量中配置。
