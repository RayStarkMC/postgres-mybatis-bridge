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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static raystark.pmb.util.Exceptions.throwing;

public final class TSRangeDeserializer {

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_DATE)
        .appendLiteral(" ")
        .append(DateTimeFormatter.ISO_TIME)
        .toFormatter();

    public TSRange deserialize(String value) {
        return
            value == null ? null :
            value.equals("empty") ? new TSRange.Empty() :
            parseNonEmpty(value);
    }

    private TSRange parseNonEmpty(String value) {
        var array = value.split(",");
        var l = array[0];
        var u = array[1];

        Bound lowerBound =
            l.equals("(") ? new Bound.Infinity() :
            l.startsWith("(") ? new Bound.Exclusive(parsePSTimeStamp(l.substring(2, l.length()-1))) :
            l.startsWith("[") ? new Bound.Inclusive(parsePSTimeStamp(l.substring(2, l.length()-1))) :
            throwing(IllegalArgumentException::new);

        Bound upperBound =
            u.equals(")") ? new Bound.Infinity() :
            u.endsWith(")") ? new Bound.Exclusive(parsePSTimeStamp(u.substring(1, u.length()-2))) :
            u.endsWith("]") ? new Bound.Inclusive(parsePSTimeStamp(u.substring(1, u.length()-2))) :
            throwing(IllegalArgumentException::new);

        return new TSRange.NonEmpty(lowerBound, upperBound);
    }

    private Instant parsePSTimeStamp(String value) {
        return FORMATTER.parse(value, LocalDateTime::from).toInstant(ZoneOffset.UTC);
    }
}
