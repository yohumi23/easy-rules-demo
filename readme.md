[CSDN参考文章:规则引擎-Easy rule](https://blog.csdn.net/weixin_42454225/article/details/139427318?ops_request_misc=&request_id=&biz_id=102&utm_term=jeasyrule&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduweb~default-2-139427318.142^v102^pc_search_result_base1&spm=1018.2226.3001.4187)

[easy-rules的官方仓库](https://github.com/j-easy/easy-rules)
## 规则引擎要解决的问题
- 规则引擎可以完美的适配**根据XX条件执行XX方法**的业务；

## easy-rules 中 DefaultRulesEngine 和 InferenceRulesEngine 的区别
在 **Easy Rules**（最新版 4.x，GitHub repo: [j-easy/easy-rules]）的源码与文档里，两者的差异可以一句话概括：

> **DefaultRulesEngine** = “一次匹配‑一次执行”  
> **InferenceRulesEngine** = “前向链式推理 (forward‑chaining)”

下面结合 **Easy Rules** 的具体实现来拆解：

|  | **DefaultRulesEngine** (`org.jeasy.rules.core.DefaultRulesEngine`) | **InferenceRulesEngine** (`org.jeasy.rules.core.InferenceRulesEngine`) |
|---|---|---|
| **基类** | 两者都继承 `AbstractRulesEngine`，因此共享 listener、priority threshold、skipOnFirst… 等公共配置 ||
| **执行流程** | `fire(rules, facts)` 只 **循环一次**：1⃣ 评估所有规则形成冲突集  2⃣ 依据优先级排序并执行:动作里即使 `facts.put()` 插入新事实，也 **不会** 重新匹配 | `fire(rules, facts)` 内部封装了一个 **while( rulesWereFired )** 循环：<br>1⃣ 评估 → 2⃣ 执行<br>若执行动作后 `facts` 发生变化且新的规则满足 `evaluate`，循环继续；直到本轮没有规则再触发 |
| **核心字段** | 无额外状态 | 维护 `boolean rulesFired;` 来判断是否继续推理 |
| **源码关键行** | `DefaultRulesEngine.fire<br>doFire(rules, facts);` | `InferenceRulesEngine.fire<br/>do {<br/>  rulesFired = doFire(rules, facts);<br/>} while (rulesFired);` |
| **适合场景** | 决策表、优惠券、字段校验等**非递归**逻辑 | 症状 → 诊断、信用审批多层规则、需要自动 **衍生事实** 的场景 |
| **性能/可控性** | 性能可预测；避免无限循环风险 | 更强大，但要防止规则互相造新事实导致死循环；可配 `skipOnFirstNonTriggeredRule`、`priorityThreshold` 等限制 |

### 如何在代码里切换？

```java
Facts facts = new Facts();
Rules rules = new Rules();
// … register rules …

// 默认一次性执行
RulesEngine engine = new DefaultRulesEngine();
engine.fire(rules, facts);

// 如果需要推理
RulesEngine inferenceEngine = new InferenceRulesEngine();
inferenceEngine.fire(rules, facts);
```

### 小贴士

1. **避免死循环**
    - 给规则设置 `@Priority`，让推理早停
    - 在动作里谨慎 `facts.put()`；必要时加“标记”避免重复生成同一事实
2. **调试**
    - 开启 `engineParameters.setVerbose(true)`（4.x 用 `DefaultRulesEngineParameters`），查看每轮冲突集
3. **渐进迁移**
    - 先用 `DefaultRulesEngine` 验证业务正确性
    - 观测哪些规则会插入衍生事实，再切到 `InferenceRulesEngine`

这样，你就能在 **Easy Rules** 中准确挑选合适的执行器了。若还想看具体示例代码或推理日志解析，可以告诉我你目前的用例！





## 引擎参数RulesEngineParameters

以下是 [RulesEngineParameters]	类中 [toString]方法中提到的几个参数的详细解释：

### 1. skipOnFirstAppliedRule

- **含义**：当第一个规则被触发并成功执行后，是否跳过剩余的规则。
- **类型**：`boolean`
- **默认值**：`false`
- 作用
  - 如果设置为 `true`，当某个规则被成功应用后，规则引擎会停止评估后续规则。
  - 适用于规则互斥的场景（例如 FizzBuzz），可以提高性能。

------

### 2. skipOnFirstNonTriggeredRule

- **含义**：当第一个规则未触发（条件不满足）时，是否跳过剩余的规则。
- **类型**：`boolean`
- **默认值**：`false`
- 作用
  - 如果设置为 `true`，当某个规则未触发时，规则引擎会停止评估后续规则。
  - 适用于需要严格按照规则顺序执行的场景。

------

### 3. skipOnFirstFailedRule

- **含义**：当第一个规则执行失败时，是否跳过剩余的规则。
- **类型**：`boolean`
- **默认值**：`false`
- 作用
  - 如果设置为 `true`，当某个规则执行失败（例如抛出异常）时，规则引擎会停止评估后续规则。
  - 适用于需要保证规则执行成功的场景。

------

### 4. priorityThreshold

- **含义**：规则优先级的阈值，超过该阈值的规则将被跳过。
- **类型**：`int`
- **默认值**：[Integer.MAX_VALUE]（即不限制优先级）
- 作用
  - 规则引擎会跳过优先级高于该阈值的规则。
  - 适用于需要限制规则执行范围的场景，例如只执行优先级较低的规则。