package dev.scx.app;

import java.util.ArrayList;
import java.util.List;

/// ScxAppModule 在当前 ScxApp 中的定义信息.
///
/// 每个 ScxAppModule 会通过 define() 返回一个 ScxAppModuleDefinition.
///
/// ScxApp 会在所有模块 define() 完成后汇总这些定义, 并据此完成:
///
/// - 模块依赖校验
/// - 模块 start 顺序计算
/// - 候选类收集
///
/// startBefore / startAfter 表达模块 start 顺序关系:
///
/// - startBefore(A) 表示当前模块的 start 在 A.start 之前执行,
///
/// - startAfter(A) 表示当前模块的 start 在 A.start 之后执行,
///
/// 如果目标模块不存在, 对应顺序约束会被忽略.
/// 如果 startBefore / startAfter 形成环, ScxApp 会在启动图构建阶段失败.
///
/// requires 表示模块存在关系.
///
/// @author scx567888
public final class ScxAppModuleDefinition {

    private final List<Class<?>> candidates;
    private final List<Class<? extends ScxAppModule>> startBefores;
    private final List<Class<? extends ScxAppModule>> startAfters;
    private final List<Class<? extends ScxAppModule>> requires;

    private ScxAppModuleDefinition() {
        // 这里我们全部采用 list 而不是 set, 因为在把多个模块聚合之前 去重是没意义的.
        this.candidates = new ArrayList<>();
        this.startBefores = new ArrayList<>();
        this.startAfters = new ArrayList<>();
        this.requires = new ArrayList<>();
    }

    public static ScxAppModuleDefinition of() {
        return new ScxAppModuleDefinition();
    }

    /// 添加当前模块提供的候选类.
    public ScxAppModuleDefinition candidate(Class<?>... candidates) {
        // 这里使用 List.of() 保证 最终的 candidates 中不会加入 null, 其他 setter 方法同理.
        this.candidates.addAll(List.of(candidates));
        return this;
    }

    /// 声明当前模块的 start 需要早于指定模块执行.
    @SafeVarargs
    public final ScxAppModuleDefinition startBefore(Class<? extends ScxAppModule>... startBefores) {
        this.startBefores.addAll(List.of(startBefores));
        return this;
    }

    /// 声明当前模块的 start 需要晚于指定模块执行.
    @SafeVarargs
    public final ScxAppModuleDefinition startAfter(Class<? extends ScxAppModule>... startAfters) {
        this.startAfters.addAll(List.of(startAfters));
        return this;
    }

    /// 要求模块必须存在.
    @SafeVarargs
    public final ScxAppModuleDefinition require(Class<? extends ScxAppModule>... requires) {
        this.requires.addAll(List.of(requires));
        return this;
    }

    // ********************** getter (保持包私有以便直接返回内部视图) *************************

    List<Class<?>> candidates() {
        return candidates;
    }

    List<Class<? extends ScxAppModule>> startBefores() {
        return startBefores;
    }

    List<Class<? extends ScxAppModule>> startAfters() {
        return startAfters;
    }

    List<Class<? extends ScxAppModule>> requires() {
        return requires;
    }

}
