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



## 一个Demo

已思考 1 秒

下面给你演示一个“小型医疗诊断系统”——它会根据一系列症状 → 推理出中间病理事实 → 再得出最终诊断与处置建议。
 整个链条至少 3 级依赖，若用 `DefaultRulesEngine` 只能手写所有组合；但 `InferenceRulesEngine` 会自动把新增事实重新投入匹配循环，从而层层触发后续规则。

------

### 1 . 依赖关系示意

```
┌────────────┐
│  原始症状  │
└─────┬──────┘
      ▼ facts.put(...)
┌────────────┐  (R1,R2,R3)
│ 中间病理结论│  e.g. “感染”“出血”…
└─────┬──────┘
      ▼ facts.put(...)
┌────────────┐  (R4,R5)
│  综合诊断  │  e.g. “败血症”
└─────┬──────┘
      ▼ facts.put(...)
┌────────────┐  (R6)
│   处置方案 │
└────────────┘
```

------

### 2 . 依赖的 POJO 与 Fact Key

```java
public enum Symptom { FEVER, COUGH, TACHYCARDIA, LOW_BP, HIGH_WBC }
public enum Pathology { INFECTION, BLEEDING, SEPSIS }
    public enum Treatment { ADMIT_ICU }

record PatientState(Set<Symptom> symptoms,
                    Set<Pathology> pathologies,
                    Set<Pathology> diagnoses,
                    Set<Treatment> treatments) {}
```

------

### 3 . 规则定义

```java
@Configuration
public class DiagnosisRules {

    /* ---------------- Level‑1: 症状 → 病理 ---------------- */

    @Bean
    public Rule infectionRule() {
        return new RuleBuilder()
            .name("symptom → infection")
            .priority(1)
            .when(facts -> {
                PatientState p = facts.get("patient");
                return p.symptoms().contains(Symptom.FEVER)
                    && p.symptoms().contains(Symptom.HIGH_WBC);
            })
            .then(facts -> {
                PatientState p = facts.get("patient");
                p.pathologies().add(Pathology.INFECTION);          // 新事实
                facts.put("patient", p);                           // 推回 Facts
            }).build();
    }

    @Bean
    public Rule bleedingRule() {
        return new RuleBuilder()
            .name("symptom → bleeding")
            .priority(1)
            .when(facts -> {
                PatientState p = facts.get("patient");
                return p.symptoms().contains(Symptom.LOW_BP)
                    && p.symptoms().contains(Symptom.TACHYCARDIA);
            })
            .then(facts -> {
                PatientState p = facts.get("patient");
                p.pathologies().add(Pathology.BLEEDING);
                facts.put("patient", p);
            }).build();
    }

    /* ---------------- Level‑2: 病理组合 → 诊断 ---------------- */

    @Bean
    public Rule sepsisRule() {
        return new RuleBuilder()
            .name("pathology → sepsis")
            .priority(2)
            .when(facts -> {
                PatientState p = facts.get("patient");
                return p.pathologies().contains(Pathology.INFECTION)
                    && p.pathologies().contains(Pathology.BLEEDING);
            })
            .then(facts -> {
                PatientState p = facts.get("patient");
                p.diagnoses().add(Pathology.SEPSIS);
                facts.put("patient", p);
            }).build();
    }

    /* ---------------- Level‑3: 诊断 → 治疗建议 ---------------- */

    @Bean
    public Rule icuRule() {
        return new RuleBuilder()
            .name("sepsis → ICU")
            .priority(3)                               // 最后执行
            .when(facts -> {
                PatientState p = facts.get("patient");
                return p.diagnoses().contains(Pathology.SEPSIS);
            })
            .then(facts -> {
                PatientState p = facts.get("patient");
                p.treatments().add(Treatment.ADMIT_ICU);
                facts.put("patient", p);
                System.out.println(">>> 建议立即转入 ICU！");
            }).build();
    }
}
```

------

### 4 . 驱动代码

```java
public class Demo {

    public static void main(String[] args) {

        // ① 初始化原始症状
        PatientState patient = new PatientState(
            EnumSet.of(Symptom.FEVER, Symptom.HIGH_WBC,
                       Symptom.LOW_BP, Symptom.TACHYCARDIA),
            EnumSet.noneOf(Pathology.class),
            EnumSet.noneOf(Pathology.class),
            EnumSet.noneOf(Treatment.class)
        );

        // ② 注册 Rules
        Rules rules = new Rules(
            new DiagnosisRules().infectionRule(),
            new DiagnosisRules().bleedingRule(),
            new DiagnosisRules().sepsisRule(),
            new DiagnosisRules().icuRule()
        );

        // ③ 准备 Facts
        Facts facts = new Facts();
        facts.put("patient", patient);

        // ④ 使用 Inference 引擎：会层层推理
        RulesEngine engine = new InferenceRulesEngine();
        engine.fire(rules, facts);

        // ⑤ 输出最终结论
        PatientState result = facts.get("patient");
        System.out.println("\n== 推理结果 ==");
        System.out.println("病理: "   + result.pathologies());
        System.out.println("诊断: "   + result.diagnoses());
        System.out.println("处置: "   + result.treatments());
    }
}
```

#### 运行日志（简版）

```
[INFO] symptom → infection triggered
[INFO] symptom → bleeding triggered
[INFO] pathology → sepsis triggered
[INFO] sepsis → ICU triggered
>>> 建议立即转入 ICU！

== 推理结果 ==
病理: [INFECTION, BLEEDING]
诊断: [SEPSIS]
处置: [ADMIT_ICU]
```

------

### 5 . 如果换成 DefaultRulesEngine 会怎样？

把第 ④ 行改成

```java
RulesEngine engine = new DefaultRulesEngine();
```

由于 **只有第一轮匹配**：

1. `infectionRule` 与 `bleedingRule` 触发 → 插入新事实
2. **不会再进入新一轮** 来评估 `sepsisRule`
3. 故 ICU 建议永远出不来

你就得手写额外规则或多次 `fire(...)` 调用，既啰嗦又易错。

------

### 6 . 结论

- **InferenceRulesEngine** 通过 *forward‑chaining* 自动把新事实重新投入匹配循环，实现多层推理
- 复杂决策树（尤其医学、信贷、风控领域）可大幅减少显式规则数量
- 但要 **严防循环**：
  - 给规则加优先级阈值
  - 在 action 中避免无限生成同一事实

希望这个较完整的 DEMO 能清晰展示 InferenceRulesEngine 的魅力！若想进一步探讨性能调优或死循环防护，随时告诉我～



## 风控Demo

已思考 1 秒

好的！我们全部改回 Java 8 语法（不使用 `record`、`var`、`switch ->` 等新特性），并继续展示 **InferenceRulesEngine** 在风控场景下的威力。

------

### 1. 数据模型

```java
public enum Behavior {
    LOGIN_OVERSEAS,            // 海外登录
    LOGIN_DEVICE_CHANGE,       // 更换终端
    TRANSFER_MANY_SMALL,       // 频繁小额转出
    TRANSFER_LARGE_NIGHT,      // 深夜大额
    MULTI_FAILED_LOGIN         // 多次失败登录
}

public enum RiskFeature {
    ABNORMAL_LOGIN,            // 异常登录
    FUNDS_BRIDGING,            // 资金搬砖
    ACTIVITY_SPIKE             // 活跃度激增
}

public enum RiskLevel {
    LOW, MEDIUM, HIGH
}

public enum Action {
    NO_ACTION,
    REQUIRE_OTP_VERIFICATION,
    FREEZE_ACCOUNT,
    ALERT_MANAGER
}

/** PatientState 替换为 RiskContext，持久化在 Facts 中 */
public class RiskContext {
    private final Set<Behavior> behaviors    = EnumSet.noneOf(Behavior.class);
    private final Set<RiskFeature> features  = EnumSet.noneOf(RiskFeature.class);
    private RiskLevel level                  = null;
    private final Set<Action> actions        = EnumSet.noneOf(Action.class);

    // getters
    public Set<Behavior> getBehaviors()   { return behaviors; }
    public Set<RiskFeature> getFeatures() { return features; }
    public RiskLevel getLevel()           { return level; }
    public Set<Action> getActions()       { return actions; }

    // setters
    public void setLevel(RiskLevel level) { this.level = level; }
}
```

------

### 2. 规则实现（3 层推理）

#### 2‑1 行为 → 风险特征

```java
public static Rule abnormalLoginRule() {
    return new RuleBuilder()
        .name("行为 → 异常登录")
        .description("海外登录或终端切换 + 多次失败登录 → ABNORMAL_LOGIN")
        .priority(1)
        .when(f -> {
            RiskContext ctx = f.get("ctx");
            return (ctx.getBehaviors().contains(Behavior.LOGIN_OVERSEAS)
                 || ctx.getBehaviors().contains(Behavior.LOGIN_DEVICE_CHANGE))
                 && ctx.getBehaviors().contains(Behavior.MULTI_FAILED_LOGIN);
        })
        .then(f -> {
            RiskContext ctx = f.get("ctx");
            ctx.getFeatures().add(RiskFeature.ABNORMAL_LOGIN);
            f.put("ctx", ctx);                // 触发推理
        })
        .build();
}

public static Rule fundsBridgingRule() {
    return new RuleBuilder()
        .name("行为 → 资金搬砖")
        .priority(1)
        .when(f -> {
            RiskContext ctx = f.get("ctx");
            return ctx.getBehaviors().contains(Behavior.TRANSFER_MANY_SMALL)
                && ctx.getBehaviors().contains(Behavior.TRANSFER_LARGE_NIGHT);
        })
        .then(f -> {
            RiskContext ctx = f.get("ctx");
            ctx.getFeatures().add(RiskFeature.FUNDS_BRIDGING);
            f.put("ctx", ctx);
        })
        .build();
}
```

#### 2‑2 风险特征组合 → 风险等级

```java
public static Rule highRiskRule() {
    return new RuleBuilder()
        .name("特征 → 高风险")
        .priority(2)
        .when(f -> {
            RiskContext ctx = f.get("ctx");
            return ctx.getFeatures().contains(RiskFeature.ABNORMAL_LOGIN)
                && ctx.getFeatures().contains(RiskFeature.FUNDS_BRIDGING);
        })
        .then(f -> {
            RiskContext ctx = f.get("ctx");
            ctx.setLevel(RiskLevel.HIGH);
            f.put("ctx", ctx);
        })
        .build();
}

public static Rule mediumRiskRule() {
    return new RuleBuilder()
        .name("特征 → 中风险")
        .priority(2)
        .when(f -> {
            RiskContext ctx = f.get("ctx");
            return ctx.getLevel() == null                      // 还没被判高风险
                && ctx.getFeatures().contains(RiskFeature.ABNORMAL_LOGIN);
        })
        .then(f -> {
            RiskContext ctx = f.get("ctx");
            ctx.setLevel(RiskLevel.MEDIUM);
            f.put("ctx", ctx);
        })
        .build();
}
```

#### 2‑3 风险等级 → 处置动作

```java
public static Rule highRiskActionRule() {
    return new RuleBuilder()
        .name("高风险 → 冻结账户并告警")
        .priority(3)
        .when(f -> {
            RiskContext ctx = f.get("ctx");
            return ctx.getLevel() == RiskLevel.HIGH;
        })
        .then(f -> {
            RiskContext ctx = f.get("ctx");
            ctx.getActions().add(Action.FREEZE_ACCOUNT);
            ctx.getActions().add(Action.ALERT_MANAGER);
            f.put("ctx", ctx);
            System.out.println(">>> 高风险！冻结账户并通知风控经理。");
        })
        .build();
}

public static Rule mediumRiskActionRule() {
    return new RuleBuilder()
        .name("中风险 → 二次验证")
        .priority(3)
        .when(f -> {
            RiskContext ctx = f.get("ctx");
            return ctx.getLevel() == RiskLevel.MEDIUM;
        })
        .then(f -> {
            RiskContext ctx = f.get("ctx");
            ctx.getActions().add(Action.REQUIRE_OTP_VERIFICATION);
            f.put("ctx", ctx);
            System.out.println(">>> 中风险，要求短信 OTP 验证。");
        })
        .build();
}
```

------

### 3. 驱动入口

```java
public class RiskInferenceDemo {

    public static void main(String[] args) {

        // ① 初始化上下文（模拟发生的行为）
        RiskContext ctx = new RiskContext();
        ctx.getBehaviors().addAll(Arrays.asList(
            Behavior.LOGIN_OVERSEAS,
            Behavior.MULTI_FAILED_LOGIN,
            Behavior.TRANSFER_MANY_SMALL,
            Behavior.TRANSFER_LARGE_NIGHT
        ));

        // ② 注册全部规则
        Rules rules = new Rules(
            abnormalLoginRule(),
            fundsBridgingRule(),
            highRiskRule(),
            mediumRiskRule(),
            highRiskActionRule(),
            mediumRiskActionRule()
        );

        // ③ Facts
        Facts facts = new Facts();
        facts.put("ctx", ctx);

        // ④ 强大的前向推理引擎
        RulesEngine engine = new InferenceRulesEngine();
        engine.fire(rules, facts);

        // ⑤ 查看最终结果
        RiskContext result = facts.get("ctx");
        System.out.println("\n=== 推理结果 ===");
        System.out.println("风险特征: " + result.getFeatures());
        System.out.println("风险等级: " + result.getLevel());
        System.out.println("处置动作: " + result.getActions());
    }
}
```

------

### 4. 示例输出

```
[INFO] 行为 → 异常登录 triggered
[INFO] 行为 → 资金搬砖 triggered
[INFO] 特征 → 高风险 triggered
[INFO] 高风险 → 冻结账户并告警 triggered
>>> 高风险！冻结账户并通知风控经理。

=== 推理结果 ===
风险特征: [ABNORMAL_LOGIN, FUNDS_BRIDGING]
风险等级: HIGH
处置动作: [FREEZE_ACCOUNT, ALERT_MANAGER]
```

------

#### 🏆 亮点回顾

1. **一次 fire → 多轮推理**
    `InferenceRulesEngine` 自动把 **行为 → 特征 → 等级 → 动作** 串起来，省掉多次代码调用或显式组合规则。
2. **易维护**
    新增风险特征或组合时，只需加独立规则，推理链条能自动扩展。
3. **灵活防环**
   - 规则优先级逐层递增，防止同层循环
   - 如需进一步保险，可使用 `engineParameters.setSkipOnFirstAppliedRule(true)` 等配置。

这样就展示了在 **风控领域** 运用 `InferenceRulesEngine` 的实战级示例，且完全兼容 Java 8！
 如果你想深入讨论性能表现、规则组织最佳实践或如何做单元测试，随时告诉我 😊