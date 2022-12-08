package raystark.pmb.range;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TSRangeTypeHandler extends BaseTypeHandler<TSRange> {
    private final TSRangeSerializer serializer = new TSRangeSerializer();
    private final TSRangeDeserializer deserializer = new TSRangeDeserializer();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TSRange parameter, JdbcType jdbcType) throws SQLException {
        var obj = new PGobject();
        obj.setType("tsrange");
        obj.setValue(serializer.serialize(parameter));
        ps.setObject(i, obj);
    }

    @Override
    public TSRange getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return deserializer.deserialize(rs.getString(columnName));
    }

    @Override
    public TSRange getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return deserializer.deserialize(rs.getString(columnIndex));
    }

    @Override
    public TSRange getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return deserializer.deserialize(cs.getString(columnIndex));
    }
}
