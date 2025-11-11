
## 面试官问答扩展（精炼实战版）

说明：下面由“面试官提问 → 候选人回答要点”的形式呈现，语言精炼、真实且可背诵。答案中尽量引用 `msdy` 项目中可核验的实现点（文件/方法名）以增强可信度。将重要的句子记成速记口吻，方便面试时快速输出。

---

### 一：快速开场与动机

Q1 自我介绍（30秒）
A1 句式模板：我叫[姓名]，软件工程在读，GPA 3.7/5。主攻 Java 后端，熟悉 Spring Boot、MyBatis-Plus、MySQL、Redis。实习做过协服供应链（`platform-ems`）的需求导入、库存/质检模块（见 `InvInventoryLocationController`、`QuaRawmatCheckRecordController`），并实际解决过导出编码、慢 SQL 优化与异步导入任务的工程问题。目标是应聘 Java 后端岗位，长期做分布式与架构方向。

Q2 为什么选择后端/Java？
A2 要点：强调系统性、数据一致性、企业级生态与团队效率；一句话："我喜欢解决影响面广的工程问题，Java 生态和 Spring 框架能让我在企业级产品上更快产出并保持稳定。"

---

### 二：项目与技术深挖（每题 30–90s 回答）

Q3 说说你在需求导入/缺口判定里遇到的最具体问题与你如何解决的？
A3 要点句：接口 POST /demand/import 接收 Excel（EasyExcel），我把解析改为行回调写入临时表以降低内存；耗时的缺口计算异步化（`@Async`），写入数据库使用 MyBatis-Plus 的 saveBatch 批量插入；为保证幂等增加导入任务表和 taskId 返回前端供轮询。结果：1k 行导入从阻塞式改为后台处理，前端即时返回 taskId。

Q4 你如何用 AOP 找慢 SQL？具体步骤是什么？
A4 要点列点：1) 在 Controller/Service 切入记录耗时（参考 `DataScopeFilterAspect` 的织入方式），超过阈值记录上下文；2) 用 p6spy 或 MyBatis SQL 日志抓取实际执行 SQL；3) EXPLAIN 检查执行计划；4) 采取索引/重写 SQL/拆查询/缓存手段。案例结论：某接口从 2s 降到 ~150ms。

Q5 请描述一次你修改导出逻辑并兼容浏览器的实操（可举 `TecProductLineController` 的例子）。
A5 要点：使用 EasyExcel 写入 `response.getOutputStream()`，设置 Content-Type 为 excel 的 mime 或 application/octet-stream，使用 URLEncoder.encode + RFC5987 的 filename* 做双头文件名；避免使用 response.getWriter()。用 Chrome/Firefox/Edge 回归验证。

Q6 面试官再问：如何保证导出过程不 OOM？
A6 要点：流式写出（EasyExcel 的 ExcelWriter）/按页写入/分页查询后写入、不要把全部数据加载到内存。若数据量巨大可走异步导出并把文件放到对象存储（MinIO），返回下载链接。

---

### 三：架构、并发与一致性

Q7 高并发下库存扣减你怎么做？（现场画图题要点）
A7 要点：推荐“预扣+锁+补偿”组合：
- 本地事务：先更新 locked_qty（UPDATE ... WHERE available_qty>=x），基于 version 做乐观锁或 SELECT FOR UPDATE 做悲观锁；
- 幂等：使用 orderId 幂等表；
- 高峰保护：用 Redis 原子脚本做预扣并异步持久化；若持久化失败，回滚 Redis 或发补偿消息。表达要点：讲清楚“锁粒度（按 sku/仓）”与“补偿路径（消息队列）”。

Q8 如何设计库存表索引？
A8 要点：主检索按 sku 查询：单列索引 sku_id；常按 sku+warehouse 查询：复合索引 (sku_id, warehouse_id)；频繁按日期或状态筛选的字段建立联合索引并避免在 where 对索引列使用函数。

Q9 如果系统需支持 10k RPS，你的扩容方案？
A9 要点：识别瓶颈（DB/CPU/IO/网络），水平拆分：读写分离 + 业务分流（库存、订单拆成独立微服务），缓存热点、限流/熔断、异步化（消息队列），数据库分库分表与垂直拆分，并用监控/压测验证。

---

### 四：工程实践与工具链

Q10 单元/集成测试怎么做？如何测试 Mapper XML？
A10 要点：Service 用 JUnit + Mockito；集成测试用 SpringBootTest + H2 或 Testcontainers；Mapper XML 用集成测试运行 SQL 验证（加载测试数据并断言结果），并在 CI 加入 SQL 映射检查。

Q11 CI/CD 你会添加哪些检查？
A11 要点：静态检查（SpotBugs/Checkstyle）、漏洞扫描（依赖漏洞）、单元测试覆盖门槛、打包验证、容器镜像扫描与自动化部署流水线（Maven -> Docker -> Kubernetes）。

Q12 日志与监控如何落地？
A12 要点：结构化日志（JSON）、链路追踪（Spring Cloud Sleuth + Zipkin/Jaeger）、指标（Prometheus + Grafana）、慢查询统计与告警（慢 SQL 表 + DB 监控）。

---

### 五：面试官喜欢的行为题与回答技巧

Q13 讲一次你推动需求落地或调度团队进度的经历（STAR）
A13 STAR 模版背诵：
- S：上线前我们发现导出乱码/下载失败，影响测试。 
- T：我负责定位并修复，保证回归通过。 
- A：抓包定位 header 问题，统一导出方法（使用 OutputStream + filename* 编码），增加回归用例并与前端约定重试策略。 
- R：三天内修复，通过率从 ~60% 提升到 ~99%。

Q14 面试官问：你在团队中最大的贡献是什么？
A14 要点：重点说“输出可量化结果或产出规范（例：导出兼容修复、SQL 优化、导入异步化）”，并说明你主动沟通前端/测试/产品的例子。

---

### 六：现场快速答题（二分钟内必须给出要点）

Q15 写出两数之和的最优解思路（口述）
A15 哈希表法：遍历数组，查询 target-nums[i] 是否已在哈希表中，存在返回索引；否则加入哈希表。时间 O(n)，空间 O(n)。

Q16 简述 Spring 事务传播常用场景
A16 REQUIRED（默认，加入现有事务），REQUIRES_NEW（开启新事务，挂起旧事务），PROPAGATION_SUPPORTS（无事务也可执行）——举例：日志写入用 REQUIRES_NEW 避免回滚。

---

结尾提示：在回答时把关键点与项目文件名短句说出（比如 `TecProductLineController.getInfoExport`、`InvInventoryLocationController.importTemplate`），能显著提升可信度与说服力。

---

我已经把扩展内容追加到 `ms.md`。下一步我可以：
- 把这些问答浓缩成一页速记版；或
- 立即开始 20 问模拟面试（我当面试官，请你回答，我实时给反馈）。

请告诉我你现在想做哪一项。 

