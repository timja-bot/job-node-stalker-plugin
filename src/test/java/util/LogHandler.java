package util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {

    Level lastLevel = Level.FINEST;

    public List<LogRecord> records = new ArrayList<LogRecord>();

    public void publish(LogRecord record) {
        records.add(record);
    }

    public List<LogRecord> getRecords(){
        return records;
    }


    public void close(){}
    public void flush(){}
}