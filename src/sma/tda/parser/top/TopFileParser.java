package sma.tda.parser.top;

import java.io.File;
import java.util.List;
import sma.tda.entity.Top;

public interface TopFileParser {
  List<Top> parse(File paramFile);
}
