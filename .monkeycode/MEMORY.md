# User Instruction Memory

This file records user instructions, preferences, and teachings for reference in future interactions.

## Format

### User Instruction Entry
User instruction entries should follow this format:

[User Instruction Summary]
- Date: [YYYY-MM-DD]
- Context: [Mentioned scenario or time]
- Instructions:
  - [Content of user teaching or instruction, described line by line]

### Project Knowledge Entry
Entries discovered by the Agent during task execution should follow this format:

[Project Knowledge Summary]
- Date: [YYYY-MM-DD]
- Context: Discovered by Agent while performing [specific task description]
- Category: [Operations & Deployment|Build Methods|Testing Methods|Troubleshooting & Debugging|Workflow & Collaboration|Environment Configuration]
- Instructions:
  - [Specific knowledge points, described line by line]

## Deduplication Strategy
- Before adding a new entry, check for similar or identical instructions.
- If a duplicate is found, skip the new entry or merge it with the existing one.
- When merging, update the context or date information.
- This helps avoid redundant entries and keeps the memory file tidy.

## Entries

[Project Knowledge Summary]
- Date: 2026-08-19
- Context: Discovered by Agent while deploying 浮元子医药流向管理系统的前后端服务时
- Category: Troubleshooting & Debugging
- Instructions:
  - 用 background_terminal_create 启动长期运行的 Web 服务（Spring Boot / Vite dev server）时，不要设置 timeout（或设为 0 表示不限制），否则服务会在超时后被自动杀死，导致预览链接掉线
  - 本项目启动命令：后端 `cd /workspace/backend && mvn -q spring-boot:run`（8080），前端 `cd /workspace/frontend && npm run dev`（5173，/api 反代到 8080）
  - 数据库使用本地 MariaDB（root/root），初始化脚本 backend/src/main/resources/db/init.sql，演示数据脚本 backend/src/main/resources/db/demo_payment.sql
  - 修复掉线问题的排查链：预览报错 -> background_terminal_list 检查是否 killed_by_timeout -> 用 timeout 0 的后台终端重启 -> curl 预览状态检测端点验证
