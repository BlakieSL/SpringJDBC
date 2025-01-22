package org.example.springjdbc.repository.implementation.library;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.sql.Types;

import static org.example.springjdbc.helper.QueryStatements.UPDATE_LIBRARY_INFO;

@Component
public class UpdateLibraryInfo extends SqlUpdate {
    UpdateLibraryInfo(DataSource ds) {
        super(ds, UPDATE_LIBRARY_INFO);
        super.declareParameter(new SqlParameter("address", Types.VARCHAR));
        super.declareParameter(new SqlParameter("phone", Types.VARCHAR));
        super.declareParameter(new SqlParameter("id", Types.BIGINT));
    }
}
