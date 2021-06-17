package net.ishchenko.idea.minibatis.util;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * restore the mybatis generate sql to original whole sql
 * @author ob
 */
public class RestoreIbatisSqlUtil {
    public static final String PREPARING = "sql语句：";
    public static final String PARAMETERS = "参数：";

    private static Set<String> needAssembledType = new HashSet<>();
    private static final String QUESTION_MARK = "?";
    private static final String REPLACE_MARK = "_o_?_b_";
    private static final String PARAM_TYPE_REGEX = "\\(\\D{3,30}?\\),{0,1}";
    private static final String PARAM_SPLIT = ",";

    //参数格式类型，暂列下面几种
    static {
        needAssembledType.add("(String)");
        needAssembledType.add("(Timestamp)");
        needAssembledType.add("(Date)");
        needAssembledType.add("(Time)");
    }

    public static String match(String p, String str) {
        Pattern pattern = Pattern.compile(p);
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    /**
     * Sql语句还原，整个插件的核心就是该方法
     * @param preparing
     * @param parameters
     * @return
     */
    public static String restoreSql(final String preparing, final String parameters) {
        try {
            String restoreSql = "";
            String preparingSql = "";
            String parametersSql = "";
            try {
                if(preparing.contains(PREPARING)) {
                    preparingSql = preparing.split(PREPARING)[1].trim();
                } else {
                    return "选中的问sql文本必须包含[sql语句：]";
                }
                boolean hasParam = false;
                if(parameters.contains(PARAMETERS)) {
                    if(parameters.split(PARAMETERS).length > 1) {
                        parametersSql = parameters.split(PARAMETERS)[1];
                        parametersSql = parametersSql.substring(parametersSql.indexOf("[") + 1, parametersSql.indexOf("]"));
                        if(StringUtils.isNotBlank(parametersSql)) {
                            hasParam = true;
                        }
                    }
                } else {
                    return "选中的问sql文本必须包含[参数：]";
                }
                if(hasParam) {
                    preparingSql = StringUtils.replace(preparingSql, QUESTION_MARK, REPLACE_MARK);
                    preparingSql = StringUtils.removeEnd(preparingSql, "\n");
                    parametersSql = StringUtils.removeEnd(parametersSql, "\n");
                    int questionMarkCount = StringUtils.countMatches(preparingSql, REPLACE_MARK);
                    String[] paramArray = parametersSql.split(PARAM_SPLIT);
                    for(int i=0; i < questionMarkCount; ++i) {
                        paramArray[i] = StringUtils.removeStart(paramArray[i], " ");
                        parametersSql = StringUtils.replaceOnce(StringUtils.removeStart(parametersSql, " "), paramArray[i], "");
                        String paramType = match("(\\(\\D{3,25}?\\))", paramArray[i]);
                        preparingSql = StringUtils.replaceOnce(preparingSql, REPLACE_MARK, "'" + paramArray[i] + "'" + "    /* SQL参数" + (i + 1) + " */    ");
                        paramType = paramType.replace("(", "\\(").replace(")", "\\)") + ", ";
                        parametersSql = parametersSql.replaceFirst(paramType, "");
                    }
                }
                restoreSql = simpleFormat(preparingSql);
                if(!restoreSql.endsWith(";")) {
                    restoreSql += ";";
                }
                if(restoreSql.contains(REPLACE_MARK)) {
                    restoreSql = StringUtils.replace(restoreSql, REPLACE_MARK, "error");
                    restoreSql += "\n---This is an error sql!---";
                }
            } catch (Exception e) {
                return "restore mybatis sql error!";
            }
            return BasicFormatter.newFormat(restoreSql);
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    /**
     * 简单的格式化
     * @param sql
     * @return
     */
    public static String simpleFormat(String sql) {
        if(StringUtils.isNotBlank(sql)) {
            return sql.replaceAll("(?i)\\s+from\\s+", "\n FROM ")
                    .replaceAll("(?i)\\s+select\\s+", "\n SELECT ")
                    .replaceAll("(?i)\\s+where\\s+", "\n WHERE ")
                    .replaceAll("(?i)\\s+left join\\s+", "\n LEFT JOIN ")
                    .replaceAll("(?i)\\s+right join\\s+", "\n RIGHT JOIN ")
                    .replaceAll("(?i)\\s+inner join\\s+", "\n INNER JOIN ")
                    .replaceAll("(?i)\\s+limit\\s+", "\n LIMIT ")
                    .replaceAll("(?i)\\s+on\\s+", "\n ON ")
                    .replaceAll("(?i)\\s+union\\s+", "\n UNION ");
        }
        return "";
    }

    public static boolean matchesIbatisSql (String str) {
        return str != null && str.length() > 0 && str.contains("sql语句：");
    }
    public static boolean matchesIbatisParam (String str) {
        return str != null && str.length() > 0 && str.contains("参数：");
    }
}