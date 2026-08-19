package dev.scx.app;

import java.util.List;

/// ScxApp
///
/// @author scx567888
public interface ScxApp {

    static ScxAppBuilder builder() {
        return new DefaultScxAppBuilder();
    }

    /// 当前 candidates (不可变)
    List<Class<?>> candidates();

    /// run
    void run() throws Exception;

    /// shutdown
    void shutdown();

    /// 根据 Class 获取模块, 不存在时返回 null.
    <T extends ScxAppModule> T getModule(Class<T> type);

}
