package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;

/**
 * 将结果写成Sql的默认实现
 * @author imbugs
 */
public class DefaultSqlFileWriter extends DefaultCallback {
    protected FileWriter    fileWriter;
    protected static String CREATE_TABLE         = "CREATE TABLE IF NOT EXISTS simperf_result( "
                                                   + "id BIGINT NOT NULL AUTO_INCREMENT ,time BIGINT NULL ,"
                                                   + "avgtps VARCHAR(45) NULL ,count BIGINT NULL ,duration BIGINT NULL ,"
                                                   + "fail BIGINT NULL ,ttps BIGINT NULL ,tcount BIGINT NULL ,tduration BIGINT NULL ,"
                                                   + "tfail BIGINT NULL ,summary TINYINT(1) NULL DEFAULT 0 ,PRIMARY KEY (id) );\n";
    protected static String SQL_TEMPLATE         = "insert into simperf_result(time ,avgtps ,count ,duration ,fail ,ttps ,tcount ,tduration ,tfail ,summary) "
                                                   + "values(%s ,'%s' ,%d ,%d ,%d ,'%s' ,%d ,%d ,%d ,0);\n";
    protected static String SUMMARY_SQL_TEMPLATE = "insert into simperf_result(time ,avgtps ,count ,duration ,fail ,ttps ,tcount ,tduration ,tfail ,summary) "
                                                   + "values(%s ,'%s' ,%d ,%d ,%d ,'%s' ,%d ,%d ,%d ,1);\n";

    public DefaultSqlFileWriter(String logFile) {
        super();
        try {
            this.fileWriter = new FileWriter(logFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStart(MonitorThread monitorThread) {
        try {
            this.fileWriter.write("\n--\n");
            List<String> messages = monitorThread.getMessages();
            for (String string : messages) {
                this.fileWriter.write("-- " + string);
            }
            this.fileWriter.write("--\n");
            this.fileWriter.write(CREATE_TABLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
        try {
            statInfo.setMsgFormat(SQL_TEMPLATE);
            statInfo.setDataFormat(null);
            this.fileWriter.write(statInfo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onExit(MonitorThread monitorThread) {
        try {
            StatInfo statInfo = monitorThread.getStatInfo();
            statInfo.setMsgFormat(SUMMARY_SQL_TEMPLATE);
            statInfo.setDataFormat(null);
            
            this.fileWriter.write(statInfo.toString());
            this.fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
