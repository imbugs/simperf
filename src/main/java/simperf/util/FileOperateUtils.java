package simperf.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件操作工具
 * @author imbugs
 * @version $Id: FileOperateUtils.java,v 0.1 2010-6-29 上午11:41:15 imbugs Exp $
 */
public class FileOperateUtils {

    /** Logger */
    protected static Logger logger = LoggerFactory.getLogger(FileOperateUtils.class);

    /**
     * 向文件中追加内容
     * @param path
     * @param contents
     */
    public static void appendFile(String path, String contents) {
        try {
            FileWriter fw = new FileWriter(path, true);
            fw.write(contents);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向文件中写入数据,会覆盖原文件
     * @param path     指定文件路径
     * @param contents 写入内容
     */
    public static void writeFile(String path, String contents) {
        writeFile(path, contents, true);
    }

    /**
     * 
     * @param path
     * @param contents
     * @param overwrite true: 覆盖原文件  false: 如指定文件已经存在则不进行操作
     */
    public static void writeFile(String path, String contents, boolean overwrite) {
        File file = new File(path);
        try {
            if (overwrite && file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
                FileWriter fw = new FileWriter(path);
                fw.write(contents);
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归查找文件
     * @param findDir 目录
     * @param fileNameRegex 文件名匹配表达式, null:不过滤任何文件
     * @param fileList 返回文件列表
     */
    public static void findFileRecursive(final String findDir, final String fileNameRegex,
                                         List<File> fileList) {
        if (fileList == null) {
            return;
        }
        File file = new File(findDir);
        if (file.isFile()) {
            if (null == fileNameRegex || file.getName().matches(fileNameRegex)) {
                fileList.add(file);
            }
        } else if (file.isDirectory()) {
            File[] dirFiles = file.listFiles();
            for (File dirFile : dirFiles) {
                findFileRecursive(dirFile.getAbsolutePath(), fileNameRegex, fileList);
            }
        }
    }

    /**
     * 备份文件
     * @param fromFile
     * @return
     */
    public static boolean backupFile(File fromFile) {

        if (!fromFile.exists()) {
            return false;
        }

        String bakFileName = fromFile.getName() + ".bak";

        return renameFile(fromFile, bakFileName);

    }

    /**
     * 备份文件；并删除原文件
     * @param fromFile
     * @return
     */
    public static boolean backupFileToDel(File fromFile) {

        if (!fromFile.exists()) {
            return false;
        }

        return backupFile(fromFile) && fromFile.delete();

    }

    /**
     * 重命名文件
     * 注意：若newName已存在，会直接删除
     * 
     * @param fromFile-原文件
     * @param toFile-目的文件
     * @return
     */
    public static boolean renameFile(File fromFile, String newName) {

        // 检查重命名的合法性；若newName文件已存在；删除
        String orgiFilePath = fromFile.getParent();
        File newFile = new File(orgiFilePath + "/" + newName);

        if (newFile.exists() && newFile.delete()) {
            logger.error(newFile.getAbsolutePath() + " 已存在并删除成功！");
        }

        // 正式备份原始文件
        if (fromFile.renameTo(newFile)) {
            logger.error(fromFile.getName() + "重命名为" + newFile.getName() + "成功！");
            return true;
        } else {
            logger.error(fromFile.getName() + " 重命名为" + newFile.getName() + "失败！");
            return false;
        }

    }

    /**
     * 复制文件（单个）;目的文件若存在，直接删除
     * @param fromFile-原文件
     * @param toFile-目的文件
     * @return
     * @throws IOException 
     */
    public static boolean copyFile(File fromFile, File toFile) throws IOException {

        if (toFile.exists() && toFile.delete()) {
            logger.error(toFile.getAbsolutePath() + " 已存在并删除成功！");
        }

        if (fromFile.exists()) {
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fromFile));
            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(toFile));

            //复制   
            int c;
            while ((c = bin.read()) != -1) {
                bout.write(c);

            }
            bin.close();
            bout.close();
            return true;
        } else {
            logger.error(fromFile.getAbsolutePath() + " 不存在，复制失败！");
            return false;
        }

    }
}
