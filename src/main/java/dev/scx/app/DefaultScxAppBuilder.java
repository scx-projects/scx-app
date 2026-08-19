package dev.scx.app;

import java.util.ArrayList;
import java.util.List;

/// DefaultScxAppBuilder
///
/// @author scx567888
final class DefaultScxAppBuilder implements ScxAppBuilder {

    /// 模块列表
    private final List<ScxAppModule> modules;

    public DefaultScxAppBuilder() {
        this.modules = new ArrayList<>();
    }

    @Override
    public DefaultScxAppBuilder module(ScxAppModule... modules) {
        this.modules.addAll(List.of(modules));
        return this;
    }

    @Override
    public ScxApp build() {
        // 创建 ScxApp
        return new DefaultScxApp(this.modules.toArray(ScxAppModule[]::new));
    }

}
