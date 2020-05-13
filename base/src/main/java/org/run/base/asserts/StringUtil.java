package org.run.base.asserts;

import org.run.base.exception.RRException;

import java.util.UUID;

public class StringUtil {
    public static boolean isEmpty(Object object) {
        if(object == null) {
            return true;
        }else {
            if(object instanceof String) {
                String str = object.toString();
                if("".equals(str.trim())) {
                    return true;
                }
            }
            return false;
        }
    }
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    /**
     * 为文件上传预先处理好文件名,避免保存出错
     * @param fileName
     * @return
     */
    public static String preFileName(String fileName) {
        if(isEmpty(fileName)) {
            throw new RRException("文件名不能为空");
        }
        String[] fileNames = fileName.split("\\.");
        if(fileNames.length < 2) {
            throw new RRException("文件不能没有后缀名");
        }
        String realFileName = fileNames[0];
        realFileName = getUUID() + realFileName;
        for(int i = 1 ; i < fileNames.length ; i++) {
            if(i == fileNames.length - 1) {
                realFileName = realFileName +"."+ fileNames[i];
            }else {
                realFileName = realFileName + fileNames[i];
            }

        }
        return realFileName;
    }
    public static String getNumberCode(String old) {
        if(isEmpty(old)) {
            return "00000001";
        }else {
            Integer oldNumber = Integer.valueOf(old)+1;
			/*if(oldNumber < 10) {
				return "0"+oldNumber;
			}else {
				return oldNumber.toString();
			}*/
            if(oldNumber.toString().length() < 8) {
                String preFix = "";
                for(int i = 0 ; i < 8 - oldNumber.toString().length() ; i++) {
                    preFix += 0;
                }
                return preFix + oldNumber;
            }
            return oldNumber.toString();
        }
    }

    public static Long getRealNumber(String code) {
        if(isEmpty(code)) {
            throw new RRException("要转换为数字的字符串不能为空.");
        }else {
            for(int i = 0 ; i < code.length() ; i++) {
                if(code.charAt(i) != 0) {
                    try {
                        return Long.valueOf(code.substring(i, code.length()));
                    } catch (Exception e) {
                        new RRException("改字符串包含不是数字的字符.");
                    }
                }
            }
        }

        throw new RRException("改code为全0的字符串.");
    }



    /**
     * 不足8位,在前面填充0，例如:10 -> 00000010
     * @param old
     * @return
     */
    public String fillZero(String old) {
        if(!isEmpty(old)) {
            String preFix = "";
            for(int i = 0 ; i < 8 - old.length() ; i++) {
                preFix += 0;
            }
            return preFix+old;
        }
        throw new RRException("要填充0的参数不能为null,或者空字符串");
    }
}
