package com.nphcswe.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.nphcswe.helper.exception.BadInputException;

public class CsvReader {
	private static final Logger logger = LogManager.getLogger();

	private CsvReader() {
	}

	public static <T> List<T> toList(Class<T> clazz, byte[] data) {

		CsvMapper csvMapper = new CsvMapper();
		CsvSchema csvSchema = csvMapper.typedSchemaFor(clazz).withHeader();
		logger.info("csvSchema: {}", csvSchema);
		List<T> resulttList = new ArrayList<>();

		try {
			MappingIterator<T> objectList = csvMapper.readerWithSchemaFor(clazz).with(csvSchema).readValues(data);
			logger.info("objectList: {}", objectList);
			resulttList = objectList.readAll();
			logger.info("resulttList: {}", resulttList);
		} catch (IOException ex) {
			if (ex.getMessage().toLowerCase().contains("too many entries")) {
				throw new BadInputException(ResponseMessage.MSG_ERR_CSV_COLUMN_FORMAT);
			}
		}
		return resulttList;

	}
}