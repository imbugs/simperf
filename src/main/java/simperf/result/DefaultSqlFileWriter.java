package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;

/**
 * 将结果写成Sql的默认实现
 * @author imbugs
 */
public class DefaultSqlFileWriter extends DefaultCallback {
    private static final Logger logger               = LoggerFactory
                                                         .getLogger(DefaultSqlFileWriter.class);

    protected FileWriter        fileWriter;
    protected static String     CREATE_TABLE         = "CREATE TABLE IF NOT EXISTS simperf_result( "
                                                       + "id BIGINT NOT NULL AUTO_INCREMENT ,time BIGINT NULL ,"
                                                       + "avgtps VARCHAR(45) NULL ,count BIGINT NULL ,duration BIGINT NULL ,"
                                                       + "fail BIGINT NULL ,ttps VARCHAR(45) NULL ,tcount BIGINT NULL ,tduration BIGINT NULL ,"
                                                       + "tfail BIGINT NULL ,summary TINYINT(1) NULL DEFAULT 0 ,PRIMARY KEY (id) );";
    protected static String     SQL_TEMPLATE         = "insert into simperf_result(time ,avgtps ,count ,duration ,fail ,ttps ,tcount ,tduration ,tfail ,summary) "
                                                       + "values(%s ,'%s' ,%d ,%d ,%d ,'%s' ,%d ,%d ,%d ,0);";
    protected static String     SUMMARY_SQL_TEMPLATE = "insert into simperf_result(time ,avgtps ,count ,duration ,fail ,ttps ,tcount ,tduration ,tfail ,summary) "
                                                       + "values(%s ,'%s' ,%d ,%d ,%d ,'%s' ,%d ,%d ,%d ,1);";

    public DefaultSqlFileWriter(String logFile) {
        super();
        try {
            this.fileWriter = new FileWriter(logFile, true);
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }
    }

    public void onStart(MonitorThread monitorThread) {
        try {
            this.fileWriter.write("\n--\n");
            List<String> messages = monitorThread.getMessages();
            for (String string : messages) {
                this.fileWriter.write("-- " + string + "\n");
            }
            this.fileWriter.write("--\n");
            this.fileWriter.write(CREATE_TABLE + "\n");
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }
    }

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
        try {
            statInfo.setMsgFormat(SQL_TEMPLATE);
            statInfo.setDataFormat(null);
            this.fileWriter.write(statInfo.toString() + "\n");
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }
    }

    public void onExit(MonitorThread monitorThread) {
        try {
            StatInfo statInfo = monitorThread.getStatInfo();
            statInfo.setMsgFormat(SUMMARY_SQL_TEMPLATE);
            statInfo.setDataFormat(null);

            this.fileWriter.write(statInfo.toString() + "\n");
            this.fileWriter.close();
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }
    }
}
