package net.letuu.util;

import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.ResultSetDynaClass;

import javax.sql.DataSource;
import java.io.StringReader;
import java.sql.*;

public class DBUtil {
    static DataSource dataSource1;
    static DataSource dataSource2;

    static {
        dataSource1 = (DataSource) SpringUtils.getBean("dataSource");
        dataSource2 = (DataSource) SpringUtils.getBean("dataSource2");
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource1.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static Connection getConnection_server1() {
        Connection conn = null;
        try {
            conn = dataSource2.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void close(ResultSet rs, Statement stmt, Connection conn)
            throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }


    /**
     * Method setValue.
     * 设置PreparedStatement中特定位置的值
     *
     * @param stmt          sql语句预声明
     * @param stmtPos       sql语句预声明当前位置
     * @param value         字段值
     * @param fieldTypeName 字段类型
     */
    public static void setValue(PreparedStatement stmt, int stmtPos, String value, String fieldTypeName) throws SQLException {

        int fieldType = DBUtil.convertSQLType(fieldTypeName);
        switch (fieldType) {
//                case Types.VARCHAR:
//                case Types.BLOB:
//                case Types.LONGVARCHAR:
            case Types.CHAR: {
                if (value != null && value.length() > 0) {
                    int size = value.length();
                    if (size > 2000) {
                        stmt.setCharacterStream(stmtPos, new StringReader(value), size);
                    } else {
                        stmt.setString(stmtPos, value);
                    }

                } else {
                    stmt.setString(stmtPos, null);
                }
                break;
            }
//                case Types.BOOLEAN:{
//                    stmt.setString(stmtPos,value);
//                    break;
//                }
            case Types.INTEGER: {
                //2006-02-28 edit by zhengxq
                //stmt.setString(stmtPos,value);
                //begin
                if (value == null || value.length() == 0) {
                    stmt.setNull(stmtPos, Types.INTEGER);
                } else {
                    stmt.setInt(stmtPos, Integer.parseInt(value));
                }
                //end
                break;
            }
            case Types.BIGINT: {
                //2006-02-28 edit by zhengxq
                //stmt.setString(stmtPos,value);
                //begin
                if (value == null || value.length() == 0) {
                    stmt.setNull(stmtPos, Types.INTEGER);
                } else {
                    stmt.setLong(stmtPos, Long.parseLong(value));
                }
                //end
                break;
            }
//                case Types.FLOAT:{
//                    stmt.setString(stmtPos,value);
//                    break;
//                }
            case Types.TIMESTAMP: {
                Date date = StringHelper.convertStringToDate(value);
                Timestamp timeStamp = null;
                if (date != null) {
                    timeStamp = new Timestamp(date.getTime());
                    ;
                }
                if (timeStamp != null) {
                    stmt.setTimestamp(stmtPos, timeStamp);
                } else {
                    stmt.setTimestamp(stmtPos, null);
                }

                break;
            }
            case Types.DATE: {
                Date date = StringHelper.convertStringToDate(value);
                if (date != null) {
                    stmt.setDate(stmtPos, date);
                } else {
                    stmt.setDate(stmtPos, null);
                }

                break;
            }
//                case Types.TIME:{
//                    stmt.setString(stmtPos,value);
//                    break;
//                }
//                case Types.DOUBLE:{
//                    stmt.setString(stmtPos,value);
//                    break;
//                }
            default: {
                //2006-02-28 edit by zhengxq
                //stmt.setString(stmtPos,value);
                //begin
                if (value != null && value.length() > 0) {
                    stmt.setString(stmtPos, value);
                } else {
                    stmt.setString(stmtPos, null);
                }
                //end
                break;
            }
        }
    }

    /**
     * 将java类型转为SQL Types
     *
     * @param fieldTypeName
     * @return
     */
    public static int convertSQLType(String fieldTypeName) {
        if (fieldTypeName.equals("java.lang.String")) {
            return Types.VARCHAR;
        }
        if (fieldTypeName.equals("java.lang.Timestamp")) {
            return Types.TIMESTAMP;
        }
        if (fieldTypeName.equals("java.lang.Long")) {
            return Types.BIGINT;
        }
        if (fieldTypeName.equals("java.lang.Integer")) {
            return Types.INTEGER;
        }

        return Types.VARCHAR;
    }

    /**
     * 根据rs生成插入的sql语句
     *
     * @param tableName
     * @param tableSuffix
     * @param rs
     * @return
     * @throws SQLException
     */
    public static String generateInsertSQL(String tableName, int tableSuffix, ResultSet rs) throws SQLException {
        StringBuffer sb = new StringBuffer();
        StringBuffer sbValue = new StringBuffer();
        boolean begin = true;

        if (tableSuffix == 0) {
            sb.append("insert into ").append(tableName).append(" (");
        } else {
            sb.append("insert into ").append(tableName).append("_").append(tableSuffix).append(" (");
        }
        ResultSetDynaClass rsDynaClass = new ResultSetDynaClass(rs);
        DynaProperty[] dynaPropertyList = rsDynaClass.getDynaProperties();

        for (DynaProperty dynaProperty : dynaPropertyList) {
            if (!begin) {
                sb.append(",");
                sbValue.append(",");
            }
            sb.append(dynaProperty.getName());
            sbValue.append("?");
            begin = false;
        }
        sb.append(") values (").append(sbValue).append(")");
        System.out.println(sb.toString());
        return sb.toString();
    }

}
