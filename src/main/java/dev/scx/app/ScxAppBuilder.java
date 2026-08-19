package dev.scx.app;

/// ScxAppBuilder
///
/// @author scx567888
public interface ScxAppBuilder {

    /// 添加模块
    ScxAppBuilder module(ScxAppModule... modules);

    ScxApp build();

    default ScxApp run() throws Exception {
        var scxApp = this.build();
        scxApp.run();
        return scxApp;
    }

}
