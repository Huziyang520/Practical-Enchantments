### 1.3.0 (2026-09-28)

- 迁移第一步：附魔定义由代码注册改为**数据包资源**（19 个附魔 JSON、6 个互斥组标签、`tradeable` 标签、盔甲商 Lv3 的「明朗」钻石头盔交易）。
- 语言文件合并为唯一的 `assets/practical_enchantments/lang/`，移除过渡期的 `enchant_sync/`。
- **本版行为与 1.2.4 一致**：附魔此刻仍由 EnchantLib 在代码侧注册，静态资源将在后续版本（前置替换为 MerlinLib）后正式接管。