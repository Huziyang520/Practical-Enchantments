# Practical Enchantments

[English](#english) | [中文](#中文)

---

## English

**Practical Enchantments** adds enchantments that do something you can feel in play — combat, mining,
defence, tridents, and how mobs react to you — instead of only moving numbers around. Every one of
them is built on **MerlinLib**, which registers them, wires their behaviour and ships the in-game tools
for looking at them.

**Enchantment list:** [`ENCHANTMENTS_LIST.md`](ENCHANTMENTS_LIST.md) — every enchantment, what it fits,
its levels, where it comes from and what it conflicts with, in one page.

### What it adds

- Enchantments for **weapons, tools, armour, crossbows and tridents**, each with an effect of its own
  rather than a plain damage bonus.
- **Mutual exclusions** wherever two effects would overlap, on the same terms as the vanilla anvil and
  enchanting table.
- **Ways to obtain them** that follow vanilla progression: the enchanting table, librarian trades, and
  enchanted books in structure loot.
- **Enchantments that work on mobs** — some of them change how a mob reacts to you rather than how much
  damage you deal.

### Requirements

- **MerlinLib, installed on the client *and* the server.** It is required, not optional.
- This mod is a client-and-server mod as well: both sides need both jars.

### Installing

1. Install MerlinLib.
2. Drop the Practical Enchantments jar into `mods/`, on the client and on the server.

### Finding and giving enchantments in game

The tooling comes from MerlinLib, so this mod ships no commands of its own:

- `/merlinlib list` — every non-vanilla enchantment in the registry, including all of these.
- `/merlinlib info <id>` — one enchantment: whether it is registered, its maximum level and weight.
- `/merlinlib book <id> [level]` — hands out the enchanted book (operators).
- Enchantment ids look like `practical_enchantments:<name>` and work in commands, data packs and the
  item editor.

Enchantment **descriptions** in the tooltip are drawn by an enchantment-description mod such as
EnchantmentDescriptions; this mod does not draw them itself.

### Settings

- `config/practical_enchantments/common.properties` — this mod's own switches, written with comments
  the first time the game starts.
- The join message in chat can also be switched off per mod from **MerlinLib settings → General →
  Edit each mod's join notices**.

### Links

- Enchantment list: [English](ENCHANTMENTS_LIST.md) · [中文](ENCHANTMENTS_LIST_CN.md)
- Repository and issues: <https://github.com/Huziyang520/Practical-Enchantments>
- Also on CurseForge under the same name.

---

## 中文

**Practical Enchantments（实用附魔）** 增加一批**在玩法上真的能感觉到**的附魔：战斗、挖掘、防御、
三叉戟，以及与生物的互动机制，而不是只改数字。每一个都构建在 **MerlinLib** 之上——由它完成注册、
行为接线，并提供游戏内查看与发放的工具。

**附魔清单：**[`ENCHANTMENTS_LIST_CN.md`](ENCHANTMENTS_LIST_CN.md)（英文版 [`ENCHANTMENTS_LIST.md`](ENCHANTMENTS_LIST.md)）
——每个附魔的适用物品、等级、获取途径与互斥关系，一页看完。

### 它增加了什么

- 面向**武器、工具、盔甲、弩与三叉戟**的附魔，每个都有自己的效果，而不是单纯的数值加成。
- 效果会互相覆盖的地方设有**互斥关系**，口径与铁砧、附魔台一致。
- 顺着原版进程的**获取途径**：附魔台、图书管理员交易、结构箱子里的附魔书。
- 一批**与生物互动**的附魔：它们改变的是生物的应对方式，而不是伤害数字。

### 前置要求

- **MerlinLib**，且**客户端与服务端都要安装**。它是必需前置，不是可选。
- 本模组同样是**客户端 + 服务端**模组，两端都要装。

### 安装

1. 先安装 MerlinLib。
2. 把 Practical Enchantments 的 jar 放进 `mods/`，客户端与服务端各放一份。

### 在游戏内查看与发放

工具由 MerlinLib 提供，本模组不带自己的指令：

- `/merlinlib list` —— 列出注册表里所有非原版附魔（含本模组全部）。
- `/merlinlib info <id>` —— 单个附魔：是否在注册表、最大等级、权重。
- `/merlinlib book <id> [等级]` —— 发放对应附魔书（需要 OP）。
- 附魔 id 形如 `practical_enchantments:<名字>`，可用于指令、数据包与物品编辑界面。

附魔的**描述文本**由附魔描述类模组（如 EnchantmentDescriptions）显示；本模组自己不绘制描述。

### 设置

- `config/practical_enchantments/common.properties` —— 本模组自己的开关，首次启动时带注释生成。
- 进服时聊天栏的公告也可以在 **MerlinLib 设置 → 通用 → 编辑各模组聊天栏提示** 里按模组关闭。

### 链接

- 附魔清单：[中文](ENCHANTMENTS_LIST_CN.md) · [English](ENCHANTMENTS_LIST.md)
- 仓库与问题反馈：<https://github.com/Huziyang520/Practical-Enchantments>
- CurseForge 上同名可搜到。
