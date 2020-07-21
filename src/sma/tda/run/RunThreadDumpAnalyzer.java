package sma.tda.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

import sma.tda.analyzer.AjpThreadReportProcessor;
import sma.tda.analyzer.CpuConsumingThreadReportProcessor;
import sma.tda.analyzer.GlobalReportProcessor;
import sma.tda.analyzer.LongThreadReportProcessor;
import sma.tda.analyzer.ThreadDumpAnalyzer;
import sma.tda.analyzer.ThreadDumpReportProcessor;
import sma.tda.entity.report.GlobalReport;
import sma.tda.parser.td.ThreadDumpParser;
import sma.tda.parser.top.ByTimeTopFileParser;
import sma.tda.parser.top.TopFileParser;
import sma.tda.report.SysoutReportPrinter;
import sma.tda.utils.Logger;

public class RunThreadDumpAnalyzer {
	public static void main(String[] args) {
		boolean initIsOk = false;
		Options options = new Options();
		Option helpOption = new Option("h", "help", false, "help");
		options.addOption(helpOption);
		Option debugOption = new Option("d", "debug", false, "debug mode");
		options.addOption(debugOption);
		Option configFileOption = new Option("c", true, "configuration file");
		options.addOption(configFileOption);
		File topFileFo = null;
		File threadDumpFileFo = null;
		DefaultParser defaultParser = new DefaultParser();
		try {
			CommandLine cmd = defaultParser.parse(options, args);
			if (cmd.hasOption("h")) {
				printUsage(options);
				return;
			}
			if (cmd.hasOption("d"))
				Logger.getInstance().setLoggingDebug(true);
			String configFile = cmd.getOptionValue("c");
			if (configFile != null)
				if (validateFile(configFile)) {
					overrideConfiguration(configFile);
				} else {
					return;
				}
			String[] parameters = cmd.getArgs();
			if (parameters != null && parameters.length == 2) {
				if (validateFile(parameters[0])) {
					topFileFo = new File(parameters[0]);
					if (validateFile(parameters[1])) {
						threadDumpFileFo = new File(parameters[1]);
						initIsOk = true;
					}
				}
			} else {
				Logger.getInstance()
						.logError("Error error received " + parameters.length + " parameters when 2 expected");
			}
		} catch (UnrecognizedOptionException e) {
			Logger.getInstance().logError("Error while parsing command line : " + e.getMessage());
		} catch (ParseException e) {
			Logger.getInstance().logError("Error while parsing command line", (Throwable) e);
		}
		if (!initIsOk) {
			printUsage(options);
			return;
		}
		List<GlobalReportProcessor> globalReportprocessors = new ArrayList<>();
		globalReportprocessors.add(new LongThreadReportProcessor());
		List<ThreadDumpReportProcessor> threadDumpReportprocessors = new ArrayList<>();
		threadDumpReportprocessors.add(new CpuConsumingThreadReportProcessor());
		threadDumpReportprocessors.add(new AjpThreadReportProcessor());
		ThreadDumpAnalyzer analyzer = new ThreadDumpAnalyzer((TopFileParser) new ByTimeTopFileParser(),
				new ThreadDumpParser(), globalReportprocessors, threadDumpReportprocessors);
		GlobalReport report = analyzer.analyze(topFileFo, threadDumpFileFo);
		SysoutReportPrinter sysoutReportPrinter = new SysoutReportPrinter();
		sysoutReportPrinter.printReport(report);
	}

	private static void printUsage(Options options) {
		(new HelpFormatter()).printHelp("java -jar ThreadDumpAnalyzer.jar [OPTIONS] <TOP FILE> <THREAD DUMP FILE>",
				options);
	}

	private static boolean validateFile(String filePath) {
		File f = new File(filePath);
		if (!f.exists()) {
			Logger.getInstance().logError("ERROR - File " + filePath + " not found");
			return false;
		}
		return true;
	}

	private static void overrideConfiguration(String configFilePath) {
		Properties newProps = new Properties();
		try (InputStream is = new FileInputStream(new File(configFilePath))) {
			newProps.load(is);
		} catch (IOException e) {
			Logger.getInstance().logError("Error while loading configuration " + configFilePath, e);
		}
	}
}
