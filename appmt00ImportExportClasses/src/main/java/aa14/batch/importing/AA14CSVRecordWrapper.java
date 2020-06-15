package aa14.batch.importing;

import org.apache.commons.csv.CSVRecord;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.util.types.Strings;


@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
class AA14CSVRecordWrapper {
	private final CSVRecord _record;
	
	public String get(final String colName) {
		String val = _record.get(colName);
		return Strings.isNOTNullOrEmpty(val) ? val.trim() : null;
	}
}
