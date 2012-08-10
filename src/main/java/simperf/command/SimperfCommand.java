package simperf.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import simperf.Simperf;
import simperf.config.SimperfConfig;
import simperf.result.JTLResult;

/**
 * Simperf的命令行用法
 * @author imbugs
 */
public class SimperfCommand {
    private CommandLine cmd     = null;
    private Options     options = new Options();

    public SimperfCommand(String[] args) {
        options.addOption("t", "thread", true, "[*] number of thread count");
        options.addOption("c", "count", true, "[*] number of each thread requests count");
        options.addOption("i", "interval", true, "[ ] interval of print messages, default 1000");
        options.addOption("j", true, "[ ] generate jtl report");
        options.addOption("m", "maxtps", true, "[ ] max tps");
        options.addOption("l", "log", true, "[ ] log filename");

        try {
            cmd = new PosixParser().parse(options, args);
        } catch (ParseException e1) {
            new HelpFormatter().printHelp("SimperfCommand options", options);
            return;
        }
    }

    public Simperf create() {
        int thread = 0;
        if (!cmd.hasOption("t")) {
            new HelpFormatter().printHelp("SimperfCommand options", options);
            return null;
        }
        thread = Integer.valueOf(cmd.getOptionValue("t"));
        if (!cmd.hasOption("c")) {
            new HelpFormatter().printHelp("SimperfCommand options", options);
            return null;
        }

        int count = Integer.valueOf(cmd.getOptionValue("c"));
        int interval = 1000;
        if (cmd.hasOption("i")) {
            interval = Integer.valueOf(cmd.getOptionValue("i"));
        }

        Simperf simperf = new Simperf(thread, count, interval);

        if (cmd.hasOption("j")) {
            String jtlFile = cmd.getOptionValue("j");
            JTLResult jtlResult = new JTLResult(jtlFile, simperf.getPrintThread());
            SimperfConfig.setConfig(SimperfConfig.JTL_RESULT, jtlResult);
        }

        if (cmd.hasOption("m")) {
            int maxTps = Integer.valueOf(cmd.getOptionValue("m"));
            simperf.setMaxTps(maxTps);
        }
        if (cmd.hasOption("l")) {
            String logFile = cmd.getOptionValue("l");
            simperf.getPrintThread().setLogFile(logFile);
        }

        return simperf;
    }
}
