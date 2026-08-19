package dev.scx.app;

import dev.scx.ansi.Ansi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/// ScxAppBanner
///
/// @author scx567888
public final class ScxAppBanner {

    /// Date 格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /// 在控制台上打印 banner
    static void printBanner() {
        var date = DATE_FORMATTER.format(LocalDateTime.now());

        Ansi.ansi()
            .red("   ▄████████ ").green(" ▄████████ ").blue("▀████    ▐████▀ ").ln()
            .red("  ███    ███ ").green("███    ███ ").blue("  ███▌   ████▀  ").ln()
            .red("  ███    █▀  ").green("███    █▀  ").blue("   ███  ▐███    ").ln()
            .red("  ███        ").green("███        ").blue("   ▀███▄███▀    ").ln()
            .red("▀███████████ ").green("███        ").blue("   ████▀██▄     ").ln()
            .red("         ███ ").green("███    █▄  ").blue("  ▐███  ▀███    ").ln()
            .red("   ▄█    ███ ").green("███    ███ ").blue(" ▄███     ███▄  ").ln()
            .red(" ▄████████▀  ").green("████████▀  ").blue("████       ███▄ ").cyan(" Date ").brightCyan(date).println();
    }

}
