package dev.scx.app;

import java.util.*;

/// ScxAppHelper
///
/// @author scx567888
final class ScxAppHelper {

    /// 返回 去重的 不可变的 List.
    public static List<Class<?>> collectCandidates(ScxAppModuleDefinition[] definitions) {
        return Arrays.stream(definitions)
            .flatMap(d -> d.candidates().stream())
            .distinct()
            .toList();
    }

    /// ScxAppModule start 顺序解析器.
    ///
    /// 规则:
    ///
    /// - A.startBefore(B) 表示 A.start 必须早于 B.start.
    /// - A.startAfter(B) 表示 A.start 必须晚于 B.start.
    /// - startBefore / startAfter 引用的模块可选存在.
    /// - requires 引用的模块必须存在.
    /// - 没有顺序关系的模块, 保持用户注册顺序.
    /// - 如果启动顺序存在环, 解析失败.
    ///
    /// 因为这里是内部方法, 所以可以认为 modules 和 definitions 始终保持一一对应, 不需要额外校验.
    public static ScxAppModule[] resolveModuleOrder(ScxAppModule[] modules, ScxAppModuleDefinition[] definitions) {
        // 1. 建立 module class -> index 映射
        // 注意: 因为 startBefore / startAfter 使用 Class 表达目标模块,
        // 所以同一个 Class 出现多个实例会产生歧义, 这里直接禁止.
        var moduleIndexMap = new HashMap<Class<? extends ScxAppModule>, Integer>();

        for (int i = 0; i < modules.length; i = i + 1) {
            var moduleClass = modules[i].getClass();

            var oldIndex = moduleIndexMap.put(moduleClass, i);

            if (oldIndex != null) {
                throw new IllegalArgumentException(
                    "Duplicate ScxAppModule class is not allowed: " + moduleClass.getName()
                );
            }
        }

        // 2. 校验 requires
        for (int i = 0; i < definitions.length; i = i + 1) {
            var moduleClass = modules[i].getClass();
            var definition = definitions[i];

            for (var requiredClass : definition.requires()) {
                if (!moduleIndexMap.containsKey(requiredClass)) {
                    throw new IllegalStateException(
                        "ScxAppModule " + moduleClass.getName() + " requires missing module: " + requiredClass.getName()
                    );
                }
            }
        }

        // 3. 构建图
        // edge: from -> to 表示 from 必须早于 to 启动
        var graph = new ArrayList<Set<Integer>>();
        var inDegree = new int[modules.length];

        for (int i = 0; i < modules.length; i = i + 1) {
            graph.add(new LinkedHashSet<>());
        }

        for (int i = 0; i < definitions.length; i = i + 1) {
            var definition = definitions[i];

            // 当前模块 startBefore(target)
            // 当前模块 -> target
            for (var targetClass : definition.startBefores()) {
                var targetIndex = moduleIndexMap.get(targetClass);

                // before/after 的目标模块不存在时忽略
                if (targetIndex == null) {
                    continue;
                }

                addEdge(graph, inDegree, i, targetIndex);
            }

            // 当前模块 startAfter(target)
            // target -> 当前模块
            for (var targetClass : definition.startAfters()) {
                var targetIndex = moduleIndexMap.get(targetClass);

                // before/after 的目标模块不存在时忽略
                if (targetIndex == null) {
                    continue;
                }

                addEdge(graph, inDegree, targetIndex, i);
            }
        }

        // 4. 稳定拓扑排序
        // PriorityQueue 按原始注册顺序取出, 保证没有依赖关系时保持用户注册顺序.
        var ready = new PriorityQueue<Integer>();

        for (int i = 0; i < inDegree.length; i = i + 1) {
            if (inDegree[i] == 0) {
                ready.add(i);
            }
        }

        var orderedModules = new ArrayList<ScxAppModule>();

        while (!ready.isEmpty()) {
            var current = ready.remove();

            orderedModules.add(modules[current]);

            for (var next : graph.get(current)) {
                inDegree[next] = inDegree[next] - 1;

                if (inDegree[next] == 0) {
                    ready.add(next);
                }
            }
        }

        // 5. 检查环
        if (orderedModules.size() != modules.length) {
            throw new IllegalStateException(
                "Circular ScxAppModule start dependency detected !!!"
            );
        }

        return orderedModules.toArray(ScxAppModule[]::new);
    }

    private static void addEdge(List<Set<Integer>> graph, int[] inDegree, int from, int to) {
        if (from == to) {
            throw new IllegalStateException("ScxAppModule cannot depend on itself !!!");
        }

        // 用 Set 防止重复边导致 inDegree 被重复增加
        if (graph.get(from).add(to)) {
            inDegree[to] = inDegree[to] + 1;
        }
    }

}
