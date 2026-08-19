package dev.scx.app;

import dev.scx.ansi.Ansi;

import java.util.List;

import static dev.scx.app.ScxAppBanner.printBanner;
import static dev.scx.app.ScxAppHelper.collectCandidates;
import static dev.scx.app.ScxAppHelper.resolveModuleOrder;

/// DefaultScxApp
///
/// @author scx567888
final class DefaultScxApp implements ScxApp {

    private ScxAppModule[] modules;
    private List<Class<?>> candidates;
    private int startedModuleIndex;

    public DefaultScxApp(ScxAppModule[] modules) {
        this.modules = modules;
        this.candidates = null;
        this.startedModuleIndex = -1;
    }

    @Override
    public List<Class<?>> candidates() {
        if (candidates == null) {
            throw new IllegalStateException("ScxApp has not been started !!!");
        }
        return candidates;
    }

    @Override
    public void run() throws Exception {
        var startTime = System.nanoTime();

        // 0, 打印 启动信息.
        printBanner();

        // 1, 获取 所有模块 定义.
        var definitions = defineModules();

        // 2, 根据 definitions 验证模块, 同时 获取真正的模块排序.
        this.modules = resolveModuleOrder(this.modules, definitions);

        // 3, 收集 候选类.
        this.candidates = collectCandidates(definitions);

        // 4, 启动模块.
        startModules();

        // 5, 添加 ShutdownHook
        Runtime.getRuntime().addShutdownHook(Thread.ofPlatform().unstarted(this::shutdown));

        // 6, 打印 启动用时.
        Ansi.ansi().brightGreen("ScxApp 启动完成, 用时 " + (System.nanoTime() - startTime) / 1_000_000 + " ms !!!").println();
    }

    @Override
    public void shutdown() {
        var startTime = System.nanoTime();

        // 0, 打印 停止信息.
        Ansi.ansi().brightRed("ScxApp 正在停止 !!!").println();

        // 1, 停止模块.
        stopModules();

        // 2, 打印 停止用时.
        Ansi.ansi().brightRed("ScxApp 停止完成, 用时 " + (System.nanoTime() - startTime) / 1_000_000 + " ms !!!").println();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ScxAppModule> T getModule(Class<T> type) {
        for (var module : modules) {
            if (module.getClass() == type) {
                return (T) module;
            }
        }
        return null;
    }

    private ScxAppModuleDefinition[] defineModules() throws Exception {
        var definitions = new ScxAppModuleDefinition[this.modules.length];

        for (int i = 0; i < this.modules.length; i = i + 1) {
            var module = this.modules[i];
            definitions[i] = module.define();
        }

        return definitions;
    }

    private void startModules() throws Exception {
        // 按排序正序启动模块
        for (int i = 0; i < this.modules.length; i = i + 1) {
            var module = this.modules[i];
            var moduleName = module.name();
            var startTime = System.nanoTime();

            try {
                Ansi.ansi().brightWhite("[").brightGreen("Starting").brightWhite("] " + moduleName).println();

                module.start(this);
                this.startedModuleIndex = i;

                Ansi.ansi().brightWhite("[").brightGreen("Start OK").brightWhite("] " + moduleName + " (" + (System.nanoTime() - startTime) / 1_000_000 + " ms)").println();
            } catch (Throwable e) {
                stopModules();
                throw e;
            }
        }
    }

    private void stopModules() {
        // 按排序倒序停止模块
        for (var i = this.startedModuleIndex; i >= 0; i = i - 1) {
            var module = this.modules[i];
            var moduleName = module.name();
            var startTime = System.nanoTime();

            try {
                Ansi.ansi().brightWhite("[").brightRed("Stopping").brightWhite("] " + moduleName).println();

                module.stop(this);

                Ansi.ansi().brightWhite("[").brightRed("Stop  OK").brightWhite("] " + moduleName + " (" + (System.nanoTime() - startTime) / 1_000_000 + " ms)").println();
            } catch (Throwable e) {
                // 忽略 stop 的异常
            }
        }

        this.startedModuleIndex = -1;
    }

}
