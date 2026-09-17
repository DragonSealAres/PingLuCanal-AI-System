# 平陆运河AI水上安全巡检系统

## 项目简介

通过AI视觉识别、智能工单闭环、AI复核、地图可视化和RAG知识问答，实现水上安全隐患智能巡检与闭环处置。

## 核心功能

- AI隐患识别
- AI风险定级
- 移动端巡检上报
- 隐患审核与管理
- 工单派发与闭环处置
- AI处理前后图片复核
- 航道隐患一张图
- Dashboard数据大屏
- RAG航运服务问答助手
- 知识库来源引用和多轮对话

## 技术栈

- 后端：Spring Boot 3、Java 17、MyBatis Plus、Maven
- 数据库：MySQL 8
- PC端：Vue3、Vite、Element Plus、ECharts、高德地图JS API
- 移动端：uni-app / H5、Vue3
- AI：千问多模态API、文本问答、Embedding

## 项目结构

```text
backend/      Spring Boot后端
admin-web/    Vue3 PC管理后台
mobile-app/   uni-app/H5移动端
docs/         交付文档、SQL脚本、测试报告
```

## 快速启动

```bash
mysql -u root -p < docs/sql/schema.sql
```

参考 `.env.example` 创建本地环境变量配置，不要提交真实密钥。

```bash
cd backend
mvn spring-boot:run
```

```bash
cd admin-web
npm install
npm run dev
```

```bash
cd mobile-app
npm install
npm run dev:h5
```

## 环境变量

```bash
MYSQL_ROOT_PASSWORD=your_mysql_password_here
AI_API_KEY=your_ai_api_key_here
AI_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1
AI_MODEL=qwen3-vl-plus
AI_EMBEDDING_MODEL=text-embedding-v4
VITE_API_BASE_URL=http://localhost:8080
VITE_AMAP_KEY=your_amap_key_here
VITE_AMAP_SECURITY_JS_CODE=your_security_js_code_here
```

## 演示流程

移动端拍照上报 -> AI识别 -> 提交隐患 -> PC审核 -> 创建工单 -> 移动端处理 -> 上传处理后图片 -> AI复核 -> 管理员完成 -> 地图/Dashboard/RAG展示。

## 文档

- `docs/部署说明.md`
- `docs/操作手册.md`
- `docs/API接口说明.md`
- `docs/系统架构.md`
- `docs/数据库设计.md`
- `docs/测试用例.md`
- `docs/测试结果.md`
- `docs/答辩演示流程.md`
- `docs/演示应急预案.md`

## 注意事项

- 不要把真实API Key、地图Key、数据库密码提交到仓库。
- 课程演示数据位于 `docs/sql/demo_data.sql`，不是生产数据。
- AI不可用时可临时启用Mock模式用于现场兜底，正式验收建议使用真实API。
