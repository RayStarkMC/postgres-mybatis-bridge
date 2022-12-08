/*
 * Copyright 2022 RayStarkMC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
