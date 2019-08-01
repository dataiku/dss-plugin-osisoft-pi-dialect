package com.dataiku.dip.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang.StringUtils;

import com.dataiku.dip.coremodel.Dataset;
import com.dataiku.dip.coremodel.SchemaColumn;
import com.dataiku.dip.datasets.Type;
import com.dataiku.dip.sql.queries.QuotedPortionFinderFactory;
import com.dataiku.dip.sql.queries.QuotedPortionFinders;
import com.dataiku.dss.shadelib.org.joda.time.DateTimeZone;

public class OSISoftPISQLDialect extends GenericSQLDialect {
    @Override
    public String getId() {
        return "OSISoftPI";
    }

    @Override
    public boolean supportsCommitAndRollback() {
        return false;
    }

    @Override
    public LimitMethod getLimitMethod() {
        return null;
    }

    @Override
    public String getLimitedQuery(String query, long size) {
        return query;
    }

    @Override
    public boolean supportsResultSetMetadataOnPreparedStatement() {
        return false;
    }

    @Override
    public boolean supportsIndexing() {
        return false;
    }

    @Override
    public boolean supportsIndexingOnTemporaryTables() {
        return false;
    }

    @Override
    public boolean supportsInDatabaseCharts() {
        return false;
    }

    @Override
    public String getValueAsDSSString(ResultSet rs, int sqlType, int colIdx, Type dssType, boolean normalizeDoubles,
            boolean timestampNoTzAsDate, DateTimeZone assumedTz) throws SQLException {
        switch (sqlType) {
        case Types.FLOAT:
        case Types.DOUBLE:
        case Types.DECIMAL:
        case Types.NUMERIC:
        case Types.REAL:
            /* OSISoft will return the string "null" when using getString on null floats - We need to intercept this */
            String v = rs.getString(colIdx);
            if ("null".equals(v)) {
                return null;
            } else if (StringUtils.isNotEmpty(v) && normalizeDoubles && needsDoubleNormalization()
                    && (dssType == Type.DOUBLE || dssType == Type.FLOAT)) {
                return Double.valueOf(v).toString();
            }
            return v;
        default:
            return super.getValueAsDSSString(rs, sqlType, colIdx, dssType, normalizeDoubles, timestampNoTzAsDate, assumedTz);
        }
    }

    /* This is far from a complete mapping, but at least allows basic metrics to work */
    @Override
    public DSSTypeSQLMapping getSQLType(SchemaColumn schemaColumn, Dataset dataset) {
        Type type = schemaColumn.getType();
        switch (type) {
        case INT:
            return new DSSTypeSQLMapping(Type.INT, Types.INTEGER, "UInt32");
        case BIGINT:
            return new DSSTypeSQLMapping(Type.BIGINT, Types.BIGINT, "UInt64");
        default:
            return super.getSQLType(schemaColumn, dataset);
        }
    }

    /* Not really tested ... */
    @Override
    public QuotedPortionFinderFactory[] getSemicolonExclusionPortionFinders() {
        return new QuotedPortionFinderFactory[] {
                QuotedPortionFinders.SingleLineCommentFinder.META,
                QuotedPortionFinders.SharpSingleLineCommentFinder.META,
                QuotedPortionFinders.MultiLineCommentFinder.META,
                QuotedPortionFinders.BackslashEscapedSingleQuotedFinder.META,
                QuotedPortionFinders.BackslashEscapedDoubleQuotedFinder.META,
                QuotedPortionFinders.BackTickedNoEscapeFinder.META
        };
    }

    @Override
    public int getIdentifiersMaxLength() {
        return 128; // ?
    }

    @Override
    public int getMaxPossibleVarcharLen() {
        return 2000; // ?
    }
}
