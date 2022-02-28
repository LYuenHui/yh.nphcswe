package com.nphcswe.helper;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class UserDateDeserialiser extends StdDeserializer<Date> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2684309462377756358L;

	public UserDateDeserialiser() {
		this(null);
	}

	public UserDateDeserialiser(Class<?> vc) {
		super(vc);
	}

	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		final String date = node.textValue();

		return DateUtil.parseDate(date, DateUtil.DATE_PATTERNS);
	}

}
