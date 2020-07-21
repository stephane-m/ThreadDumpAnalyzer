package sma.tda.parser.top;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sma.tda.conf.Configuration;
import sma.tda.entity.Top;
import sma.tda.utils.Logger;

public class ByTimeTopFileParser implements TopFileParser
{
    private Pattern mTopStartLinePattern;
    private Pattern mJbossProcessLinePattern;
    private SimpleDateFormat dateFormatter;
    
    public ByTimeTopFileParser() {
        this.mTopStartLinePattern = null;
        this.mJbossProcessLinePattern = null;
        this.dateFormatter = null;
        this.mTopStartLinePattern = Pattern.compile(Configuration.getInstance().getTopStartLineRegex());
        this.mJbossProcessLinePattern = Pattern.compile(Configuration.getInstance().getJbossProcessLineRegex());
        (this.dateFormatter = new SimpleDateFormat(Configuration.getInstance().getThreadDumpTimeFormatPattern())).setTimeZone(TimeZone.getTimeZone(Configuration.getInstance().getThreadDumpDateFormatTimeZone()));
    }
    
    public List<Top> parse(final File pTopFile) {
        final List<Top> result = new ArrayList<Top>();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(pTopFile))) {
                try {
                    String line = null;
                    Top top = null;
                    while ((line = br.readLine()) != null) {
                        final Matcher newTopMatcher = this.mTopStartLinePattern.matcher(line);
                        if (newTopMatcher.find()) {
                            top = new Top();
                            try {
                                top.setDate(this.dateFormatter.parse(newTopMatcher.group(1)));
                            }
                            catch (ParseException e) {
                                Logger.getInstance().logError("Failed to parse date " + newTopMatcher.group(1), (Throwable)e);
                            }
                            result.add(top);
                        }
                        else {
                            final Matcher matcher2 = this.mJbossProcessLinePattern.matcher(line);
                            if (!matcher2.find()) {
                                continue;
                            }
                            final int pid = Integer.parseInt(matcher2.group(1));
                            final float cpu = Float.parseFloat(matcher2.group(2));
                            if (top == null) {
                                continue;
                            }
                            top.addProcess(Integer.valueOf(pid), Float.valueOf(cpu));
                        }
                    }
                }
                finally {
                    if (br != null) {
                        br.close();
                    }
                }
            }
        }
        catch (IOException e2) {
            Logger.getInstance().logError("Error while parsing file " + pTopFile.getAbsolutePath(), (Throwable)e2);
        }
        return result;
    }
}