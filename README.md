# Practical Enchantments

[English](#english) | [中文](#中文)

---

> ## ⚠ This copy of the README describes Practical Enchantments for **Minecraft 1.20.1 Forge**
>
> It was carried over from the 26.3 release. One line in it is **wrong for this version** and matters:
> the claim that enchantment definitions, exclusion groups and villager trades are data pack
> resources that `/reload` picks up. On 1.20.1:
>
> - **The 22 enchantments are registered in code**, because there is no data driven enchantment
>   registry before 1.21. Changing anything about them needs a restart.
> - **The exclusion groups are code**, expressed through `Enchantment#checkCompatibility`.
> - **The villager trades are code**, registered through Forge's `VillagerTradesEvent`.
> - **The loot injections are code**, registered through MerlinLib's loot api.
>
> What is *still* a resource on this version: the language files (`assets/…/lang/*.json`). The
> `data/` tree of the 26.3 release is not shipped here at all.
>
> Everything else in this document is accurate for this version.

## English

**Practical Enchantments** adds a growing set of enchantments that do something you can feel in play - combat, mining, defence, tridents, and interacting with mobs - instead of only moving numbers around. Every enchantment is built on **MerlinLib**, which is what registers it, wires its behaviour and provides the in-game tools for looking at it.

### What it adds

- Enchantments for **weapons, tools, armour and tridents**, each with its own effect rather than a plain damage bonus.
- **Mutual exclusions** between enchantments where they would overlap, following the vanilla anvil and enchanting table rules.
- **Ways to obtain them** that follow vanilla progression: the enchanting table, librarian trades, and enchanted books in structure loot.
- **Enchantments that interact with mobs** - some are about how mobs react to you rather than about damage.

### Requirements

- **MerlinLib**, installed on **both** the client and the server. It is required, not optional.
- Both loaders are supported natively: **Fabric** and **NeoForge**.
- This is a **client and server** mod: install it on both sides.

### Installing

1. Install MerlinLib for your loader.
2. Drop the Practical Enchantments jar for your loader into `mods/`.
3. Client and server both need both jars.

### Finding and giving enchantments in game

MerlinLib provides the tooling, so this mod ships no commands of its own:

- `/merlinlib list` - every non-vanilla enchantment in the registry, including all of this mod's.
- `/merlinlib info <id>` - one enchantment: whether it is in the registry, its maximum level and weight.
- `/merlinlib book <id> [level]` - hands out the enchanted book (operators).
- Enchantment ids look like `practical_enchantments:<name>`, usable in commands, data packs and the item editor.

Enchantment **descriptions** in the tooltip come from an enchantment description mod (such as EnchantmentDescriptions); this mod does not draw them itself.

### Settings

- Enchantment definitions, exclusion groups and villager trades are data pack resources, so `/reload` picks up changes without restarting.
- The join message in chat: its text lives in `assets/practical_enchantments/lang/`, and its switch is `notice.merlinlib` in `config/practical_enchantments/common.properties`. It can also be overridden per mod from **MerlinLib config → General → Edit each mod's join notices**.

### Links

- Repository and issues: <https://github.com/Huziyang520/Practical-Enchantments>
- Also on CurseForge, under the same name.

---

## 中文

**Practical Enchantments（实用附魔）** 增加一批**在玩法上真的有用**的附魔：战斗、挖掘、防御、三叉戟，以及与生物互动的机制，而不是只改数字。每个附魔都构建在 **MerlinLib** 之上——由它完成注册、行为接线，并提供查看与发放的界面与指令。

当前附魔清单见 CurseForge 页面；游戏内用 `/merlinlib list` 可以直接列出。

### 它增加了什么

- 面向**武器、工具、盔甲与三叉戟**的附魔，每个都有自己的效果，而不是单纯的数值加成。
- 附魔之间的**互斥关系**，口径与铁砧、附魔台一致。
- 顺着原版进程的**获取途径**：附魔台、图书管理员交易、结构箱子里的附魔书。
- 一批**与生物互动**的附魔：它们改变的是生物的应对方式，而不是伤害数字。

### 前置要求

- **MerlinLib**，且**客户端与服务端都要安装**。它是必需前置，不是可选。
- 原生支持 **Fabric** 与 **NeoForge** 双端。
- 本模组是**客户端 + 服务端**模组，两端都要装。

### 安装

1. 为对应加载器装好 MerlinLib。
2. 把对应加载器的 Practical Enchantments 放进 `mods/`。
3. 客户端与服务端都需要这两个 jar。

### 在游戏内查看与发放

工具由 MerlinLib 提供，本模组不带自己的指令：

- `/merlinlib list` —— 列出注册表里所有非原版附魔（含本模组全部）。
- `/merlinlib info <id>` —— 单个附魔：是否在注册表、最大等级、权重。
- `/merlinlib book <id> [等级]` —— 发放对应附魔书（需要 OP）。
- 附魔 id 形如 `practical_enchantments:<名字>`，可用于指令、数据包与物品编辑器。

附魔的**描述文本**由附魔描述类模组（如 EnchantmentDescriptions）显示；本模组自己不绘制描述。

### 设置

- 附魔定义、互斥组与村民交易都是**数据包资源**，改完 `/reload` 即生效，无需重启。
- 进服时聊天栏的公告：文案在 `assets/practical_enchantments/lang/`，开关是 `config/practical_enchantments/common.properties` 的 `notice.merlinlib`；也可以在 **MerlinLib 设置 → 通用 → 编辑各模组聊天栏提示** 里按模组覆盖。

### 链接

- 仓库与问题反馈：<https://github.com/Huziyang520/Practical-Enchantments>
- CurseForge 上同名可搜到。
